window.doneLoading = true;
window.usernames = ("${usernames.replaceAll(' ', '')}").split(',');
window.passwordMatcher = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");

window.addEventListener('load', function (event) {
    $('[data-toggle="tooltip"]').tooltip();
}, false);

$('.panel-collapse ').on('hide.bs.collapse', function(event) {
    var chevronUp = $('#' + event.target.id).prev().find('i');

    chevronUp.removeClass("fa-chevron-up");
    chevronUp.addClass("fa-chevron-down");
});

$('.panel-collapse').on('show.bs.collapse', function() {
    var chevronDown;

    if (event.target.id.endsWith('_i')) {
        chevronDown = $('#' + event.target.id);
    } else {
        //its not the icon element, likely the parent
        chevronDown = $('#' + event.target.id).find('i');
    }
    chevronDown.removeClass("fa-chevron-down");
    chevronDown.addClass("fa-chevron-up");
});

$( function() {

    //hook onto global ajax events
    $(document).bind("ajaxSend", function() {
        showWaitDialog();
    }).bind("ajaxComplete", function(event, xhr, ajaxOptions) {
        hideWaitDialog();

        //session expired
        if (xhr.responseText === "Unauthorized") {
            bootbox.dialog({
                title: 'Session Expired',
                message: "Credentials required. Click OK to navigate to login page or cancel to remain on current page.",
                buttons: {
                    cancel: {
                        label: "Cancel",
                        className: 'btn-red'
                    },
                    ok: {
                        label: "OK",
                        className: 'btn-primary',
                        callback: function() {
                            window.location.reload();
                        }
                    }
                }
            });
        } else {
            if (xhr.responseJSON !== undefined) {
                for (obj in xhr.responseJSON["messages"]) {
                    showAlert(xhr.responseJSON["messages"][obj], obj);
                }
            }
        }
    });

    //autoload count initialization
    sessionStorage.setItem("autoloadCount", 1);

    //custom data attributes will show on mouseenter and mouseleave
    $('.date-time').hover(
        function() {
            $(this).html($(this).attr('data-date'));
        },
        function() {
            $(this).html($(this).attr('data-diff'));
        }
    );


    $('.jarallax').jarallax({
        type: 'scale',
        speed: -0.5
    });

    $('.jarallax-scroll').jarallax({
        speed: -0.2
    });

    $(".dropdown-toggle").dropdown();
    $('[data-toggle=".tooltip"]').tooltip(
        {
            container: 'body',
            placement: "bottom"
        }
    );

    $("#sidebar-toggle-button").on("click", function () {
        if ($('#sidebar-options').attr("class").indexOf('hidden') > -1) {
            $("#sidebar-toggle-button").removeClass("collapse-icon-collapsed");
            $("#sidebar-toggle-button").removeClass("fa-angle-double-right");
            $("#sidebar-options").removeClass("col-md-0");
            $("#activity").removeClass("col-md-8");
            $("#collapse-panel").removeClass('collapse-icon-div-collapsed');
            $('#connections-nav-tab').removeClass('hidden');
            $("#sidebar-options").removeClass("hidden");
            $("#sidebar-options").addClass("col-md-3");
            $("#sidebar-toggle-button").addClass("fa-angle-double-left");

            //activity div
            $("#activity").addClass("col-md-6");

            $(".sidebar-announcements").css("left","");
            //show all children
            $("#sidebar-options").children().show();
        } else {
            $("#sidebar-options").children().hide();
            $("#sidebar-toggle-button").removeClass("fa-angle-double-left");
            $("#sidebar-options").removeClass("col-md-3");
            $("#activity").removeClass("col-md-6");
            $("#sidebar-toggle-button").addClass("collapse-icon-collapsed");
            $("#sidebar-toggle-button").addClass("fa-angle-double-right");
            $("#sidebar-options").addClass("col-md-0");
            $("#sidebar-options").addClass("hidden");

            $("#collapse-panel").addClass('collapse-icon-div-collapsed');

            //activity div
            $("#activity").addClass("col-md-8");

            $(".sidebar-announcements").css("left", "7vw");
        }
    });

    //remove was-validated so that we're back to square one
    $('#resetButton').on('click', function() {
        $('#registerForm').removeClass("was-validated");
    });

    $('#registerForm').on('submit', function(event) {
        var failure = false;

        if (this.checkValidity() === false) {
            failure = true;
        }

        //validate firstname
        if ($('#firstName').val().trim() === "") {
            $('#firstNameInvalidFeedBack').html("Please enter your first name");
        }

        //validate lastname
        if ($('#lastName').val().trim() === "") {
            $('#lastNameInvalidFeedBack').html("Please enter your last name");
        }

        //validate username
        if (window.usernames.indexOf($('#username').val()) !== -1) {
            //this failure requires some manual manipulation to ensure the error message is shown and the
            //failure highlighting occurs since the field is not blank
            $('#username').addClass('is-invalid');
            $('#usernameInvalidFeedBack').html("Username must be unique, please choose another");
            failure = true;
        } else if ($('#username').val().trim() === "") {
            $('#usernameInvalidFeedBack').html("Please enter a username");
        }

        //validate password
        if ($('#password').val().trim() === "") {
            $('#passwordInvalidFeedBack').removeClass("col-md-12");
            $('#passwordInvalidFeedBack').addClass("col-md-4");
            $('#passwordInvalidFeedBack').html("Please enter a password");
        } else if (!window.passwordMatcher.test($('#password').val())) {
            $('#passwordInvalidFeedBack').removeClass("col-md-4");
            $('#passwordInvalidFeedBack').addClass("col-md-12");
            $('#passwordInvalidFeedBack').html("Password must be at least 8 chars and contain at least 1 numeric, " +
                "1 uppercase and 1 special char: !@#$%^&*");
        }

        //validate confirm password
        if ($('#confirmPassword').val() !== $('#password').val()) {
            $('#confirmPasswordInvalidFeedBack').html("Passwords need to match");
        }

        //validate email
        if ($('#email').val().trim() === "") {
            $('#emailInvalidFeedBack').html("You must enter an email address");
        }

        //validate institution
        if ($('#institution').find(":selected").text() === "Select Institution") {
            $('#institutionInvalidFeedBack').html("You must select an institution");
        }

        if ($('#agreement').prop('checked') === false) {
            $('#agreement').addClass('is-invalid');
            $('#agreementInvalidFeedBack').html("You must read and agree to Terms of Use");
            failure =  true;
        }

        if (failure === true) {
            event.preventDefault();
            event.stopPropagation();
        }
    });

    $('.multiple-select').select2();

    $('#addActivity').click(function () {
        bootbox.dialog({
            title: 'Create a Post',
            message: "<div class=\"form-group row\">\n" +
            "         <div class=\"col-sm-12\">\n" +
            "             <input id=\"title\" name=\"title\" class=\"form-control\" placeholder=\"Enter post title\"\n" +
            "                    required=\"\" aria-required=\"true\">\n" +
            "             <div id=\"titleInvalidFeedBack\" class=\"col-md-10 invalid-feedback\"></div>\n" +
            "         </div></div>" +
            "<div class=\"form-group row\">\n" +
            "<div class=\"col-sm-12\">\n" +
            "    <textarea id=\"postMessage\" name=\"postMessage\" class=\"form-control\" rows=\"4\" placeholder=\"Enter message here\"></textarea>\n" +
            "    <div id=\"postMessageInvalidFeedBack\" class=\"col-md-10 invalid-feedback\"></div>\n" +
            "</div></div>",
            buttons: {
                cancel: {
                    label: "Cancel",
                    className: 'btn-red'
                },
                ok: {
                    label: "Save",
                    className: 'btn-primary',
                    callback: function() {
                        var title   = $('#title').val();
                        var message = $('#postMessage').val();

                        $('#titleInvalidFeedBack').text("");
                        $('#postMessageInvalidFeedBack').text("");

                        if (title.length === 0 || message.length === 0) {
                            if (title.length === 0) {
                                $('#titleInvalidFeedBack').text("*Required");
                            }

                            if (message.length === 0) {
                                $('#postMessageInvalidFeedBack').text("*Required");
                            }
                            return false;
                        } else {
                            $.post("/activity/post", {title: title, message: message}, function(data) {
                                if (data.success) {
                                    $('.activity-feed #activity').load("/activity/getActivities");
                                }
                            });
                        }
                    }
                }
            }
        });
    });

    $('#projectSearch').keypress( function(event) {
        if ( event.which == 13 ) {
            event.preventDefault();

            var filterText = $('#projectSearch').val();
            var isMyProjects;

            if ($('#projectsTable th').text().indexOf('Owner') === -1) {
                isMyProjects = true;
            } else {
                isMyProjects = false;
            }

            $.get("/project/filtered/list", {myProjects: isMyProjects, filterTerm: filterText}, function (data) {
                replaceProjectTableContent(data);
            });
        }
    });

    $('#projectSearch').on('keyup', function (event) {
        if ($('#projectSearch').val().length !== 0){
            $('#projectSearchClear').css('display', 'block');
        } else {
            $('#projectSearchClear').css('display', 'none');
        }
    })
});

function getAttributes(element) {
    var output = "";
    if (element.hasAttributes()) {
        var attrs = element.attributes;
        output = "";
        for (var i = attrs.length - 1; i >= 0; i--) {
            output += attrs[i].name + "->" + attrs[i].value;
        }

    } else {
        output = "No attributes to show";
    }
    return output;
}

function showTermsOfUse() {
    var alertWindow = bootbox.dialog({
        title: 'Terms of Use',
        className: 'scrollable-bootbox-alert',
        message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
        closeButton: false,
        size: "large",
        buttons: {
            ok: {
                id: 'agreeButton',
                className: 'btn-secondary',
                label: 'I agree with the Terms of Use',
                callback: function () {
                    $('#register').prop('disabled', false);
                   return true;
                }
            },
            cancel: {
                id: 'disagree',
                className: 'btn-red',
                label: 'I disagree',
                callback: function () {
                    $('#register').prop('disabled', 'disabled');
                    showAlert('You will not be able to register until accepting the terms of use', 'info');
                    return true;
                }
            }
        }
    });

    alertWindow.init(function() {
        //grab the text for the license from db
        $.get("/license/termsOfUse", function (data) {
            alertWindow.find('.bootbox-body').html(data);
        });
    });
}

function showTermsOfUseNoAcknowledge() {
    var alertWindow = bootbox.dialog({
        title: 'Terms of Use',
        className: 'scrollable-bootbox-alert',
        message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
        closeButton: true,
        size: "large",
        buttons: {
            ok: {
                className: 'btn-secondary',
                label: 'OK',
                callback: function () {
                    return true;
                }
            }
        }
    });

    alertWindow.init(function() {
        //grab the text for the license from db
        $.get("/license/termsOfUse", function (data) {
            alertWindow.find('.bootbox-body').html(data);
        });
    });

}

function showSoftwareLicense(licenseId, licenseLabel) {
    var alertWindow = bootbox.alert({
        title: licenseLabel,
        message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
        closeButton: false,
        size: "large"
    });

    //make body of dialog scrollable
    alertWindow.find('.bootbox-body').addClass("scrollable-bootbox-alert");

    //grab the text for the license from db
    $.get("/license/getLicenseBody/" + licenseId, function (data) {
        alertWindow.find('.bootbox-body').html('<pre>'+ data.licenseText +'</pre>');
    });
}

function onInstitutionChange() {
    var institutionSelected = $('#institution').val();

    //if selected other then we need other input in order to create a new institute
    if (institutionSelected === "-1") {
        $("<div id=\"newInstitutionDiv\" class=\"form-group row\">" +
            "<label for=\"institutionFName\" class=\"col-sm-2 form-control-label\">" +
            "Institution Name" +
            "<span class=\"required-indicator\">*</span>" +
            "</label>" +
            "" +
            "<div class=\"form-group col-sm-3\">" +
            "<input id=\"institutionFName\" name=\"institutionFName\" class=\"form-control\" placeholder=\"Institution Full Name\" value=\"\" required=\"\" aria-required=\"true\">" +
            "<div id=\"institutionFNameInvalidFeedBack\" class=\"col-md-10 invalid-feedback\"></div>" +
            "</div>" +
            "" +
            "<label for=\"institutionSName\" class=\"col-sm-2 form-control-label\">" +
            "Institution Short Name" +
            "<span class=\"required-indicator\">*</span>" +
            "</label>" +
            "" +
            "<div class=\"form-group col-sm-3\">" +
            "<input id=\"institutionSName\" name=\"institutionSName\" class=\"form-control\" placeholder=\"Institution Short Name\" value=\"\" required=\"\" aria-required=\"true\">" +
            "<div id=\"institutionSNameInvalidFeedBack\" class=\"col-md-10 invalid-feedback\"></div>" +
            "</div>" +
            "</div>").insertAfter("#institutionDiv");
    } else {
        //remove the old controls
        if ($('#newInstitutionDiv').prop('id') != undefined) {
            $('#newInstitutionDiv').remove();
        }
    }
}

function handleInfiniteScroll(event) {
    if ((isInViewport(document.querySelector('#contact-us-form')) && isInViewport(document.querySelector('#olderContent'))) &&
        document.querySelector('#no-more-activity') === null && !$('#loadOlderActivity').is(":visible")) {

        setTimeout(function(){
            $("#loading-activity-indicator").show();

            setTimeout(function(){
                //if still visible; allows for auto-cancel if you scroll back up before timer goes off
                //if the manual button load
                if ((isInViewport(document.querySelector('#contact-us-form')) && isInViewport(document.querySelector('#olderContent'))) &&
                    document.querySelector('#no-more-activity') === null && !$('#loadOlderActivity').is(":visible")) {
                    var offset  = Number($('#offset').html());
                    var max     = Number($('#max').html());

                    if (!(isNaN(offset) && isNaN(max)) && window.doneLoading === true) {
                        window.doneLoading = false;
                        getOlderActivity(offset, max);
                    }
                } else {
                    $("#loading-activity-indicator").hide();
                }
            }, 500);
        }, 500);
    }
}

function showCommentBox(activityId, id) {

    if ($('#comment_box_' + id).is(':visible') === false) {
        var tooltipId = $("#comment-tooltip-" + activityId).attr("aria-describedby");

        $("#" + tooltipId).removeClass("show");
        $('#comment_box_' + id).addClass("comment-add-box-visible").hide().show(200);
    }
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
    $('#comment_box_' + id).removeClass("comment-add-box-visible").hide(200);
}

function cancelComment(id) {
    //reset state of textbox
    $("#comment-box-text-" + id).val("");
    $('#comment_box_' + id).removeClass("comment-add-box-visible").hide(200);
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
        $('[data-toggle="tooltip"]').tooltip('update');
    });
}

function getOlderActivity(offset, max) {

    $("#nextPage").remove();
    $("<div></div>").load("/activity/getActivities?offset="+offset+"&max="+max, function () {
        $(this).insertBefore($("#olderContent")).hide().show('fast', function() {
            window.doneLoading = true;
            $('[data-toggle="tooltip"]').tooltip('update');
            $('.date-time').hover(
                function() {
                    $(this).html($(this).attr('data-date'));
                },
                function() {
                    $(this).html($(this).attr('data-diff'));
                }
            );
        });

        //check whether we need to show the load more button or not
        if (sessionStorage.getItem("autoloadCount")) {
            var count = Number(sessionStorage.getItem("autoloadCount"));

            if (count === 3) {
                //show the load button and reset the count... make sure that the infinite scroll  if statement
                //above for isInViewport also checks if the button is visible if so, then skip this code
                $('#loadOlderActivity').show();
            } else {
                sessionStorage.setItem("autoloadCount", Number(count + 1));
            }
        } else {
            $('#loadOlderActivity').hide();
            sessionStorage.setItem("autoloadCount", 1);
        }
    });

    $("#loading-activity-indicator").hide();
}

function likePost(activityId, id) {
    var tooltipId = $("#like_tooltip_" + activityId).attr("aria-describedby");
    $("#" + tooltipId).removeClass("show");

    $.post("/activity/likePost", {'id': activityId}, function (data) {
        if (data.success === true) {
            reloadActivity(activityId)
        }
    });
}

function removeActivityLike(activityId, id) {
    var tooltipId = $("#like_tooltip_" + activityId).attr("aria-describedby");
    $("#" + tooltipId).removeClass("show");

    $.post("/activity/removelike", {'id': activityId}, function (data) {
        if (data.success === true) {
            reloadActivity(activityId);
        }
    });
}

function reloadActivity(activityId) {
    $.post("/activity/getActivity/",{id: activityId}, function (data) {
        $("#activity_post_" + activityId).html(data);
    });
}

function sharePost(activityId, id) {
    var tooltipId = $("#share-tooltip-" + activityId).attr("aria-describedby");
    $("#" + tooltipId).removeClass("show");
}

function validateProfilePic(input) {
    if (window.File && window.FileReader && window.FileList && window.Blob) {
        if(input) {
            var selectedFile = input.files[0];
            if(selectedFile) {
                var fileType = selectedFile.type;
                var control = $("#profilePic");

                // Only process image files.
                switch (fileType) {
                    case 'image/png':
                    case 'image/gif':
                    case 'image/jpeg':
                    case 'image/pjpeg':
                        break;
                    default:
                        control.val("");
                        alert('Error: Unsupported file type detected. Supported types: PNG, GIF, JPEG');
                        return;
                }

                //get the file size and file type from file input field
                var fsize = selectedFile.size;
                if (fsize > 1048576) //do something if file size more than 1 MB (1048576 bytes)
                {
                    control.val("");
                    alert("File too large. (" + fsize + "). Please limit uploads to files less than one MiB.");
                    return;
                }

                var reader = new FileReader();
                reader.onload = function(e) {
                    $("#currentProfilePic").find("img").attr("src", e.target.result);
                };

                reader.readAsDataURL(selectedFile);
            }
        }
        else {
            alert("Input was unrecognized.");
        }
    } else {
        alert("This function needs a newer browser.");
    }
}

function resetImage(imageToResetTo) {
    if (imageToResetTo === undefined) {
        $("#currentProfilePic").find("img").attr("src", "/assets/default_profile.png");
    } else {
        $("#currentProfilePic").find("img").attr("src", imageToResetTo);
    }
}

function showAllUsersModal(id, url) {
    var usersWindow = bootbox.alert({
        title: '',
        message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
        closeButton: false,
        size: "small",
        className: 'dark-theme'
    });

    //make body of dialog scrollable
    usersWindow.find('.bootbox-body').addClass("scrollable-bootbox-alert");

    if (url) {
        $.get(url, {id: id},  function (data) {
                    usersWindow.find('.bootbox-body').html(data);});
    } else {
        usersWindow.find('.bootbox-body').html("<div class=\"jumbotron-fluid\">" + url + " not supported</div>");
    }
}

function showAlert(message, level) {
    var backgroundColor;
    var title;
    var icon;

    switch (level.toLowerCase()) {
        case 'danger':
            icon = "fas fa-times";
            title = "Error";
            backgroundColor = "#ffafb4f6";
            break;
        case 'success':
            icon = "far fa-check-circle";
            title = "Success";
            backgroundColor = "#a6efb8f6";
            break;
        case 'warning':
            icon = "fas fa-exclamation-triangle";
            title = "Warning";
            backgroundColor = "#ffcfa5f6";
            break;
        default:
            icon = "fas fa-info-circle";
            title = "Info";
            backgroundColor = "#9ddefff6";
    }

    iziToast.show({
        title: title,
        icon: icon,
        message: message,
        position: 'topCenter',
        progressBarColor: '#a6efb8',
        backgroundColor: backgroundColor,
        zindex: 1040
    });
    $('.iziToast-wrapper').removeClass('iziToast-wrapper-topCenter');
    $('.iziToast-wrapper').addClass('iziToast-wrapper-topRight');
}

function showNewProjectWizard() {
    var dialog = bootbox.dialog({
        title: ' ',
        className: "new-project-wizard",
        message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
        closeButton: false,
        size: 'large',
        buttons: {
            previous: {
                id: 'previous-button',
                label: "<i class='far fa-arrow-alt-circle-left'></i>",
                className: 'btn-link wizard-button previous',
                callback: function() {
                    var stepId = $('.current-step').attr('id');
                    handlePrevious();
                    return false;
                }
            },
            next: {
                id: 'next-button',
                label: "<i class='far fa-arrow-alt-circle-right'></i>",
                className: 'btn-link wizard-button next',
                callback: function(){
                    handleNext();
                    return false;
                }
            },
            cancel: {
                label: "Cancel",
                className: 'btn-red cancel-btn'
            },
            ok: {
                label: "Continue",
                className: 'btn-primary continue-btn',
                callback: function(){
                    var idOfCurrentStep     = $($('#screens').find('.screen.current-step')).attr('id');
                    var indexOfCurrentStep  = window.stepIds.indexOf(idOfCurrentStep);
                    var indexOfNextStep     = indexOfCurrentStep + 1;
                    var idOfNextStep        = window.stepIds[indexOfNextStep];

                    //hide the form body and get next one to show
                    transitionStep(idOfCurrentStep, idOfNextStep, indexOfNextStep);

                    $('.continue-btn').hide();
                    $('.cancel-btn').hide();
                    $('.cancel-btn').css('right', '3em');
                    $('.cancel-btn').show();

                    //show other controls
                    $('.wizard-button.previous').css('display', 'flex');
                    $('.wizard-button.next').css('display', 'flex');

                    return false
                }
            },
            finish: {
                label: "Save Project",
                className: 'finish-btn btn-primary',
                callback: function() {
                    handleSave();
                }
            }
        }
    });

    dialog.init(function(){
        $('.wizard-button.previous').css('display', 'none');
        $('.wizard-button.next').css('display', 'none');
        $('.finish-btn').css('display', 'none');
        $('.modal-footer').css('display', 'inline-flex');

        $.get("/project/newProject", function (data) {
            $('.modal-body').html(data);

            initStepIds();

            //initial title
            $('.modal-header').html($('#' + window.stepIds[0] + '_title').clone());
        });
    });
}

function initStepIds() {
    var wizardWindows = $('#screens').find('.screen');
    window.stepIds = new Array(wizardWindows.length);

    wizardWindows.each( function(index) {
        window.stepIds[index] = $(wizardWindows[index]).attr('id');
    });
}

function grabFormData(formParentSelector) {
    var formData = new FormData();

    //get all the data from all the forms from the wizard for the types in the find clause
    $(formParentSelector).each ( function() {
        $(this).find("textarea, input:not('.form-check-input'), select").each ( function() {
            if($(this).attr('id') !== undefined && $(this).attr('disabled') === undefined) {
                if ($(this).prop('value') !== null && $(this).prop('value').length > 0 ) {
                    formData.append($(this).attr('id'), $(this).prop('value'));
                }
            }
        });
    });

    //For file inputs: data and code are optional
    $('input:file').each( function () {
        var elementId    = $(this).attr('id');
        var file         = document.getElementById(elementId).files[0];
        if (typeof file !== 'undefined' && elementId !== 'undefined' &&
            $(this).attr('disabled') === undefined && typeof file !== "string") {
            if (formData.has(elementId)) {
                formData.set(elementId, file);
            } else {
                formData.append(elementId, file);
            }
        }
    });

    return formData;
}

function showWaitDialog() {
    $('#busyDiv').show();
}

function hideWaitDialog() {
    $('#busyDiv').hide();
}

function replaceProjectTableContent(data) {
    var currentPageCount = $('#paging-options').find('option').length;

    //fix the number of pages
    if (currentPageCount !== data.pagesCount) {
        //rebuild the paging options
        $('#paging-options option').remove();

        for (var index = 1; index <= data.pagesCount; index++) {
            $('#paging-options').append('<option value="' + index +'">' + index + '</option>');
        }

        $('#ofPages').text('of ' + data.pagesCount);
    }

    //remove old rows
    $('#projectTableBody').find('tr').remove();

    //replace rows
    $('#projectTableBody').html(data.html);

    //disable/enable buttons as necessary
    updateToolbarButtons();
}

function onNextPage() {
    if ($('#right-table-toolbar i').first().attr('class').indexOf('fa-disabled') === -1) {
        var offsetVal = $('#paging-options').val();
        $('#paging-options').val((parseInt(offsetVal) + 1 ));
        $('#paging-options').trigger("change");
    }
}

function onPreviousPage() {
    if ($('#left-table-toolbar i').first().attr('class').indexOf('fa-disabled') === -1) {
        var offsetVal = $('#paging-options').val();
        $('#paging-options').val((parseInt(offsetVal) - 1 ));
        $('#paging-options').trigger("change");
    }
}

function onFirstPage() {
    if ($('#left-table-toolbar i').first().attr('class').indexOf('fa-disabled') === -1) {
        $('#paging-options').val(1);
        $('#paging-options').trigger("change");
    }
}

function onLastPage() {
    if ($('#right-table-toolbar i').first().attr('class').indexOf('fa-disabled') === -1) {
        var lastPage = $('#paging-options').find('option').length;
        $('#paging-options').val(lastPage);
        $('#paging-options').trigger("change");
    }
}

function onPageSelection(isMyProjects) {
    var offsetVal  = parseInt($('#paging-options').val()) - 1;
    var filterText = $('#projectSearch').val();
    $.get("/project/filtered/list", {offset: offsetVal, myProjects: isMyProjects, filterTerm: filterText}, function (data) {
        replaceProjectTableContent(data);
    });
}

function updateToolbarButtons() {
    var offset = parseInt($('#paging-options').val());
    var numberOfPages = parseInt($('#ofPages').text().trim().split('of ')[1]);

    if (offset <= 0 || offset === 1) {
        $('#left-table-toolbar').find('i').each( function() {
            $(this).addClass('fa-disabled');
        });

        $('#right-table-toolbar').find('i').each( function() {
            $(this).removeClass('fa-disabled');
        });
    } else {
        $('#left-table-toolbar').find('i').each( function() {
            $(this).removeClass('fa-disabled');
        });
    }

    if (offset === numberOfPages) {
        $('#right-table-toolbar').find('i').each( function() {
            $(this).addClass('fa-disabled');
        });

        if (offset !== 1) {
            $('#left-table-toolbar').find('i').each( function() {
                $(this).removeClass('fa-disabled');
            });
        }
    } else {
        $('#right-table-toolbar').find('i').each( function() {
            $(this).removeClass('fa-disabled');
        });
    }
}

function clearProjectSearch() {
    $('#projectSearch').val("");
    $('#projectSearch').keyup();

    //now trigger enter
    var e = $.Event("keypress");
    e.which = 13;
    e.keyCode = 13;
    $('#projectSearch').trigger(e)
}