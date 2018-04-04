<div class="card table-card">
    <div class="card-body" style="padding: 0;">
        <h6 class="card-header">
            <i class="far fa-file-code"></i>
            &nbsp;Project Code
            <g:userCanMakeChangesToProject project="${project}">
                <span style="float: right;margin-top: -10px;">
                    <button type="button" class="btn btn-secondary btn-xs" style="padding: 3px;background-color: grey;border-color: grey;" onclick="addProjectPublication()">
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
            </tr>
            </thead>
            <tbody>
            <g:set var="count" value="${1}"/>
            <g:each in="${project.codes}" var="code">
                <tr>
                    <th scope="row">${count}</th>
                    <td>${code.revision}</td>
                    <td>${code.name}</td>
                    <td data-toggle="tooltip" title="${code.description}">${code.description.substring(0,50)}...</td>
                    <td><g:formatDateWithTimezone date="${code.dateCreated}"/></td>
                    <td><a href="${code.url}">${code.url}</a></td>
                </tr>
                <g:set var="count" value="${count+1}"/>
            </g:each>
            </tbody>
        </table>
    </div>
</div>