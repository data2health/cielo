package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class CieloTagLibSpec extends Specification implements TagLibUnitTest<CieloTagLib>, DomainUnitTest<UserAccount> {

    def setup() {
    }

    def cleanup() {
    }

    void "test rawOutput"() {
        when:"passing in an html string"
            def result = tagLib.rawOutput([text: "<div><span>some span</span></div>"], null)

        then: "the output should be same as the input"
            result == "<div><span>some span</span></div>"

    }

    void "test formatDateWithTimezone"() {
        when:"passing in an html string"
            Date date = new Date()
            def result = tagLib.formatDateWithTimezone([date: date], null)

        then:
            result == date.format("EEE MMM d yyyy @ hh:mm:ss z", TimeZone.getDefault())
    }
}
