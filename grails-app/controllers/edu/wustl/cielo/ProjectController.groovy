package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class ProjectController {

    ProjectService projectService

    static allowedMethods = [save: "POST"]

    /**
     * Call to service that retrieves the most 'popular' projects
     *
     * @return a list of properties for most popular projects
     */
    def getMostPopularBundles() {
        List<Object> popularBundles

        if (params.max || params.publicOnly) {

            popularBundles = projectService.getMostViewedProjects(Integer.valueOf(params.max),
                    Boolean.valueOf(params.publicOnly))
        }
        render([bundles: popularBundles] as JSON)
    }

    @Secured('isAuthenticated()')
    def view() {
        return [bundle: Project.findById(Long.valueOf(params.id))]
    }
}
