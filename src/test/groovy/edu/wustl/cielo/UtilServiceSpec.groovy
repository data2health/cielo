package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

import java.time.LocalDateTime

class UtilServiceSpec extends Specification implements ServiceUnitTest<UtilService>, DomainUnitTest<UserAccount> {
    void setup() {

    }

    void "test getDateDiff"() {

        when:
        String result = service.getDateDiff(new Date(), new Date())

        then:
        result == "seconds ago"

        when:
        Date toDate = new Date()
        Date fromDate = new Date(toDate.year - 1, toDate.month, toDate.date, toDate.hours, toDate.minutes)
        result = service.getDateDiff(fromDate, toDate)

        then:
        result == "1 year ago"


        when:
        toDate = new Date()
        fromDate = new Date(toDate.year, toDate.month - 2, toDate.date, toDate.hours, toDate.minutes)
        result = service.getDateDiff(fromDate, toDate)

        then:
        result == "2 months ago"

        when:
        toDate = new Date()
        fromDate = new Date(toDate.year, toDate.month, toDate.date - 4, toDate.hours, toDate.minutes)
        result = service.getDateDiff(fromDate, toDate)

        then:
        result == "4 days ago"

        when:
        toDate = new Date()
        fromDate = new Date(toDate.year, toDate.month, toDate.date - 14, toDate.hours, toDate.minutes)
        result = service.getDateDiff(fromDate, toDate)

        then:
        result == "2 weeks ago"

        when:
        toDate = new Date()
        fromDate = new Date(toDate.year, toDate.month, toDate.date, toDate.hours - 4, toDate.minutes)
        result = service.getDateDiff(fromDate, toDate)

        then:
        result == "4 hours ago"

        when:
        toDate = new Date()
        fromDate = new Date(toDate.year, toDate.month, toDate.date, toDate.hours, toDate.minutes - 10)
        result = service.getDateDiff(fromDate, toDate)

        then:
        result == "10 minutes ago"
    }

    void "test timezoneDate"() {
        when:
        LocalDateTime localDateTime = service.timezoneDate(new Date())

        then:
        localDateTime
    }
}
