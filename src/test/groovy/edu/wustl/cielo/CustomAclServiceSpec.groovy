package edu.wustl.cielo

import edu.wustl.cielo.enums.UserRolesEnum
import grails.plugin.springsecurity.acl.AclEntry
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclSid
import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclObjectIdentity
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.AclCache
import spock.lang.Specification

class CustomAclServiceSpec extends Specification implements ServiceUnitTest<CustomAclService>, DomainUnitTest<AclSid> {

    def assetResourceLocator
    AclService aclService
    AclCache aclCache
    UserAccountService userAccountService
    final static String assetsRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/grails-app/assets"

    def setup() {
        mockDomains(UserAccount, UserAccountUserRole, UserRole, SoftwareLicense, Project, AclClass, AclObjectIdentity,
                AclEntry, Profile, ProfilePic, Institution, RegistrationCode)
        userAccountService = new UserAccountService()

        assetResourceLocator = [findAssetForURI: { String URI ->
            new ByteArrayResource(new File(assetsRoot + "/images/${URI}").bytes)
        }]

        userAccountService.assetResourceLocator = assetResourceLocator

        aclService = new AclService()
        aclCache = Mock()
        aclService.aclCache = aclCache
        service.aclService = aclService
    }

    void "test bootstrapAcls"() {
        UserAccount userAccount

        when:
            int size = AclSid.all.size()

        then:
            size == 0

        when:
            service.bootstrapAcls()
            size = AclSid.all.size()

        then:
            size == 0

        when:
            userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
            service.bootstrapAcls()
            size = AclSid.all.size()

        then:
            size == 1
            AclSid.all.first().sid == userAccount.username
    }
    
    void "test getAclEntriesForObjectIdentity"(){
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount.username)
        List<AclEntry> aclEntries
        AclObjectIdentity objectIdentity

        when:
            objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)
            aclEntries = service.getAclEntriesForObjectIdentity(objectIdentity)

        then:
            aclEntries.size() == 2
            aclEntries.collect { it.mask }.minus( [1, 16] ).size() == 0
    }

    void "test getBasePermissionForMask"() {
        BasePermission basePermission

        when:
            basePermission = service.getBasePermissionForMask(1)

        then:
            basePermission == BasePermission.READ

        when:
            basePermission = service.getBasePermissionForMask(2)

        then:
            basePermission == BasePermission.WRITE

        when:
            basePermission = service.getBasePermissionForMask(4)

        then:
            basePermission == BasePermission.CREATE

        when:
            basePermission = service.getBasePermissionForMask(8)

        then:
            basePermission == BasePermission.DELETE

        when:
            basePermission = service.getBasePermissionForMask(16)

        then:
            basePermission == BasePermission.ADMINISTRATION
    }

    void "test getBasePermissionName"() {
        String basePermissionName

        when:
        basePermissionName = service.getBasePermissionName(1)

        then:
            basePermissionName == "Read"

        when:
            basePermissionName = service.getBasePermissionName(2)

        then:
            basePermissionName == "Write"

        when:
            basePermissionName = service.getBasePermissionName(4)

        then:
            basePermissionName == "Create"

        when:
            basePermissionName = service.getBasePermissionName(8)

        then:
            basePermissionName == "Delete"

        when:
            basePermissionName = service.getBasePermissionName(16)

        then:
            basePermissionName == "Administration"
    }

    void "test getOrCreateObjectIdentity"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount.username)
        AclObjectIdentity objectIdentity

        given:
            !objectIdentity

        when:
            objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)

        then:
            objectIdentity
    }

    void "test grantPermission"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount.username)
        AclObjectIdentity objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)
        List<BasePermission> permissions = [BasePermission.DELETE]

        when:
            boolean hasPermission = service.hasPermission(userAccount.username, project.id, permissions)

        then:
            !hasPermission

        when:
            service.grantPermission(objectIdentity, aclSid, BasePermission.DELETE)
            hasPermission = service.hasPermission(userAccount.username, project.id, permissions)

        then:
            hasPermission
    }

    void "test hasPermission"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount.username)
        AclObjectIdentity objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)
        List<BasePermission> permissions = [BasePermission.DELETE]

        when:
            boolean hasPermission = service.hasPermission(userAccount.username, project.id, permissions)

        then:
            !hasPermission

        when:
            service.grantPermission(objectIdentity, aclSid, BasePermission.DELETE)
            hasPermission = service.hasPermission(userAccount.username, project.id, permissions)

        then:
            hasPermission
    }

    void "test patchPermissionsOnSave"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount userAccount2 = new UserAccount(username: "someuser2", password: "somePassword").save()

        UserRole userRole = new UserRole(authority: UserRolesEnum.ROLE_USER.toString()).save()
        UserAccountUserRole userAccountUserRole    = new UserAccountUserRole()
        userAccountUserRole.userRole               = userRole
        userAccountUserRole.userAccount            = userAccount
        userAccountUserRole.save()

        UserAccountUserRole userAccountUserRole2    = new UserAccountUserRole()
        userAccountUserRole2.userRole               = userRole
        userAccountUserRole2.userAccount            = userAccount2
        userAccountUserRole2.save()

        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount.username)
        AclObjectIdentity objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)
        List<BasePermission> permissions = [BasePermission.READ]

        when:
            boolean hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            hasPermission

        when:
            hasPermission = false
            project.shared = false
            project.save()
            service.patchPermissionsOnSave(project)
            hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            thrown AccessDeniedException
            !hasPermission
    }

    void "test removeAllAccess"() {
        UserAccount userAccount = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(userAccount)
        UserAccount userAccount2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: false).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount2.username)
        AclObjectIdentity objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)
        List<BasePermission> permissions = [BasePermission.WRITE]

        when:
            boolean hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            thrown AccessDeniedException
            !hasPermission

        when:
            service.grantPermission(objectIdentity, aclSid, BasePermission.WRITE)
            hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            hasPermission

        when:
            hasPermission = false
            service.removeAllAccess(objectIdentity)
            hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            thrown AccessDeniedException
            !hasPermission
    }


    void "test removePermission"() {
        UserAccount userAccount = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(userAccount)
        UserAccount userAccount2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: false).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount2.username)
        AclObjectIdentity objectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)
        List<BasePermission> permissions = [BasePermission.WRITE]

        when:
            boolean hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            thrown AccessDeniedException
            !hasPermission

        when:
            service.grantPermission(objectIdentity, aclSid, BasePermission.WRITE)
            hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            hasPermission

        when:
            hasPermission = false
            service.removePermission(objectIdentity, aclSid, BasePermission.WRITE)
            hasPermission = service.hasPermission(userAccount2.username, project.id, permissions)

        then:
            thrown AccessDeniedException
            !hasPermission
    }

    void "test setupBasePermissionsForProject"() {
        UserAccount userAccount = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(userAccount)
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: false).save()
        new AclClass(className: Project.name).save()
        List<BasePermission> permissions = [BasePermission.ADMINISTRATION]

        when:
        boolean hasPermission = service.hasPermission(userAccount.username, project.id, permissions)

        then:
            thrown AccessDeniedException
            !hasPermission

        when:
            service.setupBasePermissionsForProject(project)
            hasPermission = service.hasPermission(userAccount.username, project.id, permissions)

        then:
            hasPermission
    }

    void "test getNextAvailableAceOrder"() {
        UserAccount userAccount = userAccountService.bootstrapCreateOrGetAdminAccount()
        userAccountService.bootstrapUserRoles()
        userAccountService.bootstrapAddSuperUserRoleToUser(userAccount)
        service.bootstrapAcls()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description", shared: false).save()
        new AclClass(className: Project.name).save()
        service.setupBasePermissionsForProject(project)
        AclSid aclSid = AclSid.findBySid(userAccount.username)
        AclObjectIdentity aclObjectIdentity = service.getOrCreateObjectIdentity(project.id, Project.name, aclSid)

        when:
            int aceOrder = service.getNextAvailableAceOrder(aclObjectIdentity)

        then:
            aceOrder == 2
    }
}
