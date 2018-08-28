package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import grails.plugin.springsecurity.acl.AclSid
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclObjectIdentity
import grails.plugin.springsecurity.acl.AclEntry
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

class RestControllerSpec extends Specification implements ControllerUnitTest<RestController>, DomainUnitTest<UserAccount> {

    private static final UUID key = UUID.randomUUID()
    RestService restService
    ProjectService projectService
    CustomAclService customAclService
    SpringSecurityService springSecurityService


    def setup() {
        mockDomains(AclSid, AclClass, AclObjectIdentity, AclEntry, UserRole, UserAccountUserRole)
        springSecurityService = Mock()
        restService     = new RestService()
        customAclService = new CustomAclService()
        projectService  = new ProjectService()

        projectService.customAclService = customAclService
        controller.projectService = projectService
        controller.springSecurityService = springSecurityService
    }

    void "test appVersion"() {

        when:
            controller.appVersion()

        then:
            response.json.version == grailsApplication.config.getProperty("info.app.version")

    }

    void "test getListOfProjects"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                description: "some description").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        restService.metaClass.getNumberOfPages = { boolean filterOnSharedOnly, String filterTerm, int max ->
            if (!max || max <= 0) return 1
            else {
               return Math.round( Math.ceil(2 / max) )
            }
        }

        restService.metaClass.getTotalNumberOfProjects = { boolean filterOnSharedOnly, String filterTerm ->
           return 2
        }

        restService.metaClass.getProjectData = { boolean filterOnSharedOnly, String filterTerm, int offset, int max ->
           return [[identity: project.id], [identity: project2.id]]
        }

        controller.restService  = restService

        when:
            controller.getListOfProjects()

        then:
            response.json.numberOfPages == 1
            response.json.totalCount    == 2

        when:
            response.reset()
            params.max = 1
            controller.getListOfProjects()

        then:
            response.json.numberOfPages == 2
            response.json.totalCount    == 2
    }

    void "test createProject"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project

        UserRole userRole = new UserRole()
        userRole.authority = "ROLE_SUPERUSER"
        userRole.save()

        UserAccountUserRole userAccountUserRole = new UserAccountUserRole()
        userAccountUserRole.userRole = userRole
        userAccountUserRole.userAccount = user
        userAccountUserRole.save()

        projectService.metaClass.createNewProject = { String name, String desc, Long licenseId, String licenseLabel, String username ->
            project = new Project(name: name, description: desc, softwareLicense: SoftwareLicense.findById(licenseId), projectOwner: user).save()
            return project.id
        }
        controller.projectService = projectService

        given:
            Project.all.size() == 0

        when:
            params.name = "My project"
            params.licenseId = softwareLicense.id
            params.desc = "Description"
            controller.createProject()

        then:
            response.json.success
            response.json.projectId == Project.first().id
    }

    void "test changeProjectVisibility"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        given:
            !project.shared

        when:
            params.id = project.id
            params.shared = true
            controller.changeProjectVisibility()

        then:
            response.json.success
    }

    void "test listLicenses"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()

        when:
            controller.listLicenses()

        then:
            response.json.size() == 1
            response.json[0].name == "RER License 1.0"
            response.json[0].licenseId    == softwareLicense.id

    }

    void "test restLogin"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()

        when:
            params.user = user.username
            params.pass = user.password
            controller.restLogin()

        then:
            !response.json.token

        when:
            response.reset()
            SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(key.toString(),
                "anonymousUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))))
            params.user = user.username
            params.pass = user.password
            controller.restLogin()

        then:
            response.json.error == "User doesn't have any roles. Please contact administrator."

        when:
            response.reset()
            UserRole userRole = new UserRole()
            userRole.authority = "ROLE_SUPERUSER"
            userRole.save()

            UserAccountUserRole userAccountUserRole = new UserAccountUserRole()
            userAccountUserRole.userRole = userRole
            userAccountUserRole.userAccount = user
            userAccountUserRole.save()
            params.user = user.username
            params.pass = user.password
            controller.restLogin()

        then:
            response.json.error == "User is not a rest api user."

       //TODO: test successful login by mocking PasswordEncoder

    }
}
