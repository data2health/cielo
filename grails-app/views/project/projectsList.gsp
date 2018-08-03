<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>
<section class="">
    <div class="container-fluid">
        <div class="row">
            <div id="userEditToolbar" class="col-md-12 edit-toolbar sticky">
                <span id="edit_button" class="" onclick="showNewProjectWizard();">
                    <i class="far fa-plus-square"></i>
                    <span style="font-size: 0.75em; cursor: default;">Add New Project</span>
                </span>&nbsp;
            </div>
        </div>
    </div>
</section>

<section class="mbr-fullscreen d-block d-lg-none" style="min-height: 0; max-height: 2vh;">&nbsp;</section>

<section class="mbr-fullscreen" style="min-height: 100vh;">
    <div class="container-fluid" style="">
        <div class="row justify-content-md-center projectFilterDiv">
            <div class="col-md-12">
                <div class="custom-control custom-radio">
                    <input type="radio" id="customRadio1" name="projectType" class="custom-control-input"
                           value="all" onchange="filterProjectsList();" checked>
                    <label class="custom-control-label" for="customRadio1">All Projects</label>
                </div>
                <div class="custom-control custom-radio">
                    <input type="radio" id="customRadio2" name="projectType" class="custom-control-input"
                           onchange="filterProjectsList();" value="myProjects">
                    <label class="custom-control-label" for="customRadio2">My Projects</label>
                </div>
            </div>
        </div>

        <div class="row searchDivBackground">
            <div class="col-lg-4">
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
            <div class="col-lg-8">&nbsp;</div>
        </div>

        <g:render template="projectsTable" model="[isUserProjects: isUserProjects,
                                                projects: projects, numberOfPages: numberOfPages, pageOffset: pageOffset,
                                                nextPageCallback: 'onNextPage',
                                                previousPageCallback: 'onPreviousPage',
                                                firstPageCallback: 'onFirstPage',
                                                lastPageCallback: 'onLastPage',
                                                changeCallback: 'onPageSelection']"/>
    </div>
</section>

<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>

<script type="application/javascript">
    $( function () {
        $('#activityMenuUnderline, #teamsMenuUnderline').hide().removeClass('d-lg-block').removeClass('d-none');
        $('#projectMenuUnderline').addClass('d-lg-block').addClass('d-none').show();
    });

    function filterProjectsList() {
        //set to first page
        $('#paging-options').val('1');
        onPageSelection();
    }
</script>