package edu.wustl.cielo

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
     * @return
     */
    def register() {
        return [institutes: institutionService.getAvailableInstitutions(),
                usernames: userAccountService.getUsernames().join(", ")]
    }

    /**
     * Handles the registration of user with the data from registration form
     *
     * @return
     */
    def saveNewUser() {
        UserAccount user = new UserAccount()
        Profile profile = new Profile(user: user)
        boolean failed = false

        bindData(user, params)
        bindData(profile, params)

        //the user selected Other, we need to create a new institution based on the input from user
        if (Integer.valueOf(params.institution?.id) == -1) {
            Institution newInstitute = new Institution(fullName: params.institutionFName, shortName: params.institutionSName)
             if (!newInstitute.save())  {
                 newInstitute.errors.allErrors.each { ObjectError err ->
                     log.error(err.toString())
                 }
                 flash.danger = messageSource.getMessage('user.save.failed', null, 'Unable to save user',
                         request.locale)
                 failed = true
             }
            profile.institution = newInstitute
        }

        if (!user.save(flush: true)) {
            user.errors.allErrors.each { ObjectError err ->
                log.error(err.toString())
            }
            flash.danger = messageSource.getMessage('user.save.failed', null, 'Unable to save user',
                    request.locale)
            failed = true
        }

        if (!profile.save(flush: true)){
            profile.errors.allErrors.each { ObjectError err ->
                log.error(err.toString())
            }
            flash.danger = messageSource.getMessage('user.save.failed', null, 'Unable to save user',
                    request.locale)
            failed = true
        }

        if (failed) redirect(action: 'register')
        else {
            //get a registration code for the newly created user
            new RegistrationCode(userAccount: user).save(flush: true)
            redirect(action: 'registered', params: [userEmail: profile.emailAddress])
        }
    }

    /**
     * Generate view that has text for user to check email
     *
     * @return
     */
    def registered() {
        registrationService.scheduleRegistrationEmail(params.userEmail)
        return [emailAddress: params.userEmail]
    }

    /**
     * Given the key passed in activate the user that the key belongs to
     */
    def activateUser() {
        if (!params.ukey) render(view: "/error")
        boolean registration = userAccountService.activateUserAccount(params.ukey)

        render(view: "/registration/activated", model: [successful: registration, token: params.ukey,
                                                        link: "${grailsLinkGenerator.serverBaseURL}/login/auth"])
    }
}
