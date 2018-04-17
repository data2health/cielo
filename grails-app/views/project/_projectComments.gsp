<g:each in="${project.comments?.sort { a,b -> b.dateCreated <=> a.dateCreated }}" var="comment">
    <g:if test="${comment}">
    <div id="project_comments_${project.id}" class="card activity-post col-md-4">
    <h6 class="card-header">
        <g:getUserProfilePic user="${comment.commenter}" sticker="${true}" showLink="${true}">
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
    %{--Re-enable share at later date--}%
    %{--<span id="share_tooltip_${comment.id}" class="d-inline-block i-button" tabindex="1" data-toggle="tooltip" title="Share">--}%
    %{--<i id="share_project_comment_${comment.id}" class="far fa-share-square" onclick="shareProjectComment(${comment.id}, this.id);"></i>--}%
    %{--</span>--}%
    %{--&nbsp;--}%
    <span id="project_comment_tooltip_${comment.id}" class="i-button d-inline-block" data-toggle="tooltip" title="Reply" tabindex="0">
        <i id="reply_project_comment_${comment.id}" onclick="showProjectCommentBox(${comment.id});" class="far fa-comment fa-1x"></i>
        <span style="font-size: 0.85em; font-style: oblique;"> ${comment?.responses.size()} responses</span>
    </span>
    &nbsp;
        <g:if test="${comment?.likedByUsers?.contains(user)}">
            <span id="like_tooltip_${comment.id}" class="d-inline-block i-button" tabindex="2" data-toggle="tooltip" title="Un-Like">
            <i id="like_project_comment_${comment.id}" class="fas fa-check-circle liked-post" onclick="removelikeProjectComment(${comment.id}, this.id)"></i>
        </g:if>
        <g:else>
            <span id="like_tooltip_${comment.id}" class="d-inline-block i-button" tabindex="2" data-toggle="tooltip" title="Like">
            <i id="like_project_comment_${comment.id}" class="far fa-check-circle" onclick="likeProjectComment(${comment.id}, this.id)"></i>
        </g:else>
        <span style="font-size: 0.85em; font-style: oblique;"> ${comment?.likedByUsers.size()} likes</span>
    </span>
    <span>
        %{--Only show the first 4 if there are more then need to show a link to show all--}%
        <g:each in="${comment?.likedByUsers?.take(4)}" var="user">
            <g:getUserProfilePic user="${user}" imageSize="small" sticker="${false}" showLink="${true}" tooltipText="${user?.profile?.firstName} ${user?.profile?.lastName}${'<br><em>'}${user.username}${'</em>'}"/>
        </g:each>
    </span>

    <g:if test="${comment.likedByUsers.size() > 0 && comment?.likedByUsers?.size() > 4}">
        <button id="${comment.id}_usersLink" class="btn btn-link" style="padding: 0; margin: 0;" onclick="showAllUsersModal(${comment.id}, 'project');">...</button>
    </g:if>

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
        <g:if test="${comment.responses}">
            <g:render template="/templates/comments" model="[comments: comment?.responses, activityId: project.id,
                                                             numberOfComments: 2]"/>
        </g:if>
        <g:else>
            &nbsp;
        </g:else>
    </div>
    </div>
    </g:if>
</g:each>