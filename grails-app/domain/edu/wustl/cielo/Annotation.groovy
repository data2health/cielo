package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Annotation {
    String label
    Date dateCreated
    Date lastUpdated

    static constraints = {
        label(nullable: false, blank: false, size: 2..255)
    }
}
