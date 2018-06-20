<%@ page import="edu.wustl.cielo.UserAccount" %>
<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar" model="[user: profile.user]"/>

<section class="mbr-fullscreen" style="background-color: #eaeaea;">
    <div class="container-fluid activity-feed">
        <div class="row">
            <div id="sidebar-options" class="col-md-3 d-none d-md-block sidebar-parent">
                <g:render template="sidebar-left" model="[followers: followers, following: following,
                                                          teamsManaged: teamsManaged, contributeToTeams: contributeToTeams]"/>
            </div>
           <div id="collapse-panel" class="d-none d-md-block col-md-0 sidebar-parent collapse-icon-div">
                <span class="collapse-icon-span">
                    <i id="sidebar-toggle-button" class="fa fa-angle-double-left collapse-icon collapse-open"></i>
                </span>
            </div>
            <div id="activity" class="col-md-6">
                <g:render template="/activity/activityFeed" model="[activities: activities, activityOffset: activityOffset,
                                                                     activityMax: activityMax, user: profile.user, username: username]"/>
                <div id="olderContent">
                </div>
            </div>
            <g:render template="sidebar-right" model="[projects: mostPopularProjects]"/>
        </div>
    </div>
</section>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/addActivity"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>

<script type="application/javascript">
    $(function () {
        //add listener to load more activity data
        window.addEventListener('scroll', function (event) {
            handleInfiniteScroll(event);
        }, false);
    });

    function showUserDialog() {

        if($('.navbar').attr("class").indexOf('opened') > -1) {
            $('.navbar-toggler').click();
        }

        var usersWindow = bootbox.confirm({
            title: '',
            message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
            closeButton: false,
            buttons: {
                confirm: {
                    label: 'Save'
                },
                cancel: {
                    label: 'Cancel',
                    className: 'btn-dark'
                }
            },
            size: "medium",
            className: "dark-theme",
            callback: function (result) {
            if (result === true) {
                var usersSelected;

                if ($('.multiple-select').val() !== null) {
                    usersSelected = new Array($('.multiple-select').val().length);

                    $('.multiple-select').each( function () {
                        var selections      = $(this).select2('data');

                        for (index in selections) {
                            if (usersSelected.indexOf(selections[index].id) === -1) {
                                usersSelected[index] = selections[index].id;
                            }
                        }
                    });
                }

                $.post("${createLink(controller: "user", action: "updateUsersIFollow")}", {'users': usersSelected}, function (data) {
                    if (data.success === true) {
                        $('.activity-feed #sidebar-options').load("${createLink(controller: "home", action: "sidebarLeft")}")
                    }
                });
            }
        }
        });

        //make body of dialog scrollable
        usersWindow.find('.bootbox-body').addClass("scrollable-bootbox-alert");

        $.get("${createLink(controller: "user", action: "getUsersIFollow")}", function (data) {
            usersWindow.find('.bootbox-body').html(data);
        });
    }
</script>