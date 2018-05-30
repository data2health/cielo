<div id="myProjectsTableSection">
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
                        <table id="projectsTable" class="table table-hover table-responsive" style="min-height: 300px; max-width: 98vw;
                        background-color: white;">
                            <thead class="thead-light">
                            <tr>
                                <th scope="col" style="width: 2%;">#</th>
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
                            <tbody id="projectTableBody">
                                <g:render template="projectTableRows" model="[usersProject: usersProject, projects: projects]"/>
                            </tbody>
                        </table>
                        <g:render template="/templates/paginationToolbar" model="[pages: numberOfPages, offset: offset,
                                                                                  onChangeCallback: onChangeCallback]"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>