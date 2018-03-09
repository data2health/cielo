<g:render template="/templates/headerIncludes"/>

<body>
<g:render template="/templates/navbar"/>

<section class="engine"></section><section class="cid-qIklYeJO9W mbr-fullscreen mbr-parallax-background" id="header15-w">

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
                        <form role="mbr-form" id="loginForm" action="/login/authenticate" method="POST" autocomplete="off" class="form-signin"
                              role="form" enctype="multipart/form-data" id="registerForm" novalidate
                              style="padding-top: 2em;">
                            <div class="form-group row">
                                <label for="username" class="col-sm-4 form-control-label">
                                    Username
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-6">
                                    <input type="input" class="form-control" name="username" id="username" placeholder="username" value="" required="required" autofocus="autofocus">
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="password" class="col-sm-4 form-control-label">
                                    Password
                                    <span class="required-indicator">*</span>
                                </label>

                                <div class="col-sm-6">
                                    <input type="password" class="form-control" name="password" id="password" placeholder="password" required="required" value="">
                                </div>
                            </div>

                            <div class="form-group row">
                                <label for="rememberMe" class="col-sm-4 form-control-label">
                                    <input type="hidden" name="_remember-me"><input type="checkbox" name="remember-me" id="rememberMe">&nbsp;
                                remember me
                                </label>
                            </div>

                            <div class="form-group">
                                <span class="row input-group-btn">
                                    <button form="loginForm" type="submit" id="submit" class="col-sm-3 btn btn-primary btn-md btn-block">
                                        log in
                                    </button>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section>

<g:render template="/templates/contactUsSection"/>

<div id="scrollToTop" class="scrollToTop mbr-arrow-up"><a style="text-align: center;"><i></i></a></div>
<input name="animation" type="hidden">
</body>

<g:render template="/templates/footerlncludes"/>