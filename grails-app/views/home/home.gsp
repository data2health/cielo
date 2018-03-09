<%@ page import="edu.wustl.cielo.UserAccount" %>
<g:render template="/templates/headerIncludes"/>

<body>
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
            <g:render template="sidebar-right" model="[bundles: mostPopularBundles]"/>
        </div>
    </div>
</section>
<div id="scrollToTop" class="scrollToTop mbr-arrow-up" style=""><a style="text-align: center;"><i></i></a></div>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/footerlncludes"/>
</body>


<script type="text/javascript">
    $( function() {
        $(".dropdown-toggle").dropdown();
        $('[data-toggle="tooltip"]').tooltip();

        $("#sidebar-toggle-button").on("click", function () {
            if ($('#sidebar-options').attr("class").indexOf('hidden') > -1) {
                $("#sidebar-toggle-button").removeClass("collapse-icon-collapsed");
                $("#sidebar-toggle-button").removeClass("fa-angle-double-right");


                $("#sidebar-options").removeClass("col-md-0");
                $("#spacer").removeClass("col-md-3");
                $("#activity").removeClass("col-md-5");
                $("#collapse-panel").removeClass('collapse-icon-div-collapsed');
                $("#spacer").removeClass("spacer-collapsed");
                $('#connections-nav-tab').removeClass('hidden');
                $("#sidebar-options").removeClass("hidden");

                $("#sidebar-options").addClass("col-md-3");

                $("#sidebar-toggle-button").addClass("fa-angle-double-left");

                //spacer
                $("#spacer").addClass("col-md-2");

                //activity div
                $("#activity").addClass("col-md-4");

                //show all children
                $("#sidebar-options").children().show();
            } else {
                $("#sidebar-options").children().hide();
                $("#sidebar-toggle-button").removeClass("fa-angle-double-left");
                $("#sidebar-options").removeClass("col-md-3");
                $("#spacer").removeClass("col-md-2");
                $("#activity").removeClass("col-md-4");
                $("#sidebar-toggle-button").addClass("collapse-icon-collapsed");
                $("#sidebar-toggle-button").addClass("fa-angle-double-right");
                $("#sidebar-options").addClass("col-md-0");
                $("#sidebar-options").addClass("hidden");

                $("#collapse-panel").addClass('collapse-icon-div-collapsed');

                //activity div
                $("#activity").addClass("col-md-5");

                //spacer
                $("#spacer").addClass("col-md-3");
                $("#spacer").addClass("spacer-collapsed");
            }

        });
    });

    function showCommentBox(activityId, id) {
        var tooltipId = $("#comment-tooltip-" + activityId).attr("aria-describedby");

        $("#" + tooltipId).removeClass("show");
        $('#comment_box_' + id).addClass("comment-add-box-visible");
    }

    function postComment(id) {
        var text = $("#comment-box-text-" + id).val();

        $.post("/activity/saveComment?activityId=" + id + "&text=" + text, function (data) {
            if (data.success === true) {
                getCommentsForActivity(id);
            }
        });

        //reset state of textbox
        $("#comment-box-text-" + id).val("");
        $('#comment_box_' + id).removeClass("comment-add-box-visible");
    }

    function cancelComment(id) {
        //reset state of textbox
        $("#comment-box-text-" + id).val("");
        $('#comment_box_' + id).removeClass("comment-add-box-visible");
    }

    function getCommentsForActivity(activityId) {
        var newCommentsCount = $('#comments_activity_' + activityId).children(".activity-post-comment").length + 2;

        $.post("/activity/getComments/" + activityId, {commentCount: newCommentsCount}, function (data) {
            $("#feed_footer_activity_" + activityId).addClass("with-comments");
            $("#comments_activity_"+ activityId ).html(data);
        });
    }

    function getOlderComments(activityId) {
        var newCommentsCount = $('#comments_activity_' + activityId).children(".activity-post-comment").length + 2;

        $.post("/activity/getComments/" + activityId, {commentCount: newCommentsCount}, function (data) {
            $("#feed_footer_activity_" + activityId).addClass("with-comments");
            $("#comments_activity_"+ activityId ).html(data);
        });
    }

    function getOlderActivity(offset, max) {

        $("#nextPage").remove();
        $("<div></div>").load("/activity/getActivities?offset="+offset+"&max="+max, function () {
            $(this).insertBefore($("#olderContent"));
        });
    }

    function likePost(activityId, id) {
        var tooltipId = $("#like-tooltip-" + activityId).attr("aria-describedby");
        $("#" + tooltipId).removeClass("show");

        if ($("#" + id).attr("class").indexOf('fa-meh-o') > -1) {
            $("#" + id).removeClass("fa-meh-o");
            $("#" + id).addClass("fa-smile-o");
            $("#" + id).addClass("liked-post");
        } else {
            $("#" + id).removeClass("liked-post");
            $("#" + id).removeClass("fa-smile-o");
            $("#" + id).addClass("fa-meh-o");

        }
    }

    function sharePost(activityId, id) {
        var tooltipId = $("#share-tooltip-" + activityId).attr("aria-describedby");
        $("#" + tooltipId).removeClass("show");
    }
</script>
