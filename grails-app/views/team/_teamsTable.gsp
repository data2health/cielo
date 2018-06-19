<div id="teamsTableSection" class="col-lg-auto teams-section">
    <div class="card table-card teams-card">
        <div class="card-body" style="padding: 0; background-color: white;">
            <div class="row card-header" style="margin: 0;">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-6">&nbsp;</div>
                <div class="col-lg-4">
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="input-group input-group-sm mb-0" style="max-height: 2em;">
                                <div class="input-group-prepend">
                                    <span class="input-group-text" id="search-icon"> <i class="fas fa-search"></i></span>
                                </div>
                                <input id="searchInput" class="form-control" type="text" placeholder="Search"
                                       aria-label="Search" aria-describedby="search-icon">
                                <span id="teamSearchClear" class="searchClear" onclick="clearTeamSearch();">
                                    <i class="fas fa-times-circle" style=""></i>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <table id="teamsTable" class="table table-hover table-responsive" style="min-height: 300px; max-width: 98vw;
            background-color: white;">
                <thead class="thead-light">
                <tr>
                    <th scope="col" style="width: 2%;">#</th>
                    <th scope="col">Name</th>
                    <th scope="col">Admin</th>
                    <th scope="col">Members</th>
                    <th scope="col">Projects team contributes to</th>
                </tr>
                </thead>
                <tbody id="teamTableBody">
                    <g:render template="teamsTableRows" model="[teams: teams]"/>
                </tbody>
            </table>
            <g:render template="/templates/paginationToolbar" model="[pages: numberOfPages, offset: offset,
                                                                      onChangeCallback: 'onTeamPageSelection',
                                                                      onFirstPageCallback: 'onFirstPage',
                                                                      onPreviousPageCallback: 'onPreviousPage',
                                                                      onNextPageCallback: 'onNextPage',
                                                                      onLastPageCallback: 'onLastPage']"/>
        </div>
    </div>
</div>
