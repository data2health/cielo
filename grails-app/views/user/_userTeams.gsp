<section class="features1" id="user_teams" style="background-color: white;">
    <div class="container-fluid" style="text-align: center;padding-bottom: 4em;">
        <div class="row" style="display: inline-block; padding-top: 4em;">
            <span style="z-index: 1;font-family: inherit; font-size: 2em; color: black;padding-bottom: 2em; margin-top:-2em;">
                Teams
            </span>
        </div>
    </div>

    <g:set var="itemsPerColumn" value="${5}"/>
    <g:set var="size" value="${teams.size()}"/>
    <g:set var="index" value="${0}"/>
    <g:set var="contribtuteSize" value="${contributingTeams.size()}"/>
    <g:set var="contributeIndex" value="${0}"/>

    <g:if test="${(size == index) && (contribtuteSize == contributeIndex)}">
        <div class="col-md-12" style="text-align: center; padding-bottom: 3em;">
            <em style="font-weight: 600;">
                None Yet
            </em>
        </div>
    </g:if>
    <g:else>
        <div class="container-fluid" style="padding-bottom: 4em; padding-left: 5em;">
            <div class="row">
                <g:set var="index" value="${0}"/>
                <g:each in="${teams}" var="team">
                    <g:if test="${index == 0}">
                        <div class="col-sm-auto" style="padding-top: 1em;">
                    </g:if>
                    <g:else>
                        <g:if test="${index == itemsPerColumn}">
                            <g:set var="index" value="${0}"/>
                            </div>
                            <div class="col-sm-auto">
                                &nbsp;
                            </div>
                            <div class="col-sm-auto" style="padding-top: 1em;">
                        </g:if>
                    </g:else>
                    <span style="color: #3a3a3a;">
                        <a class="btn btn-link project-link-dark" href="${createLink(controller: "team", action: "view", id: team.id)}">
                            ${team?.name}
                        </a>
                        (Owner)
                    </span>
                    <br>
                <g:set var="index" value="${index+1}"/>
                </g:each>

                <g:each in="${contributingTeams}" var="team">
                    <g:if test="${index == 0}">
                        <div class="col-sm-auto" style="padding-top: 1em;">
                    </g:if>
                    <g:else>
                        <g:if test="${index == itemsPerColumn}">
                            <g:set var="index" value="${0}"/>
                            </div>
                            <div class="col-sm-auto">
                                &nbsp;
                            </div>
                            <div class="col-sm-auto" style="padding-top: 1em;">
                        </g:if>
                    </g:else>
                    <span style="color: #3a3a3a;">
                        <a class="btn btn-link project-link-dark" href="${createLink(controller: "team", action: "view", id: team.id)}">${team?.name}</a> (Contributor)
                    </span>
                    <br>
                    <g:set var="index" value="${index+1}"/>
                </g:each>

                <g:if test="${index != itemsPerColumn && index != 0}">
                    </div>
                </g:if>
            </div>
        </div>
    </g:else>

</section>