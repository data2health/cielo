<g:set var="background_color" value="#818992"/>
<g:set var="color" value="white"/>
<g:set var="projectLinkClass" value="project-link"/>
<g:set var="projectSectionTopPadding" value="padding-top: 90px;"/>
<g:set var="itemsPerColumn" value="${5}"/>

<g:if test="${displayLightTheme != null}">
    <g:if test="${displayLightTheme}">
        <g:set var="background_color" value="white"/>
        <g:set var="color" value="#3a3a3a"/>
        <g:set var="projectLinkClass" value="project-link-dark"/>
    </g:if>
</g:if>

<g:if test="${paddingTop != null}">
    <g:set var="projectSectionTopPadding" value="padding-top: ${paddingTop};"/>
</g:if>
    <section class="features1 cid-qHBWkob2I0" id="user_projects" style="background-color: ${background_color};
color: ${color};${projectSectionTopPadding}">
        <div class="container-fluid" style="text-align: center;padding-bottom: 4em;">
            <div class="row" style="display: inline-block;">
                <span style="z-index: 1;font-family: inherit; font-size: 2em; color: ${color};padding-bottom: 2em; margin-top:-2em;">
                    Projects
                </span>
            </div>
        </div>

    <g:set var="showContributionMessage" value="${true}"/>
    <g:if test="${showContributionType != null}">
        <g:set var="showContributionMessage" value="${showContributionType}"/>
    </g:if>

    <g:set var="size" value="${projects.size()}"/>
    <g:set var="index" value="${0}"/>
    <g:set var="contributeSize" value="${contributeTo.size()}"/>
    <g:set var="contributeIndex" value="${0}"/>

    <g:if test="${(size == index) && (contributeSize == contributeIndex)}">
    <div class="container-fluid" style="text-align: center;">
        <div class="row" style="margin-bottom: 3em; display: inline-block;">
        <em>None yet</em>
    </g:if>
    <g:else>
    <div class="container-fluid" style="padding-left: 5em;">
        <div class="row" style="margin-bottom: 3em;">
        <g:each in="${projects}" var="project">
        <g:if test="${index == 0}">
                <div class="col-sm-auto" style="padding-top: 1em;">
            </g:if>
            <g:else>
                <g:if test="${index == itemsPerColumn}">
                    <g:set var="index" value="${0}"/>
                    </div>
                    <div class="col-sm-auto">
                        &nbsp;
                    </div>
                    <div class="col-sm-auto" style="padding-top: 1em;">
                </g:if>
            </g:else>
            <span>
                <g:if test="${project?.shared}">
                    <i class="fas fa-lock-open"></i>
                </g:if>
                <g:else>
                    <i class="fas fa-lock"></i>
                </g:else>
                <g:if test="${isUsersOwnPage || project?.shared || isTeamContributor}">
                    <a class="btn btn-link ${projectLinkClass}" href="${createLink(controller: "project", action: "view", id: project.id)}">${project?.name}</a>
                    <g:if test="${showContributionMessage}">
                        (Owner)
                    </g:if>
                </g:if>
                <g:else>
                    ************
                </g:else>
            </span>
            <br>
            <g:set var="index" value="${index + 1}"/>
        </g:each>

        <g:each in="${contributeTo}" var="project">
            <g:if test="${index == 0}">
                <div class="col-sm-auto" style="padding-top: 1em;">
            </g:if>
            <g:else>
                <g:if test="${index == itemsPerColumn}">
                    <g:set var="index" value="${0}"/>
                    </div>
                    <div class="col-sm-auto">
                        &nbsp;
                    </div>
                    <div class="col-sm-auto" style="padding-top: 1em;">
                </g:if>
            </g:else>
            <span>
                <g:if test="${project?.shared}">
                    <i class="fas fa-lock-open"></i>
                </g:if>
                <g:else>
                    <i class="fas fa-lock"></i>
                </g:else>
                <g:if test="${isUsersOwnPage || project?.shared || isTeamContributor}">
                    <a class="btn btn-link ${projectLinkClass}" href="${createLink(controller: "project", action: "view", id: project.id)}">${project?.name}</a>
                    <g:if test="${showContributionMessage}">
                        (Owner)
                    </g:if>
                </g:if>
                <g:else>
                    ************
                </g:else>
            </span>
            <br>
            <g:set var="index" value="${index + 1}"/>
        </g:each>

        <g:if test="${index != itemsPerColumn && index != 0}">
            </div>
        </g:if>
    </g:else>
            </div>
        </div>
    </div>
</section>