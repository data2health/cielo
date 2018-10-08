package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import grails.testing.gorm.DomainUnitTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

class TeamServiceIntegrationSpec extends Specification implements DomainUnitTest<Code> {

    @Autowired
    TeamService teamService

    def webRoot

    void setup() {
        mockDomain(UserAccount)
        teamService = new TeamService()
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
    }

    void cleanup() {
        Team.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }
    }

    void "test bootstrapTeams"() {
        Team.withTransaction {


            given: "no teams"
            Team.list() == []

            when: "bootstrapping no teams"
            teamService.bootstrapTeams(0, 0)

            then: "still no teams"
            Team.list() == []

            when: "actually adding a team"
            teamService.bootstrapTeams(1, 0)

            then: "fails because there are no users"
            Team.list() == []

            when: "adding users first, then adding teams and members"
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            AnnotationService annotationService = new AnnotationService()
            annotationService.initializeAnnotations([new File(webRoot + "WEB-INF/startup/NCI_Thesaurus_terms_shorter.txt")])
            UserAccountService userAccountService = new UserAccountService()
            userAccountService.bootstrapUserRoles()
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            userAccountService.setupMockAppUsers(4, 0)
            teamService.bootstrapTeams(3, 2)

            then:
            Team.list() != []
            Team.count() == 15 // number of users: (1 admin + 4 mock users) * (number of teams) = 15
            Team.list().each { team ->
                assert team.members.size() == 2
            }
        }
    }
}
