package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class AccessRequestController {

    def springSecurityService
    def accessRequestService

    @Secured('isAuthenticated()')
    def list() {
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        List<AccessRequest> accessRequestList
        int offset  = params.offset ? Integer.valueOf(params.offset) : Constants.DEFAULT_OFFSET
        int max     = params.max ? Integer.valueOf(params.max).intValue() : Constants.DEFAULT_MAX
        int count   = accessRequestService.getPagesCount(user, max)

        if (user) {
            accessRequestList = accessRequestService.getAccessRequests(user, max, offset)
        }

        return [messages: accessRequestList, pages: count, pageOffset: offset]
    }

    @Secured('isAuthenticated()')
    def getTableRows() {
        int offset  = params.offset ? Integer.valueOf(params.offset) : Constants.DEFAULT_OFFSET
        int max     = params.max ? Integer.valueOf(params.max).intValue() : Constants.DEFAULT_MAX
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        int count   = accessRequestService.getPagesCount(user, max)
        List<AccessRequest> accessRequestList

        if (user) {
            accessRequestList = accessRequestService.getAccessRequests(user, max, (offset * max))
        }

        def newRowsHTML = accessRequestService.renderTableRows([requests :accessRequestList])
        render([html: newRowsHTML, pagesCount: count] as JSON)
    }
}
