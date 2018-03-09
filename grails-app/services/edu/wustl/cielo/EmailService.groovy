package edu.wustl.cielo

import com.mailjet.client.resource.Contact
import com.mailjet.client.errors.MailjetSocketTimeoutException
import com.mailjet.client.errors.MailjetException
import com.mailjet.client.MailjetClient
import com.mailjet.client.MailjetRequest
import com.mailjet.client.MailjetResponse
import com.mailjet.client.resource.Email
import grails.gorm.transactions.Transactional
import org.json.JSONArray
import org.json.JSONObject

@Transactional
class EmailService {

    def grailsApplication
    MailjetClient client
    static String FROM_NAME
    static String FROM_EMAIL

    /**
     * Configure the necessary components for email service to function properly
     *
     */
    void init() {
        client = new MailjetClient(grailsApplication.config.getProperty("mailjet.publicKey"),
                grailsApplication.config.getProperty("mailjet.secretKey"))

        FROM_NAME  = grailsApplication.config.getProperty("mailjet.from")
        FROM_EMAIL = grailsApplication.config.getProperty("mailjet.fromAddress")
    }

    /**
     * Send an email using mailjet
     *
     * @param toAddresses the list of addresses to send email to
     * @param subject the subject of the email
     * @param plainMessage the plain text message
     * @param htmlMessage the html message
     * @return
     */
    MailjetResponse sendEmail(List<String> toAddresses, String subject, String plainMessage, String htmlMessage) {
        if (!client) init()

        MailjetResponse response
        MailjetRequest email
        JSONArray recipients

        recipients = new JSONArray()

        toAddresses.each { emailAddr ->
            recipients.put(new JSONObject().put(Contact.EMAIL, emailAddr))
        }


        email = new MailjetRequest(Email.resource)
                .property(Email.FROMNAME, FROM_NAME)
                .property(Email.FROMEMAIL, FROM_EMAIL)
                .property(Email.SUBJECT, subject)
                .property(Email.TEXTPART, plainMessage)
                .property(Email.HTMLPART, htmlMessage)
                .property(Email.RECIPIENTS, recipients)
                .property(Email.MJCUSTOMID, "JAVA-Email")

        try {
            response = client.post(email)
        } catch (MailjetSocketTimeoutException | MailjetException mje) {
            log.error("There was an error sending registration email to ${toAddresses.toString()}")
            mje.printStackTrace()
        }
        return response
    }
}
