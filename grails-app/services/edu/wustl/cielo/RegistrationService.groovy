package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.web.mapping.LinkGenerator

@Transactional
class RegistrationService {
    LinkGenerator grailsLinkGenerator
    PageRenderer groovyPageRenderer

    /**
     * Schedule email to be sent asynchronously by the EmailSenderJob
     *
     * @param recipient the email of the newly created user
     * @return
     */
    def scheduleRegistrationEmail(String recipient) {
        String baselink  = grailsLinkGenerator.serverBaseURL
        Profile profile = Profile.findByEmailAddress(recipient)
        UserAccount user = profile.user

        new RegistrationEmail(toAddresses: [recipient],
            subject: "Thank you for registering!",
            plainMessage: groovyPageRenderer.render(template: "/templates/registrationEmailPlainText",
                model: [user: user, baseLink: baselink]),
            htmlMessage: groovyPageRenderer.render(template: "/templates/registrationEmail",
                    model: [user: user, baseLink: baselink])).save()
    }

    /**
     * Delete email after it's no longer needed. This is called by the EmailSenderJob
     *
     * @param email the email that we want to delete
     */
    void deleteRegistrationEmail(RegistrationEmail email) {
        email.delete()
    }
}
