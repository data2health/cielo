package edu.wustl.cielo

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CieloTagLib {
    static defaultEncodeAs  = [taglib:'html'] //html escapes characters
    static encodeAsForTags  = [rawOutput: [taglib:'raw'], getUserProfilePic: [taglib: 'raw'], dateDiff: [taglib: 'raw'],
                               userOwnsProject: [taglib: 'raw'], userCanMakeChangesToProject: [taglib: 'raw'],
                               loggedInUserCanMakeChangesToUser: [taglib: 'raw']] //, otherTagName: [taglib:'none']]
    static String DEFAULT_IMG_SIZE  = 'medium'
    static int DEFAULT_STICKER_SIZE = 52 // 48 + 2 px
    static Map imageSizes   = ['xs': '-xs', 'small': '-sm', 'medium': '-md', 'large': '-lg', 'x-large': '-x-lg',
                               'xx-large': '-xx-lg', 'xxx-large': '-xxx-lg']
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
        String divId
        String styles
        String tooltipOffset

        if (attrs.id) {
            divId = "id=\"${attrs.id}\""
        }

        if (attrs.style) {
            styles = attrs.style
        }

        if (attrs.user) {
            user = attrs.user
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

        if (user) {
            if (attrs.sticker) {
                profilePicHtml << """<div ${divId} ${tooltip.toString()} class="row" style="align-items: center; padding-right: 1em; padding-left: 1em; ${styles}"><div id="image-sticker-border" style="border: 3px solid #bbbaba; width: ${sizeOfStickerContainer + 6}px; border-radius: 50%;">"""
                profilePicHtml << """<div id="image-background" style="border: 2px solid #ffffff; border-radius: 50%; width: ${sizeOfStickerContainer}px; height: ${sizeOfStickerContainer}px; background-color:  white; ">"""
            }

            if (user.profile.picture) {
                profilePicHtml << """<img src="data:image/${user?.profile?.picture?.fileExtension};base64,${user?.profile?.picture?.fileContents.encodeBase64().toString()}" class="${imgClass}">"""
            } else {
                profilePicHtml << """<img src="/assets/default_profile.png" class="${imgClass}">"""
            }

        } else {
            profilePicHtml << """<img ${tooltip.toString()} src="/assets/default_profile.png" class="${imgClass}">"""
        }

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
                    returnVal = "${years} month ago"
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

        if (attrs.user && attrs.user == user) out << body()
        else out << null
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
