package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class ActivityController {

    static int DEFAULT_COMMENT_COUNT = 2

    def activityService
    def springSecurityService
    def messageSource

    @Secured('isAuthenticated()')
    def getActivities() {
        int max    = params.max     ? Integer.valueOf(params.max)       : activityService.DEFAULT_MAX
        int offset = params.offset  ? Integer.valueOf(params.offset)    : activityService.DEFAULT_OFFSET
        int newOffset = offset + max

        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        render(template: "activityFeed",
                model:[activities: activityService.getActivities(offset, max), activityOffset: newOffset, activityMax: max,
                       showMoreActivitiesButton: activityService.areThereMoreActivitiesToRetrieve(newOffset, max),
                       user: user, username: user?.username
                ]
        )
    }

    @Secured('isAuthenticated()')
    def saveComment() {
        boolean savedComment
        Map messages = [:]
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        savedComment = activityService.saveComment(Long.valueOf(params.activityId), params.text, user)

        if (savedComment) messages.success = messageSource.getMessage('activity.comment.save.success', null, 'Comment was saved',
                request.locale)
        else messages.danger = messageSource.getMessage('activity.comment.save.failure', null, 'Comment save failed',
                request.locale)
        render ([success: savedComment, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def getComments() {

        render (template: "/templates/comments",
                model: [comments: Activity.findById(Long.valueOf(params.id))?.comments, activityId: params.id,
                        numberOfComments: (params.commentCount ? Integer.valueOf(params.commentCount) : DEFAULT_COMMENT_COUNT)])

    }

    @Secured('isAuthenticated()')
    def likeActivity() {
        boolean succeeded
        Long activityId = Long.valueOf(params.id)

        if (activityId) {
            succeeded = activityService.likeActivity(activityId)
        }
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def removeActivityLike() {
        boolean succeeded
        Long activityId = Long.valueOf(params.id)

        if (activityId) {
            succeeded = activityService.removeActivityLike(activityId)
        }
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def getCommentLikeUsers() {
        TreeSet<UserAccount> users
        Long activityId = Long.valueOf(params.id)

        if (activityId) {
            users = activityService.getUsersWhoLikedComment(activityId)
        }
        render(template: "/templates/commentLikesUsers", model: [users: users])
    }

    @Secured('isAuthenticated()')
    def getActivity() {
        Long activityId = Long.valueOf(params.id)

        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        render(template: "singleActivity",
                model:[activity: Activity.findById(activityId), user: user]
        )
    }

    @Secured('isAuthenticated()')
    def saveNewActivity() {
        String title        = params.title
        String message      = params.message
        Object principal    = springSecurityService?.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        Map messages        = [:]
        boolean succeeded

        if (user && title && message) {
            succeeded = activityService.saveActivityForManualPost(user, title, message)
        }

        if (succeeded == false) messages.danger = messageSource.getMessage('activity.post.failure', null, 'Unable to post activity',
                request.locale)

        render([success: succeeded, messages: messages] as JSON)
    }
}
