<g:each in="[flash, request]" var="scope">
    <g:each in="['success', 'info', 'warning', 'danger']" var="level">
        <g:if test="${scope[level]}">
            <g:javascript>
                showAlert("${raw(scope[level])}", "${level}");
            </g:javascript>
        </g:if>
    </g:each>
</g:each>