package edu.wustl.cielo

import java.text.SimpleDateFormat

class CieloTagLib {
    static defaultEncodeAs = [taglib:'html'] //html escapes characters
    static encodeAsForTags = [rawOutput: [taglib:'raw']] //, otherTagName: [taglib:'none']]
    def springSecurityService

    /**
     * Use if the output needs not be html encoded. This is true when the text already has html
     *
     * Ex: <g:rawOutput text="<a href='/someLink'>clickme </a>"/>
     */
    def rawOutput = { attrs, body ->
        out << attrs.text << body()
    }

    def formatDateWithTimezone = { attrs ->
        String dateFormat = "EEE MMM d yyyy @ hh:mm:ss z"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat)
        def principal    = springSecurityService?.principal
        UserAccount user = UserAccount.get(principal?.id)
        TimeZone tz

        if (!user) {
            tz = TimeZone.getDefault()
        } else tz = TimeZone.getTimeZone(user.timezoneId)

        simpleDateFormat.setTimeZone(tz)
        out << simpleDateFormat.format(new Date(attrs.date.getTime()))
    }
}
