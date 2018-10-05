<%@ page import="org.springframework.security.acls.domain.BasePermission" %>
<%@ page import="edu.wustl.cielo.AccessRequest" %>
<%@ page import="edu.wustl.cielo.enums.AccessRequestStatusEnum" %>

<g:set var="count" value="${1}"/>
<g:each in="${projects}" var="project">
    <tr>
        <th scope="row">${count}</th>
                <g:if test="${project.name.size() > 20}">
                    <td data-toggle="tooltip" title="${project.name}">
                        <g:if test="${project.shared}">
                            <i id="sharedIcon" class="fas fa-lock-open"></i>
                        </g:if>
                        <g:else>
                            <i id="sharedIcon" class="fas fa-lock"></i>
                        </g:else>
                        <a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "project", action: "view", id: project.id)}">
                        ${project.name.substring(0,20)}...
                </g:if>
                <g:else>
                    <td>
                    <g:if test="${project.shared}">
                        <i id="sharedIcon" class="fas fa-lock-open"></i>
                    </g:if>
                    <g:else>
                        <i id="sharedIcon" class="fas fa-lock"></i>
                    </g:else>
                        <a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "project", action: "view", id: project.id)}">
                    ${project.name}
                </g:else>
            </a>
        </td>
        <td>
            <g:accessRequest var="accessRequest" project="${project}"/>
            <g:set var="loggedInUser" value="${accessRequest as AccessRequest}"/>
            <sec:notPermitted className='edu.wustl.cielo.Project' id='${project.id}' permission='${[BasePermission.READ,
                                                                                                    BasePermission.WRITE,
                                                                                                    BasePermission.ADMINISTRATION]}'>

                <g:if test="${!accessRequest?.status || accessRequest?.status == AccessRequestStatusEnum.ACKNOWLEDGED}">
                    <div class="accessControlButtonContainer">
                        <button type="button" class="btn btn-light btn-sm requestProjectAccessButton" onclick="showAccessRequestDialog(${project.id});">
                            <i class="far fa-envelope">&nbsp;</i>
                            Request access
                        </button>
                    </div>
                </g:if>

                <g:if test="${accessRequest?.status == AccessRequestStatusEnum.DENIED}">
                    <em>Request has been denied</em>
                    <span onclick="acknowledgeRequestStatus(${accessRequest?.id})"><i class="far fa-times-circle"></i></span>
                </g:if>

                <g:if test="${accessRequest?.status == AccessRequestStatusEnum.PENDING}">
                    <em>*Request is pending</em>
                </g:if>
            </sec:notPermitted>
            <sec:permitted className='edu.wustl.cielo.Project' id='${project.id}' permission='${[BasePermission.READ,
                                                                                                    BasePermission.WRITE,
                                                                                                    BasePermission.ADMINISTRATION]}'>
                <span id="accessRequestStatus_${accessRequest?.id}" style="font-style: italic; font-weight: 100;">
                    <g:if test="${accessRequest?.status == AccessRequestStatusEnum.APPROVED}">
                        <em>Request has been approved</em>
                        <span onclick="acknowledgeRequestStatus(${accessRequest?.id})"><i class="far fa-times-circle"></i></span>
                    </g:if>
                </span>

            </sec:permitted>
        </td>
            <g:if test="${project.description.size() > 20}">
                <td data-toggle="tooltip" title="${project.description}">
                ${project.description.substring(0,20)}...
            </g:if>
            <g:else>
                <td>
                    ${project.description}
            </g:else>
        </td>
        <td>
            <g:if test="${project.annotations.size() > 0}">
                <g:each in="${project.annotations}" var="annotation">
                    <span class="annotations-pill">
                        ${annotation.term}
                    </span>&nbsp;
                </g:each>
            </g:if>
            <g:else>
                <em>No annotations</em>
            </g:else>
        </td>
        <g:if test="${!usersProject}">
            <td>
                ${project.projectOwner.fullName}
            </td>
        </g:if>
        <td>
            <g:if test="${project.teams}">
                <g:set var="numberOfTeams" value="${project.teams?.size()}"/>
                <g:set var="index" value="${1}"/>
                <g:each in="${project.teams}" var="team">
                    <g:if test="${index < numberOfTeams}">
                        <a href="${createLink(controller: "project", action: "view", id: project.id, params: ['teams': true])}" class="btn btn-link" style="padding:0; margin: 0;">${team.name},</a>
                    </g:if>
                    <g:else>
                        <a href="${createLink(controller: "project", action: "view", id: project.id, params: ['teams': true])}" class="btn btn-link" style="padding:0; margin: 0;">${team.name}</a>
                    </g:else>
                    <g:set var="index" value="${index+1}"/>
                </g:each>
            </g:if>
            <g:else>
                <em>No teams</em>
            </g:else>
        </td>
        <td>
            <g:dateDiff date="${project.lastChanged?: project.lastUpdated}"/>
        </td>
        <td style="text-align: right;">
        <sec:permitted className='edu.wustl.cielo.Project' id='${project.id}' permission='${BasePermission.ADMINISTRATION}'>
            <i class="fas fa-trash-alt" onclick="deleteProject(${project.id}, '${project.name}');"></i>
        </sec:permitted>
        </td>
    </tr>
    <g:set var="count" value="${count+1}"/>
</g:each>

<script type="application/javascript">
    function showAccessRequestDialog(projectId) {
        var accessWindow = bootbox.dialog({
            title: 'Send Request',
            message: " ",
            buttons: {
                cancel: {
                    label: "Cancel",
                    className: 'btn-red'
                },
                ok: {
                    label: "Send",
                    className: 'btn-primary',
                    callback: function() {

                        if (isNaN(parseInt($('#projectAccess').val()))) {
                            $('#accessRequestError').show();
                            return false;
                        } else {
                            $('#accessRequestError').hide();

                            var bitMask = parseInt($('#projectAccess').val());

                            $.post("${createLink(controller: "project", action: "requestAccessToProject")}", {id: projectId, mask: bitMask}, function (data) {
                                if (data.success === true) {
                                    grabLatestDataFromServer();
                                }
                            });
                        }
                    }
                }
            }
        });

        accessWindow.init(function() {
            //grab the text for the license from db
            $.get("/project/accessRequestContent", function (data) {
                accessWindow.find('.bootbox-body').html( data );
            });
        });
    }

    function acknowledgeRequestStatus(requestId) {
        $.post("${createLink(controller: "project", action: "acknowledgeAccessRequestResult")}", {id: requestId}, function (result) {
            if (result.success) {
                grabLatestDataFromServer();
            }
        });
    }

    function deleteProject(projectId, projectName) {
        bootbox.confirm({
            title: "Delete Project?",
            message: "Do you really want to delete <em style='font-weight: 300;color: #149dcc;'>" + projectName + "</em> ? This cannot be undone. <p>&nbsp;<p>*Please note that any data and code will also be deleted.",
            closeButton: false,
            buttons: {
                cancel: {
                    className: 'btn-red',
                    label: '<i class="fa fa-times"></i>&nbsp;Cancel'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i>&nbsp;Confirm'
                }
            },
            callback: function (result) {
                if (result === true) {
                    $.post("${createLink(controller: "project", action: "deleteProject")}", {'id': projectId},
                        function () {
                            grabLatestDataFromServer();
                        }
                    );
                }
            }
        });
    }

    function grabLatestDataFromServer() {
        var offsetVal = parseInt($('#paging-options').val()) - 1;
        var filterText = $('#projectSearch').val();
        var filterOnProjects    = $('input[name=projectType]:checked').val();
        var myProjects;

        if (filterOnProjects === 'all' ) {
            myProjects = false;
        } else {
            myProjects = true;
        }

        $.get("${createLink(controller: "project", action: "getFilteredProjects")}",
            {offset: offsetVal, myProjects: myProjects, filterTerm: filterText}, function (data) {
                replaceProjectTableContent(data);
                //if the number of projects currently visible is zero, call change on options; page 1
                //is already selected
                if ($('#projectTableBody tr').length === 0) {
                    $('#paging-options').change();
                }
            });
    }
</script>