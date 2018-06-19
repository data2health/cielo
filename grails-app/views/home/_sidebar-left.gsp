<div class="col-sm-3 sidebar">
    <div class="col-sm-11">
        <h6 class="text-secondary"><i class="fa fa-link"></i>
            &nbsp;Connections &nbsp;<span><i class="fa fa-edit" onclick="showUserDialog();" style="color: #149dcc;"></i></span>
        </h6>
        <nav>
            <div class="nav nav-tabs" id="connections-nav-tab" role="tablist">
                <a class="nav-item nav-link nav-link-rectangle nav-link-activity active" id="nav-following-tab" data-toggle="tab" href="#nav-following" role="tab" aria-controls="nav-following" aria-selected="true">
                    Following
                </a>
                <a class="nav-item nav-link nav-link-rectangle nav-link-activity" id="nav-followed-tab" data-toggle="tab" href="#nav-followed" role="tab" aria-controls="nav-followed" aria-selected="false">
                    Follow Me
                </a>
            </div>
        </nav>
        <div class="tab-content tab-data-scrollable tab-content-activity" id="connections-nav-tabContent">
            <div class="tab-pane fade show active" id="nav-following" role="tabpanel" aria-labelledby="nav-following-tab">
                <ul class="list-group list-group-flush">
                    <g:if test="${following?.size() > 0}">
                        <g:set var="loopCount" value="${1}"/>
                        <g:each in="${following}" var="userFollowed">
                            <g:if test="${(loopCount % 2) == 0}">
                                <li class="list-group-item even-list-item">
                            </g:if>
                            <g:else>
                                <li class="list-group-item">
                            </g:else>
                                <g:getUserProfilePic imageSize="xs" sticker="${false}" user="${userFollowed}" showLink="${true}">
                                    &nbsp; ${userFollowed.fullName} (${userFollowed.username})
                                </g:getUserProfilePic>
                                </li>
                            <g:set var="loopCount" value="${loopCount+1}"/>
                        </g:each>
                    </g:if>
                    <g:else>
                        <li class="list-group-item">Nobody yet</li>
                    </g:else>
                </ul>
            </div>
            <div class="tab-pane fade" id="nav-followed" role="tabpanel" aria-labelledby="nav-followed-tab">
                <ul class="list-group list-group-flush">
                    <g:if test="${followers?.size() > 0}">
                        <g:set var="loopCount" value="${1}"/>
                        <g:each in="${followers}" var="followingUser">
                            <g:if test="${(loopCount % 2) == 0}">
                                <li class="list-group-item even-list-item">
                            </g:if>
                            <g:else>
                                <li class="list-group-item">
                            </g:else>
                                <g:getUserProfilePic imageSize="xs" sticker="${false}" user="${followingUser}" showLink="${true}">
                                    &nbsp; ${followingUser.fullName} (${followingUser.username})
                                </g:getUserProfilePic>
                                </li>
                            <g:set var="loopCount" value="${loopCount+1}"/>
                        </g:each>
                    </g:if>
                    <g:else>
                        <li class="list-group-item">Nobody yet</li>
                    </g:else>
                </ul>
            </div>
        </div>
    </div>
    <hr style="width: 50%;">
    <div class="col-sm-11">
        <h6 class="text-secondary"><i class="fa fa-users"></i>&nbsp;Teams</h6>
        <nav>
            <div class="nav nav-tabs" id="teams-nav-tab" role="tablist">
                <a class="nav-item nav-link nav-link-rectangle nav-link-activity active" id="nav-manage-tab" data-toggle="tab" href="#nav-manage" role="tab" aria-controls="nav-manage" aria-selected="true">
                    Manage
                </a>
                <a class="nav-item nav-link nav-link-rectangle nav-link-activity" id="nav-contribute-tab" data-toggle="tab" href="#nav-contribute" role="tab" aria-controls="nav-contribute" aria-selected="false">
                    Contribute To
                </a>
            </div>
        </nav>
        <div class="tab-content tab-data-scrollable tab-content-activity" id="teams-nav-tabContent">
            <div class="tab-pane fade show active" id="nav-manage" role="tabpanel" aria-labelledby="nav-following-tab">
                <ul class="list-group list-group-flush">
                    %{--teamsManaged, contributeToTeams--}%
                    <g:if test="${teamsManaged?.size() > 0}">
                        <g:set var="loopCount" value="${1}"/>
                        <g:each in="${teamsManaged}" var="managedTeam">
                            <g:if test="${(loopCount % 2) == 0}">
                                <li class="list-group-item even-list-item">
                            </g:if>
                            <g:else>
                                <li class="list-group-item">
                            </g:else>
                                    <a href="${createLink(controller: "team", action: "view", params: [id: managedTeam.id])}">
                                        ${managedTeam.name}
                                    </a>
                                </li>
                            <g:set var="loopCount" value="${loopCount+1}"/>
                        </g:each>
                    </g:if>
                    <g:else>
                        <li class="list-group-item">No team yet</li>
                    </g:else>
                </ul>
            </div>
            <div class="tab-pane fade" id="nav-contribute" role="tabpanel" aria-labelledby="nav-followed-tab">
                <ul class="list-group list-group-flush">
                    <g:if test="${contributeToTeams?.size() > 0}">
                        <g:set var="loopCount" value="${1}"/>
                        <g:each in="${contributeToTeams}" var="teamContributingTo">
                            <g:if test="${(loopCount % 2) == 0}">
                                <li class="list-group-item even-list-item">
                            </g:if>
                            <g:else>
                                <li class="list-group-item">
                            </g:else>
                                    <a href="${createLink(controller: "team", action: "view", params: [id: teamContributingTo.id])}">
                                        ${teamContributingTo.name}
                                    </a>
                                </li>
                            <g:set var="loopCount" value="${loopCount+1}"/>
                        </g:each>
                    </g:if>
                    <g:else>
                        <li class="list-group-item">No team yet</li>
                    </g:else>
                </ul>
            </div>
        </div>
    </div>
</div>