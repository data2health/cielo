package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class RestController {

    def restService

    @Secured('permitAll')
    def appVersion() {
        render([version: grailsApplication.config.getProperty("info.app.version")] as JSON)
    }

    @Secured('permitAll')
    def getListOfProjects() {
        boolean filterOnSharedOnly  = params.onlyShowPublic ? Boolean.valueOf(params.onlyShowPublic) : false
        String filterTerm           = params.filterText ?: ""
        int max     = params.max    ? Integer.valueOf(params.max)    : Constants.DEFAULT_MAX
        int page    = params.offset ? Integer.valueOf(params.offset) : Constants.DEFAULT_OFFSET
        int offset  = (page * max)
        int pages   = restService.getNumberOfPages(filterOnSharedOnly, filterTerm, max)

        render([totalCount: restService.getTotalNumberOfProjects(filterOnSharedOnly, filterTerm), numberOfPages: pages,
                projects: restService.getProjectData(filterOnSharedOnly, filterTerm, offset, max)] as JSON)
    }
}
