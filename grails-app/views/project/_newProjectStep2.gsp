<div class="screen" id="step_2" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_2_title" class="modal-title offset-title">Step 2 - Annotations</h4>
    </div>
    <div class="screen-body">
        <div class="display-5">
            Select annotations from list to help others find projects that they may be interested in.
        </div><p>&nbsp;</p>
        <div class="form-group col-sm-8" style="display: inherit;">
            <span id="annotationsSpan" class="">
                <select id="annotations" style="width: 80%" class="multiple-select multi-annotations form-control" name="annotations" multiple="multiple">
                        <g:each in="${annotations}" var="annotation">
                                <option value="${annotation.id}">${annotation.label}</option>
                            </g:each>
                    </select>
            </span>
        </div>
    </div>
</div>