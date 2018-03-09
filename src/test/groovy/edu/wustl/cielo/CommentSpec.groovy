package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class CommentSpec extends Specification implements DomainUnitTest<Comment> {

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
