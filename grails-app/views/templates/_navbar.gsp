<%@ page import="edu.wustl.cielo.UserAccount" %>
<g:loggedInUser var="currentUser"/>
<g:set var="loggedInUser" value="${currentUser as UserAccount}"/>

<g:messagesCount var="currentMessagesCount"/>
<g:set var="messagesCount" value="${currentMessagesCount as int}"/>

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
                        <div id="activityMenuUnderline" class="menu-item-underline" style="display: none;">&nbsp;</div>
                    </li>
                    <li class="li-spacer d-block d-md-none">&nbsp;</li>
                    <li>
                        &nbsp;<a href="${createLink(controller: "accessRequest", action: "list")}" style="padding: 1em;
                                 padding-right: 0;">
                            <i class="fas fa-envelope"></i>
                            Messages
                        </a>
                        <g:if test="${messagesCount > 0}">
                            &nbsp;<span id="messagesBadge" class="badge badge-danger" style="border-radius: 50%;">${messagesCount}</span>
                        </g:if>
                        <div id="messagesMenuUnderline" class="menu-item-underline" style="display: none; margin-left: 2.5em;">&nbsp;</div>
                    </li>
                    <li class="li-spacer d-block d-md-none">&nbsp;</li>
                    <li class="d-block d-md-none">
                        <span class="text-secondary" onclick="showUserDialog();">
                            <i class="fa fa-link"></i>
                            Connections
                        </span>
                    </li>
                    <li class="li-spacer">&nbsp;</li>
                    <li>
                        <a role="link" href="${createLink(controller: 'project', action: 'projectsList')}">
                            <i class="fa fa-cube"></i>
                            Projects
                        </a>
                        <div id="projectMenuUnderline" class="menu-item-underline" style="display: none;">&nbsp;</div>
                    </li>
                    <li class="li-spacer">&nbsp;</li>
                    <li>
                        <a href="${createLink(controller: "team", action: "teams")}">
                            <i class="fa fa-users"></i>
                            Teams
                        </a>
                        <div id="teamsMenuUnderline" class="menu-item-underline" style="display: none">&nbsp;</div>
                    </li>
                    <li class="li-spacer">&nbsp;</li>

                    <li class="dropdown pull-right">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="false" aria-expanded="false">
                            <i class="fas fa-info"></i>
                            About
                        </a>
                        <ul class="dropdown-menu arrow_box">
                            <li>
                                <a href="#contact-us-form">
                                    <i class="fa fa-envelope"></i>
                                    Contact Us
                                </a>
                            </li>
                            <li style="font-weight:400;">
                                <button onclick="showTermsOfUseNoAcknowledge()"
                                        style="font-weight: 400;padding: 0;padding-bottom: 0.2em;color: #149dcc !important;padding-left: 1em;margin: 0;"
                                        type="button" class="btn btn-link"><span class="menu-button-text">Terms of Use</span></button>
                            </li>
                            <li><hr style="width: 100%;"></li>
                            <li>
                                <span style="font-weight: 400;padding: 0;padding-bottom: 0.2em;color: #149dcc !important;
                                        padding-left: 1em;margin: 0; font-size: 1em;">
                                    <em>version <g:meta name="info.app.version"/></em>
                                </span>
                            </li>
                        </ul>
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
                                <a role="link" href="${createLink(controller: 'user', action: 'view', params: [id: loggedInUser.id])}">
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