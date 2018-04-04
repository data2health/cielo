package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class ProfilePic {

    byte[] fileContents
    String fileExtension

    static constraints = {
        fileContents(nullable: false, maxSize: 10 * 1024 * 1024)
        fileExtension(nullable: false)
    }
}
