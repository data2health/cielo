<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<div>
    <div style="margin: 1.65em">
        &nbsp;
    </div>
</div>

<section class="mbr-fullscreen myprojects_header">
    <div class="container-fluid">
        <div class="row justify-content-md-start">
            <div class="col-md-2" style="margin-right: -70px; align-self: center;">
                <span class="display-4" style="font-size: 2em;padding-left: 2em; white-space: nowrap;">Public Projects</span>
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
                                                 usersProject: false]"/>
</section>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>