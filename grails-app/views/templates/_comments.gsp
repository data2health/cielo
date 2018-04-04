%{--<g:each var="i" in="${ (0..2) }">--}%
<g:set var="buttonAdded" value="false"/>
<g:set var="i" value="${1}"/>
<g:each in="${comments?.sort { a,b -> b.dateCreated <=> a.dateCreated }}" var="comment">
    <g:if test="${i <= numberOfComments}">
        <g:if test="${i != 1}">
            <hr>
        </g:if>
        <div id="activity_comment_id_${comment?.id}" class="activity-post-comment">
            <span class="d-inline-block" tabindex="0" data-toggle="tooltip" title="${comment.commenter.username}">
                <g:getUserProfilePic user="${comment.commenter}" imageSize="small" sticker="${false}" tooltipText="${comment.commenter.username}"/>
            </span>
            <span class="date-time-comment">
                <g:formatDateWithTimezone date="${comment.dateCreated}"/>
            </span>
            <p class="comment-text">${comment.text}</p>
        </div>
    </g:if>
    <g:else>
        <g:if test="${buttonAdded == "false"}">
            <div id="showMoreButton" class="comments-show-more">
                <button type="button" class="btn btn-link" onclick="getOlderComments(${activityId})">Show more...</button>
            </div>
            <g:set var="buttonAdded" value="true"/>
        </g:if>
    </g:else>
    <g:set var="i" value="${i+1}"/>
</g:each>

