<div class="container-fluid pagination-toolbar">
    <div class="row">
        <div id="left-table-toolbar" style="width: 33%; text-align: left;">
            <i class="fas fa-fast-backward" onclick="${onFirstPageCallback + '();'}"></i> &nbsp; <i class="fas fa-play" onclick="${onPreviousPageCallback + '();'}" style="transform: scale(-1);"></i>
        </div>
        <div id="center-table-toolbar" style="width: 33%; text-align: center;">
            <select class="custom-select" id="paging-options" style="width: 3.5em;"
                    onchange="${onChangeCallback + '('+ isUsersProjects +');'}">
                <g:set var="count" value="${1}"/>
                <g:each in="${1..pages}">
                    <option value="${count}">${count}</option>
                    <g:set var="count" value="${count+1}"/>
                </g:each>
            </select>
            <span id="ofPages">of ${pages}</span>
        </div>
        <div id="right-table-toolbar" style="width: 33%; text-align: right;">
            <i class="fas fa-play" onclick="${onNextPageCallback + '();'}"></i> &nbsp; <i class="fas fa-fast-forward" onclick="${onLastPageCallback + '();'}"></i>
        </div>
    </div>
</div>

<asset:javascript src="jquery/jquery.min.js"/>
<script type="application/javascript">
    $(function() {
        updateToolbarButtons();
    });
</script>