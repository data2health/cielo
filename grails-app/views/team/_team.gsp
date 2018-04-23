<div class="card">
    <div class="card-header" id="headingOne" style="display: inline-flex; white-space: pre-wrap;">
        <h5 class="mb-0">
            <button class="btn btn-link" data-toggle="collapse" style="padding: 0;" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                ${team.name}&nbsp;
            </button>
        </h5>
        <g:userOwnsProject project="${project}">
            <div class="container-fluid" style="background-color: white; border-radius: 10px;
                border: 1px solid #dcdcdc; padding-top: 0.3em; padding-bottom: 0.3em;
                margin-top: 1.5em;display: inline-table;">
                <button type="button" class="btn btn-primary" style="padding: 10px;" onclick="editTeamMembers(${team.id}, ${project.id});">
                    <i class="far fa-edit"></i>
                    <span style="font-size: 0.75em; cursor: default;">&nbsp;Edit Team</span>
                </button>

                <button type="button" class="btn btn-red" style="padding: 10px;" onclick="deleteTeam(${team.id}, ${project.id});">
                    <i class="fa fa-trash-alt"></i>
                    <span style="font-size: 0.75em; cursor: default;">&nbsp;Delete Team</span>
                </button>
            </div>
        </g:userOwnsProject>
    </div>

    <div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion" style="background-image: url('${assetPath(src: "connections.svg")}');
        background-size: cover;padding-bottom: 5em;">
        <div class="team-deck" style="margin-top: 3em;">
            <div class="team-member" style="text-align: center;">
                <div class="card-title" style="display: none; font-weight: 600;margin-bottom: -0.5em;z-index: 1;position: relative;">
                    <i class="fas fa-chess-queen fa-2x" style="color: goldenrod;"></i>
                </div>
                <div style="display: inline-block;"><g:getUserProfilePic user="${team.administrator}" showLink="${true}" sticker="${true}" imageSize="x-large"/></div>
                <div class="card-footer team-users">
                    <span style="color: goldenrod;">Team Admin</span><br>
                    <span style="font-weight: 600;">${team.administrator.fullName}</span><br>
                    <span style="font-style: italic;font-weight: 100;">${team.administrator.username}</span>
                </div>
            </div>
            <g:each in="${team.members}" var="member">
                <div class="team-member" style="text-align: center;">
                    <div class="card-title" style="display: none;"></div>
                    <div style="display: inline-block;"><g:getUserProfilePic user="${member}" showLink="${true}" sticker="${true}" imageSize="x-large"/></div>
                    <div class="card-footer team-users" style="display: none;">
                        <span style="font-weight: 600;">${member.fullName}</span><br>
                        <span style="font-weight: 100;font-style: italic;">${member.username}</span>
                    </div>
                </div>
            </g:each>
        </div>
    </div>
</div>

<asset:javascript src="jquery/jquery.min.js"/>
<script type="application/javascript">
    $( function () {
        handleOnHoverForTeamMembers();
    });
</script>