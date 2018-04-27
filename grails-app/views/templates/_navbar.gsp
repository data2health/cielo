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
                            <i class="fas fa-bullhorn"></i>
                            Activity
                        </a>
                    </li>
                    <li class="li-spacer d-block d-md-none">&nbsp;</li>
                    <li class="d-block d-md-none">
                        <span class="text-secondary" onclick="showUserDialog();">
                            <i class="fa fa-link"></i>
                            Connections
                        </span>
                    </li>
                    <li class="li-spacer">&nbsp;</li>
                    <li class="dropdown pull-right">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="false" aria-expanded="false">
                            <i class="fa fa-cube"></i>
                            Projects
                        </a>
                        <ul class="dropdown-menu arrow_box">
                            <li>
                                <a role="link" href="${createLink(controller: 'project', action: 'myProjects')}">
                                    My Projects
                                </a>
                            </li>
                            <li>
                                <a role="link" href="${createLink(controller: 'project', action: 'publicProjectsList')}">
                                    Public Projects
                                </a>
                            </li>
                            <li>
                                <button type="button" class="btn btn-link" onclick="showNewProjectWizard();"
                                        style="padding: 0;color: #149dcc;font-weight: 400; margin: 0;margin-left: 1em;">
                                    <i class="fa fa-plus"></i>
                                    New Project
                                </button>
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

                    <li>
                        <a href="#contact-us-form">
                            <i class="fa fa-envelope"></i>
                            Contact Us
                        </a>
                    </li>
                    <li class="li-spacer d-block d-md-none">&nbsp;</li>
                    <li class="dropdown pull-right" style="margin-left: 1em;">
                            <g:getUserProfilePic imageSize="small" showLink="${false}">
                            <span class="caret"></span>
                            <span style="color: #149dcc;">
                                <sec:username/>
                            </span>
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></a>
                            </g:getUserProfilePic>
                            </a>
                        <ul class="dropdown-menu arrow_box" style="margin-left: 1em;">
                            <li class="menu-with-icon">
                                <a role="link" href="${createLink(controller: 'user', action: 'view', params: [id: user.id])}">
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
                </ul>
        </sec:ifLoggedIn>
        <sec:ifNotLoggedIn>
            <div class="navbar-buttons mbr-section-btn"><a class="btn btn-sm btn-primary display-4" href="/register">Join</a> <a class="btn btn-sm btn-primary display-4" href="${createLink(controller: 'login', action: 'auth')}">Login<br></a></div>
        </sec:ifNotLoggedIn>
        </div>
    </nav>
</section>