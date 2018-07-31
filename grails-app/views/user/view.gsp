<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>
<g:set var="userCanEdit" value="${false}"/>
<section class="mbr-section" style="margin-top: 4.5em; background-color: white;">
    <g:loggedInUserCanMakeChangesToUser user="${user}">
        <g:set var="userCanEdit" value="${true}"/>
    </g:loggedInUserCanMakeChangesToUser>
    <div class="container-fluid">
        <div class="row">
                <div id="userEditToolbar" class="col-md-12 edit-toolbar sticky">
                    <g:if test="${userCanEdit}">
                        <span id="edit_button" class="" onclick="editUser();">
                            <i class="far fa-edit"></i>
                            <span style="font-size: 0.75em; cursor: default;">Edit</span>
                        </span>&nbsp;
                        <span style="font-weight:100; opacity: 0.8;">|&nbsp;</span>
                        <span id="save_button" class="fa-disabled" onclick="updateUser();">
                            <i class="far fa-save"></i>
                            <span style="font-size: 0.75em; cursor: not-allowed;">Save</span>
                        </span>&nbsp;
                        <span style="font-weight:100; opacity: 0.8;">|&nbsp;</span>
                        <span id="cancel_button" class="fa-disabled" onclick="cancelEditUser();">
                            <i class="fas fa-ban"></i>
                            <span style="font-size: 0.75em; cursor: not-allowed;">Cancel</span>
                        </span>&nbsp;
                    </g:if>
                    <g:else>
                        <g:if test="${loggedInUser.connections.contains(user)}">
                            <span id="follow_button" class="" onclick="unFollowUser();">
                                <i class="fas fa-user-times"></i>&nbsp;
                                <span style="font-size: 0.75em; cursor: default;">Un-Follow</span>
                            </span>&nbsp;
                        </g:if>
                        <g:else>
                            <span id="follow_button" class="" onclick="followUser();">
                                <i class="fas fa-user-plus"></i>&nbsp;
                                <span style="font-size: 0.75em;  cursor: default;">Follow</span>
                            </span>&nbsp;
                        </g:else>
                    </g:else>
                </div>
        </div>
    </div>
</section>


<section id="userEditSection" class="mbr-section" style="margin-top: 4.5em; background-color: white;">
    <div class="container-fluid">
        <div class="row" style="text-align: center;">
            <div class="col-lg-3">
                <div id="profilePicSelection" style="display: none;">
                    <form id="imageForm">
                        <input type="file" name="profilePic" id="profilePic" value="" onchange="validateProfilePic(this);">
                    </form>

                </div>
                <g:getUserProfilePic id="currentProfilePic" style="margin-top: 1.8em;display: inline-block;"
                                     showLink="${true}" user="${user}" sticker="${true}"
                                     imageSize="xxx-large"/>
            </div>
            <div class="col-lg-9">
                <div class="form-container">
                    <div class="media-container-column">
                        <form action="${createLink(controller: "user", action: "updateUser")}" method="post" name="userUpdateForm" class="form-horizontal"
                              role="form" enctype="multipart/form-data" id="userUpdateForm" novalidate
                              style="padding-top: 2em;">
                            <div class="form-group row">
                                <label for="firstName" class="col-sm-2 form-control-label">
                                    First Name
                                </label>

                                <div class="col-sm-3">
                                    <input id="firstName" name="firstName" class="form-control" placeholder="First Name" value="${user.profile.firstName}"
                                           required="" aria-required="true" disabled="disabled">
                                    <div id="firstNameInvalidFeedBack" class="col-md-10 invalid-feedback"></div>
                                </div>

                                <label for="lastName" class="col-sm-2 form-control-label" style="padding-left: 1.5em;">
                                    Last Name
                                </label>

                                <div class="col-sm-3">
                                    <input id="lastName" name="lastName" class="form-control" placeholder="Last Name" value="${user.profile.lastName}"
                                           required="" aria-required="true" disabled="disabled">
                                    <div id="lastNameInvalidFeedBack" class="col-md-10 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <div class="col-sm-8" style="display: none;">
                                    <input class="form-control" name="userId" id="userId" placeholder="userId" value="${user.id}"
                                           required="" aria-required="true">
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="username" class="col-sm-2 form-control-label">
                                    Username
                                </label>

                                <div class="col-sm-5">
                                    <input class="form-control" name="username" id="username" placeholder="Username" value="${user.username}"
                                           required="" aria-required="true" disabled="disabled">
                                    <div id="usernameInvalidFeedBack" class="col-md-4 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="password" class="col-sm-2 form-control-label">
                                    Password
                                </label>

                                <div class="col-sm-5">
                                    <input type="password" class="form-control" name="password" id="password" placeholder="Password" required="" aria-required="true"
                                           disabled="disabled" value="*************">
                                    <div id="passwordInvalidFeedBack" class="col-md-12 invalid-feedback"></div>
                                </div>
                                %{--<div class="col-sm-3" style="align-self: center;">--}%
                                    %{--<button id="changePass" type="button" class="btn btn-link" disabled="disabled" style="width: max-content;" onclick="changePassword();">Change Password</button>--}%
                                %{--</div>--}%
                            </div>

                            <div class="form-group row">
                                <label for="email" class="col-sm-2 form-control-label">
                                    Email
                                </label>

                                <div class="col-sm-8">
                                    <input type="email" class="form-control" name="emailAddress" id="email" placeholder="Email" value="${user.profile.emailAddress}"
                                           required="" aria-required="true" disabled="disabled">
                                    <div id="emailInvalidFeedBack" class="col-md-5 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="timezoneId" class="col-sm-2 form-control-label">
                                    Timezone
                                </label>

                                <div class="col-sm-8">
                                    <g:customTimeZoneSelect id="timezoneId"/>
                                </div>
                            </div>

                            <div id="institutionDiv" class="form-group row">
                                <label for="institution" class="col-sm-2 form-control-label">
                                    Institution
                                </label>

                                <div class="form-group col-sm-8">
                                    <select id="institution" name="institution.id" required="required" class="form-control" aria-required="true" disabled="disabled">
                                        <option value="">Select Institution</option>
                                        <g:each var="institute" in="${institutes}">
                                            <option value="${institute.id}">${institute.fullName}</option>
                                        </g:each>
                                    </select>
                                    <div id="institutionInvalidFeedBack" class="col-md-4 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="background" class="col-sm-2 form-control-label">
                                    Background
                                </label>

                                <div class="col-sm-8">
                                    <textarea id="background" name="background" class="form-control" rows="4" disabled="disabled">${user.profile.background}</textarea>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="annotations" class="col-sm-2 form-control-label">
                                    Research Interests
                                </label>

                                <div class="form-group col-sm-8" style="display: inherit;">
                                    <span id="annotations" class="">
                                        <select id="annotationsSelect" style="width: 80%" class="multiple-select form-control" name="annotations" multiple="multiple" disabled="disabled">
                                            <g:each in="${annotations}" var="annotation">
                                                <option value="${annotation.id}">${annotation.label}</option>
                                            </g:each>
                                        </select>
                                    </span>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/usersFeatureDiv" model="[users: user.connections, title: 'Connections']"/>
<g:render template="userTeams" model="[teams: myTeams, teamsIContributeTo: contributingTeams, isUsersOwnPage: userCanEdit]"/>
<g:render template="userProjects" model="[projects: projects, isUsersOwnPage: userCanEdit,
                                          contributeTo: projectsUserContributesTo]"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>

<script type="application/javascript">

    $( function () {
        $('#institution').val(${user.profile.institution.id});
        $('#timezoneId').prop('disabled', 'disabled');
        $('#timezoneId').val('${user.timezoneId}');

        var annotations = "${user.profile.annotations.collect { it.label }.join(',')}".replace(/^\s+|\s+$/, '').split(",");
        var options = new Array(annotations.length);

        //setup multi-select for the annotations for the project
        for (index in annotations) {
            //exact match the annotation using filter
            var textId = $('.multiple-select option').filter(function () {
                return $(this).text() === annotations[index].trim();
            }).attr("value");
            options[index] = textId;
        }

        $('.multiple-select').val(options).trigger("change");

        //check the image the user has for profile, if different than default save in case they want to edit and then
        //hit cancel so we can properly reset it
        window.imgSrc = undefined;
        var currentProfilePicSrc = $("#currentProfilePic").find("img").attr("src");

        if (currentProfilePicSrc.indexOf('/assets/default_profile.png') === -1) {
            window.imgSrc = currentProfilePicSrc;
        }

        $('#activityMenuUnderline, #teamsMenuUnderline, #activityMenuUnderline').hide().removeClass('d-lg-block').removeClass('d-none');
    });

    function editUser() {
        //toggle button states
        $('#save_button').removeClass('fa-disabled');
        $('#cancel_button').removeClass('fa-disabled');
        $('#edit_button').addClass('fa-disabled');

        $('#edit_button').find("span").css("cursor", "not-allowed");
        $('#cancel_button').find("span").css("cursor", "default");
        $('#save_button').find("span").css("cursor", "default");

        $('#userUpdateForm').find(':input, #changePass, #annotationsSelect').each( function (index) {
            var id = $(this).attr('id');
            if (id !== undefined)
                if (id.indexOf('password') === -1 && id.indexOf('username') === -1) {
                $(this).prop('disabled', '');
            }
        });

        //move the profile pic div down by setting margin top to 1.8em
        $('#currentProfilePic').css("margin-top", "");
        $('#profilePicSelection').show();
    }

    function cancelEditUser() {
        $('#save_button').addClass('fa-disabled');
        $('#cancel_button').addClass('fa-disabled');
        $('#edit_button').removeClass('fa-disabled');

        $('#edit_button').find("span").css("cursor", "default");
        $('#cancel_button').find("span").css("cursor", "not-allowed");
        $('#save_button').find("span").css("cursor", "not-allowed");

        $('#userUpdateForm').find(':input, #changePass, #annotationsSelect').each( function (index) {
            if ($(this).attr('id') !== undefined)
                if ($(this).attr('id').indexOf('password') === -1) {
                    $(this).prop('disabled', 'disabled');
                }
        });
        $('#profilePicSelection').hide();
        $('#currentProfilePic').css("margin-top", "1.8em");

        resetImage(window.imgSrc);
        document.getElementById('imageForm').reset();
    }

    function followUser() {
        $.post("${createLink(controller: "user", action: "followUser", params: [id: user.id])}", function (data) {
            if (data.success === true) {
                toggleFollowButton();
            }
        });
    }

    function unFollowUser() {
        $.post("${createLink(controller: "user", action: "unFollowUser", params: [id: user.id])}", function (data) {
            if (data.success === true) {
                toggleFollowButton();
            }
        });
    }

    function toggleFollowButton() {
        var classList = $('#follow_button').find('i').attr('class');

        if (classList.indexOf('fa-user-times') !== -1) {
            $('#follow_button').find('i').removeClass('fa-user-times');
            $('#follow_button').find('i').addClass('fa-user-plus');
            $('#follow_button').find('span').text("Follow");
            $('#follow_button').attr('onclick', 'followUser();');
        } else {
            $('#follow_button').find('i').removeClass('fa-user-plus');
            $('#follow_button').find('i').addClass('fa-user-times');
            $('#follow_button').find('span').text("Un-Follow");
            $('#follow_button').attr('onclick', 'unFollowUser();');
        }
    }

    function updateUser() {
        $('#username').attr('disabled', '');

        var formData = new FormData();

        //get all the data from all the forms from the wizard for the types in the find clause
            $('#userUpdateForm').find('textarea, input, select').each ( function() {
                if($(this).attr('id') !== undefined) {
                    if ($(this).prop('value') !== null && $(this).prop('value').length > 0 ) {
                        formData.append($(this).attr('id'), $(this).prop('value'));
                    }
                }
            });

        //for multiselect
        $('.multiple-select').each( function () {
            var controlId       = $(this).attr('id');
            var selections      = $(this).select2('data');
            var selectedIds     = [];

            for (index in selections) {
                if (selectedIds.indexOf(selections[index].id) === -1) {
                    selectedIds[index] = selections[index].id;
                }
            }
            if (formData.has(controlId)) {
                formData.set(controlId, selectedIds);
            } else {
                formData.append(controlId, selectedIds);
            }
        } );


        //For file inputs: data and code are optional
        $('input:file').each( function () {
            var elementId    = $(this).attr('id');
            var file         = document.getElementById(elementId).files[0];
            if (typeof file !== "undefined") {
                if (formData.has(elementId)) {
                    formData.set(elementId, file);
                } else {
                    formData.append(elementId, file);
                }
            }
        });

        $.ajax({
            type: "POST",
            url: "${createLink(controller: "user", action: "updateUser")}",
            data: formData,
            contentType: false,
            processData: false,
            success : function () {
                cancelEditUser();
            }
        });


        $('#username').attr('disabled', 'disabled');
    }

    function changePassword() {
       conole.log('Add mechanism to change password');
    }
</script>