package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.web.mapping.LinkGenerator
import org.springframework.validation.ObjectError

@Transactional
class RegistrationService {
    LinkGenerator grailsLinkGenerator
    PageRenderer groovyPageRenderer

    /**
     * Schedule email to be sent asynchronously by the EmailSenderJob
     *
     * @param recipientEmailAddress the email of the newly created user
     * @return registration email or null if failure
     */
    def scheduleRegistrationEmail(String recipientEmailAddress) {
        String baselink  = grailsLinkGenerator.serverBaseURL
        Profile profile = Profile.findByEmailAddress(recipientEmailAddress)
        UserAccount user = profile?.user
        RegistrationEmail registrationEmail

        if (user){
            registrationEmail = new RegistrationEmail(toAddresses: [recipientEmailAddress],
                subject: "Thank you for registering!",
                plainMessage: groovyPageRenderer.render(template: "/templates/registrationEmailPlainText",
                    model: [user: user, baseLink: baselink]),
                htmlMessage: groovyPageRenderer.render(template: "/templates/registrationEmail",
                        model: [user: user, baseLink: baselink]))

            if (!registrationEmail.save()) {
                registrationEmail.getErrors().allErrors.each { ObjectError err ->
                    log.error(err.toString())
                }
                return null
            }
        }

        return registrationEmail
    }

    /**
     * Delete email after it's no longer needed. This is called by the EmailSenderJob
     *
     * @param email the email that we want to delete
     */
    void deleteRegistrationEmail(RegistrationEmail email) {
        email?.delete()
    }
}
