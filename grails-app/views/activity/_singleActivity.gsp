<h6 class="card-header">
    <g:getUserProfilePic user="${activity.user}" sticker="${true}" showLink="${true}">
        &nbsp;${activity.activityInitiatorUserName}
    </g:getUserProfilePic>
    <g:dateDiff date="${activity.dateCreated}"/>
</h6>
<div class="card-body">
    <h6 class="card-title activity-post-title">${activity.eventTitle}</h6>
    <g:rawOutput text="${activity.eventText}"/>
</div>
<g:if test="${activity?.comments}">
    <div id="feed_footer_activity_${activity.id}" class="card-footer button-block with-comments">
</g:if>
<g:else>
    <div id="feed_footer_activity_${activity.id}" class="card-footer button-block">
</g:else>
%{--Re-enable share at later date--}%
%{--<span id="share-tooltip-${activity.id}" class="d-inline-block i-button" tabindex="1" data-toggle="tooltip" title="Share">--}%
%{--<i id="share_${activity.id}" class="far fa-share-square" onclick="sharePost(${activity.id}, this.id);"></i>--}%
%{--</span>--}%
%{--&nbsp;--}%
<span id="comment-tooltip-${activity.id}" class="d-inline-block i-button" tabindex="0" data-toggle="tooltip" title="Leave a comment">
    <i id="${activity.id}" class="far fa-comment fa-1x" onclick="showCommentBox(${activity.id}, this.id);"></i>
    <span style="font-size: 0.85em; font-style: oblique;"> ${activity?.comments.size()} comments</span>
</span>
&nbsp;
<g:if test="${activity?.likedByUsers?.contains(user)}">
    <span id="like_tooltip_${activity.id}" class="d-inline-block i-button" tabindex="2" data-toggle="tooltip" title="Un-Like">
    <i id="like_project_comment_${activity.id}" class="fas fa-check-circle liked-post" onclick="removeActivityLike(${activity.id}, this.id)"></i>
</g:if>
<g:else>
    <span id="like_tooltip_${activity.id}" class="d-inline-block i-button" tabindex="2" data-toggle="tooltip" title="Like">
    <i id="like_project_comment_${activity.id}" class="far fa-check-circle" onclick="likePost(${activity.id}, this.id)"></i>
</g:else>
<span style="font-size: 0.85em; font-style: oblique;"> ${activity?.likedByUsers.size()} likes</span>
</span>
<span>
    %{--Only show the first 4 if there are more then need to show a link to show all--}%
    <g:each in="${activity?.likedByUsers?.take(4)}" var="user">
        <g:getUserProfilePic user="${user}" imageSize="small" sticker="${false}" showLink="${true}" tooltipText="${user?.profile?.firstName} ${user?.profile?.lastName}${'<br><em>'}${user.username}${'</em>'}"/>
    </g:each>
</span>

<g:if test="${activity.likedByUsers.size() > 0 && activity?.likedByUsers?.size() > 4}">
    <button id="${activity.id}_usersLink" class="btn btn-link" style="padding: 0; margin: 0;" onclick="showAllUsersModal(${activity.id}, 'activity');">...</button>
</g:if>

</div>
<div class="comment-add-box" id="comment_box_${activity.id}">
    <div class="input-group">
        <div class="input-group-prepend">
            <span class="input-group-text"><g:getUserProfilePic sticker="${false}" showLink="${true}"/></span>
        </div>
        <textarea class="form-control" id="comment-box-text-${activity.id}"></textarea>
    </div>
    <input type="button" class="btn-danger add-comment-button cancel-comment-button" value="Cancel" onclick="cancelComment(${activity.id})">
    <input type="button" class="btn-primary add-comment-button" value="Post" onclick="postComment(${activity.id})">
</div>
<div id="comments_activity_${activity.id}">
    <g:if test="${activity?.comments}">
        <g:render template="/templates/comments" model="[comments: activity?.comments, activityId: activity.id,
                                                         numberOfComments: 2]"/>
    </g:if>
</div>
