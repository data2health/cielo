package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class LicenseController {

    def messageSource

    @Secured('isAuthenticated()')
    def getLicenseBody() {
        Long licenseId = Long.valueOf(params.id)
        SoftwareLicense license = SoftwareLicense.findById(licenseId)

        render ( [licenseText: license?.body] as JSON )
    }

    @Secured('permitAll')
    def getTermsOfUse() {
        render(template: "/templates/termsOfUse")
    }
}
