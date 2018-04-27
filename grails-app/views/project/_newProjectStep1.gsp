<div class="screen" id="step_1" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_1_title" class="modal-title offset-title">Step 1 - Project Name and Description</h4>
    </div>
    <div class="screen-body">
        <div class="form-group">
            <label class="form-control-label mbr-fonts-style display-7" for="name">*Project Name</label>
            <input type="text" class="form-control" name="name" data-form-field="Name" required="required" id="name">
        </div>
        <div class="form-group">
            <label class="form-control-label mbr-fonts-style display-7" for="description">*Description</label>
            <textarea id="description" type="text" class="form-control" name="description" rows="7" onkeyup="setCharacterCount()" data-form-field="Description" maxlength="255" required="required"></textarea>
        </div>
        <div id="charCount">0/255</div>
    </div>
</div>