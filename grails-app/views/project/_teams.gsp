<g:if test="${project.teams}">
    <div id="accordion" style="max-width: 90%">
        <g:each in="${project.teams}" var="team">
            <div class="card">
                <div class="card-header" id="headingOne">
                    <h5 class="mb-0">
                        <button class="btn btn-link" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                            ${team.name}
                        </button>
                    </h5>
                </div>

                <div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion">
                    <div class="team-deck" style="margin-top: 3em;">
                        <div class="team-member" style="text-align: center;">
                            <div class="card-title" style="display: none; font-weight: 600;margin-bottom: -0.5em;z-index: 1;position: relative;">
                                <i class="fas fa-chess-queen fa-2x" style="color: goldenrod;"></i>
                            </div>
                            <div style="display: inline-block;"><g:getUserProfilePic user="${team.administrator}" showLink="${true}" sticker="${true}" imageSize="x-large"/></div>
                            <div class="card-footer" style="display: none;">
                                <span style="color: goldenrod;">Team Admin</span><br>
                                <span style="font-weight: 600;">${team.administrator.fullName}</span><br>
                                <span style="font-style: italic;font-weight: 100;">${team.administrator.username}</span>
                            </div>
                        </div>
                        <g:each in="${team.members}" var="member">
                            <div class="team-member" style="text-align: center;">
                                <div class="card-title" style="display: none;"></div>
                                <div style="display: inline-block;"><g:getUserProfilePic user="${member}" showLink="${true}" sticker="${true}" imageSize="x-large"/></div>
                                <div class="card-footer" style="display: none;">
                                    <span style="font-weight: 600;">${member.fullName}</span><br>
                                    <span style="font-weight: 100;font-style: italic;">${member.username}</span>
                                </div>
                            </div>
                        </g:each>
                    </div>
                </div>
            </div>
        </g:each>
    </div>
</g:if>
<g:else>
    <div class="jumbotron" style="margin-right: 3em;">
        <div class="container">
            <h1 class="display-6">No Teams</h1>
            <p class="lead">There are no teams assigned to this project yet.</p>
        </div>
    </div>
</g:else>