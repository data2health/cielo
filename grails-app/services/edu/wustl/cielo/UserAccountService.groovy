package edu.wustl.cielo

import edu.wustl.cielo.enums.AccountStatusEnum
import edu.wustl.cielo.enums.UserRolesEnum
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.acl.AclEntry
import grails.plugin.springsecurity.acl.AclObjectIdentity
import grails.plugin.springsecurity.acl.AclSid
import groovy.util.logging.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.springframework.security.access.annotation.Secured
import org.springframework.security.acls.domain.BasePermission
import org.springframework.validation.ObjectError
import com.thedeanda.lorem.Lorem
import com.thedeanda.lorem.LoremIpsum
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder as SCH

@Transactional
@Slf4j
class UserAccountService {

    def springSecurityService
    def assetResourceLocator
    def teamService
    def projectService
    def customAclService
    static Lorem lorem = LoremIpsum.getInstance()

    /**
     * On success mark the DB where necessary by clearing failed attempts, removing the registration code,
     * marking the correct status
     *
     * @param loggedInUser the username of user that successfully logged in
     */
    void handleOnAuthSuccess(String username) {
        UserAccount authenticatedUser = findUser(username)

        if (authenticatedUser && springSecurityService.principal.username == username) {
            if (!authenticatedUser.status.equals(AccountStatusEnum.ACCOUNT_UNVERIFIED)) {
                //set the account to active
                authenticatedUser.status = AccountStatusEnum.ACCOUNT_ACTIVE
                authenticatedUser.registrationCode?.delete()
                authenticatedUser.failedAttempts = 0
                authenticatedUser.lastLogin = new Date()
                authenticatedUser.save()
            }
        }
    }

    /**
     * On failed attempt, mark the failure in db
     *
     * @param username the username entered in the login screen
     */
    void handleBadCredentials(String username) {
        markUserFailedLogin(findUser(username))
    }

    /**
     * return the user instance if exists
     *
     * @param principalUsername
     * @return
     */
    UserAccount findUser(String principalUsername) {
        return UserAccount.findByUsername(principalUsername)
    }

    /**
     * Given the account, save the account failure count by increasing existing count by one
     *
     * @param account instance of UserAccount
     */
    protected static void markUserFailedLogin(UserAccount account) {
        //if the user was found we need to mark the failure so that we can handle too many attempts
        if (account) {
            account.failedAttempts++
            account.save()
        }
    }

    /**
     * Setup user roles if they do not exist
     */
    void bootstrapUserRoles() {
        if (UserRole.count() == 0 || (UserRole.count() == 1 && UserRole.all.collect{ it.authority }.contains("ROLE_API"))) {
            log.info("****************************")
            log.info("Creating user roles...")
            log.info("****************************\n")
            UserRolesEnum.values().each { roleName ->
                if (!UserRole.findByAuthority(roleName)) {
                    UserRole newRole = new UserRole(authority: roleName.name())
                    if (!newRole.save()) {
                        newRole.errors.getAllErrors().each { ObjectError err ->
                            log.error(err.toString())
                        }
                        log.error("Unable to save roles. Exiting.")
                        System.exit(-1)
                    }
                    log.info("\tCreated ${roleName.name()}")
                }
            }
            log.info("\n")
        }
    }

    /**
     *  Create or retrieve admin account
     *
     * @return the system admin account
     */
    UserAccount bootstrapCreateOrGetAdminAccount() {
        UserAccount superAdmin

        if (!UserAccountUserRole.findByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))?.userAccount) {

            log.info("****************************")
            log.info("Creating admin user...")
            log.info("****************************\n")

            superAdmin = new UserAccount(username: "admin",
                    password: "wustlCielo@2017",
                    status: AccountStatusEnum.ACCOUNT_VERIFIED,
                    timezoneId: "EST",
                    accountLocked: false
            )
            if (!superAdmin.save()) {
                superAdmin.errors.getAllErrors().each { ObjectError err ->
                    log.error(err.toString())
                }
                log.error("Unable to save default admin account. Exiting.")
                System.exit(-1)
            }
            log.info("\tSaved admin user: ${superAdmin.toString()}")

            //save profile pic
            byte[] imageContents  = assetResourceLocator.findAssetForURI("admin.png")?.getInputStream()?.bytes
            ProfilePic profilePic = new ProfilePic([fileContents: imageContents, fileExtension: "png"])

            if (!profilePic.save()) {
                profilePic.errors.getAllErrors().each { ObjectError err ->
                    log.error(err.toString())
                }
                log.error("Unable to save default admin profilePic. Exiting.")
                System.exit(-1)
            }
            log.info("\tSaved admin profilePic ${profilePic.toString()}")

            //save profile stuff
            Institution institution = new Institution(fullName: "Washington University in St Louis",
                    shortName: "WUSTL").save()
            Profile adminProfile = new Profile(
                    emailAddress: "no-reply@cielo.wustl.edu",
                    firstName: "Administrator",
                    lastName: "Cielo",
                    institution: institution,
                    user: superAdmin,
                    picture: profilePic
            )

            if (!adminProfile.save()) {
                adminProfile.errors.getAllErrors().each { ObjectError err ->
                    log.error(err.toString())
                }
                log.error("Unable to save default admin profile. Exiting.")
                System.exit(-1)
            }
            log.info("\tSaved admin profile ${adminProfile.toString()}")

            // need registration code
            RegistrationCode registrationCode = new RegistrationCode(userAccount: superAdmin)

            if (!registrationCode.save()) {
                registrationCode.errors.getAllErrors().each { err ->
                    log.error(err.toString())
                }
                log.error("Unable to save admin registration code. Exiting.")
                System.exit(-1)
            }
            log.info("\tSaved registration code for admin: ${registrationCode.toString()}")
        } else {
            superAdmin = UserAccountUserRole.findByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name())).userAccount

            if (superAdmin) {
                log.info("****************************")
                log.info("Found admin user...")
                log.info("****************************\n")
            } else {
                log.error("Unable to find admin account. Exiting.")
                System.exit(-1)
            }
        }
        return superAdmin
    }

    /**
     * Add admin user role to the user
     *
     * @param user should only be a user that is to be an admin
     */
    void bootstrapAddSuperUserRoleToUser(UserAccount user) {
        if (UserAccountUserRole.countByUserAccount(user) == 0) {
            log.info("****************************")
            log.info("Adding SUPER_USER role to admin user...")
            log.info("****************************\n")

            UserRole adminRole = UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name())

            if (adminRole) {
                UserAccountUserRole adminUserAccountUserRole = UserAccountUserRole.create(user,adminRole, true)
                if (!adminUserAccountUserRole) {
                    adminUserAccountUserRole.errors.getAllErrors().each { ObjectError err ->
                        log.error(err.toString())
                    }
                    log.error("Unable to save default admin role. Exiting.")
                    System.exit(-1)
                }
                log.info("\tSaved default admin role")
            } else {
                throw new Exception("Could not set the admin's authority. Exiting")
            }
        }
    }

    /**
     * Setup a number of mock users
     *
     * @param numberOfUsers count of users to setup
     */
    void setupMockAppUsers(int numberOfUsers, int numAnnotations) {

        UserAccount superAdmin = UserAccountUserRole.findByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))?.userAccount
        List<UserAccount> users = UserAccount.list() - superAdmin
        if (users.size() == 0) {
            log.info("****************************")
            log.info("Creating ${numberOfUsers} users...")
            log.info("****************************\n")

            def annotations = Annotation.list(max: 20)
            def institutions = Institution.list()

            numberOfUsers.times { index ->
                String username  = RandomStringUtils.random(3, true, true)
                UserAccount user = new UserAccount(
                        username: username,
                        password: '123',
                        status: AccountStatusEnum.ACCOUNT_VERIFIED,
                        accountLocked: false,
                )

                if (!user.save()) {
                    user.errors.getAllErrors().each { ObjectError err ->
                        log.error(err.toString())
                    }
                    log.error("Unable to save account for ${username}. Exiting.")
                    System.exit(-1)
                }
                log.info("\tSaved user: ${user.toString()}")

                //save profile pic
                byte[] imageContents = assetResourceLocator.findAssetForURI("user_${index}.jpg")?.getInputStream()?.bytes
                ProfilePic profilePic = new ProfilePic([
                        fileExtension: "jpg",
                        fileContents: imageContents
                ])

                if (!profilePic.save()) {
                    profilePic.errors.getAllErrors().each { ObjectError err ->
                        log.error(err.toString())
                    }
                    log.error("Unable to save default admin profilePic. Exiting.")
                    System.exit(-1)
                }
                log.info("\tSaved admin profilePic ${profilePic.toString()}")

                Profile profile = new Profile(
                        emailAddress: username + '@' + RandomStringUtils.random(5, true, false) + '.' + "edu",
                        firstName: lorem.getFirstName(),
                        lastName:  lorem.getLastName(),
                        institution: institutions?.get(new Random().nextInt(institutions?.size())),
                        user: user,
                        picture: profilePic
                )

                if (annotations.size() > 0) {
                    numAnnotations.times {
                        Annotation annotationToAdd = annotations?.get(new Random().nextInt(annotations.size()))
                        while (profile.annotations?.contains(annotationToAdd) && annotations.size() > 1) {
                            annotationToAdd = annotations?.get(new Random().nextInt(annotations.size()))
                        }
                        profile.addToAnnotations(annotationToAdd)
                    }
                }

                if (!profile.save()) {
                    profile.errors.getAllErrors().each { ObjectError err ->
                        log.error(err.toString())
                    }
                    log.error("Unable to save profile for ${username}. Exiting.")
                    System.exit(-1)
                }
                log.info("\tSaved profile for ${username}")

                UserRole role = UserRole.findByAuthority(UserRolesEnum.ROLE_USER.name())

                if (role) {
                    UserAccountUserRole userAccountUserRole = new UserAccountUserRole(userAccount: user,
                            userRole: role)
                    if (!userAccountUserRole.save()) {
                        userAccountUserRole.errors.getAllErrors().each { ObjectError err ->
                            log.error(err.toString())
                        }
                        log.error("Unable to ${userAccountUserRole.userRole.authority}. Exiting.")
                        System.exit(-1)
                    }
                    log.info("\tSaved ${userAccountUserRole.userRole.authority} role for ${user.toString()}")
                }
                log.info('id: ' + user.id + ' username: ' + user.username + ' password: 123')
            }
            log.info("\n")
        }
    }

    /**
     * Setup mock data for user connections/followers
     *
     * @param numFollowers
     */
    void bootstrapFollowers(int numFollowers) {

        log.info('******************************************')
        log.info("Creating ${numFollowers} followers...")
        log.info('******************************************')

        UserAccount superAdmin = UserAccountUserRole.findByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))?.userAccount
        List<UserAccount> users = UserAccount.list() - superAdmin
        users?.each { UserAccount user ->
            if (!user.connections) {
                List<UserAccount> usersCopy = (users - user)
                numFollowers.times {
                    def anotherUser = usersCopy.size() > 0 ? usersCopy?.head() : null

                    if (anotherUser) {
                        user.connections?.add(anotherUser)

                        if (!user.save()) {
                            user.getErrors().allErrors.each { ObjectError err ->
                                log.error(err.toString())
                            }
                            log.error("There was an error saving the user connections for user ${user.username}")
                            System.exit(-1)
                        }
                        usersCopy = (usersCopy - anotherUser)
                        log.info("\tSaved user: ${user.toString()}")
                    }
                }
                log.info("\n")
            }
        }
        log.info("\n")
    }

    /**
     * Get a list of all the usernames saved in the DB
     *
     * @return list of usernames
     */
    List<String> getUsernames() {
        return UserAccount.list().collect { it.username }
    }

    /**
     * Activate a user given the user token
     *
     * @param registrationToken the autogenerated token to use

     * @return true if successful, false otherwise
     */
    boolean activateUserAccount(String registrationToken) {
        RegistrationCode registrationCode = RegistrationCode.findByToken(registrationToken)
        UserAccount userAccount = registrationCode?.userAccount

        if (userAccount && registrationCode) {
            userAccount.status = AccountStatusEnum.ACCOUNT_VERIFIED
            userAccount.accountLocked = false

            if (!userAccount.save()) {
                userAccount.errors.getAllErrors().each { ObjectError err ->
                    log.error(err.toString())
                }
                log.error("Unable to activate account ${userAccount.username}.")
                return false
            }
            log.info("Activated account for ${userAccount.username}")

            //now need to delete the old registration code
            registrationCode.delete()

            if (registrationCode.hasErrors()) {
                registrationCode.errors.getAllErrors().each { ObjectError err ->
                    log.error(err.toString())
                }
                log.error("Unable to delete registration code for ${userAccount.username}.")
                return false
            }
            log.info("Removed old registration token for ${userAccount.username}")

            //now need to grant user access to all public projects
            setupNewUserPermissions(userAccount)

            return true
        } else return false
    }

    /**
     * Register new user
     *
     * @param user the unsaved user object bound in controller
     * @param profile the unsaved profile bound in controller
     * @param institutionFName the full name of the institution (optional; if user selects other)
     * @param institutionSName the short name of the institution (optional; if user selects other)
     *
     * @return true if the user was saved successfully
     */
    boolean registerUser(UserAccount user, Profile profile, String institutionFName, String institutionSName) {
        boolean successful

        //the user selected Other, we need to create a new institution based on the input from user
        if (!profile.institution.id && institutionFName && institutionSName) {
            Institution newInstitute = new Institution(fullName: institutionFName, shortName: institutionSName)
            if (!newInstitute.save())  {
                newInstitute.errors.allErrors.each { ObjectError err ->
                    log.error(err.toString())
                }
            }
            profile.institution = newInstitute
        }

        if (!user.save()) {
            user.errors.allErrors.each { ObjectError err ->
                log.error(err.toString())
            }
        } else {

            //we also need to add user role
            UserAccountUserRole userRole    = new UserAccountUserRole()
            userRole.userRole               = UserRole.findByAuthority(UserRolesEnum.ROLE_USER.toString())
            userRole.userAccount            = user

            if (!userRole.save()) {
                userRole.errors.allErrors.each { ObjectError err ->
                    log.error(err.toString())
                }
            }

            if (!profile.save()) {
                profile.errors.allErrors.each { ObjectError err ->
                    log.error(err.toString())
                }
            }
            successful = true

            //get a registration code for the newly created user
            new RegistrationCode(userAccount: user).save()
        }

        return successful
    }

    /**
     * Save an image, in the form of byte[] and extension name to the DB
     *
     * @param imageContent the byte[] of the image file
     * @param extension the extension of the image
     *
     * @return returns the instance of the profile pic or null if save fails
     */
    ProfilePic saveProfilePic(byte[] imageContent, String extension) {
        return new ProfilePic(fileExtension: extension, fileContents: imageContent).save()
    }

    /**
     * Follow a given user
     *
     * @param loggedInUser the user that is logged in
     * @param userId the id of the user to follow
     *
     * @return true if the connection was successful, false otherwise
     */
    boolean addConnection(UserAccount loggedInUser, Long userId) {
        UserAccount userToFollow = UserAccount.findById(userId)

        if (userToFollow) {
            loggedInUser.connections.add(userToFollow)

            if (!loggedInUser.save()) {
                loggedInUser.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
            return true
        }
       return false
    }

    /**
     * Remove a connection to a user
     *
     * @param loggedInUser the user that is logged in
     * @param userId the id of the user to remove from connections (to logged in user)
     *
     * @return true if successful, false otherwise
     */
    boolean removeConnection(UserAccount loggedInUser, Long userId) {
        UserAccount userToUnFollow = UserAccount.findById(userId)

        if (userToUnFollow) {
            loggedInUser.connections.remove(userToUnFollow)

            if (!loggedInUser.save()) {
                loggedInUser.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
            return true
        }
        return false
    }

    /**
     * Update a user
     *
     * @param userAccount the user account to update
     *
     * @return true if the account saves correctly otherwise false
     */
    boolean updateUser(UserAccount userAccount) {
        if (!userAccount.save()) {
            return false
        }
        return true
    }

    /**
     * Update the users connections
     *
     * @param user the user who's connections we will update
     * @param userIds the id's of the users to follow
     *
     * @return true if successful, false otherwise
     */
    boolean updateConnections(UserAccount user, List<Long> userIds) {
        user.connections.clear()

        userIds.each { userId ->
            UserAccount followingUser = UserAccount.findById(userId)
            if (followingUser) {
                user.connections.add(followingUser)
            }
        }

        if(!user.save()) {
            user.errors.allErrors.each { ObjectError error ->
                log.error(error.toString())
            }
            return false
        }
        return true
    }

    /**
     * Get the latest list of users followed
     *
     * @param user the person we retrieve the list for
     *
     * @return list of users
     */
    Set<UserAccount> getUsersFollowing(UserAccount user) {
        return user.connections
    }

    /**
     * Get the latest list of users that are following someone
     *
     * @param user the user to retrieve the list for
     *
     * @return list of users
     */
    Set<UserAccount> getUsersFollowingMe(UserAccount user) {
        return user.followers
    }

    /**
     * Get current list of projects owned by user
     *
     * @param userAccount the user to retrieve list for
     *
     * @return list of projects or null if none
     */
    List<Project> getProjectsUserOwns(UserAccount userAccount) {
        return Project.findAllByProjectOwner(userAccount)
    }

    /**
     * Retrieve the projects that a user contributes to
     *
     * @param user the user we will use to filter on
     *
     * @return list of projects or empty list
     */
    List<Project> getProjectsUserContributesTo(UserAccount user) {
        List<Project> matches = []

        teamService.getListOfTeamsUserIsMemberOf(user).each { team ->
            List<Project> subList = projectService.getListOfProjectsTeamContributesTo(team)

            if (subList) matches.addAll(subList)
        }
        return matches
    }

    /**
     * Get the logged in user
     *
     * @return a user instance or null
     */
    UserAccount getLoggedInUser() {
      return UserAccount.findById(springSecurityService.principal?.id)
    }

    /**
     * Add necessary permissions for users so that they can start looking at public projects
     *
     * @param userAccount the user that needs access
     */
    void setupNewUserPermissions(UserAccount userAccount) {
        //create an SID for user
        AclSid userAcl = AclSid.findBySid(userAccount.username)

        //now iterate through all the projects and add access to it for user
        Project.findAllByShared(true).each { Project project ->
            String projectClassName = Project.class.name
            AclSid aclSid           = AclSid.findBySid(project.projectOwner.username)
            AclObjectIdentity aclObjectIdentity = customAclService.getOrCreateObjectIdentity(project.id, projectClassName, aclSid)
            customAclService.grantPermission(aclObjectIdentity, userAcl, BasePermission.READ)
        }
    }

    /**
     * Remove all the permissions for a given user
     *
     * @param userAccount the user that we want to remove access to
     */
    void removePermissionsForUser(UserAccount userAccount) {
        //to be called when a user is deleted
        AclSid aclSid = AclSid.findBySid(userAccount.username)

        //iterate through all the acl entries and remove them if they belong to the user
        AclEntry.findAllBySid(aclSid).each { AclEntry aclEntry ->
            aclEntry.delete()
        }

        //remove any object identities the user owned
        AclObjectIdentity.findAllByOwner(aclSid).each { AclObjectIdentity aclObjectIdentity ->
            aclObjectIdentity.delete()
        }

        //remove the sid for the user
        aclSid.delete()
    }

    /**
     * Helper method for acl changes since we have to be admin
     */
    @Secured(['ROLE_ADMIN'])
    protected void loginAsSuperUser() {
        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
                'admin', 'wustlCielo@2017',
                AuthorityUtils.createAuthorityList('ROLE_SUPERUSER'))
    }

    /**
     * Helper method to logout programmatically
     */
    @Secured(['ROLE_ADMIN', 'ROLE_SUPERUSER'])
    protected void logout() {
        SCH.clearContext()
    }

    /**
     * Is the user passed in only have ROLE_API
     *
     * @param userAccount the user to check
     *
     * @return true if user only has ROLE_API, false otherwise
     */
    boolean isUserApiUserOnly(UserAccount userAccount) {
        UserRole apiUserRole = UserRole.findByAuthority("ROLE_API")
        List<UserAccountUserRole> userAccountUserRoles = UserAccountUserRole.findAllByUserAccount(userAccount)

        if (userAccountUserRoles.size() == 1) {
            return userAccountUserRoles[0].userRole == apiUserRole
        }
        return false
    }
}
