<div class="tab-pane" id="bundles" role="tabpanel" aria-labelledby="bundles-tab">
    <div class="row" style="max-width: 100%;">
        <div class="col-md-5">
            <g:render template="bundleData" model="[project: project]"/>
        </div>
        <div class="col-md-5">
            <g:render template="bundleCode" model="[project: project]"/>
        </div>
    </div>
</div>