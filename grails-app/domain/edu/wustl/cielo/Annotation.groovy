package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Annotation {
    String term
    String code
    Date dateCreated
    Date lastUpdated

    static constraints = {
        term(nullable: false, blank: false, unique: true)
        code(blank: false)
    }

    static mapping = {
        term sqlType: 'text'
    }
}
