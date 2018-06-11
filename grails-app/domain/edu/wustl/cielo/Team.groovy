package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Team {

    String name
    UserAccount administrator
    List<UserAccount> members = []
    Date dateCreated
    Date lastUpdated

    static hasMany      = [members: UserAccount]

    static constraints = {
        name(nullable: false, size: 1..30, unique: true)
        administrator(nullable: false)
    }

    String toString() {
        return name
    }

    List<Project> listAllProjectsThatHaveThisTeamAssigned() {
        List<Project> matches = []

        Project.list().each { project ->
            if (project.teams.size() > 0) {
                if (project.isTeamAssignedToProject(this)) {
                    matches.add(project)
                }
            }
        }
        return matches
    }
}