package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import java.util.Random

class ProfilePicSpec extends Specification implements DomainUnitTest<ProfilePic> {

    def webRoot

    def setup() {
        mockDomain(Profile)
        webRoot =  new File(".").canonicalPath +  "/grails-app/assets/images/"
    }

    void "test saving"() {
        Profile profile
        ProfilePic profilePic
        Institution institution
        UserAccount user
        byte[] contents

        when: "no props"
            profilePic = new ProfilePic()

        then: "save fails"
            !profilePic.save()

        when: "get contents of a real image"
            contents = new File(webRoot + "mbr-9-1620x1080.jpg").bytes
            profilePic = new ProfilePic(fileContents: contents)

        then: "fails due to missing props"
            !profilePic.save()

        when: "save with all props"
            user = new UserAccount(username: "someuser", password: "somePassword")
            institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL")
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                institution: institution, user: user).save()
            profilePic = new ProfilePic(fileContents: contents, fileExtension: "jpg", profile: profile)

        then: "save works"
            profilePic.save()

        when: "save with all props - contents up to max size"
            contents = new byte[(10 * 1024 * 1024)]
            new Random().nextBytes(contents)
            profilePic = new ProfilePic(fileContents: contents, fileExtension: "jpg", profile: profile)

        then: "save works"
            profilePic.save()

        when: "save with all props, but bad contents - too large"
            contents = new byte[(10 * 1024 * 1024) + 1]
            new Random().nextBytes(contents)
            profilePic = new ProfilePic(fileContents: contents, fileExtension: "jpg", profile: profile)

        then: "save fails, image too large"
            !profilePic.save()
    }
}
