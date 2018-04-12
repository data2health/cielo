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

        projectService.metaClass.saveProjectBasicChanges = { Long projectId, String name, String description,  List<Long> tags ->
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
}