package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class CommentIntegrationSpec extends Specification {

    void cleanup() {

        Comment.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }
    }

    void "test saving"() {
        Comment comment

        expect:"no comments"
            Comment.list() == []

        when: "creating a comment with no props"
            comment = new Comment().save()

        then: "fails to save"
            !comment

        when: "creating comment with just text"
            comment = new Comment(text: "some comment").save()

        then: "fails due to missing required prop"
            !comment

        when: "saving comment with all props"
            UserAccount user = new UserAccount(username: "ricky", password: "test").save()
            comment = new Comment(text: "some comment", commenter: user).save()

        then: "save is successful"
            comment
    }
}
