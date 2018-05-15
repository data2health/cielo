<div class="screen" id="step_5" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_5_title" class="modal-title offset-title">Step 5 - Upload Data</h4>
    </div>
    <div class="screen-body">
        <div class="display-5">
            Add data to the project, if you have any now. You can always add data at a later date from the project edit
            screen.
            <br>&nbsp;<br>
            <p style="font-size: 0.75em; font-style: italic;">*Please note that only de-identified data should be uploaded. To review the Terms of Use, click About -> Terms of Use.</p>
        </div><br>
        <g:render template="newUploadScreen" model="[type: 'data', projectId: 'unsaved', requireDescription: false]"/>
    </div>
</div>
