package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ProfileSpec extends Specification implements DomainUnitTest<Profile> {

    void "test saving"() {
        Profile profile
        Institution institution
        UserAccount user

        when: "save with no props"
            profile = new Profile()

        then:
            !profile.save()

        when: "save with first and last name"
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez")

        then: "fails due to missing props"
            !profile.save()

        when: "save with firstname, lastname and email address"
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu")

        then: "fails due to missing props"
            !profile.save()

        when: "save with firstname, lastname, email address and institution"
            institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL").save()
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                    institution: institution)

        then: "fails due to missing props"
            !profile.save()

        when: "save with firstname, lastname, email address, institution and user"
            user = new UserAccount(username: "someuser", password: "somePassword")
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "r",
                    institution: institution, user: user)

        then: "doesn't save due to bad email address"
            !profile.save()

        when: "save with firstname, lastname, email address, institution and user"
            user = new UserAccount(username: "someuser", password: "somePassword")
            profile = new Profile(firstName: "Ricky", lastName: "RodriguezRodrigue", emailAddress: "rrodriguez@wustl.edu",
                    institution: institution, user: user)

        then: "fails...last name too long"
            !profile.save()

        when: "save with firstname, lastname, email address, institution and user"
            user = new UserAccount(username: "someuser", password: "somePassword")
            profile = new Profile(firstName: "RickyRickyRickyRicky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                    institution: institution, user: user)

        then: "fails...first name too long"
            !profile.save()

        when: "save with firstname, lastname, email address, institution and user"
            user = new UserAccount(username: "someuser", password: "somePassword")
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "ricardo_e_rodriguez@email.wustl.edu",
                    institution: institution, user: user)

        then: "fails...email too long"
            !profile.save()

        when: "save with firstname, lastname, email address, institution and user"
            user = new UserAccount(username: "someuser", password: "somePassword")
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "ricardo_e_rodriguez@email.wustl",
                    institution: institution, user: user)

        then: "fails...email bad format"
            !profile.save()

        when: "save with firstname, lastname, email address, institution and user"
            user = new UserAccount(username: "someuser", password: "somePassword")
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                    institution: institution, user: user)

        then: "saves correctly"
            profile.save()

    }

    void "test toString"() {

        UserAccount user
        Profile profile
        Institution institution

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL").save()
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                    institution: institution, user: user).save()

        when: "toString is called"
            def returnVal = profile.toString()

        then:
            assert returnVal == "Profile for ${user.username}"
    }
}
