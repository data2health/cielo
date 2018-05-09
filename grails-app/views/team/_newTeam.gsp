<div class="container-fluid">
    <div class="form-group row" style="white-space: nowrap; padding-top: 2em;">
        <div class="col-md-2">
            <div class="form-check">
                <input class="form-check-input" type="radio" name="TeamRadio" id="existingTeamRadio" onchange="selectRadioOption(this)" value="option2" checked>
                <label class="form-check-label" for="existingTeamRadio" style="font-weight: 700;">
                    Existing Team&nbsp;
                </label>
            </div>
        </div>
        <div class="col-md-12" style="padding-left: 3em;">
            <div class="form-group row" style="white-space: nowrap; padding-top: 2em;">
                <div class="col-md-2">
                    <label for="teamSelect" class="col-sm-2 form-control-label" style="font-weight: 500;">
                        Select a team
                    </label>
                </div>
                <div class="col-md-7">
                    <select id="teamSelect" class="form-control">
                        <g:if test="${teams.size() == 0}">
                            <option value="-1">No teams</option>
                        </g:if>
                        <g:else>
                            <g:each in="${teams}" var="team">
                                <option value="${team.id}">${team.name} (admin: ${team.administrator.username},
                                contributors: ${team.members.size() > 0 ? team.members.collect { it.username }.join(", ") : "none"})</option>
                            </g:each>
                        </g:else>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <div class="form-group row" style="white-space: nowrap; padding-top: 2em;">
        <div class="col-md-2">
            <div class="form-check">
                <input class="form-check-input" type="radio" name="TeamRadio" id="newTeamRadio" onchange="selectRadioOption(this)" value="option3">
                <label class="form-check-label" for="newTeamRadio" style="font-weight: 700;">
                    New Team&nbsp;
                </label>
            </div>
        </div>
        <div class="col-md-12" style="padding-left: 3em;">
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
                    <select class="multiple-select form-control multi-users" id="members" multiple="multiple">
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
    </div>
</div>

<script type="application/javascript">

    $( function () {
        initMultiSelect();

        //disable controls for creating new team by default
        $('#teamName').attr('disabled', 'disabled');
        $('#members').attr('disabled', 'disabled');
    });

    function initMultiSelect() {
        $('.multiple-select').select2( {
            templateResult: formatOption,
            templateSelection: formatOption
        });
        setTimeout(function() {
            $('.select2-container').css('min-width', "32.5vw");
        }, 250);
    }


    function formatOption(option) {
        return $( '<span>' + option.text + '</span>');
    }

    function selectRadioOption(control) {
        if ($(control).attr('id') === "newTeamRadio") {
            $('#teamSelect').attr('disabled', 'disabled');
            $('#teamName').removeAttr('disabled');
            $('#members').removeAttr('disabled');
        } else if ($(control).attr('id') === "noneTeamRadio") {
            $('#teamSelect').attr('disabled', 'disabled');
            $('#teamName').attr('disabled', 'disabled');
            $('#members').attr('disabled', 'disabled');
        } else {
            $('#teamName').attr('disabled', 'disabled');
            $('#members').attr('disabled', 'disabled');
            $('#teamSelect').removeAttr('disabled');
        }
    }

</script>