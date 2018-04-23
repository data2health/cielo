package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.JSON

class TeamController {
    def teamService
    def springSecurityService

    @Secured('isAuthenticated()')
    def getTeamMembers() {
        ArrayList<UserAccount> users
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.id) {
            Long teamId = Long.valueOf(params.id)
            users = teamService.getTeamMembers(teamId)
        }
        render(template: "/templates/addUsersDialogContent", model: [follow: users, users: UserAccount.list() - user])
    }

    @Secured('isAuthenticated()')
    def updateTeamUsers() {
        boolean succeeded
        Long teamId = params.id ? Long.valueOf(params.id) : -1L
        List<Long> userIds = []
        UserAccount user = springSecurityService.principal ? UserAccount.get(springSecurityService.principal.id) : null

        if (user) {
            if (teamId && params."users[]") {
                params."users[]".each { userId ->
                    userIds.add(Long.valueOf(userId))
                }
                succeeded = teamService.updateTeamMembers(user, teamId, userIds)
            }
        }
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def teamMembersSnippet() {
        Long teamId    = params.teamId      ? Long.valueOf(params.teamId)       : -1L
        Long projectId = params.projectId   ? Long.valueOf(params.projectId)    : -1L

        if (teamId && projectId) {
            render(template: "team", model: [team: Team.findById(teamId), project: Project.findById(projectId)])
        }
    }

    @Secured('isAuthenticated()')
    def deleteTeam() {
        boolean succeeded
        Long teamId    = params.teamId      ? Long.valueOf(params.teamId)       : -1L
        Long projectId = params.projectId   ? Long.valueOf(params.projectId)    : -1L

        if (teamId && projectId) {
            succeeded = teamService.deleteTeam(teamId, projectId)
        }

        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def newTeamForm() {
        UserAccount user = springSecurityService.principal ? UserAccount.get(springSecurityService.principal.id) : null

        if (user) {
            render(template: "newTeam", model: [users: UserAccount.list() - user])
        }
    }
}
