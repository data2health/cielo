package edu.wustl.cielo

import edu.wustl.cielo.enums.FileUploadType
import edu.wustl.cielo.enums.ProjectStatusEnum
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclSid
import grails.plugin.springsecurity.acl.AclObjectIdentity
import grails.plugin.springsecurity.acl.AclEntry
import grails.testing.services.ServiceUnitTest
import grails.web.mapping.LinkGenerator
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.acls.model.AclCache
import spock.lang.Specification
import grails.testing.gorm.DomainUnitTest
import grails.plugin.springsecurity.SpringSecurityService

class ProjectServiceSpec extends Specification implements ServiceUnitTest<ProjectService>, DomainUnitTest<Project> {

    def webRoot
    def assetsRoot
    def assetResourceLocator
    PageRenderer pageRenderer
    LinkGenerator grailsLinkGenerator
    ActivityService activityService
    SpringSecurityService springSecurityService
    UserAccountService userAccountService
    CloudService cloudService
    AclService aclService
    AclCache aclCache
    CustomAclService customAclService

    def setup() {
        mockDomains(UserRole, Institution, Profile, Annotation, SoftwareLicense, RegistrationCode, UserAccountUserRole,
                AclClass, AclSid, AclObjectIdentity, AclEntry)
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        assetsRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/grails-app/assets"
        grailsLinkGenerator = Mock()
        activityService = Mock()
        springSecurityService = Mock()
        cloudService = new CloudService()
        pageRenderer = new PageRenderer()
        customAclService = new CustomAclService()
        aclService = new AclService()
        aclCache = Mock()
        aclService.aclCache = aclCache
        customAclService.aclService = aclService

        service.customAclService = customAclService
        service.grailsLinkGenerator = grailsLinkGenerator
        service.activityService = activityService

        userAccountService = new UserAccountService()
        userAccountService.assetResourceLocator = assetResourceLocator
        userAccountService.assetResourceLocator = [findAssetForURI: { String URI ->
            new ByteArrayResource(new File(assetsRoot + "/images/${URI}").bytes)
        }]

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

    void "test saveProjectComment" () {
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

        when:
            def result = service.saveProjectComment(user, project.id, "My comment")

        then:
            result

    }

    void "test saveProjectCommentReply"() {
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

        when:
            def result = service.saveProjectComment(user, project.id, "My comment")

        then:
            result

        //now get the comment and then add a reply to it
        when:
            Comment comment = project.comments[0]
            assert comment.responses.size() == 0
            service.saveProjectCommentReply(user, comment.id, "My reply")

        then:
            comment.responses.size() == 1
    }

    void "test getComments"() {
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

        when:
            def result = service.saveProjectComment(user, project.id, "My comment")

        then:
            result

        //now get the comment and then add a reply to it
        when:
            Comment comment = project.comments[0]
            assert comment.responses.size() == 0
            def results = service.getComments(project.id)

        then:
            results == [comment]
    }

    void "test saveProjectBasicChanges"() {

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

        when:
             service.saveProjectBasicChanges(project.id, "My Project", "new description", project.annotations.collect { it.id }, softwareLicense.id, false)

        then:
            project.name == "My Project"
            project.description == "new description"
    }

    void "test likeProjectComment"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense

        springSecurityService = new SpringSecurityService()
        service.springSecurityService = springSecurityService
        service.springSecurityService.metaClass.principal = [id: user.id]

        expect: "no projects"
        Project.list() == []

        when: "create a good project"
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        then: "saves correctly"
            Project.list()

        when:
            def result = service.saveProjectComment(user, project.id, "My comment")

        then:
            result

        when:
            Comment comment = project.comments[0]
            result = service.likeProjectComment(comment.id)

        then:
            result
            comment.likedByUsers.contains(user)
    }

    void "test removeProjectCommentLike"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense

        springSecurityService = new SpringSecurityService()
        service.springSecurityService = springSecurityService
        service.springSecurityService.metaClass.principal = [id: user.id]

        expect: "no projects"
            Project.list() == []

        when: "create a good project"
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        then: "saves correctly"
            Project.list()

        when:
            def result = service.saveProjectComment(user, project.id, "My comment")

        then:
            result

        when:
            Comment comment = project.comments[0]
            result = service.likeProjectComment(comment.id)

        then:
            result
            comment.likedByUsers.contains(user)

        //now remove the like
        when:
            result = service.removeProjectCommentLike(comment.id)

        then:
            result
            comment.likedByUsers.size() == 0

    }

    void "test getUsersWhoLikedComment"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense

        springSecurityService = new SpringSecurityService()
        service.springSecurityService = springSecurityService
        service.springSecurityService.metaClass.principal = [id: user.id]

        expect: "no projects"
            Project.list() == []

        when: "create a good project"
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()

        then: "saves correctly"
            Project.list()

        when:
            def result = service.saveProjectComment(user, project.id, "My comment")

        then:
            result

        when:
            Comment comment = project.comments[0]
            result = service.likeProjectComment(comment.id)

        then:
            result
            comment.likedByUsers.contains(user)

        when:
            def usersResults = service.getUsersWhoLikedComment(comment.id)

        then:
            usersResults == new TreeSet<UserAccount>([user])
    }

    void "test incrementViewsCounter"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount viewer = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense

        springSecurityService = new SpringSecurityService()
        service.springSecurityService = springSecurityService
        service.springSecurityService.metaClass.principal = [id: viewer.id]

        expect: "no projects"
            Project.list() == []

        when: "create a good project"
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        then: "saves correctly"
            Project.list()

        when:
            //owner and contributors cannot increase the popularity of a project
            service.incrementViewsCounter(project)

        then:
            project.views == 1

        when:
            service.incrementViewsCounter(project)

        then:
            project.views == 2
    }

    void "test getMyProjects"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description").save()

        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when:
            def results = service.getMyProjects(user, 1, 0)

        then:
            results.contains(project)
            !results.contains(project1)

        when:
            Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                    description: "some description").save()
            Project project3 = new Project(projectOwner: user, name: "Project3", license: softwareLicense,
                    description: "some description").save()
            results = service.getMyProjects(user, 5, 0)

        then:
            results.contains(project)
            results.contains(project2)
            results.contains(project3)
            !results.contains(project1)

    }

    void "test getNumberOfPagesForMyProjects"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description").save()

        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when:
            int results = service.getNumberOfPagesForMyProjects(user, 1)

        then:
            results == 1

        when:
            Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                    description: "some description").save()
            Project project3 = new Project(projectOwner: user, name: "Project3", license: softwareLicense,
                    description: "some description").save()
            results = service.getNumberOfPagesForMyProjects(user, 1)

        then:
            results == 3

        when:
            results = service.getNumberOfPagesForMyProjects(user, 3)

        then:
            results == 1
    }

    void "test getPublicProjects"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()

        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when:
            List<Project> results = service.getPublicProjects(0, 5)

        then:
            results.contains(project1)
            !results.contains(project)

        when:
            Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()
            Project project3 = new Project(projectOwner: user, name: "Project3", license: softwareLicense,
                description: "some description", shared: true).save()
            results = service.getPublicProjects(0, 5)

        then:
            results.size() == 3
            results.contains(project1)
            results.contains(project2)
            results.contains(project3)
    }

    void "test getNumberOfPagesForPublicProjects"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()

        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when:
            int results = service.getNumberOfPagesForPublicProjects(5)

        then:
            results == 1

        when:
            Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()
            Project project3 = new Project(projectOwner: user, name: "Project3", license: softwareLicense,
                description: "some description", shared: true).save()
            results = service.getNumberOfPagesForPublicProjects(5)

        then:
            results == 1

        when:
            results = service.getNumberOfPagesForPublicProjects(1)

        then:
            results == 3
    }

    void "test deleteProject"() {
        Project project
        UserAccount user = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(user)
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        customAclService.bootstrapAcls()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()
        new AclClass(className: Project.name).save()
        customAclService.setupBasePermissionsForProject(project)
        customAclService.setupBasePermissionsForProject(project1)

        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        cloudService.metaClass.deleteRepo = { Long projectId ->
            return true
        }

        service.cloudService = cloudService

        when:
            int results = service.getNumberOfPagesForPublicProjects(5)

        then:
            results == 1

        when:
            Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()
            customAclService.setupBasePermissionsForProject(project2)
            Project project3 = new Project(projectOwner: user, name: "Project3", license: softwareLicense,
                description: "some description", shared: true).save()
            customAclService.setupBasePermissionsForProject(project3)
            boolean returnVal = service.deleteProject(user, project3.id)

        then:
            returnVal

        when:
            returnVal = service.deleteProject(user2, project2.id)

        then:
            !returnVal

        when:
            returnVal = service.deleteProject(user2, project1.id)

        then:
            returnVal
    }

    void "test addTeamToProject"() {
        Project project
        UserAccount user = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(user)
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        customAclService.bootstrapAcls()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()

        customAclService.setupBasePermissionsForProject(project)
        customAclService.setupBasePermissionsForProject(project1)

        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService
        customAclService.setupBasePermissionsForProject(project)

        when:
            service.addTeamToProject(user, project.id, "New Team", [user2.id])

        then:
            project.getTeams().size() == 1

        when:
            Team team = new Team(name: "The avengers", administrator: user).save()
            boolean results = service.addTeamToProject(project.id, team.id)

        then:
            results
            project.teams[1].name == "The avengers"
    }

    void "test getNewEmptyProjectForUser"() {
        Project project
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()

        when:
            project = service.getNewEmptyProjectForUser(user)

        then:
            project
            project.projectOwner == user
    }

    void "test saveNewProject"() {
        UserAccount user = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(user)
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        customAclService.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description")

        customAclService.setupBasePermissionsForProject(project)

        when:
            service.saveNewProject(user, project, null, softwareLicense.id, "", null, -1L , null, null)

        then:
            project.id

        when:
            service.saveNewProject(user, project, null, softwareLicense.id, "My team", new ArrayList<Long>([user2.id]), -1L, null, null)

        then:
            project.teams[0].name == "My team"
            project.teams[0].members.contains(user2)
    }

    void "test addBundleToProject"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        given:
            project.codes.size() == 0
            project.datas.size() == 0

        when:
            service.addBundleToProject(project.id, FileUploadType.CODE, "http://myurl.edu/folder", null, "file.ext", "short desctiption")

        then:
            project.codes.size() == 1
            project.datas.size() == 0

        when:
            service.addBundleToProject(project.id, FileUploadType.DATA, "http://myurl.edu/folder", null, "file.ext", "short desctiption")

        then:
            project.codes.size() == 1
            project.datas.size() == 1
    }

    void "test removeTeam"() {
        Project project
        UserAccount user = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(user)
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        customAclService.bootstrapAcls()
        SoftwareLicense softwareLicense
        softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project1 = new Project(projectOwner: user2, name: "Project2", license: softwareLicense,
                description: "some description", shared: true).save()

        customAclService.setupBasePermissionsForProject(project)
        customAclService.setupBasePermissionsForProject(project1)
        springSecurityService = new SpringSecurityService()
        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when:
            Team team = new Team(name: "The avengers", administrator: user).save()
            boolean results = service.addTeamToProject(project.id, team.id)

        then:
            results
            project.teams[0].name == "The avengers"

        when:
            service.removeTeam(user, team.id, project.id)

        then:
            project.teams.size() == 0
            team //but team is still there - not deleted
    }

    void "test removeBundleFromProject"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        given:
            project.codes.size() == 0
            project.datas.size() == 0

        when:
            service.addBundleToProject(project.id, FileUploadType.CODE, "http://myurl.edu/folder", null, "file.ext", "short desctiption")

        then:
            project.codes.size() == 1
            project.datas.size() == 0

        when:
            service.addBundleToProject(project.id, FileUploadType.DATA, "http://myurl.edu/folder", null, "file.ext", "short desctiption")

        then:
            project.codes.size() == 1
            project.datas.size() == 1

        when:
            service.removeBundleFromProject(user, project.id, project.codes[0].id, FileUploadType.CODE)

        then:
            project.codes.size() == 0
            project.datas.size() == 1
    }

    void "test renderTableRows"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        pageRenderer.metaClass.render = { Map args ->
            if (args.model.usersProject) return "You want your projects"
            else return "You want the public projects"
        }

        service.groovyPageRenderer = pageRenderer

        when:
            String returnVal = service.renderTableRows([projects: [project], usersProject: true])

        then:
            returnVal == "You want your projects"

        when:
            returnVal = service.renderTableRows([projects: [project], usersProject: false])

        then:
            returnVal == "You want the public projects"
    }

    void "test projectAlreadyHaseBundleWithSameName"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        when:
            boolean result = service.projectAlreadyHasBundleWithSameName(project.id, FileUploadType.DATA, "filename.txt")

        then:
            !result

        when:
            Data data = new Data(name: "filename.txt", description: "Some description", repository: "repo", project: project).save()
            project.datas.add(data)
            project.save()
            result = service.projectAlreadyHasBundleWithSameName(project.id, FileUploadType.DATA, "filename.txt")

        then:
            result
    }

    void "test nextRevisionForBundle"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        when:
            int revision = service.nextRevisionForBundle(project.id, FileUploadType.DATA, "filename.txt")

        then:
            revision == 0

        when:
            Data data = new Data(name: "filename.txt", description: "Some description", repository: "repo", project: project).save()
            project.datas.add(data)
            project.save()
            revision = service.nextRevisionForBundle(project.id, FileUploadType.DATA, "filename.txt")

        then:
            revision == 1
    }
}