package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import org.hibernate.FlushMode
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Rollback
class ProjectIntegrationSpec extends Specification {

    @Autowired
    SessionFactory sessionFactory

    void cleanup() {
        SoftwareLicense.list().each {
            it.delete()
        }

        Project.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Comment.findByCommenter(it)?.delete()
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }
    }

    void "test saving"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        when: "save project with no props"
            project = new Project()

        then: "fails to save"
            !project.save()

        when: "create with not null description and name only"
            project = new Project(name: "Project1",description: "desc")

        then: "save fails"
            !project.save()

        when: "add user as well"
            user = new UserAccount(username: "someuser", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save(flush: true)
            sessionFactory.getCurrentSession().flush()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
            description: "some description").save(flush: true)

        then: "saves correctly"
            project
    }
}
