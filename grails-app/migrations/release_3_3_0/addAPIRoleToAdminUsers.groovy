package release_3_3_0

import edu.wustl.cielo.UserAccountUserRole
import edu.wustl.cielo.UserRole
import edu.wustl.cielo.enums.UserRolesEnum
import grails.plugin.springsecurity.acl.AclSid

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (manual)", id: "1535117228830-1") {
        grailsChange {
            change {
                UserRole apiRole = new UserRole(authority: UserRolesEnum.ROLE_API.name()).save(flush: true)
                AclSid aclSid       = new AclSid()
                aclSid.principal    = false
                aclSid.sid          = UserRolesEnum.ROLE_API.name()
                aclSid.save(flush: true)
                UserAccountUserRole.findAllByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))?.each { UserAccountUserRole userAccountUserRole ->

                    if (apiRole) UserAccountUserRole.create(userAccountUserRole.userAccount, apiRole).save()
                }
            }
        }
    }
}