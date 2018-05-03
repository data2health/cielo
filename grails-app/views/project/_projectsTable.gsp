<div id="myProjectsTableSection">
    <section class="mbr-fullscreen" style="background-color: #f1f1f1;">
        <div class="container-fluid">
            <div class="row justify-content-md-start">
                <div class="col-lg-auto myprojects-section" style="padding-top: 2em;">
                    <div class="card table-card myprojects-card">
                        <div class="card-body" style="padding: 0; background-color: white;">
                            <h6 class="card-header">
                                <i class="fas fa-cube"></i>&nbsp;Projects&nbsp;
                                <g:if test="${usersProject}">
                                    <button type="button" onclick="showNewProjectWizard()" class="btn btn-primary"
                                            style="margin: 0; padding: 3px;">
                                        <i class="fas fa-plus"></i>
                                    </button>
                                </g:if>
                            </h6>
                            <table class="table table-hover table-responsive" style="min-height: 300px; max-width: 98vw;
                            background-color: white;">
                                <thead class="thead-light">
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">Name</th>
                                    <th scope="col">Description</th>
                                    <th scope="col">License</th>
                                    <th scope="col">Visibility</th>
                                    <th scope="col">Teams</th>
                                    <th scope="col">Date Created</th>
                                    <th scope="col">Last Updated</th>
                                    <g:if test="${usersProject}">
                                        <th scope="col"></th>
                                    </g:if>
                                </tr>
                                </thead>
                                <tbody>
                                <g:set var="count" value="${1}"/>
                                <g:each in="${projects}" var="project">
                                    <tr>
                                        <th scope="row">${count}</th>
                                        <td><a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "project", action: "view", id: project.id)}">${project.name}</a></td>
                                        <td data-toggle="tooltip" title="${project.description}">
                                            <g:if test="${project.description.size() > 20}">
                                                ${project.description.substring(0,20)}...
                                            </g:if>
                                            <g:else>
                                                ${project.description}
                                            </g:else>
                                        </td>
                                        <td>
                                            <button id="softwareLicenseButton" onclick="showSoftwareLicense(${project.license.id}, '${project.license.label}')"
                                                    class="btn btn-link" style="padding: 0; margin: 0; margin-left: -2px;">
                                                ${project.license.label}
                                            </button>
                                        </td>
                                        <td>
                                            <g:if test="${project.shared}">
                                                <i id="sharedIcon" class="fas fa-lock-open"></i>
                                            </g:if>
                                            <g:else>
                                                <i id="sharedIcon" class="fas fa-lock"></i>
                                            </g:else>
                                            <g:projectVisibility value="${project.shared}"/>
                                        </td>
                                        <td>
                                            <g:if test="${project.teams}">
                                                <g:set var="numberOfTeams" value="${project.teams?.size()}"/>
                                                <g:set var="index" value="${1}"/>
                                                <g:each in="${project.teams}" var="team">
                                                    <g:if test="${index == 1}">
                                                        <a href="${createLink(controller: "project", action: "view", id: project.id, params: ['teams': true])}" class="btn btn-link" style="padding-top:0; padding-left: 0; margin: 0;">${team.name}</a>
                                                    </g:if>
                                                    <g:else>
                                                        ,&nbsp;<a href="${createLink(controller: "project", action: "view", id: project.id, params: ['teams': true])}" class="btn btn-link" style="padding-top:0; padding-left: 0; margin: 0;">${team.name}</a>
                                                    </g:else>
                                                    <g:set var="index" value="${index+1}"/>
                                                </g:each>
                                            </g:if>
                                            <g:else>
                                                <em>No teams</em>
                                            </g:else>
                                        </td>
                                        <td>
                                            <g:dateDiff date="${project.dateCreated}"/>
                                        </td>
                                        <td>
                                            <g:dateDiff date="${project.lastChanged?: project.lastUpdated}"/>
                                        </td>
                                        <g:if test="${usersProject}">
                                            <td style="text-align: right;"><i class="fas fa-trash-alt" onclick="deleteProject(${project.id}, '${project.name}');"></i></td>
                                        </g:if>
                                    </tr>
                                    <g:set var="count" value="${count+1}"/>
                                </g:each>
                                </tbody>
                            </table>
                            <g:render template="/templates/paginationToolbar" model="[pages: numberOfPages, offset: offset,
                                                                                      onChangeCallback: onChangeCallback]"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>