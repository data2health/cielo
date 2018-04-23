<g:userOwnsProject project="${project}">
    <div id="addTeamToolbar" class="col-md-12 edit-toolbar" style="padding-top: 0.5em;margin-top: -2em;">
        <button type="button" class="btn btn-primary" style="padding: 10px;margin: 0;" onclick="addTeam(${project.id});">
            <i class="fa fa-plus"></i>
            <span style="font-size: 0.75em; cursor: default;">&nbsp;Add Team</span>
        </button>
    </div>
</g:userOwnsProject>

<g:if test="${project.teams}">
    <g:each in="${project.teams}" var="team">
        <div id="accordion_${team.id}">
            <div id="team_${team.id}">
                <g:render template="/team/team" model="[project: project, team: team]"/>
            </div>
        </div>
    </g:each>
</g:if>
<g:else>
    <div class="jumbotron">
        <div class="container">
            <h1 class="display-6">No Teams</h1>
            <p class="lead">There are no teams assigned to this project yet.</p>
        </div>
    </div>
</g:else>