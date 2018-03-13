package edu.wustl.cielo

import edu.wustl.cielo.enums.ProjectStatusEnum
import grails.testing.services.ServiceUnitTest
import grails.web.mapping.LinkGenerator
import spock.lang.Specification
import grails.testing.gorm.DomainUnitTest
import grails.plugin.springsecurity.SpringSecurityService

class ProjectServiceSpec extends Specification implements ServiceUnitTest<ProjectService>, DomainUnitTest<Project> {

    def webRoot
    LinkGenerator grailsLinkGenerator
    ActivityService activityService
    SpringSecurityService springSecurityService

    def setup() {
        mockDomains(UserRole, Institution, Profile, Annotation, SoftwareLicense, RegistrationCode, UserAccountUserRole)
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        grailsLinkGenerator = Mock()
        activityService = Mock()
        springSecurityService = Mock()

        service.grailsLinkGenerator = grailsLinkGenerator
        service.activityService = activityService

        messageSource.addMessage('ACTIVITY_NEW_USER', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_PROJECT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_UPLOAD_DATA', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_UPLOAD_CODE', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_TEAM', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_NAME', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_DESCRIPTION', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_LICENSE', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_STATUS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_SHARED', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_TEAMS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_CODES', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_DATAS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_PUBLICATIONS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_ANNOTATIONS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_COMMENTS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_METADATAS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_INSTITUTION', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_PICTURE', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_INTERESTS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_EMAIL_ADDRESS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_CONNECTIONS', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_USER_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_PROJECT_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_UPLOAD_DATA_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_UPLOAD_CODE_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_NEW_TEAM_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_NAME_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_DESCRIPTION_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_LICENSE_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_STATUS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_SHARED_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_TEAMS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_CODES_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_DATAS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_PUBLICATIONS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_ANNOTATIONS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_COMMENTS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_PROJECT_METADATAS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_INSTITUTION_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_PICTURE_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_INTERESTS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_EMAIL_ADDRESS_BODY_TEXT', Locale.getDefault(), "hello")
        messageSource.addMessage('ACTIVITY_UPDATE_USER_CONNECTIONS_BODY_TEXT', Locale.getDefault(), "hello")
    }

    void "test bootstrapProjects"() {

        expect:"no projects"
            Project.list() == []

        when: "calling service to generate 0 projects"
            service.bootstrapProjects(0)

        then: "still no projects"
            Project.list() == []

        when: "calling service to generate 5 projects"
            service.bootstrapProjects(5)

        then: "still no projects becuase there are no user to associate with"
            Project.list() == []

        when: "boostraping required props"
            //first setup institutions
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            AnnotationService annotationService = new AnnotationService()
            annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
            UserAccountService userAccountService = new UserAccountService()
            userAccountService.bootstrapUserRoles()
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            userAccountService.setupMockAppUsers(2, 1)
            SoftwareLicenseService softwareLicenseService = new SoftwareLicenseService()
            softwareLicenseService.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))
            service.bootstrapProjects(5)

        then: "save is successfull"
            Project.list()
    }

    void "test bootstrapCodesToProject"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        expect: "no codes saved"
            Code.list() == []

        when: "creating new code with no props"
            service.bootstrapCodesToProject(null, "")

        then: "No codes are saved"
            Code.list() == []

        when: "calling with valid project"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            service.bootstrapCodesToProject(project, "")

        then: "save is successful, second param can be empty string"
            Code.list()
    }

    void "test bootstrapAnnotationsForProject"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense
        Annotation annotation

        expect: "no annotations saved"
            Annotation.list() == []

        when: "creating new code with no props"
            service.bootstrapAnnotationsForProject(null, null)

        then: "No annotations are saved"
            Annotation.list() == []

        when: "calling with valid project"
            user = new UserAccount(username: "admin", password: "somePassword").save()
            annotation = new Annotation(label: "ID").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            service.bootstrapAnnotationsForProject(project, Annotation.list())

        then: "Annotations saved to project"
            project.annotations

    }

    void "test bootstrapDatasForProject"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        expect: "no datas saved"
            Data.list() == []

        when: "creating new code with no props"
            service.bootstrapDatasForProject(null, "")

        then: "No codes are saved"
            Data.list() == []

        when: "calling with valid project"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            service.bootstrapDatasForProject(project, "")

        then: "save is successful, second param can be empty string"
            Data.list()
    }

    void "test bootstrapPublicationsForProject"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        expect: "no publications saved"
            Publication.list() == []

        when: "no props saved yet"
            service.bootstrapPublicationsForProject(null, null)

        then: "should fail"
            Publication.list() == []

        when: "Calling with the correct information"
            user = new UserAccount(username: "admin", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            service.bootstrapPublicationsForProject(project, null)

        then: "saves correctly"
            Publication.list()

    }

    void "test boostrapCommentsForProject"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        expect: "no comments saved"
            Comment.list() == []

        when: "no props saved yet"
            service.boostrapCommentsForProject(null)

        then: "should fail"
            Comment.list() == []

        when: "Calling with the correct information"
            user = new UserAccount(username: "admin", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            service.boostrapCommentsForProject(project)

        then: "saves correctly"
            Comment.list()
    }

    void "test saveProjectAndLog"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        expect: "no projects"
            Project.list() == []

        when: "create a good project"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()

        then: "saves correctly"
            Project.list()

        when: "changing something on the project"
            project.status = ProjectStatusEnum.COMPLETED
            service.saveProjectAndLog(project)

        then: "project saves correctly and the change is reflected"
            project.status == ProjectStatusEnum.COMPLETED
    }
}
