package edu.wustl.cielo

import edu.wustl.cielo.enums.UserRolesEnum
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclEntry
import grails.plugin.springsecurity.acl.AclObjectIdentity
import grails.plugin.springsecurity.acl.AclSid
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.validation.ObjectError
import grails.plugin.springsecurity.acl.AclService

@Transactional
class CustomAclService {

    static AclService aclService

    /**
     * Setup acls for existing objects
     */
    void bootstrapAcls() {
        //add the userRole to the DB
        UserRole.all.each { UserRole role ->
            if (!AclSid.findBySid(role.authority)) {
                AclSid aclSid       = new AclSid()
                aclSid.principal    = false
                aclSid.sid          = role.authority
                aclSid.save(flush: true)
            }
        }

        UserAccount.all.each { UserAccount user ->
            AclSid aclSid       = new AclSid()
            aclSid.principal    = true
            aclSid.sid          = user.username
            aclSid.save(flush: true)
        }

        //add acl class if not exists
        String projectClassName = Project.class.name
        if (!AclClass.findByClassName(projectClassName)) {
            AclClass aclClass   = new AclClass()
            aclClass.className  = projectClassName
            aclClass.save(flush: true)
        }

        //For all projects that exits already, check whether we need to create an object id or not
        Project.all.each { Project project ->
            setupBasePermissionsForProject(project)
        }
    }

    /**
     * Setup base permissions for given project. Owner has Admin ADMINISTRATION, users in teams have WRITE permission
     *
     * @param project the project we want to setup permissions on
     */
    void setupBasePermissionsForProject(Project project) {
        String projectClassName = Project.class.name
        AclSid aclSid           = AclSid.findBySid(project.projectOwner.username)
        AclObjectIdentity aclObjectIdentity = getOrCreateObjectIdentity(project.id, projectClassName, aclSid)

        //now set the permisssion on the projects
        grantPermission(aclObjectIdentity, aclSid, BasePermission.ADMINISTRATION)

        //if the project is shared then everyone should be allowed to view it
        if (project.shared) {
            UserAccount.all.each { UserAccount userAccount ->
                AclSid userAcl = AclSid.findBySid(userAccount.username)
                grantPermission(aclObjectIdentity, userAcl, BasePermission.READ)
            }
        } else {
            //admin should have read access
            UserAccount userAccount  = UserAccountUserRole.findByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))?.userAccount
            AclSid userAcl = AclSid.findBySid(userAccount.username)
            grantPermission(aclObjectIdentity, userAcl, BasePermission.READ)
        }

        //now same for each member of teams
        project.teams.each { Team team ->
            team.members.each { UserAccount userAccount ->
                AclSid userAcl = AclSid.findBySid(userAccount.username)
                grantPermission(aclObjectIdentity, userAcl, BasePermission.READ)
                grantPermission(aclObjectIdentity, userAcl, BasePermission.WRITE)
            }
        }
    }

    /**
     * Grant a given permission to an object
     *
     * @param aclObjectIdentity the objectIdentity to create the permission for
     * @param aclSid the id of the entity the permission is for
     * @param permission the level of access being requested
     */
    static boolean grantPermission(AclObjectIdentity aclObjectIdentity, AclSid aclSid, Permission permission) {
        int aceOrder = getNextAvailableAceOrder(aclObjectIdentity)

        AclEntry aclEntry           = new AclEntry()
        aclEntry.aclObjectIdentity  = aclObjectIdentity
        aclEntry.sid                = aclSid
        aclEntry.granting           = true
        aclEntry.mask               = permission.getMask()
        aclEntry.aceOrder           = aceOrder

        if (!aclEntry.save(flush: true)) {
            aclEntry.errors.allErrors.each { ObjectError error ->
                log.error(error.toString())
            }
            return false
        }
        clearAclCache()
        return true
    }

    /**
     * Given an object and the granted user, remove the permission
     *
     * @param aclObjectIdentity the object that we are referrencing
     * @param aclSid the identity of the user that has access
     * @param permission the permission in question to remove
     */
    static void removePermission(AclObjectIdentity aclObjectIdentity, AclSid aclSid, Permission permission) {
        AclEntry.findByAclObjectIdentityAndSidAndMask(aclObjectIdentity, aclSid, permission.getMask())?.delete()
        clearAclCache()
    }

    /**
     * Remove all permissions to a given object identity. This is intended for use when deleting a project
     *
     * @param aclObjectIdentity the object we want to remove access to
     */
    static void removeAllAccess(AclObjectIdentity aclObjectIdentity) {
        AclEntry.findAllByAclObjectIdentity(aclObjectIdentity)*.delete()
        clearAclCache()
    }

    /**
     * Either retrieve or create Object identity
     *
     * @param objectId the id of the object
     * @param objectClassName the class of the object
     * @param aclSid the aclSid of the owner
     *
     * @return a new aclObjectIdentity or the existing one from the DB
     */
    static AclObjectIdentity getOrCreateObjectIdentity(Long objectId, String objectClassName, AclSid aclSid) {
        AclClass aclClass  = AclClass.findByClassName(objectClassName)
        AclObjectIdentity aclObjectIdentity

        aclObjectIdentity = AclObjectIdentity.findByAclClassAndObjectId(aclClass, objectId)

        if (!aclObjectIdentity && aclSid) {
            aclObjectIdentity = new AclObjectIdentity()
            aclObjectIdentity.objectId = objectId
            aclObjectIdentity.aclClass = aclClass
            aclObjectIdentity.owner    = aclSid
            aclObjectIdentity.save(flush: true)
        }
        return aclObjectIdentity
    }

    /**
     * Given a mask return the proper BasePermission
     *
     * @param mask the value (int) of mask in question
     *
     * @return an instance of BasePermission
     */
    BasePermission getBasePermissionForMask(int mask) {
        BasePermission basePermission

        switch( mask ) {
            case 1:
                basePermission = BasePermission.READ
                break
            case 2:
                basePermission = BasePermission.WRITE
                break
            case 4:
                basePermission = BasePermission.CREATE
                break
            case 8:
                basePermission = BasePermission.DELETE
                break
            case 16:
                basePermission = BasePermission.ADMINISTRATION
                break
        }
        return basePermission
    }

    /**
     * Return string representation of the mask
     *
     * @param mask the mask to stringify
     *
     * @return a string with the access mask or Unknown
     */
    String getBasePermissionName(int mask) {
        String permissionName

        switch( mask ) {
            case 1:
                permissionName = "Read"
                break
            case 2:
                permissionName = "Write"
                break
            case 4:
                permissionName = "Create"
                break
            case 8:
                permissionName = "Delete"
                break
            case 16:
                permissionName = "Administration"
                break
            default:
                permissionName = "Unknown"
        }
        return permissionName
    }

    /**
     * Clear the aclcache after making changes so they are reflected in session
     */
    static void clearAclCache() {
        aclService.aclCache.clearCache()
    }

    /**
     * Get a list of acl entries for a given object identity
     *
     * @param aclObjectIdentity
     *
     * @return list of entries describing the access to the given object
     */
    List<AclEntry> getAclEntriesForObjectIdentity(AclObjectIdentity aclObjectIdentity) {
        return AclEntry.findAllByAclObjectIdentity(aclObjectIdentity)
    }

    /**
     * Does a user have permissions to access object
     *
     * @param userName
     * @param objectId
     * @param permissions
     *
     * @return true if successful, false otherwise
     *
     * @throws AccessDeniedException if the user is not allowed to view object
     */
    boolean hasPermission(String userName, Long objectId, List<BasePermission> permissions) throws AccessDeniedException {
        boolean successful
        AclObjectIdentity aclObjectIdentity = AclObjectIdentity.findByObjectId(objectId)
        AclSid aclSid                       = AclSid.findBySid(userName)

        List<AclEntry> aclEntries = AclEntry.findAllByAclObjectIdentityAndSid(aclObjectIdentity, aclSid)

        if (aclEntries.size() == 0) {
            throw new AccessDeniedException("You do not have permission to view object")
        }
        else {
            List<Integer> masks             = aclEntries.collect { Integer.valueOf(it.mask) }
            List<Integer> permissionMasks   = permissions.collect { Integer.valueOf(it.mask) }

            successful = ( permissionMasks.minus(masks).size() < permissionMasks.size() )
        }
        return successful
    }

    /**
     * Change persmissions based on the shared property of the project
     *
     * @param project the project to change permissions on
     */
    void patchPermissionsOnSave(Project project) {
        String projectClassName = Project.class.name
        AclSid aclSid           = AclSid.findBySid(project.projectOwner.username)
        AclObjectIdentity aclObjectIdentity = getOrCreateObjectIdentity(project.id, projectClassName, aclSid)

        //if the project is shared then everyone should be allowed to view it
        if (project.shared) {
            UserAccount.all.each { UserAccount userAccount ->
                AclSid userAcl = AclSid.findBySid(userAccount.username)
                grantPermission(aclObjectIdentity, userAcl, BasePermission.READ)
            }
        } else {
            //the project shared property was switched to private
            UserAccount.all.each { UserAccount userAccount ->
                AclSid userAcl = AclSid.findBySid(userAccount.username)
                removePermission(aclObjectIdentity, userAcl, BasePermission.READ)
            }
        }
    }

    /**
     * Get the aceOrder value for the next entry based on the existing ones. This is important due to the fact that
     * the object can have permissions revoked meaning that an ace order that existed would be deleted breaking the
     * linear nature of the values.
     *
     * @param aclObjectIdentity the object identity
     *
     * @return int value representing the next aceOrder
     */
    static int getNextAvailableAceOrder(AclObjectIdentity aclObjectIdentity) {
        int nextAceOrder = -1
        int objectCount = AclEntry.countByAclObjectIdentity(aclObjectIdentity)

        AclEntry.findAllByAclObjectIdentity(aclObjectIdentity).sort { a,b -> a.aceOrder <=> b.aceOrder  }.eachWithIndex { AclEntry obj, int index ->
            if (index != obj.aceOrder) {
                nextAceOrder =  index
                return true
            }
        }

        if (nextAceOrder == -1) {
            nextAceOrder = objectCount
        }
        return nextAceOrder
    }
}
