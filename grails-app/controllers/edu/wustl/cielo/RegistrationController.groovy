package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.validation.ObjectError
import grails.web.mapping.LinkGenerator
import grails.gsp.PageRenderer

class RegistrationController {

    LinkGenerator grailsLinkGenerator
    PageRenderer groovyPageRenderer
    def institutionService
    def userAccountService
    def messageSource
    def emailService
    def registrationService

    /**
     * Handles making necessary data model available to register page
     *
     * @return map with the necessary data for view
     */
    @Secured('permitAll')
    def register() {
        return [institutes: institutionService.getAvailableInstitutions(),
                annotations: null,
                usernames: userAccountService.getUsernames().join(", ")]
    }

    /**
     * Handles the registration of user with the data from registration form
     *
     * @return
     */
    @Secured('permitAll')
    def saveNewUser() {
        boolean registered
        String institutionFName
        String institutionSName
        UserAccount user = new UserAccount()
        Profile profile = new Profile(user: user)
        ProfilePic profilePic

        bindData(user, params)
        bindData(profile, params)

        if (!profile.institution.id) {
            //user selected other
            institutionFName = params.institutionFName
            institutionSName = params.institutionSName
        }

        if (params.profilePic.filename) {
            ArrayList filename = params.profilePic.filename.tokenize('.')
            String extension    = filename[filename.size()-1]
            byte[] imageContent = params.profilePic.bytes
            profilePic = userAccountService.saveProfilePic(imageContent, extension)
            profile.picture = profilePic
        }

        registered = userAccountService.registerUser(user, profile, institutionFName, institutionSName)

        if (!registered) {
            flash.danger = messageSource.getMessage('user.save.failed', null, 'Unable to save user',
                    request.locale)
            redirect(action: 'register')
        }
        else {
            redirect(action: 'registered', params: [userEmail: profile.emailAddress])
        }
    }

    /**
     * Generate view that has text for user to check email
     *
     * @return map with the users email
     */
    @Secured('permitAll')
    def registered() {
        registrationService.scheduleRegistrationEmail(params.userEmail)
        return [emailAddress: params.userEmail]
    }

    /**
     * Given the key passed in activate the user that the key belongs to
     */
    @Secured('permitAll')
    def activateUser() {
        if (!params.ukey) render(view: "/error")
        boolean activated = userAccountService.activateUserAccount(params.ukey)

        if (activated){
            render(view: "/registration/activated", model: [successful: activated, token: params.ukey,
                                                            link: "${grailsLinkGenerator.serverBaseURL}/login/auth"])
        } else {
            chain(controller: "home", acion: "index")
        }
    }
}
