<g:each in="${comments.sort { a,b -> b.dateCreated <=> a.dateCreated }}" var="comment">
    <div id="project_comments_${project.id}" class="card activity-post col-md-4">
    <h6 class="card-header">
        <g:getUserProfilePic user="${comment.commenter}" sticker="${true}">
            &nbsp;${comment.commenter.username}
        </g:getUserProfilePic>
        <span class="date-time">
            <g:formatDateWithTimezone date="${comment.dateCreated}"/>
        </span>
    </h6>
    <div class="card-body">
        <g:rawOutput text="${comment.text}"/>
    </div>
    <g:if test="${comment?.responses}">
        <div id="project_comments_footer_${comment.id}" class="card-footer button-block with-comments">
    </g:if>
    <g:else>
        <div id="project_comments_footer_${comment.id}" class="card-footer button-block">
    </g:else>
    <span id="project_comment_tooltip_${comment.id}" class="i-button d-inline-block" data-toggle="tooltip" title="Reply" tabindex="0">
        <i id="reply_project_comment_${comment.id}" onclick="showProjectCommentBox(${comment.id});" class="far fa-comment fa-1x"></i>
    </span>
    &nbsp;
    <span id="share_tooltip_${comment.id}" class="d-inline-block i-button" tabindex="1" data-toggle="tooltip" title="Share">
        <i id="share_project_comment_${comment.id}" class="far fa-share-square" onclick="shareProjectComment(${comment.id}, this.id);"></i>
    </span>
    &nbsp;
    <span id="like_tooltip_${comment.id}" class="d-inline-block i-button" tabindex="2" data-toggle="tooltip" title="Like">
        <i id="like_project_comment_${comment.id}" class="far fa-check-circle" onclick="likeProjectComment(${comment.id}, this.id)"></i>
    </span>
    </div>
    <div class="comment-add-box" id="project_comment_box_${comment.id}">
        <div class="input-group">
            <div class="input-group-prepend">
                <span class="input-group-text"><g:getUserProfilePic/></span>
            </div>
            <textarea class="form-control" id="project_comment_box_text_${comment.id}"></textarea>
        </div>
        <input type="button" class="btn-danger add-comment-button cancel-comment-button" value="Cancel" onclick="cancelProjectReply(${comment.id})">
        <input type="button" class="btn-primary add-comment-button" value="Post" onclick="postCommentReply(${comment.id})">
    </div>
    <div id="responses_project_${comment.id}">
        <g:if test="${comment?.responses}">
            <g:render template="/templates/comments" model="[comments: comment?.responses, activityId: project.id,
                                                             numberOfComments: 2]"/>
        </g:if>
        <g:else>
            &nbsp;
        </g:else>
    </div>
    </div>
</g:each>