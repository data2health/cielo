package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Data {

    int revision = 0
    String name
    String description
    String repository
    Date dateCreated
    Date lastUpdated
    URL url

    static constraints = {
        name(nullable: false)
        description(nullable: false)
        url(nullable: true)
        revision(nullable: false)
        repository(blank: false)
    }

    static mapping = {
        description type: 'text'
    }

    String toString() {
        return name + " " + revision
    }
}
