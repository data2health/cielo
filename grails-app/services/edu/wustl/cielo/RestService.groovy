package edu.wustl.cielo

import grails.gorm.transactions.Transactional

@Transactional
class RestService {

    def projectService

    /**
     * Get a list of project data for all projects in the system
     *
     * @param filterOnSharedOnly whether we should only show results for public projects
     * @param filterTerm the search term we want to filter the list on
     * @param offset the page offset
     * @param max the max number of results
     *
     * @return a list of objects that each contain a subset of project properties
     */
    List<Object> getProjectData(boolean filterOnSharedOnly, String filterTerm, int offset, int max) {
        List<Object> projectsObject = []

        projectService.retrieveFilteredProjectsFromDB(null, filterOnSharedOnly, filterTerm, offset, max).collect { Project project ->
            Map object = [:]

            object.identity     = project.id
            object.name         = project.name
            object.owner        = project.projectOwner.fullName
            object.description  = project.description
            object.created      = project.dateCreated
            object.lastUpdated  = project.lastChanged

            projectsObject.add(object)
        }
        return projectsObject
    }

    /**
     * Count the number of pages available given the search term and max number of results per page
     *
     * @param filterOnSharedOnly should we only show public projects
     * @param filterTerm the term to filter on
     * @param max the max number of results per page
     *
     * @return an integer representing the number of pages for the given criteria; minimum is 1 regardless
     */
    int getNumberOfPages(boolean filterOnSharedOnly, String filterTerm, int max) {
        return projectService.countFilteredProjectsPages(null, filterOnSharedOnly, filterTerm, max)
    }

    /**
     * Get the total count of objects for a given request
     *
     * @param filterOnSharedOnly whether we should filter on public projects
     * @param filterTerm the search term or null/empty string
     *
     * @return an integer representing the toal count of objects
     */
    int getTotalNumberOfProjects(boolean filterOnSharedOnly, String filterTerm) {
        return projectService.countFilteredProjects(filterOnSharedOnly, filterTerm)
    }
}
