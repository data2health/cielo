<div class="container-fluid">
    <div class="form-group row" style="white-space: nowrap; padding-top: 2em;">
        <div class="col-md-2">
            <label for="teamName" class="col-sm-2 form-control-label" style="font-weight: 500;">
                Name
            </label>
        </div>
        <div class="col-md-7">
            <input id="teamName" name="name" class="form-control" placeholder="Team Name" value="" required="" aria-required="true">
        </div>
    </div>
    <div class="row">&nbsp;</div>
    <div class="form-group row" style="white-space: nowrap; align-items: baseline;">
        <div class="col-md-2">
            <label for="members" class="col-sm-2 form-control-label" style="font-weight: 500;">
                Team Members
            </label>
        </div>
        <div class="col-md-7">
            <select class="multiple-select form-control" id="members" multiple="multiple">
                <g:each in="${users}" var="user">
                    <option value="${user.id}">
                    <g:getUserProfilePicNotRaw user="${user}" sticker="${false}" showLink="${true}">
                        ${user.profile.firstName} ${user.profile.lastName} (<em>${user.username}</em>)<br>
                    </g:getUserProfilePicNotRaw>
                    </option>
                </g:each>
            </select>
        </div>
    </div>
</div>

<script type="application/javascript">

    function formatOption(option) {
        return $( '<span>' + option.text + '</span>');
    }

    $( function () {
        $('.multiple-select').select2( {
            templateResult: formatOption,
            templateSelection: formatOption
        });
        setTimeout(function() {
            $('.select2-container').css('min-width', "32.5vw");
        }, 250);
    });
</script>