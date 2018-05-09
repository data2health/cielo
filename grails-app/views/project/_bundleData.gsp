<div class="card table-card">
    <div class="card-body" style="padding: 0;">
        <h6 class="card-header">
            <i class="far fa-file-alt"></i>
            &nbsp;Project Data
            <g:userCanMakeChangesToProject project="${project}">
                <span style="float: right;margin-top: -10px;">
                    <button type="button" class="btn btn-secondary btn-xs" style="padding: 3px;background-color: grey;border-color: grey;" onclick="addNewBundle(${project.id}, 'data')">
                        <i class="fa fa-plus"></i>
                    </button>
                </span>
            </g:userCanMakeChangesToProject>
        </h6>
        <table class="table table-hover table-responsive">
            <thead class="thead-light">
            <tr>
                <th scope="col">#</th>
                <th scope="col">Revision</th>
                <th scope="col">Name</th>
                <th scope="col">Description</th>
                <th scope="col">Created</th>
                <th scope="col">Url</th>
                <th>&nbsp;</th>
            </tr>
            </thead>
            <tbody>
            <g:set var="count" value="${1}"/>
            <g:each in="${project.datas}" var="data">
                <tr>
                    <th scope="row">${count}</th>
                    <td>${data.revision}</td>
                    <td>${data.name}</td>
                    <td data-toggle="tooltip" title="${data.description}">
                        ${data.description.size() > 50 ? data.description.substring(0,50) : data.description}...
                    </td>
                    <td><g:formatDateWithTimezone date="${data.dateCreated}"/></td>
                    <td><a href="${data.url}">${data.url}</a></td>
                    <g:userCanMakeChangesToProject project="${project}">
                        <td><i class="fas fa-trash-alt" style="color: red;" onclick="deleteBundle('${project.id}', '${data.id}', 'data');"></i> </td>
                    </g:userCanMakeChangesToProject>
                </tr>
                <g:set var="count" value="${count+1}"/>
            </g:each>
            </tbody>
        </table>
    </div>
</div>