package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import org.springframework.core.io.ByteArrayResource
import spock.lang.Specification

class TeamServiceSpec extends Specification implements ServiceUnitTest<TeamService>, DomainUnitTest<UserAccount> {

    def webRoot
    def assetsRoot
    def assetResourceLocator
    UserAccountService userAccountService
    UtilService utilService
    AnnotationService annotationService

    void setup() {
        mockDomains(Institution, Profile, UserRole, UserAccountUserRole, RegistrationCode)
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        assetsRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/grails-app/assets"

        utilService = new UtilService()
        utilService.metaClass.getDateDiff = { Date fromDate, Date toDate = null ->
            "today"
        }
        annotationService = new AnnotationService()

        annotationService.metaClass.saveNewAnnotation = { List<String> names, String code ->
            ; //nothing to be done
        }

        annotationService.utilService = utilService
        userAccountService = new UserAccountService()
        userAccountService.assetResourceLocator = assetResourceLocator
        userAccountService.assetResourceLocator = [findAssetForURI: { String URI ->
            new ByteArrayResource(new File(assetsRoot + "/images/${URI}").bytes)
        }]
    }

    void "test bootstrapTeams"() {
        given: "no teams"
            Team.list() == []

        when: "bootstrapping no teams"
            service.bootstrapTeams(0,0)

        then: "still no teams"
            Team.list() == []

        when: "actually adding a team"
            service.bootstrapTeams(1,0)

        then: "fails because there are no users"
            Team.list() == []

        when: "adding users first, then adding teams and members"
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))

            annotationService.metaClass.saveNewAnnotation = { List<String> names, String code ->
                ; //nothing to be done
            }

            annotationService.initializeAnnotations([new File(webRoot + "WEB-INF/startup/NCI_Thesaurus_terms_shorter.txt")])
            userAccountService.bootstrapUserRoles()
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            userAccountService.setupMockAppUsers(4, 0)
            service.bootstrapTeams(3,2)

        then:
            Team.list() != []
            Team.count() == 15 // number of users: (1 admin + 4 mock users) * (number of teams) = 15
            Team.list().each { team ->
                assert team.members.size() == 2
            }
    }

    void "test getTeamMembers"() {
        InstitutionService institutionService = new InstitutionService()
        institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
        annotationService.initializeAnnotations([new File(webRoot + "WEB-INF/startup/NCI_Thesaurus_terms_shorter.txt")])
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
        userAccountService.setupMockAppUsers(4, 0)
        service.bootstrapTeams(3,2)

        when:
            Team team = Team.list().take(1)[0]
            ArrayList<UserAccount> users = service.getTeamMembers(team.id)

        then:
            users.each { UserAccount userAccount ->
                team.members.contains(userAccount)
            }

    }

    void "test updateTeamMembers"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        when:
            def result = service.updateTeamMembers(user, team.id, [user2.id])

        then:
            result
    }

    void "test deleteTeam"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()

        project.teams.add(team)

        when:
            def result = service.deleteTeam(team.id)

        then:
            result
    }

    void "test listTeams"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        when:
            def results = service.listTeams(0, 5)

        then:
            results.size() == 1

        when:
            Team team1 = new Team(name: "Team2", administrator: user).save()
            results = service.listTeams(0, 5)

        then:
            results.size() == 2
            results.contains(team)
            results.contains(team1)
    }


    void "test getNumberOfTeamPages"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()
        Team team1 = new Team(name: "Team2", administrator: user).save()

        when:
            def results = service.getNumberOfTeamPages(2)

        then:
            results == 1

        when:
            results = service.getNumberOfTeamPages(1)

        then:
            results == 2
    }

    void "test getListOfTeamsUserIsMemberOf"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()
        Team team1 = new Team(name: "Team2", administrator: user).save()

        when:
            def results = service.getListOfTeamsUserIsMemberOf(user2)

        then:
            results.size() == 0

        when:
            team.members.add(user2)
            team.save()
            results = service.getListOfTeamsUserIsMemberOf(user2)

        then:
            results.size() == 1

        when:
            team1.members.add(user2)
            team1.save()
            results = service.getListOfTeamsUserIsMemberOf(user2)

        then:
            results.size() == 2
    }

    void "test getListOfTeamsUserOwns"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()
        Team team1 = new Team(name: "Team2", administrator: user).save()

        when:
            def results = service.getListOfTeamsUserOwns(user2)

        then:
            results.size() == 0

        when:
            results = service.getListOfTeamsUserOwns(user)

        then:
            results.size() == 2
    }
}
