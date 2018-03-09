package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Profile {

    String userClass
    String emailAddress
    String firstName
    String lastName
    ProfilePic picture
    String interests
    String background
    Date dateCreated
    Date lastUpdated
    Institution institution

    static belongsTo    = [user: UserAccount]
    static hasMany      = [annotations: Annotation]

    static constraints = {
        userClass(nullable: true)
        emailAddress(nullable: false, size: 1..32, unique: true, email: true,
                validator: { val, obj, errors ->
                    if (val =~ /\w*@\w*.*(org|edu|com)/) return ['badEmail', val]
        })
        firstName(nullable: false, size: 1..16)
        lastName(nullable: false, size: 1..16)
        picture(nullable: true) // limit file size to 2MB)
        institution(nullable: false)
        interests(nullable: true, blank: true)
        background(nullable: true, blank: true)
        user(nullable: false)
    }

    String toString() {
        return "Profile for ${user.username}"
    }
}
