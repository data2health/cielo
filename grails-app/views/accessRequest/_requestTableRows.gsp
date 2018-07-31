<%@ page import="edu.wustl.cielo.Project" %>
<%@ page import="edu.wustl.cielo.enums.AccessRequestStatusEnum" %>

<g:set var="count" value="${1}"/>
<g:each in="${requests}" var="request">
    <g:set var="project" value="${ Project.findById(request.projectId) as Project}"/>
    <tr>
        <th scope="row">${count}</th>
        <g:if test="${project.name.size() > 20}">
            <td data-toggle="tooltip" title="${project.name}">
            <a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "project", action: "view", id: project.id)}">
            ${project.name.substring(0,20)}...
        </g:if>
        <g:else>
            <td>
            <a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "project", action: "view", id: project.id)}">
            ${project.name}
        </g:else>
        </a>
        </td>
        <td>
             ${request.user.username}
        </td>
        <td>
            ${request.status.toString().toLowerCase().capitalize()}
        </td>
        <td>
            <g:getAccessMaskName mask="${request.mask}"/>
        </td>
        <td style="text-align: right;">
            <g:if test="${request.status == AccessRequestStatusEnum.PENDING}">
                <div class="accessControlButtonContainer">
                    <div id="approve_${request.id}" class="accessControlButton" onclick="approveRequest(${request.id});">
                        <i class="far fa-check-circle"></i>
                        &nbsp;Approve
                    </div>
                    &nbsp;
                    <div id="deny_${request.id}" class="accessControlButton-red" onclick="denyRequest(${request.id});">
                        <i class="fas fa-ban"></i>
                        &nbsp;Deny
                    </div>
                </div>
            </g:if>
        </td>
    </tr>
    <g:set var="count" value="${count+1}"/>
</g:each>