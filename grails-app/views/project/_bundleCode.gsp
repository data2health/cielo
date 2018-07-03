<div class="card table-card">
    <div class="card-body" style="padding: 0;">
        <h6 class="card-header">
            <i class="far fa-file-code"></i>
            &nbsp;Project Code
            <g:userCanMakeChangesToProject project="${project}">
                <span style="float: right;margin-top: -10px;">
                    <button type="button" class="btn btn-secondary btn-xs" style="padding: 3px;background-color: grey;border-color: grey;" onclick="addNewBundle(${project.id}, 'code')">
                        <i class="fa fa-plus"></i>
                    </button>
                </span>
            </g:userCanMakeChangesToProject>
        </h6>
        <table id="codeTable" class="table table-hover table-responsive">
            <thead class="thead-light">
            <tr>
                <th scope="col">#</th>
                <th scope="col">Revision</th>
                <th scope="col">Name</th>
                <th scope="col">Description</th>
                <th scope="col">Created</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
            </tr>
            </thead>
            <tbody id="codeRows">
                <g:render template="bundleRows" model="[projectId: project.id, bundles: project.codes, bundleType: 'code']"/>
            </tbody>
        </table>
    </div>
</div>