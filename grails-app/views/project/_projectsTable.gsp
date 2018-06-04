<div id="myProjectsTableSection">
    <div class="container-fluid">
        <div class="row justify-content-md-start">
            <div class="col-lg-auto myprojects-section" style="padding-top: 2em;">
                <div class="card table-card myprojects-card">
                    <div class="card-body" style="padding: 0; background-color: white;">
                        <div class="row card-header" style="margin: 0;">
                            <div class="col-lg-2">
                                <h6>
                                    <i class="fas fa-cube"></i>&nbsp;Projects&nbsp;
                                    <g:if test="${usersProject}">
                                        <button type="button" onclick="showNewProjectWizard()" class="btn btn-primary"
                                                style="margin: 0; padding: 3px;">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </g:if>
                                </h6>
                            </div>
                            <div class="col-lg-6">&nbsp;</div>
                            <div class="col-lg-4">
                                <div class="row">
                                    <div class="col-lg-12">
                                        <div class="input-group input-group-sm mb-0" style="max-height: 2em;">
                                            <div class="input-group-prepend">
                                                <span class="input-group-text" id="search-icon"> <i class="fas fa-search"></i></span>
                                            </div>
                                            <input id="projectSearch" type="text" class="form-control" placeholder="Search"
                                                   aria-label="Search" aria-describedby="search-icon">
                                            <span id="projectSearchClear" onclick="clearProjectSearch();">
                                                <i class="fas fa-times-circle" style=""></i>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <table id="projectsTable" class="table table-hover table-responsive" style="min-height: 300px; max-width: 98vw;
                        background-color: white;">
                            <thead class="thead-light">
                            <tr>
                                <th scope="col" style="width: 2%;">#</th>
                                <th scope="col">Name</th>
                                <th scope="col">Description</th>
                                <th scope="col">Annotations</th>
                                <g:if test="${!usersProject}">
                                    <th scope="col">Owner</th>
                                </g:if>
                                <th scope="col">Teams</th>
                                <th scope="col">Visibility</th>
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
                                                                                  isUsersProjects: usersProject,
                                                                                  onChangeCallback: onChangeCallback]"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>