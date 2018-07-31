<%@ page import="org.springframework.security.acls.domain.BasePermission" %>
<div id="userAccessDiv_${userObject?.user?.id}" onmouseover="toggleAccessControlButton(${userObject?.user?.id});"
     style="display: inline-block;" data-id="${userObject?.user?.id}" onmouseout="toggleAccessControlButton(${userObject?.user?.id});">
    <g:set var="accessLevelText" value=""/>
    <g:each in="${userObject?.access}" var="access">
        <g:if test="${accessLevelText.length() == 0}">
            <g:set var="accessLevelText" value="(${access.name}"/>
        </g:if>
        <g:else>
            <g:set var="accessLevelText" value="${accessLevelText}, ${access.name}"/>
        </g:else>
    </g:each>
    <g:set var="accessLevelText" value="${accessLevelText})"/>
    <table style="color: white;">
        <tr>
            <td colspan="2" style="height: 40px;">
            <sec:permitted className='edu.wustl.cielo.Project' id='${projectId}' permission='${[BasePermission.ADMINISTRATION]}'>
                <div id="removeAccessDiv_${userObject?.user?.id}" class="removeAccessDiv"
                     onclick="showUserAccessDialog(${projectId}, ${userObject?.user?.id}, '${userObject?.access}');" style="display: none;">
                    <i class="far fa-edit"></i>
                </div>
            </sec:permitted>
            </td>
        </tr>
        <tr>
            <td>
                ${userObject?.user?.fullName}<br>
                ${accessLevelText}
            </td>
            <td valign="middle" rowspan="3"></td>
        </tr>
        <tr>
            <td>
                <g:getUserProfilePic style="padding-left: 5em;padding-right: 5em;" user="${userObject?.user}"
                                     sticker="${true}"
                                     imageSize="x-large"/>&nbsp;
            </td>
        </tr>
    </table>
</div>
