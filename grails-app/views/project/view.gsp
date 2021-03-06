<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<div>
    <div style="margin: 1.65em">
        &nbsp;
    </div>
    <div class="mbr-fullscreen project_header_image">
        <div class="container-fluid">
            <div class="row justify-content-md-start">
                <div class="col-md-1" style="margin-right: -70px; align-self: center;">
                    <g:getUserProfilePic user="${project?.projectOwner}" sticker="${true}" showLink="${true}" imageSize="xxx-large"/>
                </div>
                <div class="project-header-container mbr-white col-md-10">
                    <h1 class="mbr-section-title mbr-bold pb-3 mbr-fonts-style display-2">
                        <span>
                            <span id="project-name-label">${project?.name}&nbsp;</span>
                            <span id="project-name-input" style="display: none; font-size: 0.75em;">
                                <input type="text" value="${project?.name}" name="project-name" style="border-radius: 5px; width: -webkit-fill-available; max-width: 50%;"></span>
                        </span>
                        <g:userOwnsProject project="${project}">
                            <span style="font-size: 0.75em;">
                                <span id="edit-button">
                                    <i class="fas fa-pencil-alt" onclick="editProject();"></i>
                                </span>
                                <span id="save-cancel" style="display: none;">
                                    <i class="fas fa-save" onclick="saveProjectChanges();"></i>&nbsp;
                                    <i class="fas fa-times-circle" onclick="cancelEditing();"></i>
                                </span>
                            </span>
                        </g:userOwnsProject><br>
                    </h1>
                    <div id="project-annotations" style="margin-top: -20px; margin-bottom: 30px;display: flex;">
                        <i class="fas fa-tags" style="transform: translateY(4px) translateX(4px) rotate(90deg);color: gold;padding-right: 1em;"></i>
                        <div id="annotations-text">
                            <div id="annotation-labels">
                                <g:set var="counter" value="${1}"/>
                                <g:set var="annotationsCount" value="${project?.annotations?.size()}"/>
                                <g:each in="${project?.annotations}" var="annotation">
                                    <span id="${annotation.id}">${annotation.term}</span>
                                    <g:if test="${counter != annotationsCount}">
                                        ,
                                    </g:if>
                                    <g:set var="counter" value="${counter+1}"/>
                                </g:each>
                            </div>
                            <span id="annotations-select-span" style="display: none;padding-left: 1em;">
                                <select id="annotations-select" class="multiple-select" name="annotations-select" multiple="multiple">
                                    <g:each in="${annotations}" var="annotation">
                                        <option value="${annotation.id}">${annotation.term}</option>
                                    </g:each>
                                </select>
                            </span>
                        </div>
                    </div>
                    <div class="mbr-text pb-3 mbr-fonts-style display-6">
                        Managed by ${project?.projectOwner?.profile?.firstName} ${project?.projectOwner?.profile?.lastName}<br>
                        <span>${project?.projectOwner?.profile?.institution?.fullName}</span><br>
                        <span style="font-size: 80%; font-style: oblique;">
                            Last updated <g:dateDiff date="${project?.lastChanged?:project?.lastUpdated}"/>
                        </span>
                        <span style="font-size: 80%; font-style: oblique;"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="project-header-container-bottom">
        &nbsp;
    </div>
</div>

<div class="project-details">
    <ul class="nav nav-tabs project-nav-tabs" id="myTab" role="tablist">
        <li class="nav-item">
            <a class="nav-link active" id="details-tab" data-toggle="tab" href="#details" role="tab" aria-controls="details" aria-selected="true">Details</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" id="bundles-tab" data-toggle="tab" href="#bundles" role="tab" aria-controls="bundle" aria-selected="false">Bundles</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" id="teams-tab" data-toggle="tab" href="#teams" role="tab" aria-controls="teams" aria-selected="false">Teams</a>
        </li>
    </ul>

    <!-- Tab panes -->
    <div class="tab-content project-tab-content">
        <div class="tab-pane active" id="details" role="tabpanel" aria-labelledby="details-tab">
            <div class="row" style="max-width: 100%;">
                <div class="col">
                    <div>
                        <span id="description-section">
                            <span id="description-label" class="display-5" style="font-weight: 300;">
                                <i class="fas fa-quote-left">&nbsp;</i>
                                <span id="project-description-text">${project?.description}</span>&nbsp;
                                <i class="fas fa-quote-right"></i>
                            </span>
                            <span id="project-description-input" style="display: none; font-size: 1.25em;">
                                <textarea class="project-description-textarea" name="project-description" style="border-radius: 5px;white-space: nowrap;" rows="5">${project?.description}</textarea>
                            </span>
                        </span>
                    </div>
                    <div class="row" style="align-items: center;padding-top: 2em;">
                        <div class="col-sm-3">Created:</div>
                        <div class="col-sm-9">
                            <g:dateDiff date="${project?.dateCreated}"/>
                        </div>
                    </div>
                    <div class="row" style="align-items: center;">
                        <div class="col-sm-3">License:</div>
                        <div class="col-sm-9">
                            <g:getSoftwareLicenseOptions/>
                            <button id="softwareLicenseButton"onclick="showSoftwareLicense(${project?.license?.id}, '${project?.license?.label}')"
                                    class="btn btn-link" style="padding: 0; margin-left: -2px;">
                                ${project?.license?.label}
                            </button>
                        </div>
                    </div>
                    <div class="row" style="align-items: center; padding-bottom: 2em;">
                        <div class="col-sm-3">Visibility:</div>
                        <div class="col-sm-9">
                            <span id="sharedSelectSpan" style="display: none;">
                                <select id="sharedSelect" class="form-control">
                                    <option value="0">Private</option>
                                    <option value="1">Public</option>
                                </select>
                            </span>
                            <span id="sharedSpan">
                                <g:if test="${project?.shared}">
                                    <i id="sharedIcon" class="fas fa-lock-open"></i>
                                </g:if>
                                <g:else>
                                    <i id="sharedIcon" class="fas fa-lock"></i>
                                </g:else>
                                <span id="sharedText"><g:projectVisibility value="${project?.shared}"/></span>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <g:if test="${project?.publications}">
                        <g:render template="publications" model="[project: project]"/>
                    </g:if>
                    <g:else>
                        <div class="row" style="align-items: center;padding-top: 2em;">
                            <div class="col-sm-3">Publications:</div>
                            <div class="col-sm-9">None yet</div>
                        </div>
                    </g:else>
                </div>

                <g:if test="${!project.shared}">
                <div class="col-lg-12">
                    <g:render template="userAccessDiv" model="[users: usersAccess, title: 'Individual User Access',
                    projectId: project.id]"/>
                </div>
                </g:if>
            </div>
        </div>
        <g:render template="bundles" model="[project: project]"/>
        <div class="tab-pane" id="teams" role="tabpanel" aria-labelledby="teams-tab">
            <div class="container-fluid">
                <div class="row" style="margin-left: -5em;">
                    <div id="teams_div" class="col-lg-12" style="padding: 0;">
                        <g:render template="teams" model="[project: project]"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">

</div>

<div>
    <hr style="margin: 0;">
    <div class="container-fluid projects-comments-header">
        <div class="projects-comments-header-text">Posts&nbsp;<i class="far fa-comment fa-1x"></i></div>
    </div>
</div>

<div id="comments-toolbar" class="container-fluid" style="background-color: rgb(129, 137, 146); padding-top: 0.2em;">
    <span onclick="addProjectComment(${project?.id})" style="color: white; padding-left: 1em;">
        <i class="fa fa-plus"></i>
        <span>New Comment</span>
    </span>
%{--<div id="comments-toolbar" class="container-fluid" style="background-color: #eeeeee;">--}%
    %{--<button type="button" class="btn btn-secondary btn-xs" style="padding: 3px;" onclick="addProjectComment(${project.id})"><i class="fa fa-plus"></i></button>--}%
</div>

<section class="features1" id="project-comments-section">
    <div class="container-fluid">
        <div class="media-body" style="padding: 0;">
            <div id="project-comments-body" class="card p-3 col-lg-12">
                <div class="comment-add-box" id="project_new_comment_box_${project?.id}">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><g:getUserProfilePic/></span>
                        </div>
                        <textarea class="form-control" id="project_comment_box_text_${project?.id}"></textarea>
                    </div>
                    <input type="button" class="btn-danger add-comment-button cancel-comment-button" value="Cancel" onclick="cancelProjectComment(${project?.id})">
                    <input type="button" class="btn-primary add-comment-button" value="Post" onclick="postProjectComment(${project?.id})">
                </div>
                <div id="project_comments">
                    <g:if test="${project?.comments}">
                        <g:render template="projectComments" model="[project: project, user: userProfile.user]"/>
                    </g:if>
                    <g:else>
                        <div class="jumbotron-fluid">
                            No comments yet. Be the first to comment!
                        </div>
                    </g:else>
                </div>
            </div>
            </div>
        </div>
    </div>
</section>

<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>

<script type="application/javascript">
    $( function() {
        var showTeamsTab    = ${showTeams};
        var showBundleTab   = ${showBundles};

        if (showTeamsTab === true) {
            $('.nav-tabs a[href="#teams"]').tab('show');
        } else if (showBundleTab === true) {
            $('.nav-tabs a[href="#bundles"]').tab('show');
        }

        handleOnHoverForTeamMembers();
    });

    function showProjectCommentBox(commentId) {
        var commentBox = $('#project_comment_box_' + commentId);

        if (commentBox.is(':visible') === false) {
            var tooltipId = $('#project_comment_tooltip_' + commentId).attr('aria-describedby');
            $("#" + tooltipId).removeClass("show");
            commentBox.addClass("comment-add-box-visible").hide().show(200);
        }
    }

    function shareProjectComment(commentId, id) {
        console.log("inside share function");
    }

    function likeProjectComment(commentId, id) {
        var tooltipId = $("#like_tooltip_" + commentId).attr("aria-describedby");
        $("#" + tooltipId).removeClass("show");

        if ($("#" + id).attr("class").indexOf('fa-check-circle') > -1 && $("#" + id).attr("class").indexOf('far') > -1) {
            $("#" + id).removeClass("far");
            $("#" + id).addClass("fas");
            $("#" + id).addClass("liked-post");
        } else {
            $("#" + id).removeClass("liked-post");
            $("#" + id).removeClass("fas");
            $("#" + id).addClass("far");
        }

        $("#" + tooltipId).tooltip('update').removeClass("show");

        $.post("/project/likePost", {'id': commentId}, function (data) {
            if (data.success === true) {
                getProjectComments(${project?.id});
            }
        });
    }

    function removelikeProjectComment(commentId, id) {
        var tooltipId = $("#like_tooltip_" + commentId).attr("aria-describedby");
        $("#" + tooltipId).removeClass("show");

        $.post("/project/removeLike", {'id': commentId}, function (data) {
            if (data.success === true) {
                getProjectComments(${project?.id});
            }
        });
    }

    function postProjectComment(projectId) {
        var text = $("#project_comment_box_text_" + projectId).val();

        $.post("/project/saveComment", {'comment': text, 'id': projectId}, function (data) {
            if (data.success === true) {
                  getProjectComments(projectId);
            }
        });
    }

    function getProjectComments(projectId) {
        var commentBox = $('#project_new_comment_box_' + projectId);

        $.post("/project/getProjectComments/" + projectId, function (data) {
            $('#project_comments').html(data);
            $("#project_comment_box_text_" + projectId).val('');
            commentBox.removeClass("comment-add-box-visible").hide(200);
        });
    }

    function postCommentReply(commentId) {
        var text = $("#project_comment_box_text_" + commentId).val();

        $.post("/project/saveReply", {'reply': text, 'id': commentId}, function (data) {
            if (data.success === true) {
                getProjectComments(${project?.id});
            }
        });
    }

    function cancelProjectReply(id) {
        //reset state of textbox
        $("#project_comment_box_text_" + id).val('');
        $('#project_comment_box_' + id).removeClass("comment-add-box-visible").hide(200);
    }

    function addProjectComment(projectId) {
        var commentBox = $('#project_new_comment_box_' + projectId);

        if (commentBox.is(':visible') === false) {
            commentBox.addClass("comment-add-box-visible").hide().show(200);
        }
    }

    function cancelProjectComment(projectId) {
        var commentBox = $('#project_new_comment_box_' + projectId);

        if (commentBox.is(':visible') === true) {
            $("#project_comment_box_text_" + projectId).val('');
            commentBox.removeClass("comment-add-box-visible").hide(200);
        }
    }

    function addProjectPublication() {
        console.log("you want to add a new publication");
    }

    function editProject() {
        var annotations = $('#annotation-labels span');
        var projectLicense = $('#softwareLicenseButton').html().trim();

        $.each(annotations, function(key, value) {
            var newOption = new Option($(value).text().trim(), $(value).prop('id'), true, true);
            $('.multiple-select').append(newOption).trigger('change');
        });

        //hide/show where appropriate
        $('#annotation-labels').hide();
        $('#annotations-select-span').show();

        $('#edit-button').hide();
        $('#save-cancel').show();

        $('#project-name-label').hide();
        $('#description-label').hide();

        $('.project-description-textarea').val($('#description-label').text().trim());
        $('#project-description-input').show();

        $('#project-name-input').val($('#project-name-label').text());
        $('#project-name-input').show();

        //license stuff
        $('#licenses option').each( function() {
            if ($(this).text() === projectLicense) {
                $('#licenses').val($(this).attr('value'));
            }
        });

        $('#softwareLicenseButton').hide();
        $('#licenses').show();

        //project visibility stuff
        $('#sharedSpan').hide();
        var sharedVal = $('#sharedText').text().trim();

        if (sharedVal === "Public") {
            $('#sharedSelect').val(1);
        } else {
            $('#sharedSelect').val(0);
        }

        $('#sharedSelectSpan').show();
    }

    function cancelEditing() {
        $('#save-cancel').hide();
        $('#edit-button').show();

        $('#annotations-select-span').hide();
        $('.multiple-select').val(null).trigger("change");
        $('#annotation-labels').show();

        $('#project-name-input').hide();
        $('#project-name-label').show();

        $('#project-description-input').hide();
        $('#description-label').show();

        $('#licenses').hide();
        $('#softwareLicenseButton').show();

        $('#sharedSelectSpan').hide();
        $('#sharedSpan').show();
    }

    function saveProjectChanges() {
        var newProjectName      = $('[name=project-name]').val();
        var projectTags         = $('.multiple-select').val();
        var description         = $('.project-description-textarea').val();
        var licenseID           = $('#licenses').val();
        var projectShared       = $('#sharedSelect').val();

        $.post("/project/saveChanges", {'name': newProjectName, 'id': "${project?.id}", 'tags': projectTags,
            'desc': description, 'licenseId': licenseID, 'shared': projectShared}, function (data) {
            if (data.success === true) {
                var tagsLabel = "";
                //set the new name and new tags
                $('#project-name-label').text(newProjectName);

                var projectTagObjects = $('.multiple-select').select2('data');

                for (index in projectTagObjects) {
                    tagsLabel += '<span id="' + projectTagObjects[index].id + '">' + projectTagObjects[index].text + '</span>';
                    if (Number.parseInt(index) !== (projectTagObjects.length - 1)) {
                        tagsLabel += ", "
                    }
                }

                $('#annotation-labels').html( $.parseHTML( tagsLabel ) );
                $('#project-description-text').text($('.project-description-textarea').val());

                //license change
                var selectedText = $('#licenses option[value=' + $('#licenses').val() + ']').text();
                $('#softwareLicenseButton').html(selectedText);

                //shared
                var projectSharedText = $('#sharedSelect option[value=' + $('#sharedSelect').val() + ']').text();

                if (projectSharedText === "Public") {
                    $('#sharedIcon').removeClass('fa-lock');
                    $('#sharedIcon').addClass('fa-lock-open');
                } else {
                    $('#sharedIcon').removeClass('fa-lock-open');
                    $('#sharedIcon').addClass('fa-lock');
                }

                $('#sharedText').text(projectSharedText);

                //editing is done - re-use cancelEditing for now
                cancelEditing();
            }
        });
    }

    function resetMultiSelect() {
        $('.multiple-select').val(null).trigger("change");
    }

    function addTeam(projectId) {
        var usersWindow = bootbox.confirm({
            title: 'Add/Create new team',
            message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
            closeButton: false,
            buttons: {
                confirm: {
                    label: 'Save'
                },
                cancel: {
                    label: 'Cancel',
                    className: 'btn-dark btn-red'
                }
            },
            size: "large",
            callback: function (result) {
                if (result === true) {
                    var teamName        = $('#teamName').val();
                    var usersSelected = new Array();

                    $.each($('.multiple-select').find(':selected'), function (index, option) {
                        usersSelected[index] = option.value;
                    });

                    var parameters;
                    if ($('#newTeamRadio').is(':checked')) {
                        parameters = {id: projectId, name: teamName, members: usersSelected};
                    } else {
                        parameters = {id: projectId, teamId: $('#teamSelect').val()};
                    }

                    $.post("${createLink(controller: "project", action: "addTeamToProject")}", parameters, function (data) {
                        if (data.success === true) {
                            getProjectTeams(projectId);
                        }
                    });
                }
            }
        });

        //make body of dialog scrollable
        usersWindow.find('.bootbox-body').addClass("scrollable-bootbox-alert");

        $.get("${createLink(controller: "team", action: "newTeamForm")}", function (data) {
            usersWindow.find('.bootbox-body').html(data);
        });
    }

    function handleOnHoverForTeamMembers() {
        $('.team-member').hover(
            function() {
                var cardTitle   = $(this).find('.card-title');
                var cardFooter  = $(this).find('.card-footer');

                cardTitle.show();
                cardFooter.show();
            },
            function() {
                var cardTitle   = $(this).find('.card-title');
                var cardFooter  = $(this).find('.card-footer');

                cardTitle.hide();
                cardFooter.hide();
            }
        );
    }

    function editTeamMembers(teamId, projectId) {
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
                            $("#accordion_" + teamId + " #team_" + teamId).load("${createLink(controller: "team", action: "teamMembersSnippet")}", {teamId: teamId, projectId: projectId});
                            handleOnHoverForTeamMembers();
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

    function removeTeam(teamId, projectId) {
        $.post("${createLink(controller: "project", action: "removeTeam")}", {projectId: projectId, teamId: teamId}, function (data) {
            if (data.success === true) {
                getProjectTeams(projectId);
            }
        });
    }

    function getProjectTeams(projectId) {
        $("#teams #teams_div").load("${createLink(controller: "project", action: "getTeams")}", {id: projectId}, function() {
            initMultiSelect();
            handleOnHoverForTeamMembers();
        });
    }

    function addNewBundle(projectId, type) {
        var dialog = bootbox.dialog({
            title: 'Upload or link external file(s)',
            className: "new-project-wizard",
            message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
            closeButton: false,
            size: 'large',
            buttons: {
                cancel: {
                    label: "Cancel",
                    className: 'btn-red cancel-btn'
                },
                ok: {
                    label: "Save",
                    className: 'finish-btn btn-primary',
                    callback: function() {
                        var formData = grabFormData('.new-upload-dialog');
                        formData.append("projectId", projectId);
                        formData.append("type", type);

                        var isValid = isBundleFormValid($('#' + type + 'Form'));
                        if(!isValid)
                            return false;

                        $.ajax({
                            type: "POST",
                            url: "${createLink(controller: "project", action: "addBundleToProject")}",
                            data: formData,
                            contentType: false,
                            processData: false,
                            success : function () {
                                $('#' + type + 'Table #'+ type + 'Rows').load("${createLink(controller: "project", action: "getBundles")}", {projectId: projectId, bundleType: type});
                            }
                        });
                    }
                }
            }
        });

        dialog.init(function(){
            $.get("/project/newUploadScreen", {projectId: projectId, type: type}, function (data) {
                $('.modal-body').html(data);
            });
        });
    }

    function deleteBundle(projectId, bundleId, type) {
        $.post("${createLink(controller: 'project', action: 'removeBundleFromProject')}", {projectId: projectId, bundleId: bundleId, type: type}, function (data) {
            $('#' + type + 'Table #'+ type + 'Rows').load("${createLink(controller: "project", action: "getBundles")}", {projectId: projectId, bundleType: type});
        });
    }

    function toggleAccessControlButton(userId) {
        var control = $('#removeAccessDiv_' + userId);
        if (control.is(":visible")) {
            $('#removeAccessDiv_' + userId).hide();
        } else {
            $('#removeAccessDiv_' + userId).show();
        }
    }

    function showUserAccessDialog(projectId, userId, accessObj) {
        var arrayOfValues = new Array();
        arrayOfValues = stringToJavascriptArrayOfObject(accessObj);

        var alertWindow = bootbox.dialog({
            title: 'User Access',
            className: 'scrollable-bootbox-alert',
            message: ' ',
            closeButton: false,
            size: "medium",
            buttons: {
                cancel: {
                    id: 'cancel',
                    className: 'btn-red',
                    label: 'Cancel',
                    callback: function () {
                        return true;
                    }
                },
                ok: {
                    id: 'saveButton',
                    className: 'btn-secondary',
                    label: 'Save',
                    callback: function () {
                        var revokableMasks = new Array();

                        $('#accessForm').find('input:not(:checked)').each( function () {
                            revokableMasks.push($(this).val());
                        });

                        revokeUserAccess(userId, revokableMasks, projectId);
                        return true;
                    }
                }
            }
        });

        alertWindow.init(function() {
            var html = generateAccessForm(arrayOfValues);
            alertWindow.find('.bootbox-body').html(html);
        });

    }

    function revokeUserAccess(userId, accessObj, projectId) {
        //actually post to server
        $.post("${createLink(controller: "project", action: "revokeAccessToProject")}",
            {projectId: projectId, userId: userId, masks: accessObj}, function (result) {
           if (result.success) {

               $.post("${createLink(controller: "project", action: "renderIndividualUserAccess")}", {projectId: projectId, userId: userId}, function (data) {
                   $('#userAccessDiv_' + userId).replaceWith(data);
               });
           }
        });
    }

    function stringToJavascriptArrayOfObject(stringObject) {
        var lengthOfStr = stringObject.length;
        var currentLength = 0;
        var array = new Array();

        while (currentLength < lengthOfStr - 1) {
            var substring;
            var object = new Object();
            var leftBracket  = stringObject.indexOf("{", currentLength);
            var rightBracket = stringObject.indexOf("}", currentLength);

            substring = stringObject.substring(leftBracket + 1, rightBracket);

            if (stringObject.charAt(rightBracket + 1) === ','){
                currentLength = rightBracket + 2;
            } else {
                currentLength = rightBracket + 1;
            }

            var propSplit = substring.split(",");
            for (var param in propSplit) {
                var propKeyValSplit = propSplit[param].split("=");
                object[propKeyValSplit[0].trim()] = propKeyValSplit[1];
            }
            array.push(object);
        }
        return array;
    }

    function generateAccessForm(accessObject) {

        var html = "<div class='container'>" +
            "<div class='row'><div class='col-lg-12'>Unselect the access you would like to revoke for the user</div><br>" +
            "</div><div class='row'><div id='accessForm' class='col-lg-12'><br>";

        for (var index in accessObject) {
            html += "<div class='row'><input type='checkbox' value='" + accessObject[index].mask + "' name='" + accessObject[index].name + "' checked/><label for=''>"+ accessObject[index].name +"</label></div>"
        }

        html +=  "</div>" +
            "</div></div>";

        return html;
    }
</script>