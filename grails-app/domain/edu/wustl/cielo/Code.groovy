package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import com.google.cloud.storage.BlobId
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Code {

    int revision = 0
    String name
    String description
    String repository
    Date dateCreated
    Date lastUpdated
    URL url
    BlobId blobId

    static constraints = {
        name(nullable: false)
        description(nullable: false, maxSize: 255)
        url(nullable: true)
        blobId(nullable: true)
        revision(min: 0, max: Integer.MAX_VALUE)
        repository(blank: false)
    }

    static mapping = {
        description type: 'text'
    }

    String toString() {
        return name + " " + revision
    }
}
