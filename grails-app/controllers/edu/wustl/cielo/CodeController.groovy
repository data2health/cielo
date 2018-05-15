package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured

class CodeController {

    def messageSource

    @Secured('isAuthenticated()')
    def view() {
        Long codeId = params.id ? Long.valueOf(params.id) : -1L
        Code code = Code.findById(codeId)

        if (code) {
            Project project
            Project.list().each {
                if (it.codes.contains(code)) project = it
            }

            if (project) {
                redirect(controller: "project", action: "view",  params: [id: project.id, teams: false, bundles: true])
            } else redirect(url: request.getHeader("referer"))
        } else {
            flash.danger = messageSource.getMessage('code.doesNotExist', null, 'Code no longer exists in the database',
                    request.locale)
            redirect(url: request.getHeader("referer"))
        }
    }
}
