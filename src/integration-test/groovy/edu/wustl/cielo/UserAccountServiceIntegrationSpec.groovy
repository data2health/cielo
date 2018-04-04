package edu.wustl.cielo

import edu.wustl.cielo.enums.AccountStatusEnum
import edu.wustl.cielo.enums.UserRolesEnum
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class UserAccountServiceIntegrationSpec extends Specification {

    @Autowired
    UserAccountService userAccountService

    @Autowired
    SessionFactory sessionFactory

    def webRoot
    def assetResourceLocator

    def setup() {
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        userAccountService.assetResourceLocator = assetResourceLocator
    }

    def cleanup() {

        UserAccount.list().each {
            SoftwareLicense.findAllByCreator(it).each { it.delete() }
            Profile.findByUser(it).delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.projects.each { it.delete() }
            it.delete()
        }

        Annotation.list().each {
            it.delete()
        }

        Institution.list().each {
            it.delete()
        }
    }

    void "test handleOnAuthSuccess"() {
        given: "No users"
            UserAccount.list() == []

        when: "calling to handle auth has no effect"
            userAccountService.springSecurityService = [principal: null]
            userAccountService.handleOnAuthSuccess("")

        then: "nothing happens"
            true == true

        when: "adding a user and calling handle auth"
            UserAccount userAccount = new UserAccount(username: "someuser", password: "somepass").save()
            userAccountService.springSecurityService = [principal: userAccount]
            userAccountService.handleOnAuthSuccess("someuser")
            sessionFactory.getCurrentSession().flush()

        then: "check the user object"
            userAccount.status != AccountStatusEnum.ACCOUNT_ACTIVE

        when: "user account has been verified"
            userAccount.status = AccountStatusEnum.ACCOUNT_VERIFIED
            userAccountService.handleOnAuthSuccess("someuser")
            sessionFactory.getCurrentSession().flush()

        then: "then user can be set to active"
            userAccount.status == AccountStatusEnum.ACCOUNT_ACTIVE
    }

    void "test handleBadCredentials"() {
        given: "No users"
            UserAccount.list() == []

        when: "calling handle bad credentials"
            userAccountService.handleBadCredentials("")

        then: "nothing happens"
            true == true

        when: "adding a user then calling it"
            UserAccount userAccount = new UserAccount(username: "someuser", password: "somepass").save(flush: true)
            userAccount.failedAttempts == 0
            userAccountService.handleBadCredentials(userAccount.username)
            sessionFactory.getCurrentSession().flush()

        then: "the user account will have a failed login"
            userAccount.failedAttempts == 1

        when: "try it again"
            userAccountService.handleBadCredentials(userAccount.username)
            sessionFactory.getCurrentSession().flush()

        then: "one more failed attempt"
            userAccount.failedAttempts == 2
    }


    void "test findUser"() {
        UserAccount userAccount
        UserAccount foundUser

        when: "trying to find a user before saved"
            foundUser = userAccountService.findUser("someuser")

        then: "the user account is not found"
            foundUser == null

        when: "adding a user"
            userAccount = new UserAccount(username: "someuser", password: "somepass").save(flush: true)
            foundUser = userAccountService.findUser("someuser")

        then: "found user is the one just created"
            foundUser == userAccount
    }

    void "test markUserFailedLogin"() {
        UserAccount userAccount

        when: "marking a null user"
            userAccountService.markUserFailedLogin(null)

        then: "Nothing happens"
            true == true

        when: "creating a user and marking failed login"
            userAccount = new UserAccount(username: "someuser", password: "somepass").save(flush: true)
            userAccount.failedAttempts == 0
            userAccountService.markUserFailedLogin(userAccount)
            sessionFactory.getCurrentSession().flush()

        then: "someuser has a failed login"
            userAccount.failedAttempts == 1
    }

    void "test bootstrapUserRoles"() {
        given: "no Roles"
            UserRole.list() == []

        when: "bootstrapping roles"
            userAccountService.bootstrapUserRoles()
            sessionFactory.getCurrentSession().flush()

        then: "some roles are created"
            UserRole.list() != []
            UserRole.count() > 1

    }

    void "test bootstrapCreateOrGetAdminAccount and bootstrapAddSuperUserRoleToUser in one test"() {
        UserAccount admin

        given: "no users"
            UserAccount.list() == []

        when: "bootstrapping admin user"
            admin = userAccountService.bootstrapCreateOrGetAdminAccount()
            sessionFactory.getCurrentSession().flush()

        then: "user is created but no roles associated yet so not an admin"
            UserAccount.list() != [] && UserAccount.count() == 1
            !admin.getAuthorities().contains(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))

        when: "associating admin role"
            userAccountService.bootstrapUserRoles()
            sessionFactory.getCurrentSession().flush()
            userAccountService.bootstrapAddSuperUserRoleToUser(admin)
            sessionFactory.getCurrentSession().flush()

        then: "now an admin"
            UserAccount.list() != [] && UserAccount.count() == 1
            admin.getAuthorities().contains(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))
    }

    void "test setupMockAppUsers"() {

        given: "no users"
            UserAccount.list() == []

        when: "setting up mock users"
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            AnnotationService annotationService = new AnnotationService()
            if (Annotation.count() == 0) {
                annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
            }
            if (UserAccountUserRole.count() == 0) {
                userAccountService.bootstrapUserRoles()
            }
            UserAccount admin = userAccountService.bootstrapCreateOrGetAdminAccount()
            userAccountService.bootstrapAddSuperUserRoleToUser(admin)
            userAccountService.setupMockAppUsers(2, 0)

        then:
            UserAccount.count() == 3
            UserAccount.list().each { user -> user.profile.annotations == [] }

        when: "remove all the users"
            UserAccount.list().each {
                SoftwareLicense.findAllByCreator(it).each { it.delete() }
                Profile.findByUser(it).delete()
                RegistrationCode.findByUserAccount(it)?.delete()
                UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
                it.delete(flush: true)
            }

        then: "removed all the user including the admin"
            UserAccount.list() == []

        when: "setting up 4 mock users"
            userAccountService.setupMockAppUsers(4, 3)
            sessionFactory.getCurrentSession().flush()

        then:
            UserAccount.count() == 4
            UserAccount.list().eachWithIndex { user, index ->
                assert user.profile.annotations.size() == 3
            }
    }

    void "test bootstrapFollowers"() {
        given: "no users"
            UserAccount.list() == []

        when: "setting up mock users"
            InstitutionService institutionService = new InstitutionService()
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            AnnotationService annotationService = new AnnotationService()
            if (Annotation.count() <= 2) {
                annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
            }
            if (UserAccountUserRole.count() == 0) {
                userAccountService.bootstrapUserRoles()
            }
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            userAccountService.setupMockAppUsers(2, 0)
            sessionFactory.getCurrentSession().flush()

        then:
            UserAccount.count() == 3 //2 + 1 admin
            UserAccount.list().each { user -> user.profile.annotations == [] }

        when: "adding a few more with annotations"
            UserAccount.list().each {
                Profile.findByUser(it).delete()
                RegistrationCode.findByUserAccount(it)?.delete()
                UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
                it.delete(flush: true)
            }
            userAccountService.setupMockAppUsers(4, 3)
            sessionFactory.getCurrentSession().flush()

        then:
            UserAccount.count() == 4
            UserAccount.list().eachWithIndex { user, index ->
                assert user.profile.annotations.size() == 3
                assert user.connections.size() == 0
            }

        when: "adding followers to the users"
            userAccountService.bootstrapFollowers(2)
            sessionFactory.getCurrentSession().flush()

        then: "each user should have two followers"
        UserAccount.list().eachWithIndex { user, index ->
            assert user.profile.annotations.size() == 3
            assert user.connections.size() == 2
        }
    }
}
