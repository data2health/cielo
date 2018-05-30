<g:set var="count" value="${1}"/>
<g:each in="${bundles}" var="bundle">
    <tr>
        <th scope="row">${count}</th>
        <td>${bundle.revision}</td>
        <td>${bundle.name}</td>
        <td data-toggle="tooltip" title="${bundle.description}">
            ${bundle.description.size() > 50 ? bundle.description.substring(0,50) : bundle.description}...
        </td>
        <td><g:formatDateWithTimezone date="${bundle.dateCreated}"/></td>
        <td><a href="${bundle.url}">${bundle.url}</a></td>
        <g:userCanMakeChangesToProject project="${project}">
            <td><i class="fas fa-trash-alt" style="color: red;" onclick="deleteBundle('${project.id}', '${bundle.id}', '${bundleType}');"></i> </td>
        </g:userCanMakeChangesToProject>
    </tr>
    <g:set var="count" value="${count+1}"/>
</g:each>