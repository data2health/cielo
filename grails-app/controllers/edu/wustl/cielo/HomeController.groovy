package edu.wustl.cielo

import groovy.util.logging.Slf4j
import grails.plugin.springsecurity.annotation.Secured

@Slf4j
class HomeController {
    def springSecurityService
    def projectService
    def userAccountService
    def activityService
    def teamService

    @Secured('isAuthenticated()')
    def home() {
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (!user) {
            log.error("User is not logged in. Redirecting to login page.")
            redirect controller: 'login', action: 'auth'
            return
        }

        return [username: user.username, profile: user.profile,
                teamsManaged: teamService.getManagedTeams(user),
                contributeToTeams: teamService.getTeamsContributedTo(user),
                followers: user.followers,
                following: user.getConnections(),
                activities: activityService.getActivities(),
                mostPopularProjects: projectService.getMostViewedProjects(3, true),
                activityOffset: Constants.DEFAULT_MAX, activityMax: Constants.DEFAULT_MAX,
                showMoreActivitiesButton: activityService.areThereMoreActivitiesToRetrieve(10, 10)
        ]
    }

    /**
     * Landing page which contains popular projects
     *
     * @return
     */
    @Secured('permitAll')
    def index() {
        if (springSecurityService.isLoggedIn()) {
            redirect(action: 'home')
        }
        else {
            return [bundles: projectService.getMostViewedProjects(3, true)]
        }
    }

    @Secured('isAuthenticated()')
    def sidebarLeft() {
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (user) {
            render(template: "sidebar-left",
                    model: [followers: userAccountService.getUsersFollowingMe(user),
                            following: userAccountService.getUsersFollowing(user),
                            teamsManaged: teamService.getManagedTeams(user),
                            contributeToTeams: teamService.getTeamsContributedTo(user)
                    ]
            )
        }
    }
}
