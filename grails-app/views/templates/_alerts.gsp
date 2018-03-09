<g:each in="[flash, request]" var="scope">
    <g:each in="['success', 'info', 'warning', 'danger']" var="level">
        <g:if test="${scope[level]}">
            <div class="alert alert-${level} alert-dismissible fade show" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                ${raw(scope[level])}</div>
        </g:if>
    </g:each>
</g:each>