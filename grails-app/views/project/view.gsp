<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar" model="[profile: userProfile, user: userProfile.user]"/>

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
                            <span id="annotation-labels">
                                <g:set var="counter" value="${1}"/>
                                <g:set var="annotationsCount" value="${project.annotations.size()}"/>
                                <g:each in="${project.annotations}" var="annotation">
                                    <g:if test="${counter != annotationsCount}">
                                        ${annotation.label},
                                    </g:if>
                                    <g:else>
                                        ${annotation.label}
                                    </g:else>
                                    <g:set var="counter" value="${counter+1}"/>
                                </g:each>
                            </span>
                            <span id="annotations-select-span" style="display: none;padding-left: 1em;">
                                <select class="multiple-select" name="annotations-select" multiple="multiple">
                                    <g:each in="${annotations}" var="annotation">
                                        <option value="${annotation.id}">${annotation.label}</option>
                                    </g:each>
                                </select>
                            </span>
                        </div>
                    </div>
                    <div class="mbr-text pb-3 mbr-fonts-style display-6">
                        Managed by ${project?.projectOwner?.profile?.firstName} ${project?.projectOwner?.profile?.lastName}<br>
                        <span>${project?.projectOwner?.profile?.institution.fullName}</span><br>
                        <span style="font-size: 80%; font-style: oblique;">
                            Last updated <g:dateDiff date="${project?.lastUpdated}"/>
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
                                <span id="project-description-text">${project.description}</span>&nbsp;
                                <i class="fas fa-quote-right"></i>
                            </span>
                            <span id="project-description-input" style="display: none; font-size: 1.25em;">
                                <textarea class="project-description-textarea" name="project-description" style="border-radius: 5px;white-space: nowrap;" rows="5">${project.description}</textarea>
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
                            <button id="softwareLicenseButton"onclick="showSoftwareLicense(${project.license.id}, '${project.license.label}')"
                                    class="btn btn-link" style="padding: 0; margin-left: -2px;">
                                ${project.license.label}
                            </button>
                        </div>
                    </div>
                    <div class="row" style="align-items: center;">
                        <div class="col-sm-3">Visibility:</div>
                        <div class="col-sm-9">
                            <span id="sharedSelectSpan" style="display: none;">
                                <select id="sharedSelect" class="form-control">
                                    <option value="0">Private</option>
                                    <option value="1">Public</option>
                                </select>
                            </span>
                            <span id="sharedSpan">
                                <g:if test="${project.shared}">
                                    <i id="sharedIcon" class="fas fa-lock-open"></i>
                                </g:if>
                                <g:else>
                                    <i id="sharedIcon" class="fas fa-lock"></i>
                                </g:else>
                                <span id="sharedText"><g:projectVisibility value="${project.shared}"/></span>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <g:if test="${project.publications}">
                        <g:render template="publications" model="[project: project]"/>
                    </g:if>
                    <g:else>
                        <div class="row" style="align-items: center;padding-top: 2em;">
                            <div class="col-sm-3">Publications:</div>
                            <div class="col-sm-9">None yet</div>
                        </div>
                    </g:else>
                </div>
            </div>
        </div>
        <g:render template="bundles" model="[project: project]"/>
        <div class="tab-pane" id="teams" role="tabpanel" aria-labelledby="teams-tab">
            <g:render template="teams" model="[project: project]"/>
        </div>
    </div>
</div>
<div>
    <hr>
    <div class="container-fluid projects-comments-header">
        <div class="projects-comments-header-text">Posts&nbsp;<i class="far fa-comment fa-1x"></i></div>
    </div>
</div>

<div id="comments-toolbar" class="container-fluid" style="background-color: rgb(129, 137, 146); padding-top: 0.2em;">
    <span onclick="addProjectComment(${project.id})" style="color: white; padding-left: 1em;">
        <i class="fa fa-plus"></i>
        <span>New Comment</span>
    </span>
%{--<div id="comments-toolbar" class="container-fluid" style="background-color: #eeeeee;">--}%
    %{--<button type="button" class="btn btn-secondary btn-xs" style="padding: 3px;" onclick="addProjectComment(${project.id})"><i class="fa fa-plus"></i></button>--}%
</div>

<section class="features1" id="project-comments-section">
    <div class="container-fluid">
        <div class="media-body">
            <div id="project-comments-body" class="card p-3 col-lg-12">
                <div class="comment-add-box" id="project_new_comment_box_${project.id}">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><g:getUserProfilePic/></span>
                        </div>
                        <textarea class="form-control" id="project_comment_box_text_${project.id}"></textarea>
                    </div>
                    <input type="button" class="btn-danger add-comment-button cancel-comment-button" value="Cancel" onclick="cancelProjectComment(${project.id})">
                    <input type="button" class="btn-primary add-comment-button" value="Post" onclick="postProjectComment(${project.id})">
                </div>
                <div id="project_comments">
                    <g:if test="${project.comments}">
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
<g:render template="/templates/footerlncludes"/>

<script type="application/javascript">

    $( function() {
        $('[data-toggle="tooltip"]').on('shown.bs.tooltip', function () {
                $('.tooltip').css('top', parseInt($('.tooltip').css('top')) + (-25) + 'px');
            });

            $('[data-toggle="tooltip"]').on('hidden.bs.tooltip', function () {
                $('.tooltip').css('top', parseInt($('.tooltip').css('top')) + (+25) + 'px');
            });
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
                getProjectComments(${project.id});
            }
        });
    }

    function removelikeProjectComment(commentId, id) {
        var tooltipId = $("#like_tooltip_" + commentId).attr("aria-describedby");
        $("#" + tooltipId).removeClass("show");

        $.post("/project/removeLike", {'id': commentId}, function (data) {
            if (data.success === true) {
                getProjectComments(${project.id});
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
                getProjectComments(${project.id});
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
        var annotations = $('#annotation-labels').text().replace(/^\s+|\s+$/, '').split(",");
        var options = new Array(annotations.length);
        var projectLicense = $('#softwareLicenseButton').html().trim();

        //setup multi-select for the annotations for the project
        for (index in annotations) {
            //exact match the annotation using filter
            var textId = $('.multiple-select option').filter(function () {
                return $(this).text() === annotations[index].trim();
            }).attr("value");
            options[index] = textId;
        }

        //project-description-input description-label

        $('.multiple-select').val(options).trigger("change");

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

        $.post("/project/saveChanges", {'name': newProjectName, 'id': "${project.id}", 'tags': projectTags,
            'desc': description, 'licenseId': licenseID, 'shared': projectShared}, function (data) {
            if (data.success === true) {
                var tagsLabel = "";
                //set the new name and new tags
                $('#project-name-label').text(newProjectName);

                var projectTagObjects = $('.multiple-select').select2('data');
                for (index in projectTagObjects) {
                    if (index === "0") {
                        tagsLabel += projectTagObjects[index].text;
                    } else {
                        tagsLabel += ", " + projectTagObjects[index].text;
                    }
                }

                $('#annotation-labels').text(tagsLabel);
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
</script>