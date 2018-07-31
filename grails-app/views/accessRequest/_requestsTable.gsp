<div class="table-card myprojects-card requestsTable table-responsive">
          <table id="requestsTable" class="table table-hover" style="min-height: 300px;
                              background-color: white;">
          <thead class="thead-light">
              <tr>
                  <th scope="col" style="width: 2%;">#</th>
                  <th scope="col">Project Name</th>
                  <th scope="col">Requestor</th>
                  <th scope="col">Status</th>
                  <th scope="col">Access Requested</th>
                  <th scope="col">&nbsp;</th>
              </tr>
          </thead>
          <tbody id="requestsTableBody">
<g:render template="requestTableRows" model="[requests: accessRequests]"/>
</tbody>
</table>
<g:render template="/templates/paginationToolbar" model="[pages: pagesCount, offset: offset,
                                                          isUsersProjects: false,
                                                          onNextPageCallback: 'onNextPage',
                                                          onPreviousPageCallback: 'onPreviousPage',
                                                          onFirstPageCallback: 'onFirstPage',
                                                          onLastPageCallback: 'onLastPage',
                                                          onChangeCallback: 'onMessagesPageSelection']"/>