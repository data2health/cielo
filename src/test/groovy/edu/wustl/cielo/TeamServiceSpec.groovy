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

    void setup() {
        mockDomains(Institution, Profile, UserRole, UserAccountUserRole, RegistrationCode)
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        assetsRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/grails-app/assets"

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
            AnnotationService annotationService = new AnnotationService()
            annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
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
}
