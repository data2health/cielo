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
    var alertWindow = bootbox.alert({
        title: 'Terms of Use',
        message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> Loading...</div>',
        closeButton: false,
        size: "large"
    });

    alertWindow.init(function() {
        setTimeout(function(){
            alertWindow.find('.bootbox-body').html('<div>some information here....</div>');
        }, 1500)
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