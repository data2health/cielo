package edu.wustl.cielo

import groovy.util.logging.Slf4j
import com.mailjet.client.MailjetResponse

@Slf4j
class EmailSenderJob {
    def emailService
    def communicationsService
    def registrationService

    static triggers = {
        //every 5 minutes check for new emails to send
        cron name: 'cronTrigger', startDelay: 300000, cronExpression: '0 0/5 * 1/1 * ? *'
    }

    /**
     * Execute async email sends
     *
     * @param context
     * @return
     */
    def execute(context) {
        // execute job
        log.debug("Checking for new emails to send...")

        //Registration emails to send
        RegistrationEmail.findAllByAttemptsLessThan(3).each { regEmail ->
            MailjetResponse response = emailService.sendEmail(regEmail.toAddresses,
                        regEmail.subject, regEmail.plainMessage, regEmail.htmlMessage)

            if (response.getStatus() == 200) registrationService.deleteRegistrationEmail(regEmail)
        }

        //contact us emails to send
        ContactUsEmail.findAllByAttemptsLessThan(3).each { contactUsEmail ->
            MailjetResponse response = emailService.sendEmail(contactUsEmail.toAddresses,
                    contactUsEmail.subject, contactUsEmail.plainMessage, contactUsEmail.htmlMessage)

            if (response.getStatus() == 200) communicationsService.deleteContactUsEmail(contactUsEmail)
        }
    }
}
