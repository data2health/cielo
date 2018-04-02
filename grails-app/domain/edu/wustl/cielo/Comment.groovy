package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Comment implements Comparable {
    String text
    UserAccount commenter
    SortedSet<Comment> responses = new TreeSet<Comment>()
    Date dateCreated
    Date lastUpdated

    static hasMany = [responses: Comment]

    static constraints = {
        text(nullable: false)
        commenter(nullable: false)
    }

    static mapping = {
        text type: 'text'
    }

    int compareTo(Object comment) {
         ((Comment)comment)?.dateCreated <=> dateCreated
    }

    void addToResponses(Comment comment) {
        responses.add(comment)
    }

    void removeFromResponses(Comment comment) {
        responses.remove(comment)
    }
}
