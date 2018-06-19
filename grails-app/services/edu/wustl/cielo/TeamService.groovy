package edu.wustl.cielo

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.springframework.validation.ObjectError
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy

@Transactional
@Slf4j
class TeamService {

    def groovyPageRenderer
    TransactionAwareDataSourceProxy dataSource

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
     * Delete a team
     *
     * @param teamId the id of the team to delete
     *
     * @return true if successful, false otherwise
     */
    boolean deleteTeam(Long teamId) {
        Team team = Team.findById(teamId)

        if (team) {

            try {
                team.delete(flush: true)
            } catch (Exception e) {
                log.error(e.toString())
                return false
            }
            return true
        }
        return false
    }

    /**
     * return the full list of teams
     *
     * @param pageOffset the page you want to look at
     * @param max the number of items per page
     *
     * @return a list of teams
     */
    List<Team> listTeams(int pageOffset, int max) {
        return Team.findAll([max: max, offset: (pageOffset * max)])
    }

    /**
     * retrieve list of teams from the database 
     * 
     * @param pageOffset the pageOffset to use
     * @param max the max number of items per page
     * @param filterText the text to filter on
     * 
     * @return list of teams or null
     */
    @ReadOnly
    @Cacheable("filtered_teams")
    List<Team> getFilteredTeamsFromDB(int pageOffset, int max, String filterText) {
        Sql sql = new Sql(dataSource)
        StringBuffer whereClause = new StringBuffer(' WHERE ')
        List params = []

        whereClause << """
            (
                lower(projects.projectsname) like lower(${"'%" + filterText + "%'"}) OR
                lower(team.name) like lower(${"'%" + filterText + "%'"}) OR
                lower(teamMembers.members) like lower(${"'%" + filterText + "%'"}) OR
                lower(admin_profile.adminFullName) like lower(${"'%" + filterText + "%'"})
            )
        """

        String query = """
            SELECT 
                 team.id
                FROM team
                LEFT JOIN (
                 SELECT
                  team.id AS t_id,
                  string_agg(profile.first_name || ' ' || profile.last_name, ', ') AS members
                 FROM team     
                  LEFT JOIN team_user_account AS tuac ON tuac.team_members_id=team.id
                  LEFT JOIN profile ON profile.user_id=tuac.user_account_id
                 GROUP BY team.id
                 ORDER BY team.id
                ) AS teamMembers ON teamMembers.t_id=team.id
                
                LEFT JOIN (
                 SELECT 
                  user_id,
                  string_agg(profile.first_name || ' ' || profile.last_name, ', ') AS adminFullName
                 FROM profile
                 GROUP BY user_id
                ) as admin_profile on admin_profile.user_id=team.administrator_id
                LEFT JOIN (
                 SELECT 
                  project_team.team_id as team_id,
                  string_agg(project.name, ', ') as projectsName
                 FROM project
                 RIGHT JOIN project_team on project_team.project_teams_id=project.id
                 GROUP BY project_team.team_id
                ) as projects on projects.team_id=team.id
                ${whereClause}
                GROUP BY team.id
                ORDER BY team.id ASC
                OFFSET ? LIMIT ?
        """

        params.add(pageOffset)
        params.add(max)

        return sql.rows(query, params).collect { Team.findById(Long.valueOf(it.id)) }
    }

    /**
     * return the total count of teams
     * @param filterTerm the terms to search on
     * @param max the max number of items per page
     *
     * @return the total count of pages or 1
     */
    @ReadOnly
    @Cacheable("filtered_teams_count")
    int countFilteredTeams(String filterText, int max) {
        int numberOfCountedItems
        Sql sql = new Sql(dataSource)
        StringBuffer whereClause = new StringBuffer(' WHERE ')
        List params = []

        whereClause << """
            (
                lower(projects.projectsname) like lower(${"'%" + filterText + "%'"}) OR
                lower(team.name) like lower(${"'%" + filterText + "%'"}) OR
                lower(teamMembers.members) like lower(${"'%" + filterText + "%'"}) OR
                lower(admin_profile.adminFullName) like lower(${"'%" + filterText + "%'"})
            )
        """

        String query = """
            SELECT DISTINCT 
                 team.id
                FROM team
                LEFT JOIN (
                 SELECT
                  team.id AS t_id,
                  string_agg(profile.first_name || ' ' || profile.last_name, ', ') AS members
                 FROM team     
                  LEFT JOIN team_user_account AS tuac ON tuac.team_members_id=team.id
                  LEFT JOIN profile ON profile.user_id=tuac.user_account_id
                 GROUP BY team.id
                 ORDER BY team.id
                ) AS teamMembers ON teamMembers.t_id=team.id
                
                LEFT JOIN (
                 SELECT 
                  user_id,
                  string_agg(profile.first_name || ' ' || profile.last_name, ', ') AS adminFullName
                 FROM profile
                 GROUP BY user_id
                ) as admin_profile on admin_profile.user_id=team.administrator_id
                LEFT JOIN (
                 SELECT 
                  project_team.team_id as team_id,
                  string_agg(project.name, ', ') as projectsName
                 FROM project
                 RIGHT JOIN project_team on project_team.project_teams_id=project.id
                 GROUP BY project_team.team_id
                ) as projects on projects.team_id=team.id
                ${whereClause}
                GROUP BY team.id
                ORDER BY team.id ASC
        """

        numberOfCountedItems = sql.rows(query, params).size()

        if (numberOfCountedItems == 0 || numberOfCountedItems <= max) return 1
        else return Math.ceil(numberOfCountedItems / max).toInteger().intValue()
    }
    /**
     * return the number of possible pages based on max items per page
     *
     * @param max the max number of items per page
     *
     * @return an integer representing the number of pages available
     */
    int getNumberOfTeamPages(int max) {
        int numberOfTeams = Team.count()

        if (numberOfTeams == 0 || numberOfTeams <= max) return 1
        else return Math.ceil(numberOfTeams / max).toInteger().intValue()
    }

    /**
     * Render table rows for teams view
     *
     * @param model the model to use to generate view with
     *
     * @return String representation of the HTML of the new table content
     */
    String renderTableRows(Map model) {
        return groovyPageRenderer.render(template: "/team/teamsTableRows", model: model)
    }

    /**
     * Get the teams a user belongs to
     *
     * @param user the user to search for teams
     *
     * @return list of teams or empty list
     */
    List<Team> getListOfTeamsUserIsMemberOf(UserAccount user) {
        List<Team> teams = []

        Team.list().each { team ->
            if (team.members.contains(user)) teams.add(team)
        }
        return teams
    }

    /**
     * Get list of teams that a user administers
     *
     * @param userAccount the user to find the teams for
     *
     * @return list of teams or null
     */
    List<Team> getListOfTeamsUserOwns(UserAccount userAccount) {
        return Team.findAllByAdministrator(userAccount)
    }

    /**
     * Get a list of names
     * @param teamId
     * @return
     */
    @ReadOnly
    @Cacheable("filtered_teams_project_ids")
    List<Long> getIdsOfProjectsForTeam(Long teamId) {
        List<Long> projectIds = []
        Sql sql = new Sql(dataSource)
        String query = """
                SELECT 
                    project_team.team_id as team_id,
                    string_agg(project.id::character varying, ', ') as projectIds
                FROM project
                RIGHT JOIN project_team on project_team.project_teams_id=project.id
                WHERE team_id=?
                GROUP BY team_id
        """

        GroovyRowResult row = sql.firstRow(query, [teamId])

        if (row?.projectIds) {
            projectIds = row.projectIds.tokenize(',').collect { Long.valueOf(it.trim()) }
        }
        return projectIds
    }
}
