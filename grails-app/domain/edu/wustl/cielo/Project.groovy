package edu.wustl.cielo

import edu.wustl.cielo.enums.ProjectStatusEnum
import edu.wustl.cielo.meta.Metadata
import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Project {

    int views = 0 //should only update when someone other than the projectOwner or team -> members click to view
    boolean shared = false
    String name
    String description
    Date dateCreated
    Date lastUpdated
    Date lastChanged = new Date() //last time the someone made a change to the domain object other than updating count
    SoftwareLicense license
    List<Team> teams = []
    List<Code> codes = []
    List<Data> datas = []
    List<Publication> publications   = []
    List<Annotation> annotations     = []
    List<Comment> comments           = []
    List<Metadata> metadatas         = []
    ProjectStatusEnum status = ProjectStatusEnum.IN_PROGRESS

    static hasMany = [annotations: Annotation, teams: Team, codes: Code, datas: Data, publications: Publication,
    comments: Comment, metadatas: Metadata]

    static belongsTo = [projectOwner: UserAccount]

    static mapping = {
        dynamicUpdate true
        codes cascade: 'all-delete-orphan'
        datas cascade: 'all-delete-orphan'
        publications cascade: 'all-delete-orphan'
        comments cascade: 'all-delete-orphan'
        metadatas cascade: 'all-delete-orphan'
    }

    static constraints = {
        license(nullable: false)
        name(nullable: false)
        projectOwner(nullable: false)
        description(nullable: false, blank: false, maxSize: 255)
        lastChanged(nullable: true)
    }

    List<Team> getTeams() {
        return teams
    }

    boolean isTeamAssignedToProject(Team team) {
        return teams.find { it == team } ? true : false
    }
}