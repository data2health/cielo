<div id="announcements" class="col-md-3 d-none d-md-block sidebar-announcements">
    <div class="col-sm-3 sidebar-right">
        <div class="col-sm-10">
            <div class="card" style="border: 1px solid #e4e4e4;">
              <div class="card-header text-secondary">
                Featured Bundles
              </div>
                <g:each in="${bundles}" var="bundle">
                    <div class="card-body" style="padding-bottom: 0.5em;">
                        <h5 class="card-title"><a href="/bundle/${bundle.projectId}">${bundle.projectName}</a></h5>
                        <p class="card-text">
                            <span class="d-inline-block" tabindex="0" data-toggle="tooltip" title="${bundle.description}">
                                ${bundle.description.toString().substring(0,55)}...
                            </span>

                        </p>
                        <span style="color: grey">
                            <i>-${bundle.projectOwner}</i><br>
                        </span>
                        <span style="color: #c36464">
                            ${bundle.ownerInstitution}
                        </span>
                    </div>
                </g:each>
            </div>
        </div>
    </div>
</div>