package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Institution {

    String fullName
    String shortName
    Date dateCreated
    Date lastUpdated

    static constraints = {
        fullName(nullable: false, blank: false, maxSize: 64)
        shortName(nullable: false, blank: false, maxSize: 16)
    }
}
