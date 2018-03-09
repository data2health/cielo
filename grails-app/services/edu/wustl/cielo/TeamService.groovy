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
}
