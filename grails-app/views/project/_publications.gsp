<div class="card table-card">
    <div class="card-body" style="padding: 0;">
        <h6 class="card-header">
            <i class="far fa-newspaper"></i>&nbsp; Publications
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
                <th scope="col" style="width: 10%;">#</th>
                <th scope="col" style="width: 20%;">Title</th>
                <th scope="col" style="width: 20%;">Url</th>
                <th scope="col" style="width: 15%;">ID's</th>
            </tr>
            </thead>
            <tbody>
            <g:set var="count" value="${1}"/>
            <g:each in="${project.publications}" var="publication">
                <tr>
                    <th scope="row">${count}</th>
                    <td>${publication.label}</td>
                    <td><a href="${publication.url}">${publication.url}</a></td>
                    <td>
                        <span class="data-property">ISSN:</span> ${publication.issn}<br>
                        <span class="data-property">ISBN:</span> ${publication.isbn}<br>
                        <span class="data-property">SICI:</span> ${publication.sici}<br>
                        <span class="data-property">PMID:</span> ${publication.pmid}<br>
                        <span class="data-property">OAI:</span>  ${publication.oai}<br>
                        <span class="data-property">DOI:</span>  ${publication.doi}<br>
                        <span class="data-property">NBN:</span>  ${publication.nbn}<br>
                    </td>
                </tr>
                <g:set var="count" value="${count+1}"/>
            </g:each>
            </tbody>
        </table>
    </div>
</div>