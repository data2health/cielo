<div id="team_${team.id}" class="card">
    <g:if test="${isTeamOwner}">
        <div class="card-header" id="heading_${team.id}" style="display: inline-flex; white-space: pre-wrap;">
            <div class="container-fluid" style="background-color: white; border-radius: 10px;
            border: 1px solid #dcdcdc; padding-top: 0.3em; padding-bottom: 0.3em;
            margin-top: 1.5em;display: inline-table;">
                <button type="button" class="btn btn-primary" style="padding: 10px;" onclick="editTeamMembers(${team.id});">
                    <i class="far fa-edit"></i>
                    <span style="font-size: 0.75em; cursor: default;">&nbsp;Edit Team</span>
                </button>

                <button type="button" class="btn btn-red" style="padding: 10px;" onclick="deleteTeam(${team.id});">
                    <i class="fa fa-trash-alt"></i>
                    <span style="font-size: 0.75em; cursor: default;">&nbsp;Delete Team</span>
                </button>
            </div>
        </div>
    </g:if>

    <div id="collapse_${team.id}" class="collapse show" aria-labelledby="heading_${team.id}" data-parent="#accordion" style="background-image: url('${assetPath(src: "connections.svg")}');
    background-size: cover;padding-bottom: 5em;">
        <div class="team-deck" style="margin-top: 3em;">
            <g:each in="${team.members}" var="member">
                <div class="team-member" style="text-align: center;">
                    <div class="card-title" style="display: none;"></div>
                    <div style="display: inline-block;">
                        <g:getUserProfilePic user="${member}" showLink="${true}" sticker="${true}"
                                             tooltipText="${member.fullName} ${'<br><em>'}${member.username}${'</em>'}"
                                             tooltipOffset="-5"
                                             imageSize="x-large"/>
                    </div>
                    <div class="card-footer team-users" style="display: none;">
                        <span style="font-weight: 600;">${member.fullName}</span><br>
                        <span style="font-weight: 100;font-style: italic;">${member.username}</span>
                    </div>
                </div>
            </g:each>
        </div>
    </div>
</div>

<script type="application/javascript">
    function editTeamMembers(teamId) {
        var getUrl = "${createLink(controller: 'team', action:'getTeamMembers')}";
        var updateTeamMembersUrl = "${createLink(controller: 'team', action:'updateTeamUsers')}";

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
                    var usersSelected = new Array();

                    $.each($('.multiple-select').find(':selected'), function (index, option) {
                        usersSelected[index] = option.value;
                    });

                    $.post(updateTeamMembersUrl, {id: teamId, users: usersSelected}, function (data) {
                        if (data.success === true) {
                            $("#teamSection #team_" + teamId).load("${createLink(controller: "team",
                            action: "userTeamMembersSnippet")}", {teamId: teamId});
                        }
                    });
                }
            }
        });

        //make body of dialog scrollable
        usersWindow.find('.bootbox-body').addClass("scrollable-bootbox-alert");

        $.get(getUrl, {id: teamId}, function (data) {
            usersWindow.find('.bootbox-body').html(data);
        });
    }

    function deleteTeam(teamId) {
        var numberOfProjects = $('.project-link').length;

        if (numberOfProjects >= 1) {
            bootbox.alert({
                title: 'Unable to Delete',
                message: '<div>Team is contributing to at least one project. In order to delete the team' +
                ' you must first remove it from all projects.</div>',
                closeButton: true,
                size: "small"
            });
        } else {
            //delete the team in the backend
            $.post("${createLink(controller: "team", action: "deleteTeam")}", {teamId: teamId}, function (data) {
                if (data.success === true) {
                    window.location="/";
                }
            });
        }
    }
</script>