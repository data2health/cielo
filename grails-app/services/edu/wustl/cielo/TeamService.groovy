package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.validation.ObjectError

@Transactional
@Slf4j
class TeamService {

    /**
     * Add mock teams to the db for dev
     */
    def bootstrapTeams(int numberOfTeamsPerUsers, int numberOfMembers) {

        log.info("****************************")
        log.info("Creating ${numberOfTeamsPerUsers} teams...")
        log.info('****************************\n')

        List<UserAccount> users = UserAccount.list()
        users?.each { UserAccount user ->
            if (Team.countByAdministrator(user) == 0) {
                numberOfTeamsPerUsers.times {
                    Team team = new Team(name: user.username + '-group-' + it, administrator: user)
                    List<UserAccount> members = (users - user).take(numberOfMembers)
                    team.members.addAll(members)

                    if (!team.save()){
                        team.getErrors().allErrors.each { ObjectError err ->
                            log.error(err.toString())
                        }
                        log.error("There was an error attempting to save Team ${user.username + '-group-' + it}")
                        System.exit(-1)
                    }
                    log.info("\tSaved team: ${team.name}")
                }
            }
        }
        log.info("\n")
    }

    /**
     * Get list of teams managed by a given user
     *
     * @param user the user who is the admin of the team
     *
     * @return set of teams
     */
    Set<Team> getManagedTeams(UserAccount user) {
        return Team.findAllByAdministrator(user)
    }

    /**
     * Get list of teams a given user contributes to
     *
     * @param user the contributor
     *
     * @return list of teams
     */
    Set<Team> getTeamsContributedTo(UserAccount user) {
        Set<Team> teamsUserContributesTo = []

        Team.list().each { Team team ->
            if (team.members.contains(user)) teamsUserContributesTo.add(team)
        }
        return teamsUserContributesTo
    }

    /**
     * Get list of members of a team
     *
     * @param teamId the team to get the members from
     *
     * @return list of members
     */
    ArrayList<UserAccount> getTeamMembers(Long teamId) {
        return Team.findById(teamId).members
    }

    /**
     * Update the members of a team
     *
     * @param userAccount the user initiating the change
     * @param teamId the team that we are modifying the members for
     * @param userIds the members to be associated to the team
     *
     * @return true if successful, false otherwise
     */
    boolean updateTeamMembers(UserAccount userAccount, Long teamId, List<Long> userIds) {
        Team team = Team.findById(teamId)

        if (team) {
            if (team.administrator.equals(userAccount)) {
                team.members.clear()
                userIds.each { Long id ->
                    UserAccount member = UserAccount.findById(id)
                    if (member) {
                        team.members.add(member)
                    }
                }
                if (!team.save()) {
                    team.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    return false
                }
                return true
            }
        }
    }

    /**
     * Delete a team that is associated to a given project
     *
     * @param teamId the id of the team to delete
     * @param projectId the project the team is associated with
     *
     * @return true if successful, false otherwise
     */
    boolean deleteTeam(Long teamId, Long projectId) {
        Project project = Project.findById(projectId)
        Team team       = Team.findById(teamId)

        if (project && team) {
            project.removeFromTeams(team)

            if (!project.save()) {
                project.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
            team.delete()
            return true
        }
    }
}
