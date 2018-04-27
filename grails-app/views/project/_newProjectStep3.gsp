<div class="screen" id="step_3" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_3_title" class="modal-title offset-title">Step 3 - Software License</h4>
    </div>
    <div class="screen-body">
        <div class="display-5">
            Select a license for the project
        </div>
        <p>&nbsp;</p>
        <div class="form-group col-sm-8" style="display: inherit;">
            <span id="licenseSpan" class="">
                <select id="license" style="width: 80%" class="form-control" name="license">
                    <g:each in="${licences}" var="license">
                        <option value="${license.id}">${license.label}</option>
                    </g:each>
                </select>
            </span>
        </div>
    </div>
</div>