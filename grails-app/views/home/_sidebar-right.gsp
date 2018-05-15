<div id="announcements" class="col-md-3 d-none d-md-block sidebar-announcements">
    <div class="col-sm-3 sidebar-right">
        <div class="col-sm-10">
            <div class="card" style="border: 1px solid #e4e4e4; height: calc(100vh - 150px); overflow: scroll; position: fixed;
            width: 21%;">
              <div class="card-header text-secondary">
                Featured Projects
              </div>
                <g:each in="${projects}" var="project">
                    <div class="card-body" style="padding-bottom: 0.5em;">
                        <h5 class="card-title"><a href="${createLink(controller: "project", action: "view", params: [id: project.projectId])}">${project.projectName}</a></h5>
                        <p class="card-text">
                            <span class="d-inline-block" tabindex="0" data-toggle="tooltip" title="${project.description}">
                                ${project.description.size() > 55 ? (project.description.toString().substring(0,55) + "...") : project.description }
                            </span>
                        </p>
                        <span style="color: grey">
                            <i>-${project.projectOwner}</i><br>
                        </span>
                        <span style="color: #c36464">
                            ${project.ownerInstitution}
                        </span>
                    </div>
                </g:each>
            </div>
        </div>
    </div>
</div>