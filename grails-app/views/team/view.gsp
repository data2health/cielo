<g:render template="/templates/headerIncludes"/>
<g:render template="/templates/navbar"/>

<g:set var="isTeamOwner" value="${false}"/>
<g:userOwnsTeam team="${team}">
    <g:set var="isTeamOwner" value="${true}"/>
</g:userOwnsTeam>

<g:set var="isTeamContributor" value="${false}"/>
<g:userContributesToTeam team="${team}">
    <g:set var="isTeamContributor" value="${true}"/>
</g:userContributesToTeam>

<div>
    <div style="margin: 1.65em">
        &nbsp;
    </div>
    <div class="mbr-fullscreen team_header_image">
        <div class="container-fluid">
            <div class="row justify-content-md-start">
                <div class="col-md-1" style="margin-right: -70px; align-self: center;">
                    <g:getUserProfilePic user="${team.administrator}" sticker="${true}" showLink="${true}" imageSize="xxx-large"/>
                </div>
                <div class="project-header-container mbr-white col-md-10">
                    <h1 class="mbr-section-title mbr-bold pb-3 mbr-fonts-style display-2">
                        <span>
                            <span id="project-name-label">${team.name}&nbsp;</span>
                            <span id="project-name-input" style="display: none; font-size: 0.75em;">
                                <input type="text" value="${team?.name}" name="project-name" style="border-radius: 5px; width: available; max-width: 50%;"></span>
                        </span><br>
                    </h1>
                    <div class="mbr-text pb-3 mbr-fonts-style display-6">
                        Administered by ${team.administrator?.fullName}<br>
                        <span>${team.administrator?.profile?.institution.fullName}</span><br>
                        <span style="font-size: 80%; font-style: oblique;">
                            Last updated <g:dateDiff date="${team?.lastUpdated}"/>
                        </span>
                        <span style="font-size: 80%; font-style: oblique;"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="team-header-container-bottom">
        &nbsp;
    </div>
</div>

<div id="teamSection">
    <g:render template="userTeam" model="[team: team, isTeamOwner: isTeamOwner]"/>
</div>
<g:render template="/user/userProjects" model="[projects: [], isUsersOwnPage: isTeamOwner,
                                                userContributesToTeam: isTeamContributor,
                                                showContributionType: false,
                                                displayLightTheme: false,
                                                paddingTop: '1em',
                                                contributeTo: projectsContributeTo]"/>
<g:render template="/templates/scrollToTop"/>
<g:render template="/templates/contactUsSection"/>
<g:render template="/templates/pageFooterIncludes"/>