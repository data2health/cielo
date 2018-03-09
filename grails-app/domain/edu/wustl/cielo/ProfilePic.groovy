package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class ProfilePic {

    byte[] fileContents
    String fileExtension

    static belongsTo = [profile: Profile]

    static constraints = {
        fileContents(nullable: false, maxSize: 10 * 1024 * 1024)
        fileExtension(nullable: false)
    }

    String toString() {
        return "image for " + profile.toString() + " with id ${this.id} and size ${fileContents.size()}"
    }
}
