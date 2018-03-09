package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import java.util.Random

class ProfilePicSpec extends Specification implements DomainUnitTest<ProfilePic> {

    def webRoot

    def setup() {
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/grails-app/assets/images/"
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
            contents = new File(webRoot + "CIELOhometilev5.jpg").bytes
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

    void "test toString"() {
        Profile profile
        ProfilePic profilePic
        Institution institution
        UserAccount user
        byte[] contents

        given:
            contents = new File(webRoot + "CIELOhometilev5.jpg").bytes
            user = new UserAccount(username: "someuser", password: "somePassword")
            institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL")
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                    institution: institution, user: user).save()
            profilePic = new ProfilePic(fileContents: contents, fileExtension: "jpg", profile: profile)

        when: "toString is called"
            def returnVal = profilePic.toString()

        then:
            assert returnVal   == "image for " + profile.toString() + " with id ${profilePic.id} and size ${contents.size()}"
    }
}
