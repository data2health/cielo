<div class="screen" id="step_4" style="display: none;">
    <div class="screen-header" style="display: none;">
        <h4 id="step_4_title" class="modal-title offset-title">Step 4 - Teams</h4>
    </div>
    <div class="screen-body">
        <div class="display-5">Add a team name and members if you would like to add a team now or, leave blank and continue to next step. You
        will only be allowed to add one team from this wizard but from the project edit window, you can add more at a later
        date.</div>
       <g:render template="/team/newTeam" model="[users: users]"/>
    </div>
</div>

<script type="application/javascript">
    $( function() {
        $('#teamName').removeAttr('required');
    });

    function formatOption(option) {
        return $( '<span>' + option.text + '</span>');
    }
</script>