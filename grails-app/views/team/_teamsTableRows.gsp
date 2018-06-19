<%@ page import="edu.wustl.cielo.Team" %>
<g:set var="count" value="${1}"/>
<g:each in="${teams}" var="team">
    <tr>
        <th scope="row">${count}</th>
        <td>
            <a class="btn btn-link" style="padding-top:0; margin: 0;" href="${createLink(controller: "team", action: "view", id: team.id)}">
                ${team.name}
            </a>
        </td>
        <td>
            ${team.administrator.fullName}
        </td>
        <td>
            <g:if test="${team.members.size() > 0}">
                ${team.members.collect { it.fullName }.join(', ')}
            </g:if>
            <g:else>
                <em>None yet</em>
            </g:else>
        </td>
        <td>
           <g:getProjectsForTeam team="${team}"/>
        </td>
    </tr>
    <g:set var="count" value="${count+1}"/>
</g:each>