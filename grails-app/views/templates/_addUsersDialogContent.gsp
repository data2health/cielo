<select class="multiple-select dark-theme" id="follow-users-select" multiple="multiple">
    <g:each in="${users}" var="user">
        <g:if test="${follow.contains(user)}">
            <option value="${user.id}" selected="selected">
        </g:if>
        <g:else>
            <option value="${user.id}">
        </g:else>
            <g:getUserProfilePicNotRaw user="${user}" sticker="${false}" showLink="${true}">
                ${user.profile.firstName} ${user.profile.lastName} (<em>${user.username}</em>)<br>
            </g:getUserProfilePicNotRaw>
        </option>
    </g:each>
</select>

<script type="application/javascript">

    function formatOption(option) {
        return $( '<span>' + option.text + '</span>');
    }

    $( function () {
        $('.multiple-select').select2( {
            templateResult: formatOption,
            templateSelection: formatOption
        });
        setTimeout(function(){
            var dialogContentWidth = $('.modal-content').width() * 0.9;
            $('.select2-container').css('min-width', dialogContentWidth + "px");
        }, 250);

        $('.select2-container--default .select2-selection--multiple').css('background-color','#525151');

        $('.multiple-select').on('select2:open', function (e) {
            $('.select2-container--open .select2-dropdown--below').css('color','white');
            $('.select2-container--open .select2-dropdown--below').css('border-color','black');
            $('.select2-container--open .select2-dropdown--below').css('background-color','#525151');
        });
    });
</script>