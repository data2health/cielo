
<g:each in="${activities}" var="activity">
    <div id="activity_post_${activity.id}" class="card activity-post col-md-4">
    <h6 class="card-header">
    <g:if test="${activity.user?.profile?.picture}">
        <asset:image class="activity-profile-pic" src="data:image/png;base64,${activity.user?.profile?.picture?.fileContents}"/>
    </g:if>
    <g:else>
        <asset:image class="activity-profile-pic" src="default_profile.png"/>
    </g:else>
        &nbsp;${activity.activityInitiatorUserName}
    <span class="date-time">
        <g:formatDateWithTimezone date="${activity.dateCreated}"/>
    </span>
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
    <span id="comment-tooltip-${activity.id}" class="d-inline-block i-button" tabindex="0" data-toggle="tooltip" title="Leave a comment">
        <i id="${activity.id}" class="fa fa-edit" onclick="showCommentBox(${activity.id}, this.id);"></i>
    </span>
    &nbsp;
    <span id="share-tooltip-${activity.id}" class="d-inline-block i-button" tabindex="0" data-toggle="tooltip" title="Share">
        <i id="share_${activity.id}" class="fa fa-external-link" onclick="sharePost(${activity.id}, this.id);"></i>
    </span>
    &nbsp;
    <span id="like-tooltip-${activity.id}" class="d-inline-block i-button" tabindex="0" data-toggle="tooltip" title="Like">
        <i id="like_${activity.id}" class="fa fa-meh-o" onclick="likePost(${activity.id}, this.id)"></i>
    </span>
    </div>
    <div class="comment-add-box" id="comment_box_${activity.id}">
        <div class="input-group">
            <div class="input-group-prepend">
                <span class="input-group-text">${username}@</span>
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
    </div>
</g:each>

<div id="nextPage" style="display: flex;justify-content: center;">
    <g:if test="${showMoreActivitiesButton}">
        <button id="retrieveOlderActivityButton" type="button" class="btn btn-link" onclick="getOlderActivity(${activityOffset}, ${activityMax})">Load older activity</button>
    </g:if>
    <g:else>
        <p class="text-secondary">Hurray! You have read all activity posts. Now get back to work!</p>
    </g:else>
</div>