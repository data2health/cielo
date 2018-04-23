package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.*

class ProjectControllerSpec extends Specification implements ControllerUnitTest<ProjectController>, DomainUnitTest<Project> {

    ProjectService projectService
    SpringSecurityService springSecurityService

    void setup() {
        projectService = new ProjectService()
        springSecurityService = new SpringSecurityService()
        mockDomains(Profile, UserAccount, UserAccountUserRole)
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
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        user = new UserAccount(username: "someuser", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        projectService.metaClass.incrementViewsCounter = { Project project2 ->
            null
        }

        controller.projectService = projectService

        given:
            params.id = project.id

        when:
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

        controller.projectService = projectService

        when:
            params.name         = "The Avengers"
            params.id           = project.id
            params."members[]"  = [user2.id]
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

}