package edu.wustl.cielo

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import grails.web.mapping.LinkGenerator

class CieloTagLib {
    static defaultEncodeAs  = [taglib:'html'] //html escapes characters
    static encodeAsForTags  = [rawOutput: [taglib:'raw'], getUserProfilePic: [taglib: 'raw'], dateDiff: [taglib: 'raw'],
                               userOwnsProject: [taglib: 'raw'], userCanMakeChangesToProject: [taglib: 'raw'],
                               loggedInUserCanMakeChangesToUser: [taglib: 'raw'], getSoftwareLicenseOptions: [taglib: 'raw'],
                               customTimeZoneSelect: [taglib: 'raw']] //, otherTagName: [taglib:'none']]
    static String DEFAULT_IMG_SIZE  = 'medium'
    static int DEFAULT_STICKER_SIZE = 52 // 48 + 4 px
    static Map imageSizes   = ['xs': '-xs', 'small': '-sm', 'medium': '-md', 'large': '-lg', 'x-large': '-x-lg',
                               'xx-large': '-xx-lg', 'xxx-large': '-xxx-lg', 'huge': '-huge']

    LinkGenerator grailsLinkGenerator
    def springSecurityService

    /**
     * Use if the output needs not be html encoded. This is true when the text already has html
     *
     * Ex: <g:rawOutput text="<a href='/someLink'>clickme </a>"/>
     */
    def rawOutput = { attrs, body ->
        out << attrs.text << body()
    }

    /**
     * Format a date/time using the users timezone
     */
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

    /**
     * Generate the html code for user profile pic in either sticker or non-sticker format. Image is read from the DB,
     * if there is one, otherwise the default_profile.png asset is used
     */
    def getUserProfilePic = { attrs, body ->
        def principal               = springSecurityService?.principal
        UserAccount user
        StringBuffer profilePicHtml = new StringBuffer("")
        StringBuffer imgClass       = new StringBuffer("activity-profile-pic")
        StringBuffer tooltip        = new StringBuffer("")
        int sizeOfStickerContainer  = DEFAULT_STICKER_SIZE
        String divId = ""
        String styles = ""
        String tooltipOffset = ""
        String userLink = ""

        if (attrs.id) {
            divId = "id=\"${attrs.id}\""
        }

        if (attrs.style) {
            styles = attrs.style
        }

        if (attrs.user) {
            user = attrs.user
            if (attrs.showLink) {
                userLink = grailsLinkGenerator.link(controller: "user", action: "view", id: attrs.user.id)
            }
        } else {
            user = UserAccount.get(principal?.id)
        }

        if (attrs.tooltipOffset) {
            tooltipOffset = "offset=\"${attrs.tooltipOffset}\""
        }

        if (attrs.tooltipText) {
            tooltip << "data-toggle=\"tooltip\" data-html=\"true\" title=\"${attrs.tooltipText}\" data-placement=\"top\" ${tooltipOffset}"
        }

        if (attrs.imageSize) {
            imgClass << (imageSizes.get(attrs.imageSize)?: imageSizes.get(DEFAULT_IMG_SIZE))
            sizeOfStickerContainer = imageSizes.get(attrs.imageSize) ? getStickerSize(attrs.imageSize) : getStickerSize(DEFAULT_IMG_SIZE)
        } else {
            imgClass << imageSizes.get(DEFAULT_IMG_SIZE)
        }

        if (attrs.showLink) profilePicHtml << """<a href="${userLink}">"""

        if (user) {
            if (attrs.sticker) {
                profilePicHtml << """<div ${divId} ${tooltip.toString()} class="row" style="align-items: center; padding-right: 1em; padding-left: 1em; ${styles}"><div id="image-sticker-border" style="border: 1px solid #a9a7a785; width: ${sizeOfStickerContainer + 2}px; border-radius: 50%;">"""
                profilePicHtml << """<div id="image-background" style="border: 2px solid #ffffff; border-radius: 50%; width: ${sizeOfStickerContainer}px; height: ${sizeOfStickerContainer}px; background-color:  white; ">"""

                if (user.profile?.picture) {
                    profilePicHtml << """<img src="data:image/${user?.profile?.picture?.fileExtension};base64,${user?.profile?.picture?.fileContents.encodeBase64().toString()}" class="${imgClass}">"""
                } else {
                    profilePicHtml << """<img src="/assets/default_profile.png" class="${imgClass}">"""
                }
            } else {
                if (user.profile?.picture) {
                    profilePicHtml << """<img ${divId} ${tooltip.toString()} src="data:image/${user?.profile?.picture?.fileExtension};base64,${user?.profile?.picture?.fileContents.encodeBase64().toString()}" class="${imgClass}">""" << (attrs.showLink? "</a>" : '')
                } else {
                    profilePicHtml << """<img ${divId} ${tooltip.toString()} src="/assets/default_profile.png" class="${imgClass}"></a>"""
                }
            }

        } else {
            profilePicHtml << """<img ${tooltip.toString()} src="/assets/default_profile.png" class="${imgClass}">"""
        }

        if (attrs.showLink) profilePicHtml << "</a>"

        if (attrs.sticker) {
            profilePicHtml << """</div></div>${body()}</div>"""
        } else { profilePicHtml << body() }

        out << profilePicHtml.toString()
    }

    /**
     * Output Yes/No instead of True/False
     */
    def booleanOut = { attrs ->
        if (attrs.value)    out << "Yes"
        else                out << "No"
    }

    def dateDiff = { attrs ->
        String returnVal
        LocalDateTime from  = timezoneDate(new Date(attrs.date.getTime()))
        LocalDateTime today = LocalDateTime.now()

        //number of years first
        long years = from.until(today, ChronoUnit.YEARS)

        if (years) {
            if (years > 1L) {
                returnVal = "${years} years ago"
            } else {
                returnVal = "${years} year ago"
            }
        } else {
            //now months
            long months = from.until(today, ChronoUnit.MONTHS)

            if (months) {
                if (months > 1L) {
                    returnVal = "${months} months ago"
                } else {
                    returnVal = "${months} month ago"
                }
            } else {
                long weeks = from.until(today, ChronoUnit.WEEKS)

                if (weeks) {
                    if (weeks > 1L) {
                        returnVal = "${weeks} weeks ago"
                    } else {
                        returnVal = "${weeks} week ago"
                    }

                } else {
                    long days = from.until(today, ChronoUnit.DAYS)

                    if (days) {
                        if (days > 1L) {
                            returnVal = "${days} days ago"
                        } else {
                            //here it is technically not a full day difference (based on hours) so check the day value
                            //and use it instead
                            def diff = today.date.day - from.date.day

                            if (diff) {
                                if (diff == 1) returnVal = "yesterday"
                                else returnVal = "${diff} days ago"
                            } else {
                                returnVal = "today"
                            }
                        }
                    } else {
                        def diff = today.date.day - from.date.day

                        if (diff) {
                            if (diff == 1) returnVal = "yesterday"
                            else returnVal = "${diff} days ago"
                        } else {
                            returnVal = "today"
                        }
                    }
                }
            }
        }

        out << "<span class='date-time date-diff' data-date='${g.formatDateWithTimezone(date: attrs.date)}' data-diff='${returnVal}'>${returnVal}</span>"
    }

    /**
     * Generate whether project is private or public
     */
    def projectVisibility = { attrs ->
         if (attrs.value)
             out << "Public"
        else out << "Private"
    }

    /**
     * if user own's project then show body
     */
    def userOwnsProject = { attrs, body ->

        if (attrs.project) {
            def principal    = springSecurityService?.principal
            UserAccount user = UserAccount.get(principal?.id)

            if (user && attrs.project.projectOwner == user) {
                out << body()
            }
        }
    }

    /**
     * Can user make changes to a given project
     */
    def userCanMakeChangesToProject = { attrs, body ->
        if (attrs.project) {
            Project project = (Project)attrs.project
            def principal    = springSecurityService?.principal
            UserAccount user = UserAccount.get(principal?.id)
            List<UserAccount> contributors = project.teams.each {Team team ->
                team.members.collect()
            }

            if (user && project.projectOwner == user || contributors.contains(user)) {
                out << body()
            }
        }
    }

    /**
     * Is the user passed in the same as the logged in user
     */
    def loggedInUserCanMakeChangesToUser = { attrs, body ->
        def principal    = springSecurityService?.principal
        UserAccount user = UserAccount.get(principal?.id)

        if (attrs.user && (attrs.user.equals(user))) out << body()
        else out << null
    }

    /**
     * Generate the HTML dropdown for software licenses
     */
    def getSoftwareLicenseOptions = { attrs, body ->
        StringBuffer options = new StringBuffer()

        options << """<select id="licenses" name="license" required="required" class="form-control" style="display:none;" aria-required="true">"""

        SoftwareLicense.list().each {
             options << """<option value="${it.id}">${it.label}</option>"""
        }

        options << """</select>"""

        out << options
    }

    /**
     * Return a list of timezones in a select control
     */
    def customTimeZoneSelect = { attrs ->
        String selectId = ""

        if (attrs.id) selectId = """ id="${attrs.id}" """

        String select = """<select ${selectId} name="selTimezone" size="1" class="form-control">
                            <option value="Kwajalein">(GMT -12:00) Eniwetok, Kwajalein</option>
                            <option value="Pacific/Midway">(GMT -11:00) Midway Island, Samoa</option>
                            <option value="US/Hawaii">(GMT -10:00) Hawaii</option>
                            <option value="US/Alaska">(GMT -9:00) Alaska</option>
                            <option value="US/Pacific">(GMT -8:00) Pacific Time (US &amp; Canada)</option>
                            <option value="US/Mountain">(GMT -7:00) Mountain Time (US &amp; Canada)</option>
                            <option value="US/Central">(GMT -6:00) Central Time (US &amp; Canada), Mexico City</option>
                            <option value="US/Eastern">(GMT -5:00) Eastern Time (US &amp; Canada), Bogota, Lima</option>
                            <option value="Canada/Atlantic">(GMT -4:00) Atlantic Time (Canada), Caracas, La Paz</option>
                            <option value="Canada/Newfoundland">(GMT -3:30) Newfoundland</option>
                            <option value="America/Buenos_Aires">(GMT -3:00) Brazil, Buenos Aires, Georgetown</option>
                            <option value="Etc/GMT+2">(GMT -2:00) Mid-Atlantic</option>
                            <option value="Atlantic/Azores">(GMT -1:00) Azores, Cape Verde Islands</option>
                            <option value="UTC" selected="selected">(GMT) Western Europe Time, London, Lisbon, Casablanca</option>
                            <option value="Europe/Paris">(GMT +1:00) Berlin, Brussels, Copenhagen, Madrid, Paris</option>
                            <option value="Europe/Istanbul">(GMT +2:00) Athens, Helsinki, Istanbul, Jerusalem</option>
                            <option value="Asia/Riyadh">(GMT +3:00) Baghdad, Riyadh</option>
                            <option value="Asia/Tehran">(GMT +3:30) Tehran</option>
                            <option value="Asia/Dubai">(GMT +4:00) Abu Dhabi, Muscat, Baku, Tbilisi, Moscow, St. Petersburg</option>
                            <option value="Asia/Kabul">(GMT +4:30) Kabul</option>
                            <option value="Asia/Yekaterinburg">(GMT +5:00) Ekaterinburg, Islamabad, Karachi, Tashkent</option>
                            <option value="Asia/Calcutta">(GMT +5:30) Bombay, Calcutta, Madras, New Delhi</option>
                            <option value="Asia/Katmandu">(GMT +5:45) Kathmandu</option>
                            <option value="Asia/Almaty">(GMT +6:00) Almaty, Dhaka, Colombo</option>
                            <option value="Asia/Bangkok">(GMT +7:00) Bangkok, Hanoi, Jakarta</option>
                            <option value="Asia/Shanghai">(GMT +8:00) Beijing, Perth, Singapore, Hong Kong</option>
                            <option value="Asia/Tokyo">(GMT +9:00) Tokyo, Seoul, Osaka, Sapporo, Yakutsk</option>
                            <option value="Australia/Darwin">(GMT +9:30) Adelaide, Darwin</option>
                            <option value="Australia/Sydney">(GMT +10:00) Eastern Australia, Guam, Vladivostok</option>
                            <option value="Asia/Magadan">(GMT +11:00) Magadan, Solomon Islands, New Caledonia</option>
                            <option value="Pacific/Fiji">(GMT +12:00) Auckland, Wellington, Fiji, Kamchatka</option>
                        </select>
        """

        out << select
    }

    /**
     * Get the size of the sticker that surrounds the image
     *
     * @param imageSize the imageSize attr value in the g tag (getUserProfilePic)
     *
     * @return the size in int's of the sticker
     */
    private int getStickerSize(String imageSize) {
        int size = DEFAULT_STICKER_SIZE

        switch(imageSize) {
            case 'medium':
                size = DEFAULT_STICKER_SIZE
                break
            case 'xs':
                size = (24 + 2)
                break
            case 'small':
                size = (36 + 2)
                break
            case 'large':
                size = (60 + 2)
                break
            case 'x-large':
                size = (72 + 2)
                break
            case 'xx-large':
                size = (84 + 2)
                break
            case 'xxx-large':
                size = (96 + 2)
                break
            case 'huge':
                size = (190 + 2)
                break
        }
        return size
    }

    /**
     * Convert date to the users timezone, no formatting
     *
     * @param date the date to convert
     *
     * @return a new date that represents the original in the users timezone
     */
    private LocalDateTime timezoneDate(Date date) {
        def principal    = springSecurityService?.principal
        UserAccount user = UserAccount.get(principal?.id)
        TimeZone tz

        if (!user) {
            tz = TimeZone.getDefault()
        } else tz = TimeZone.getTimeZone(user.timezoneId)

        ZoneId zoneId = tz.toZoneId()

        return LocalDateTime.ofInstant(date.toInstant(), zoneId)
    }
}
