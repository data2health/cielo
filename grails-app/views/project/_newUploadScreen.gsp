<div id="${projectId}_upload_${type}" class="container-fluid new-upload-dialog">
   <div class="row">
        <div class="form-group col-md-12">
            <input class="form-check-input" type="radio" name="uploadType" id="uploadFile" onchange="selectRadioOption(this)" value="option1" checked>
            <label class="form-check-label" for="uploadFile">
               Upload a file:&nbsp;
            </label>
            <input id="fileInputControl" class="form-control" type="file">
        </div>
   </div>
   <div class="row">
        <div class="form-group col-md-12" style="white-space: nowrap;">
            <input class="form-check-input" type="radio" name="uploadType" id="uploadLink" onchange="selectRadioOption(this)" value="option2">
            <label class="form-check-label" for="uploadLink">
                Link to an external file:&nbsp;
            </label>
            <input class="form-control" id="urlInput" type="url" placeholder="Enter Link to external resource"
                   style="min-width: -webkit-fill-available; border: 1px solid grey;" disabled="disabled">
            <label class="form-control-label" for="externalFileName">
                Enter name of external resource:
            </label>
            <input id="externalFileName" class="form-control" type="text" disabled="disabled" placeholder="File name">
        </div>
    </div>
    <div class="row">
        <div class="form-group col-md-12">
            <label class="form-check-label" for="uploadLink">
                *Descripton:&nbsp;
            </label>
            <textarea id="uploadDescription" class="form-control" placeholder="Enter a short description" required="required"></textarea>
        </div>
    </div>
</div>

<script type="application/javascript">
    function selectRadioOption(control) {
        if ($(control).attr('id') === "uploadLink") {
            $('#fileInputControl').attr('disabled', 'disabled');
            $('#urlInput').removeAttr('disabled');
            $('#externalFileName').removeAttr('disabled');
        } else {
            $('#urlInput').attr('disabled', 'disabled');
            $('#externalFileName').attr('disabled', 'disabled');
            $('#fileInputControl').removeAttr('disabled');
        }
    }
</script> 