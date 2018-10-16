package edu.wustl.cielo

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class UtilService {

    def springSecurityService

    /**
     * Return a string representation of the date/time difference between two dates
     *
     * @param fromDate the start/from date
     * @param toDate the end date or null for current date/time
     *
     * @return a string describing the difference
     */
    String getDateDiff(Date fromDate, Date toDate = null) {
        String dateDiffString
        LocalDateTime from  = timezoneDate(fromDate)
        LocalDateTime today = toDate ? timezoneDate(toDate) : timezoneDate(new Date())

        //number of years first
        long years = from.until(today, ChronoUnit.YEARS)

        if (years) {
            if (years > 1L) {
                dateDiffString = "${years} years ago"
            } else {
                dateDiffString = "${years} year ago"
            }
        } else {
            //now months
            long months = from.until(today, ChronoUnit.MONTHS)

            if (months) {
                if (months > 1L) {
                    dateDiffString = "${months} months ago"
                } else {
                    dateDiffString = "${months} month ago"
                }
            } else {
                long weeks = from.until(today, ChronoUnit.WEEKS)

                if (weeks) {
                    if (weeks > 1L) {
                        dateDiffString = "${weeks} weeks ago"
                    } else {
                        dateDiffString = "${weeks} week ago"
                    }

                } else {
                    long days = from.until(today, ChronoUnit.DAYS)

                    if (!(days <= 0L)) {
                        dateDiffString = "${days} days ago"
                    } else {
                        //must be either hours or something smaller than that
                        def hoursDiff = from.until(today, ChronoUnit.HOURS)

                        if (hoursDiff) {
                            dateDiffString = "${hoursDiff} hours ago"
                        } else {
                            def minutesDiff = from.until(today, ChronoUnit.MINUTES)

                            if (minutesDiff) dateDiffString = "${minutesDiff} minutes ago"
                            else dateDiffString = "seconds ago"
                        }
                    }
                }
            }
        }
        return dateDiffString
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