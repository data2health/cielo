<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<section class="engine"></section><section class="cid-qIklYeJO9W mbr-fullscreen jarallax-scroll" id="header15-w">

    <div class="mbr-overlay" style="opacity: 0.2; background-color: rgb(15, 118, 153);"></div>

    <div class="container align-center">
        <div class="row justify-content-md-center">
            <div class="mbr-white col-md-10" style="padding-top: 2em;">
                <h1 class="mbr-section-title mbr-bold pb-3 mbr-fonts-style display-2">An open science environment for health analytics</h1>
                <p class="mbr-text pb-3 mbr-fonts-style display-5">
                    CIELO fosters multi-disciplinary collaboration in health analytics, allowing users to share and collaborate across distributed research activities.
                </p>
            </div>
        </div>
        <g:render template="/templates/alerts"/>
        <div class="row">
            <div class="col-lg-12">
                <div class="form-container">
                    <div class="media-container-column">
                        <form action="${createLink(controller: "registration", action: "saveNewUser")}" method="post" name="registerForm" class="form-horizontal"
                              role="form" enctype="multipart/form-data" id="registerForm" novalidate
                              style="padding-top: 2em;">
                            <div class="form-group row">
                                <label for="firstName" class="col-sm-2 form-control-label">
                                    First Name
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-3">
                                    <input id="firstName" name="firstName" class="form-control" placeholder="First Name" value="" required="" aria-required="true">
                                    <div id="firstNameInvalidFeedBack" class="col-md-10 invalid-feedback"></div>
                                </div>

                                <label for="lastName" class="col-sm-2 form-control-label">
                                    Last Name
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-3">
                                    <input id="lastName" name="lastName" class="form-control" placeholder="Last Name" value="" required="" aria-required="true">
                                    <div id="lastNameInvalidFeedBack" class="col-md-10 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="username" class="col-sm-2 form-control-label">
                                    Username
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-8">
                                    <input class="form-control" name="username" id="username" placeholder="Username" value="" required="" aria-required="true">
                                    <div id="usernameInvalidFeedBack" class="col-md-4 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="password" class="col-sm-2 form-control-label">
                                    Password
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-8">
                                    <input type="password" class="form-control" name="password" id="password" placeholder="Password" required="" aria-required="true">
                                    <div id="passwordInvalidFeedBack" class="col-md-12 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="confirmPassword" class="col-sm-2 form-control-label">
                                    Confirm Password
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-8">
                                    <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" placeholder="Confirm Password" required="" aria-required="true">
                                    <div id="confirmPasswordInvalidFeedBack" class="col-md-4 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="email" class="col-sm-2 form-control-label">
                                    Email
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-8">
                                    <input type="email" class="form-control" name="emailAddress" id="email" placeholder="Email" value="" required="" aria-required="true">
                                    <div id="emailInvalidFeedBack" class="col-md-5 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="timezoneId" class="col-sm-2 form-control-label">
                                    Timezone
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-8">
                                    <g:customTimeZoneSelect id="timezoneId"/>
                                </div>
                            </div>

                            <div id="institutionDiv" class="form-group row">
                                <label for="institution" class="col-sm-2 form-control-label">
                                    Institution
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="form-group col-sm-8">
                                    <select id="institution" name="institution.id" required="required" class="form-control"
                                            onchange="onInstitutionChange()" aria-required="true">
                                        <option value="">Select Institution</option>
                                        <g:each var="institute" in="${institutes}">
                                            <option value="${institute.id}">${institute.fullName}</option>
                                        </g:each>
                                        <option value="-1">Other</option>
                                    </select>
                                    <div id="institutionInvalidFeedBack" class="col-md-4 invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="profile" class="col-sm-2 form-control-label">
                                    Background
                                </label>

                                <div class="col-sm-8">
                                    <textarea id="profile" name="profile" class="form-control" rows="4"></textarea>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="profilePic" class="col-sm-2 form-control-label">
                                    Profile Picture
                                </label>
                                <div class="col-sm-2">
                                    <div id="currentProfilePic">
                                        <asset:image src="default_profile.png" sizes="100x100"
                                                     class="img-circle img-responsive img-profile-md img-thumbnail noBorder"
                                                     style="height: 70px; width: 70px;background-color:  transparent;"/>
                                    </div>
                                    <div>
                                        <input type="file" name="profilePic" id="profilePic" value="" onchange="validateProfilePic(this);">
                                    </div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="annotations" class="col-sm-2 form-control-label">
                                    Research Interests
                                </label>

                                <div class="col-sm-8 ui-widget">
                                    <span id="annotations" class="">
                                        <select class="multiple-select form-control" name="annotations-select" multiple="multiple">
                                            <g:each in="${annotations}" var="annotation">
                                                <option value="${annotation.id}">${annotation.label}</option>
                                            </g:each>
                                        </select>
                                    </span>
                                </div>
                            </div>

                            <div class="form-group row">
                                <label class="col-sm-8 col-sm-offset-2" for="agreement">
                                    <input type="checkbox" value="true" name="agreement" id="agreement" required="" aria-required="true">
                                    <span>I have read and agree with the
                                        <button onclick="showTermsOfUse()" style="padding-left: 0px;" type="button" class="btn btn-link">Terms of use</button></span>
                                </label>
                            </div>
                            <div class="form-group row">
                                <div id="agreementInvalidFeedBack" class="col-md-7 invalid-feedback"></div>
                            </div>


                            <div class="form-group row">
                                <div class="col-sm-12">
                                    <button type="submit" class="btn btn-primary" id="register">
                                        join
                                    </button>
                                    <button id="resetButton" type="reset" value="Reset" class="btn btn-primary" onclick="resetImage()">reset
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/footerlncludes"/>