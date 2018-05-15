package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured

class DataController {

    def messageSource

    @Secured('isAuthenticated()')
    def view() {
        Long dataId = params.id ? Long.valueOf(params.id) : -1L
        Data data = Data.findById(dataId)

        if (data) {
            Project project
            Project.list().each {
                if (it.datas.contains(data)) project = it
            }

            if (project) {
                redirect(controller: "project", action: "view",  params: [id: project.id, teams: false, bundles: true])
            } else redirect(url: request.getHeader("referer"))
        } else {
            flash.danger = messageSource.getMessage('data.doesNotExist', null, 'Data no longer exists in the database',
                    request.locale)
            redirect(url: request.getHeader("referer"))
        }
    }
}
