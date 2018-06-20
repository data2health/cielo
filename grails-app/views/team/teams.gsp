<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar" model="[profile: userProfile, user: userProfile.user]"/>

<section class="features1" style="height: 5em;">
    &nbsp;
</section>

<section class="mbr-fullscreen myprojects_header">
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-2" style="margin-right: -70px; align-self: center;">
                <span class="display-4" style="font-size: 2em;padding-left: 2em;">Teams</span>
            </div>
        </div>
    </div>
</section>
<div class="project-header-container-bottom" style="height: 1em;">
    &nbsp;
</div>

<section>
    <div class="container-fluid">
        <div class="row">
            <g:render template="teamsTable" model="[teams: teams, numberOfPages: numberOfPages, offset: offset]"/>
        </div>
    </div>
</section>

<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/footerIncludes"/>

<script type="application/javascript">
    function onTeamPageSelection(ignoreMe) {
        var offsetVal  = parseInt($('#paging-options').val()) - 1;
        var filterText = $('#searchInput').val();
        $.get("${createLink(controller: "team", action: "teamTableRows")}", {offset: offsetVal, filterTerm: filterText},
            function (data) {
            replaceTeamTableContent(data);
        });
    }
</script>