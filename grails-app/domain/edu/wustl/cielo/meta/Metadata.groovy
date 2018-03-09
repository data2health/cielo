package edu.wustl.cielo.meta

import grails.compiler.GrailsCompileStatic
import edu.wustl.cielo.Project

@GrailsCompileStatic
class Metadata {

    String key
    String value

    static constraints = {
        key(nullable: false, blank: false)
        value(nullable: false, blank: false)
    }

    String toString() {
        return "${key}: ${value}"
    }
}
