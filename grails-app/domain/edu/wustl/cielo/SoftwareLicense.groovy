package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class SoftwareLicense {

    boolean custom = false
    String url
    String label
    String body
    UserAccount creator
    Date dateCreated
    Date lastUpdated

    static mapping = {
        body sqlType: 'text'
    }

    static constraints = {
        creator(nullable: false)
        body(nullable: false)
        label(nullable: false)
        url(nullable: false)
    }
}
