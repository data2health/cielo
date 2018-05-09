<div class="screen" id="step_4" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_4_title" class="modal-title offset-title">Step 4 - Teams</h4>
    </div>
    <div class="screen-body">
        <div class="display-5">Add an existing team or a new team; select none if you do not want to add a team at this
        time. You will only be able to add one team from this wizard but, from the project edit window you can add
        more.</div>
        <div class="container" style="padding-top: 1em;">
            <div class="row">
                <div class="col-md-2">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="TeamRadio" id="noneTeamRadio" onchange="selectRadioOption(this)" value="option1" checked>
                        <label class="form-check-label" for="noneTeamRadio" style="font-weight: 700;">
                            None&nbsp;
                        </label>
                    </div>
                </div>
            </div>
        </div>
       <g:render template="/team/newTeam" model="[users: users, teams: teams]"/>
    </div>
</div>

<script type="application/javascript">
    $( function() {
        $('#teamName').removeAttr('required');
        $('#existingTeamRadio').removeAttr('checked');
        $('#teamSelect').attr('disabled', 'disabled');
        $('#noneTeamRadio').prop('checked', 'checked');
    });

    function formatOption(option) {
        return $( '<span>' + option.text + '</span>');
    }
</script>