<g:set var="count" value="${1}"/>
<g:each in="${projects}" var="project">
    <tr>
        <th scope="row">${count}</th>
        <td><a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "project", action: "view", id: project.id)}">${project.name}</a></td>
        <td data-toggle="tooltip" title="${project.description}">
            <g:if test="${project.description.size() > 20}">
                ${project.description.substring(0,20)}...
            </g:if>
            <g:else>
                ${project.description}
            </g:else>
        </td>
        <td>
            <button id="softwareLicenseButton" onclick="showSoftwareLicense(${project.license.id}, '${project.license.label}')"
                    class="btn btn-link" style="padding: 0; margin: 0; margin-left: -2px;">
                ${project.license.label}
            </button>
        </td>
        <td>
            <g:if test="${project.shared}">
                <i id="sharedIcon" class="fas fa-lock-open"></i>
            </g:if>
            <g:else>
                <i id="sharedIcon" class="fas fa-lock"></i>
            </g:else>
            <g:projectVisibility value="${project.shared}"/>
        </td>
        <td>
            <g:if test="${project.teams}">
                <g:set var="numberOfTeams" value="${project.teams?.size()}"/>
                <g:set var="index" value="${1}"/>
                <g:each in="${project.teams}" var="team">
                    <g:if test="${index < numberOfTeams}">
                        <a href="${createLink(controller: "project", action: "view", id: project.id, params: ['teams': true])}" class="btn btn-link" style="padding-top:0; padding-left: 0; margin: 0;">${team.name},</a>
                    </g:if>
                    <g:else>
                        <a href="${createLink(controller: "project", action: "view", id: project.id, params: ['teams': true])}" class="btn btn-link" style="padding-top:0; padding-left: 0; margin: 0;">${team.name}</a>
                    </g:else>
                    <g:set var="index" value="${index+1}"/>
                </g:each>
            </g:if>
            <g:else>
                <em>No teams</em>
            </g:else>
        </td>
        <td>
            <g:dateDiff date="${project.dateCreated}"/>
        </td>
        <td>
            <g:dateDiff date="${project.lastChanged?: project.lastUpdated}"/>
        </td>
        <g:if test="${usersProject}">
            <td style="text-align: right;"><i class="fas fa-trash-alt" onclick="deleteProject(${project.id}, '${project.name}');"></i></td>
        </g:if>
    </tr>
    <g:set var="count" value="${count+1}"/>
</g:each>