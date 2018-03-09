package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Publication {

    String label
    String url
    String issn //international standard serial number
    String isbn //international standard book number
    String nbn  //national biomedical id
    String doi  //digial object id
    String sici //serial item and contribution id
    String pmid //pubmed id
    String oai //open archives initiative id
    Date dateCreated
    Date lastUpdated

    static constraints = {
        url(nullable: true, blank: false)
        label(nullable: false, blank: false, size: 4..256)
        issn(nullable: true, blank: false)
        isbn(nullable: true, blank: false)
        nbn(nullable: true, blank: false)
        doi(nullable: true, blank: false)
        sici(nullable: true, blank: false)
        pmid(nullable: true, blank: false)
        oai(nullable: true, blank: false)
    }

    String toString() {
        return "${label} (${url})"
    }
}
