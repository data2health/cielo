<%@ page import="edu.wustl.cielo.UserAccount" %>
<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<section class="mbr-fullscreen">
    <div class="container-fluid activity-feed">
        <div class="row">
            <g:render template="sidebar-left" model="[followers: followers, following: following,
                                                      teamsManaged: teamsManaged, contributeToTeams: contributeToTeams]"/>
            <div id="collapse-panel" class="d-none d-md-block col-md-0 sidebar-parent collapse-icon-div">
                <span class="collapse-icon-span">
                    <i id="sidebar-toggle-button" class="fa fa-angle-double-left collapse-icon collapse-open"></i>
                </span>
            </div>
            <div id="activity" class="col-md-4">
                <g:render template="/templates/activityFeed" model="[activities: activities, activityOffset: activityOffset,
                                                                     activityMax: activityMax, username: username]"/>
                <div id="olderContent">
                </div>
            </div>
            <div id="spacer" class="col-md-2">&nbsp;</div>
            <g:render template="sidebar-right" model="[projects: mostPopularProjects]"/>
        </div>
    </div>
</section>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/footerlncludes"/>

<script type="application/javascript">
    $(function () {
        //add listener to load more activity data
        window.addEventListener('scroll', function (event) {
            handleInfiniteScroll(event);
        }, false);
    });
</script>