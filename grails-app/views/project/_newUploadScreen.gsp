<div>
    <g:message code="upload.deidentified.message"/>
</div>

<div id="${projectId}_upload_${type}" class="container-fluid new-upload-dialog">
    <form id="${type}Form">
        <g:if test="${allowNone}">
            <div class="row">
                <div class="form-group col-md-12">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="uploadType" id="${type}UploadNone"
                               onchange="toggleRadioOption(this, '${type}')" value="none" checked>
                        <label class="form-check-label" for="${type}UploadNone" style="font-weight: 700;">
                            None
                        </label>
                    </div>
                </div>
            </div>
        </g:if>
        <div class="row">
            <div class="form-group col-md-12">
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="uploadType" id="${type}UploadFile"
                           onchange="toggleRadioOption(this, '${type}')" value="option1" ${allowNone ? "" : "checked"}>
                    <label class="form-check-label" for="${type}UploadFile" style="font-weight: 700;">
                        Upload a file (max file size = 150MB):&nbsp;
                    </label>
                </div>
                <input id="${type}File" class="form-control" type="file">
            </div>
        </div>

        <div class="row">
            <div class="form-group col-md-12" style="white-space: nowrap;">
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="uploadType" id="${type}UploadLink"
                           onchange="toggleRadioOption(this, '${type}')" value="option2">
                    <label class="form-check-label" for="${type}UploadLink" style="font-weight: 700;">
                        Link to an external file:&nbsp;
                    </label>
                </div>
                <input class="form-control" id="${type}UrlInput" type="url"
                       placeholder="Enter Link to external resource"
                       style="min-width: -webkit-fill-available; border: 1px solid grey;" disabled="disabled">
                <label class="form-control-label" for="${type}ExternalFileName">
                    Enter name of external resource:
                </label>
                <input id="${type}ExternalFileName" class="form-control" type="text" disabled="disabled"
                       placeholder="File name">
            </div>
        </div>

        <div class="row">
            <div class="form-group col-md-12">
                <label class="form-check-label" for="${type}UploadDescription">
                    *Description:&nbsp;
                </label>
                <g:if test="${requireDescription}">
                    <input id="${type}UploadDescription" type="text" class="form-control"
                           placeholder="Enter a short description" required="required">
                </g:if>
                <g:else>
                    <input id="${type}UploadDescription" type="text" class="form-control"
                           placeholder="Enter a short description">
                </g:else>
            </div>
        </div>
    </form>
</div>

<script type="application/javascript">

    function toggleRadioOption(control, type) {
        var radioControlId = $(control).attr('id');

        if (radioControlId.indexOf("UploadLink") !== -1) {
            $('#' + type + 'File').attr('disabled', 'disabled');
            $('#' + type + 'UrlInput').removeAttr('disabled');
            $('#' + type + 'ExternalFileName').removeAttr('disabled');
        } else {
            $('#' + type + 'UrlInput').attr('disabled', 'disabled');
            $('#' + type + 'ExternalFileName').attr('disabled', 'disabled');
            $('#' + type + 'File').removeAttr('disabled');
        }
    }
</script> 