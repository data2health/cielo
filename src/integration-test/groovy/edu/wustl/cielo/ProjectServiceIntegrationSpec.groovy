package edu.wustl.cielo

import edu.wustl.cielo.enums.ProjectStatusEnum
import grails.plugin.springsecurity.acl.AclSid
import org.grails.plugin.cache.GrailsCacheManager
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class ProjectServiceIntegrationSpec extends Specification {

    @Autowired
    ProjectService projectService

    @Autowired
    GrailsCacheManager grailsCacheManager

    TeamService teamService

    @Autowired
    SessionFactory sessionFactory

    def webRoot
    def assetResourceLocator

    def setup() {
        teamService = new TeamService()
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        cleanup()
    }

    def cleanup() {
        SoftwareLicense.list().each {
            it.delete()
        }

        Project.list().each {
            it.delete()
        }

        Annotation.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Profile.findByUser(it).delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }

        Institution.list().each {
            it.delete()
        }
    }

    void "test bootstrapProjects"() {
        expect:"no projects"
            Project.list() == []

        when: "calling service to generate 0 projects"
            projectService.bootstrapProjects(0)

        then: "still no projects"
            Project.list() == []

        when: "calling service to generate 5 projects"
            projectService.bootstrapProjects(5)

        then: "still no projects because there are no user to associate with"
            Project.list() == []

        when: "boostraping required props"
            //first setup institutions
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            if (Annotation.count() == 0) {
                AnnotationService annotationService = new AnnotationService()
                annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
            }
            UserAccountService userAccountService = new UserAccountService()
            userAccountService.assetResourceLocator = assetResourceLocator
            if (UserAccountUserRole.count() == 0) userAccountService.bootstrapUserRoles()
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            userAccountService.setupMockAppUsers(2, 1)
            SoftwareLicenseService softwareLicenseService = new SoftwareLicenseService()
            softwareLicenseService.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))
            sessionFactory.getCurrentSession().flush()
            projectService.bootstrapProjects(2)
            sessionFactory.getCurrentSession().flush()

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
            projectService.bootstrapCodesToProject(null, "")
            sessionFactory.getCurrentSession().flush()

        then: "No codes are saved"
            Code.list() == []

        when: "calling with valid project"
            user = new UserAccount(username: "admin", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save(flush: true)
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save(flush: true)
            projectService.bootstrapCodesToProject(project, "")
            sessionFactory.getCurrentSession().flush()

        then: "save is successful, second param can be empty string"
            Code.list()
    }

    void "test bootstrapAnnotationsForProject"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        expect: "no annotations saved"
            Annotation.list() == []

        when: "creating new code with no props"
            projectService.bootstrapAnnotationsForProject(null, null)
            sessionFactory.getCurrentSession().flush()

        then: "No annotations are saved"
            Annotation.list() == []

        when: "calling with valid project"
            user = new UserAccount(username: "admin", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save(flush: true)
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save(flush: true)
            new Annotation(label: "some label").save(flush: true)
            projectService.bootstrapAnnotationsForProject(project, Annotation.list())
            sessionFactory.getCurrentSession().flush()

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
            projectService.bootstrapDatasForProject(null, "")
            sessionFactory.getCurrentSession().flush()

        then: "No codes are saved"
            Data.list() == []

        when: "calling with valid project"
            user = new UserAccount(username: "someuser", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save(flush: true)
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save(flush: true)
            projectService.bootstrapDatasForProject(project, "")
            sessionFactory.getCurrentSession().flush()

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
            projectService.bootstrapPublicationsForProject(null, null)

        then: "should fail"
            Publication.list() == []

        when: "Calling with the correct information"
            user = new UserAccount(username: "someuser", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save(flush: true)
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save(flush: true)
            projectService.bootstrapPublicationsForProject(project, null)
            sessionFactory.getCurrentSession().flush()

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
            projectService.boostrapCommentsForProject(null)

        then: "should fail"
            Comment.list() == []

        when: "Calling with the correct information"
            user = new UserAccount(username: "someuser", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save(flush: true)
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save(flush: true)
            projectService.boostrapCommentsForProject(project)
            sessionFactory.getCurrentSession().flush()

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
            user = new UserAccount(username: "someuser", password: "somePassword").save(flush: true)
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save(flush: true)
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save(flush: true)
            sessionFactory.getCurrentSession().flush()

        then: "saves correctly"
            Project.list()

        when: "changing something on the project"
            project.status = ProjectStatusEnum.COMPLETED
            projectService.saveProjectAndLog(project)
            sessionFactory.getCurrentSession().flush()

        then: "project saves correctly and the change is reflected"
            project.status == ProjectStatusEnum.COMPLETED
    }

    void "test getMostViewedProjects"() {
        expect:"no projects"
            Project.list() == []

        when: "calling service to generate 0 projects"
            projectService.bootstrapProjects(0)

        then: "still no projects"
            Project.list() == []

        when: "calling service to generate 5 projects"
            projectService.bootstrapProjects(2)
            sessionFactory.getCurrentSession().flush()

        then: "still no projects becuase there are no user to associate with"
            Project.list() == []

        when: "boostraping required props"
            //first setup institutions
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            AnnotationService annotationService = new AnnotationService()
            if (Annotation.count() == 0) {
                annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
            }
            UserAccountService userAccountService = new UserAccountService()
            userAccountService.assetResourceLocator = assetResourceLocator
            if (UserAccountUserRole.count() == 0) {
                userAccountService.bootstrapUserRoles()
            }
            sessionFactory.getCurrentSession().flush()
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            userAccountService.setupMockAppUsers(2, 1)
            sessionFactory.getCurrentSession().flush()
            SoftwareLicenseService softwareLicenseService = new SoftwareLicenseService()
            softwareLicenseService.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))
            sessionFactory.getCurrentSession().flush()
            projectService.bootstrapProjects(2)
            sessionFactory.getCurrentSession().flush()

        then: "save is successfull"
            Project.list()

        when: "getting the 2 most popular projects"
            List<Project> mostPopular = projectService.getMostViewedProjects(2, false)

        then: "the top two returned should match the top two projects in list"
            mostPopular.collect { it.projectId } == Project.executeQuery("select distinct p from Project p order by views desc, dateCreated desc",[max: 2]).collect { it.id }
    }

    void "test retrieveFilteredProjectsFromDB"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        List<Project> projects

        when:
            projects = projectService.retrieveFilteredProjectsFromDB(user, false, "", 0, 5)

        then:
            projects
            projects.size() == 1

        when:
            projects.clear()
            projects = projectService.retrieveFilteredProjectsFromDB(user, false, "Project2", 0, 5)

        then:
            projects.size() == 0
    }

    void "test countFilteredProjectsPages"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        int countedProjectPages

        when:
            countedProjectPages = projectService.countFilteredProjectsPages(user, false, "", 5)

        then:
            countedProjectPages == 1

        when:
            countedProjectPages = projectService.countFilteredProjectsPages(user, false, "Jupiter", 5)

        then:
            countedProjectPages == 1 //returns 1 even if no results

        when:
            new Project(projectOwner: user, name: "Second", license: softwareLicense,
                    description: "something").save()
            countedProjectPages = projectService.countFilteredProjectsPages(user, false, "", 1)

        then:
            countedProjectPages == 2
    }

    void "test getListOfProjectsTeamContributesTo"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        teamService.metaClass.getIdsOfProjectsForTeam = { Long teamId ->
            if (project.teams.collect{ it.id }.contains(teamId)) return [project.id]
        }

        projectService.teamService = teamService

        when:
            def results = projectService.getListOfProjectsTeamContributesTo(team)

        then:
            !results

        when:
            project.teams.add(team)
            project.save()
            results = projectService.getListOfProjectsTeamContributesTo(team)

        then:
            results

    }

    void "test getProjectsUserContributesTo"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        teamService.metaClass.getIdsOfProjectsForTeam = { Long teamId ->
            if (project.teams.collect{ it.id }.contains(teamId)) return [project.id]
        }

        teamService.metaClass.getListOfTeamsUserIsMemberOf = {UserAccount userAccount ->
            []
        }

        teamService.metaClass.getListOfTeamsUserOwns = {UserAccount userAccount1 ->
            [team]
        }

        projectService.teamService = teamService

        when:
            def results = projectService.getProjectsUserContributesTo(user2)

        then:
            !results

        when:
            team.members.add(user2)
            team.save()
            project.teams.add(team)
            results = projectService.getProjectsUserContributesTo(user2)

        then:
            results
    }

    void "test countFilteredProjects"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        int countedProjects

        when:
            countedProjects = projectService.countFilteredProjects(false, "")

        then:
            countedProjects == 1

        when:
            countedProjects = projectService.countFilteredProjects(true, "")

        then:
            countedProjects == 0

        when:
            new Project(projectOwner: user, name: "Second", license: softwareLicense,
                    description: "something").save()
            grailsCacheManager.getCache("filtered_projects_count").clear()
            countedProjects = projectService.countFilteredProjects(true, "")

        then:
            countedProjects == 0

        when:
            countedProjects = projectService.countFilteredProjects(false, "" )

        then:
            countedProjects == 2
    }

    void "test createNewProject"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        AclSid aclSid       = new AclSid()
        aclSid.principal    = false
        aclSid.sid          = user.username
        aclSid.save(flush: true)

        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()

        when:
            List<Project> projects = Project.all

        then:
            projects.size() == 0

        when:
            Long projectId = -1L
            projectId = projectService.createNewProject("My Project", "My description", softwareLicense.id, "", user.username)

        then:
            projectId != -1L
    }
}
