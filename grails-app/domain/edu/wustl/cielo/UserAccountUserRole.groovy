package edu.wustl.cielo

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UserAccountUserRole implements Serializable {

	private static final long serialVersionUID = 1

	UserAccount userAccount
	UserRole userRole
	Date dateCreated

	@Override
	boolean equals(other) {
		if (other instanceof UserAccountUserRole) {
			other.userAccountId == userAccount?.id && other.userRoleId == userRole?.id
		}
	}

    @Override
	int hashCode() {
	    int hashCode = HashCodeHelper.initHash()
        if (userAccount) {
            hashCode = HashCodeHelper.updateHash(hashCode, userAccount.id)
		}
		if (userRole) {
		    hashCode = HashCodeHelper.updateHash(hashCode, userRole.id)
		}
		hashCode
	}

	static UserAccountUserRole get(long userAccountId, long userRoleId) {
		criteriaFor(userAccountId, userRoleId).get()
	}

	static boolean exists(long userAccountId, long userRoleId) {
		criteriaFor(userAccountId, userRoleId).count()
	}

	private static DetachedCriteria criteriaFor(long userAccountId, long userRoleId) {
		UserAccountUserRole.where {
			userAccount == UserAccount.load(userAccountId) &&
			userRole == UserRole.load(userRoleId)
		}
	}

	static UserAccountUserRole create(UserAccount userAccount, UserRole userRole, boolean flush = false) {
		def instance = new UserAccountUserRole(userAccount: userAccount, userRole: userRole)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(UserAccount u, UserRole r) {
		if (u != null && r != null) {
			UserAccountUserRole.where { userAccount == u && userRole == r }.deleteAll()
		}
	}

	static int removeAll(UserAccount u) {
		u == null ? 0 : UserAccountUserRole.where { userAccount == u }.deleteAll() as int
	}

	static int removeAll(UserRole r) {
		r == null ? 0 : UserAccountUserRole.where { userRole == r }.deleteAll() as int
	}

	static constraints = {
		userRole validator: { UserRole r, UserAccountUserRole ur ->
			if (ur.userAccount?.id) {
				UserAccountUserRole.withNewSession {
					if (UserAccountUserRole.exists(ur.userAccount.id, r.id)) {
						return ['userRole.exists']
					}
				}
			}
		}
	}

	static mapping = {
		id composite: ['userAccount', 'userRole']
		version false
	}
}
