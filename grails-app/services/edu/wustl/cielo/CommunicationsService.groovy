package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.web.mapping.LinkGenerator
import org.springframework.validation.ObjectError

@Transactional
class CommunicationsService {
    static String TO_EMAIL
    String BASE_LINK
    LinkGenerator grailsLinkGenerator
    PageRenderer groovyPageRenderer
    def grailsApplication
    def springSecurityService

    /**
     * Initialize static variables
     *
     */
    void init() {
        TO_EMAIL = grailsApplication.config.getProperty("mailjet.fromAddress")
        BASE_LINK  = grailsLinkGenerator?.serverBaseURL
    }

    /**
     * Persist a contact us email to the db to be retrieved by email sender job asynchronously
     *
     * @param params map that contains the serialized data from the contact form submitted
     *
     * @return true if successful, false otherwise
     */
    boolean scheduleContactUsEmail(Map params) {
        boolean successful = false
        if (!TO_EMAIL) init()

        if (!params.name) {
            //then the user is already logged in so we need to get the info for that user
            Object principal = springSecurityService?.principal
            UserAccount user = principal ? UserAccount.get(principal.id) : null

            if (user) {
                params.name  = "${user.profile.firstName} ${user.profile.lastName}"
                params.email = user.profile.emailAddress
                params.phone = user.username
            }
        }

        Map model = [name: params.name,
                     email: params.email,
                     phone: params.phone,
                     message: params.message,
                     baseLink: BASE_LINK]

        ContactUsEmail email = new ContactUsEmail(toAddresses: [TO_EMAIL],
                subject: params.subject,
                plainMessage: groovyPageRenderer.render(template: "/templates/contactUsEmailPlainText",
                        model: model),
                htmlMessage: groovyPageRenderer.render(template: "/templates/contactUsEmail",
                        model: model))

        if (!email.save()) {
            email.getErrors().allErrors.each { ObjectError err ->
                log.error(err.toString())
            }
        } else {
            successful = true
            log.info("Contact email has been saved to the DB")
        }

        return successful
    }

    /**
     * Delete email after it's no longer needed. This is called by the EmailSenderJob
     *
     * @param email the email that we want to delete
     */
    void deleteContactUsEmail(ContactUsEmail email) {
        email.delete()
    }
}
