package edu.wustl.cielo.meta

import edu.wustl.cielo.Profile
import edu.wustl.cielo.Project
import edu.wustl.cielo.RegistrationCode
import edu.wustl.cielo.SoftwareLicense
import edu.wustl.cielo.UserAccount
import edu.wustl.cielo.UserAccountUserRole
import spock.lang.Specification

class MetadataIntegrationSpec extends Specification {

    void cleanup() {
        Metadata.list().each {
            it.delete()
        }

        Project.list().each {
            it.delete()
        }

        SoftwareLicense.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }
    }

    void "test saving"() {
        UserAccount user
        SoftwareLicense softwareLicense
        Project project
        Metadata metadata

        Metadata.withTransaction {
            given:
                user = new UserAccount(username: "someuser", password: "somePassword").save()
                softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                        url: "http://www.rerlicense.com").save()
                project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                        description: "some description").save()

            when: "no props"
                metadata = new Metadata()

            then: "save fails"
                !metadata.save()

            when: "add key"
                metadata = new Metadata(key: "something")

            then: "still fails due to missing value prop"
                !metadata.save()

            when: "add key and value"
                metadata = new Metadata(key: "something", value: "something's value")

            then: "still fails due to missing project prop"
                !metadata.save()

            when: "add key, value and project"
                metadata = new Metadata(key: "something", value: "something's value", project: project)

            then: "save successful"
                metadata.save()
        }
    }

    void "test toString"() {
        UserAccount user
        SoftwareLicense softwareLicense
        Project project
        Metadata metadata

        Metadata.withTransaction {
            given:
                user = new UserAccount(username: "someuser", password: "somePassword").save()
                softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                        url: "http://www.rerlicense.com").save()
                project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                        description: "some description").save()
                metadata = new Metadata(key: "something", value: "something's value", project: project).save()

            when: "toString is called"
                def returnVal = metadata.toString()

            then:
                assert returnVal == "${metadata.key}: ${metadata.value}"
        }
    }
}
