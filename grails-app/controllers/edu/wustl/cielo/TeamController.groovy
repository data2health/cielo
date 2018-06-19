package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.JSON

class TeamController {
    def teamService
    def springSecurityService
    def messageSource
    def projectService

    @Secured('isAuthenticated()')
    def getTeamMembers() {
        ArrayList<UserAccount> users
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.id) {
            Long teamId = Long.valueOf(params.id)
            users       = teamService.getTeamMembers(teamId)
        }
        render(template: "/templates/addUsersDialogContent", model: [follow: users, users: UserAccount.list() - user])
    }

    @Secured('isAuthenticated()')
    def updateTeamUsers() {
        Map messages = [:]
        boolean succeeded
        Long teamId         = params.id ? Long.valueOf(params.id) : -1L
        List<Long> userIds  = []
        UserAccount user    = springSecurityService.principal ? UserAccount.get(springSecurityService.principal.id) : null

        if (user) {
            if (teamId) {
                if (params."users[]"){
                    if (params."users[]".class.simpleName == "String") userIds.add(Long.valueOf(params."users[]"))
                    else {
                        params."users[]".each { userId ->
                            userIds.add(Long.valueOf(userId))
                        }
                    }
                }
                succeeded = teamService.updateTeamMembers(user, teamId, userIds)
            }
        }
        if (succeeded) messages.success = messageSource.getMessage('team.update.successful', null, 'Team has been updated',
                request.locale)
        else messages.danger = messageSource.getMessage('team.update.failure', null, 'Unable to update team',
                request.locale)
        render([success: succeeded, messages: messages] as JSON)
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
    def userTeamMembersSnippet() {
        Long teamId    = params.teamId      ? Long.valueOf(params.teamId)       : -1L

        if (teamId != -1L) {
            render(template: "userTeam", model: [team: Team.findById(teamId)])
        }
    }

    @Secured('isAuthenticated()')
    def deleteTeam() {
        boolean succeeded
        Map messages    = [:]
        Long teamId     = params.teamId      ? Long.valueOf(params.teamId)       : -1L

        if (teamId) {
            succeeded = teamService.deleteTeam(teamId)
        }

        if (!succeeded) messages.danger = messageSource.getMessage("team.delete.failed", null, Locale.getDefault())
        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def newTeamForm() {
        UserAccount user = springSecurityService.principal ? UserAccount.get(springSecurityService.principal.id) : null

        if (user) {
            render(template: "newTeam", model: [users: UserAccount.list() - user, teams: Team.list()])
        }
    }

    @Secured('isAuthenticated()')
    def view() {
        Long teamId         = params.id ? Long.valueOf(params.id) : -1L
        Team team           = Team.findById(teamId)
        Object principal    = springSecurityService?.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null

        if (team) {
            return [team: team,
                    projectsContributeTo: projectService.getListOfProjectsTeamContributesTo(team),
                    userProfile: user.profile]
        } else {
            flash.danger = messageSource.getMessage('team.doesNotExist', null, 'Team has been deleted',
                    request.locale)
            redirect(url: request.getHeader("referer"))
        }
    }

    @Secured('isAuthenticated()')
    def teams() {
        Object principal    = springSecurityService?.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        int max             = params.max ?      Integer.valueOf(params.max).intValue()      : Constants.DEFAULT_MAX
        int pageOffset      = params.offset ?   Integer.valueOf(params.offset).intValue()   : Constants.DEFAULT_OFFSET
        String filterText   = params.filterTerm ?: ""

        return [teams: teamService.getFilteredTeamsFromDB((pageOffset * max), max, filterText),
                offset: (pageOffset * max),
                numberOfPages: teamService.countFilteredTeams(filterText, max),
                max: max,
                userProfile: user.profile]
    }

    @Secured('isAuthenticated()')
    def teamTableRows() {
        int max             = params.max ?      Integer.valueOf(params.max).intValue()      : Constants.DEFAULT_MAX
        int pageOffset      = params.offset ?   Integer.valueOf(params.offset).intValue()   : Constants.DEFAULT_OFFSET
        String filterText   = params.filterTerm ? params.filterTerm : ""
        int numberOfPages   = teamService.countFilteredTeams(filterText, max)
        List<Team> teams    = teamService.getFilteredTeamsFromDB((pageOffset * max), max, filterText)

        def newRowsHTML = teamService.renderTableRows([teams: teams])
        render([html: newRowsHTML, pagesCount: numberOfPages] as JSON)
    }
}
