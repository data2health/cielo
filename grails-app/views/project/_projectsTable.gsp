<div class="table-card myprojects-card table-responsive" style="background-color: white; margin-bottom: 4em;
            margin-top: 1px;">
          <table id="projectsTable" class="table table-hover" style="min-height: 300px;
                              background-color: white;">
          <thead class="thead-light">
              <tr>
                  <th scope="col" style="width: 2%;">#</th>
                  <th scope="col">Name</th>
                  <th scope="col">&nbsp;</th>
                  <th scope="col">Description</th>
                  <th scope="col">Annotations</th>
                  <th scope="col">Owner</th>
                  <th scope="col">Teams</th>
                  <th scope="col">Last Updated</th>
                  <th>&nbsp;</th>
              </tr>
          </thead>
          <tbody id="projectTableBody">
<g:render template="projectTableRows" model="[usersProject: isUserProjects, projects: projects]"/>
</tbody>
</table>
<g:render template="/templates/paginationToolbar" model="[pages: numberOfPages, offset: pageOffset,
                                                          isUsersProjects: false,
                                                          onNextPageCallback: nextPageCallback,
                                                          onPreviousPageCallback: previousPageCallback,
                                                          onFirstPageCallback: firstPageCallback,
                                                          onLastPageCallback: lastPageCallback,
                                                          onChangeCallback: changeCallback]"/>