package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclSid
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import edu.wustl.cielo.enums.FileUploadType
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.acls.model.AclCache

import javax.servlet.http.Part
import grails.plugin.springsecurity.acl.AclObjectIdentity
import grails.plugin.springsecurity.acl.AclEntry
import spock.lang.*

class ProjectControllerSpec extends Specification implements ControllerUnitTest<ProjectController>, DomainUnitTest<Project> {

    def assetResourceLocator
    ProjectService projectService
    SpringSecurityService springSecurityService
    CloudService cloudService
    AclService aclService
    AclCache aclCache
    CustomAclService customAclService
    UserAccountService userAccountService
    final static String assetsRoot = new File(".").canonicalPath +  "/grails-app/assets"


    void setup() {
        projectService = new ProjectService()
        springSecurityService = new SpringSecurityService()
        cloudService = new CloudService()
        customAclService = new CustomAclService()
        userAccountService = new UserAccountService()
        userAccountService.springSecurityService = springSecurityService
        mockDomains(Profile, UserAccount, UserAccountUserRole, AclObjectIdentity, AclEntry, AclClass, AclSid, RegistrationCode)

        assetResourceLocator = [findAssetForURI: { String URI ->
            new ByteArrayResource(new File(assetsRoot + "/images/${URI}").bytes)
        }]

        aclService = new AclService()
        aclCache = Mock()
        aclService.aclCache = aclCache
        customAclService.aclService = aclService
        projectService.customAclService = customAclService
        controller.projectService = projectService
        userAccountService.assetResourceLocator = assetResourceLocator

        messageSource.addMessage('project.creation.succeeded', Locale.getDefault(), "hello")
        messageSource.addMessage('project.creation.failed', Locale.getDefault(), "hello")
        messageSource.addMessage('project.deletion.succeeded', Locale.getDefault(), "hello")
        messageSource.addMessage('project.deletion.failed', Locale.getDefault(), "hello")
    }

    void "test getMostPopularProjects"() {
        List bundles = []

        projectService.metaClass.getMostViewedProjects = { int maxNumberOfProjects, boolean sharedOnly ->
            bundles
        }

        controller.projectService = projectService

        given:
            Project.list().size() == 0

        when:
            controller.getMostPopularProjects()

        then:
            !response.json.projects

        when:
            response.reset()
            params.publicOnly = true
            params.max = 1
            UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            bundles.add(project)
            controller.getMostPopularProjects()

        then:
            response.json.projects
     }

    void "test view"() {
        UserAccount user =  userAccountService.bootstrapCreateOrGetAdminAccount()
        customAclService.bootstrapAcls()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(user)
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.incrementViewsCounter = { Project project2 ->
            null
        }

        projectService.userAccountService = userAccountService
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
        projectService.userAccountService.springSecurityService.metaClass.principal = [id: user.id]
        projectService.customAclService = customAclService
        controller.projectService = projectService

        given:
            params.id = project.id

        when:
            controller.view()

        then:
            response.status == 302 //user does not have access to view the project

        when:
            response.reset()
            controller.projectService.customAclService.setupBasePermissionsForProject(project)
            params.id = project.id
            controller.view()

        then:
            response.status == 200
    }

    void "test likeComment"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Comment comment = new Comment(text: "some comment", commenter: user).save()
        user = new UserAccount(username: "someuser", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.likeProjectComment = { Long commentId ->
            true
        }

        controller.projectService = projectService

        when:
            null

        then:
            comment.likedByUsers.size() == 0

        when:
            project.addToComments(comment)
            project.save()
            params.id = comment.id
            controller.likeComment()

        then:
            response.json.success
    }

    void "test removeCommentLike"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Comment comment = new Comment(text: "some comment", commenter: user).save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.removeProjectCommentLike = { Long commentId ->
            true
        }

        controller.projectService = projectService

        when:
            null

        then:
            comment.likedByUsers.size() == 0

        when:
            project.addToComments(comment)
            project.save()
            params.id = comment.id
            controller.removeCommentLike()

        then:
            response.json.success
    }

    void "test getCommentLikeUsers"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Comment comment = new Comment(text: "some comment", commenter: user).save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]

        projectService.metaClass.getUsersWhoLikedComment = { Long commentId ->
            [user]
        }

        controller.projectService = projectService

        views["/templates/_commentLikesUsers.gsp"] = "mock view"

        when:
            params.id = comment.id
            controller.getCommentLikeUsers()

        then:
            response.text == "mock view"

    }

    void "test getProjectComments"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Comment comment = new Comment(text: "some comment", commenter: user).save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        views["/project/_projectComments.gsp"] = "mock view"

        when:
            project.comments.add(comment)
            project.save()
            params.id = project.id
            controller.getProjectComments()

        then:
            response.text == "mock view"

    }

    void "test saveCommentReply"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Comment comment = new Comment(text: "some comment", commenter: user).save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.saveProjectCommentReply = { UserAccount user2, Long commentId, String commentStr ->
            true
        }

        controller.projectService = projectService

        when:
            params.id = comment.id
            params.reply = "a reply"
            controller.saveCommentReply()

        then:
            response.json.success

    }

    void "test saveProjectBasicChanges"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.saveProjectBasicChanges = { Long projectId, String name, String description,  List<Long> tags,
                                                             Long licenseId, boolean shared ->
            true
        }

        controller.projectService = projectService


        when:
            params.id = project.id
            controller.saveProjectBasicChanges()

        then:
            response.json.success

    }

    void "test saveProjectComment"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.saveProjectComment = { UserAccount user2, Long projectId,  String comment ->
            true
        }

        controller.projectService = projectService


        when:
            params.id = project.id
            params.comment = "some comment"
            controller.saveProjectComment()

        then:
            response.json.success
    }

    void "test myProjects"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.getMyProjects = { UserAccount userAccount, int max, int offset ->
            [project]
        }

        controller.projectService = projectService

        when:
            def returnVal = controller.myProjects()

        then:
            returnVal.projects.contains(project)
            !returnVal.projects.contains(project2)
    }

    void "test publicProjectList"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.getPublicProjects = { int offset, int max ->
            [project]
        }

        controller.projectService = projectService

        when:
        def returnVal = controller.publicProjectsList()

        then:
        returnVal.projects.contains(project)
        !returnVal.projects.contains(project2)
    }

    void "test deleteProject"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.deleteProject = { UserAccount userAccount, Long projectId  ->
            if (userAccount.equals(user) && Project.findById(projectId)?.projectOwner.equals(userAccount)) return true
            return false
        }

        controller.projectService = projectService

        when:
            params.id = project.id
            controller.deleteProject()

        then:
            response.json.success
            response.reset()

        when:
            params.id = project2.id
            controller.deleteProject()

        then:
            !response.json.success
            response.json.messages.danger
    }

    void "test addTeamToProject"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.addTeamToProject = { UserAccount userAccount, Long projectId,
                String teamName, List<Long> userIds ->
            return true
        }

        projectService.metaClass.addTeamToProject = { Long projectId, Long teamId ->
            return true
        }

        controller.projectService = projectService

        when:
            params.name         = "The Avengers"
            params.id           = project.id
            params."members[]"  = [user2.id]
            controller.addTeamToProject()

        then:
            response.json.success

        when:
            response.reset()
            params.teamId = 2L
            controller.addTeamToProject()

        then:
            response.json.success
    }

    void "test getTeams"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.addTeamToProject = { UserAccount userAccount, Long projectId,
                                                      String teamName, List<Long> userIds ->
            return true
        }

        controller.projectService = projectService

        views["/project/_teams.gsp"] = "addTeamToolbar"

        when:
            params.name         = "The Avengers"
            params.id           = project.id
            params."members[]"  = [user2.id]
            controller.addTeamToProject()

        then:
            response.json.success
            response.reset()

        when:
            params.id = project.id
            def result = controller.getTeams()

        then:
            response.text == "addTeamToolbar"
    }

    void "test newProject"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService

        views["/project/_newProjectWizard.gsp"] = "newProjectWizard Dialog"

        when:
            controller.newProject()

        then:
            response.text == "newProjectWizard Dialog"
    }


    void "test SaveProject"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService

        when:
            projectService.metaClass.saveNewProject = { UserAccount userAccount, Project project, ArrayList annotations,
                                                        Long licenseId, String teamName, ArrayList teamMembers, Long teamId,
                                                        Map dataUpload, Map codeUpload ->
                return true
            }
            controller.projectService = projectService
            controller.saveProject()

        then:
            response.json.success

        when:
            response.reset()
            projectService.metaClass.saveNewProject = { UserAccount userAccount, Project project, ArrayList annotations,
                                                        Long licenseId, String teamName, ArrayList teamMembers, Long teamId,  Map dataUpload, Map codeUpload ->
                return false
            }
            controller.projectService = projectService
            controller.saveProject()

        then:
            response.json.messages.danger
            !response.json.success
    }

    void "test renderNewUploadScreen"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        when:
            params.projectId = project.id
            params.type = "data"
            controller.renderNewUploadScreen()

        then:
            response.text.contains(params.projectId + "_upload_" + params.type)
    }

    void "test addBundleToProject"() {
        projectService.metaClass.addBundleToProject = { Long projectId, FileUploadType type, String externalFileLink, Part filePart,
            String filename, String description ->
            return false
        }
        controller.projectService = projectService

        when:
            params.urlInput         = "http://myurl.edu/folder/subfolder"
            params.uploadDescription="some description"
            params.projectId        = 1L
            params.type             = "data"
            controller.addBundleToProject()

        then:
            !response.json.success

        when:
            projectService.metaClass.addBundleToProject = { Long projectId, FileUploadType type, String externalFileLink, Part filePart,
                                                            String filename, String description ->
                return true
            }
            controller.projectService = projectService
            response.reset()
            params.urlInput         = "http://myurl.edu/folder/subfolder"
            params.uploadDescription="some description"
            params.projectId        = 1L
            params.type             = "data"
            controller.addBundleToProject()

        then:
            response.json.success

    }

    void "test removeTeam"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Team team = new Team(name: "Team1", administrator: user).save()

        projectService.metaClass.addTeamToProject = { Long projectId, Long teamId ->
            project.addToTeams(team)
            project.save()
            return true
        }

        projectService.metaClass.removeTeam = { UserAccount userAccount, Long teamId, Long projectId ->
            project.removeFromTeams(team)
            return true
        }

        controller.projectService = projectService

        when:
            params.id = project.id
            params.teamId = team.id
            controller.addTeamToProject()

        then:
            response.json.success
            project.teams.contains(team)

        when:
            response.reset()
            params.projectId = project.id
            params.teamId = team.id
            controller.removeTeam()

        then:
            response.json.success
            !project.teams.contains(team)
    }

    void "test removeBundleFromProject"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Code code

        projectService.metaClass.removeBundleFromProject = { UserAccount userAccount, Long projectId, Long bundleId,
                                                             FileUploadType type ->
            return true
        }

        controller.projectService = projectService

        when:
            code = new Code(name: "Some name", description: "Some description", repository: "repo", project: project).save()
            params.projectId = project.id
            params.bundleId = code.id
            params.type = "code"
            controller.removeBundleFromProject()

        then:
            response.json.success

    }

    void "test getBundles"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        when:
            params.projectId = project.id
            params.bundleType = "code"
            controller.getBundles()

        then:
            response.status == 200

        when:
            params.projectId = project.id
            params.bundleType = "data"
            controller.getBundles()

        then:
            response.status == 200
    }

    void "test getFilteredProjects"() {

        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService

        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description")

        projectService.metaClass.retrieveFilteredProjectsFromDB = { UserAccount userAccount, boolean filterOnSharedOnly, String filterText, int offset, int max ->
            return [project]
        }

        projectService.metaClass.countFilteredProjectsPages = { UserAccount userAccount, boolean filterOnShared, String filterText, int max ->
            return 1
        }

        projectService.metaClass.renderTableRows = { Map model ->
            return "<p>some data</p>"
        }

        controller.projectService = projectService

        when:
            params.myProjects = true
            params.max  = 5
            params.offset = 0
            controller.getFilteredProjects()

        then:
            response.json.html == "<p>some data</p>"
            response.json.pagesCount == 1
    }

    void "test downloadFile"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        cloudService.metaClass.downloadFile = { Long projectId, String fileName, String gitCommitHash ->
            if (projectId == -1L) return null
            else "this is a sample".bytes
        }

        controller.cloudService = cloudService

        when:
            response.reset()
            params.id = project.id
            params.name = "some name"
            params.hash = "some hash"
            controller.downloadFile()

        then:
            response.text == "this is a sample"
    }
}