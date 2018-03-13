<section class="menu cid-qHAUKIofDN" once="menu" id="menu1-3">
    <sec:ifLoggedIn>
        <nav class="navbar navbar-expand beta-menu navbar-dropdown align-items-center navbar-fixed-top navbar-toggleable-sm">
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <nav class="navbar navbar-expand beta-menu navbar-dropdown align-items-center navbar-fixed-top navbar-toggleable-sm bg-color transparent">
    </sec:ifNotLoggedIn>
        <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <div class="hamburger">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
            </div>
        </button>
        <div class="menu-logo">
            <div class="navbar-brand">
                <span class="navbar-logo">
                    <asset:image src="cielo-blue-new-240x150.png" alt="CIELO" title="" style="height: 2.5rem;"/>
                </span>
                <span class="navbar-caption-wrap">
                    <a class="navbar-caption text-secondary display-5" href="/">CIELO</a>
                    <div class="preProdText">Beta!</div>
                </span>
            </div>
        </div>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <sec:ifLoggedIn>
                <ul class="nav navbar-nav navbar-right">
                    <li>
                        <a href="${createLink(controller: "home", action: "home")}">
                            <i class="fa fa-bullhorn"></i>
                            Activity
                        </a>
                    </li>
                    <li class="li-spacer d-block d-md-none">&nbsp;</li>
                    <li class="d-block d-md-none">
                        <a href="#">
                            <i class="fa fa-link"></i>
                            Connections
                        </a>
                    </li>
                    <li class="li-spacer">&nbsp;</li>
                    <li class="dropdown pull-right">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="false" aria-expanded="false">
                            <i class="fa fa-cubes"></i>
                            Bundles
                        </a>
                        <ul class="dropdown-menu arrow_box">
                            <li>
                                <a role="link" href="#">
                                    My Bundles
                                </a>
                            </li>
                            <li>
                                <a role="link" href="#">
                                    Public Bundles
                                </a>
                            </li>
                            <li>
                                <a role="link" href="#">
                                    <i class="fa fa-plus"></i>
                                    Create Bundle
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li class="li-spacer d-block d-md-none">&nbsp;</li>
                    <li class="dropdown pull-right d-block d-md-none">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                            <i class="fa fa-users"></i>
                            Teams
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <a role="link" href="#">
                                    My Teams
                                </a>
                            </li>
                            <li>
                                <a role="link" href="#">
                                    <i class="fa fa-plus"></i>
                                    New Team
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li class="li-spacer">&nbsp;</li>
                        <li class="dropdown pull-right">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                                <g:if test="${profile.picture}">
                                    <img src="data:image/png;base64,${profile.picture.fileContents}"
                                         style="" class="media-object img-profile img-circle img-responsive"/>
                                </g:if>
                                <g:else>
                                    <img src="assets/default_profile.png" class="img-circle img-responsive"
                                         style="max-width: 24px; max-height: 24px"/>
                                </g:else>
                                <sec:username/>
                                <span class="caret"></span></a>
                            <ul class="dropdown-menu arrow_box" style="margin-left: 1em;">
                                <li class="menu-with-icon">
                                    <a role="link" href="#">
                                        &nbsp;&nbsp;&nbsp;<span class="oi oi-cog"></span>
                                        Account
                                    </a>
                                </li>
                                <li><hr style="width: 100%;"></li>
                                <li class="menu-with-icon">
                                    <a role="link" href="/logout">
                                        &nbsp;&nbsp;&nbsp;<span class="oi oi-account-logout"></span>
                                        Logout
                                    </a>
                                </li>
                            </ul>
                        </li>
                        <li class="li-spacer">&nbsp;</li>
        </sec:ifLoggedIn>
        <sec:ifNotLoggedIn>
            <div class="navbar-buttons mbr-section-btn"><a class="btn btn-sm btn-primary display-4" href="/register">Join</a> <a class="btn btn-sm btn-primary display-4" href="${createLink(controller: 'login', action: 'auth')}">Login<br></a></div>
        </sec:ifNotLoggedIn>
        </div>
    </nav>
</section>