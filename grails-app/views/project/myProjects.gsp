<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar" model="[profile: userProfile, user: userProfile.user]"/>

<div>
    <div style="margin: 1.65em">
        &nbsp;
    </div>
</div>

<section class="mbr-fullscreen myprojects_header">
        <div class="container-fluid">
            <div class="row justify-content-md-start">
                <div class="col-md-2" style="margin-right: -70px; align-self: center;">
                    <span class="display-4" style="font-size: 2em;padding-left: 2em;">My Projects</span>
                </div>
            </div>
        </div>
</section>
<div class="project-header-container-bottom" style="height: 1em;">
    &nbsp;
</div>

<section id="projectsDiv" class="mbr-fullscreen">
    <g:render template="projectsTable" model="[projects: projects, offset: offset, numberOfPages: numberOfPages,
                                                 onChangeCallback: 'onPageSelection', onFirstPageCallback: 'onFirstPage',
                                                 onPreviousPageCallback: 'onPreviousPage',
                                                 onNextPageCallback: 'onNextPage',
                                                 onLastPageCallback: 'onLastPage',
                                                 usersProject: true]"/>
</section>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/footerlncludes"/>

<script type="application/javascript">
    function deleteProject(projectId, projectName) {
        bootbox.confirm({
            title: "Delete Project?",
            message: "Do you really want to delete <em style='font-weight: 300;color: #149dcc;'>" + projectName + "</em> ? This cannot be undone. <p>&nbsp;<p>*Please note that any data and code will also be deleted.",
            closeButton: false,
            buttons: {
                cancel: {
                    className: 'btn-red',
                    label: '<i class="fa fa-times"></i>&nbsp;Cancel'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i>&nbsp;Confirm'
                }
            },
            callback: function (result) {
                if (result === true) {
                    $.post("${createLink(controller: "project", action: "deleteProject")}", {'id': projectId},
                        function () {
                            var offsetVal = parseInt($('#paging-options').val()) - 1;
                            var filterText = $('#projectSearch').val();

                            $.get("${createLink(controller: "project", action: "getFilteredProjects")}",
                                {offset: offsetVal, myProjects: true, filterTerm: filterText}, function (data) {
                                replaceProjectTableContent(data);
                                //if the number of projects currently visible is zero, call change on options; page 1
                                //is already selected
                                 if ($('#projectTableBody tr').length === 0) {
                                     $('#paging-options').change();
                                 }
                            });
                        }
                    );
                }
            }
        });
    }
</script>