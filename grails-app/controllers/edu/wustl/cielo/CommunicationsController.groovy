package edu.wustl.cielo

class CommunicationsController {
    def messageSource
    def communicationsService

    /**
     * Method for which to communicate with the helpdesk
     *
     * @return redirect to the page the request originated from
     */
    def contactUs() {

        if (communicationsService.scheduleContactUsEmail(params)) {
            flash.success = messageSource.getMessage('communications.contactUs', null, 'Thank you! We will get back to you shortly',
                    request.locale)
        } else {
            flash.danger = messageSource.getMessage('communications.contactUs.failure', null, 'There was an error saving email',
                    request.locale)
        }

        redirect(url: request.getHeader("referer"))
    }
}
