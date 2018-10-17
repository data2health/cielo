<div class="screen" id="step_6" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_6_title" class="modal-title offset-title">Step 6 - Upload Code</h4>
    </div>
    <div class="screen-body">
        <div class="display-5">
            Add code to the project, if you have any now. You can always add data at a later date from the project edit
            screen<br>&nbsp;<br>
        </div><br>
        <g:render template="newUploadScreen" model="[type: 'code', projectId: 'unsaved', requireDescription: false, allowNone: true]"/>
    </div>
</div>