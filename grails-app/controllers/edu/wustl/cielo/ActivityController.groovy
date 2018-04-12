package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class ActivityController {

    static int DEFAULT_COMMENT_COUNT = 2

    def activityService
    def springSecurityService

    @Secured('isAuthenticated()')
    def getActivities() {
        int max    = Integer.valueOf(params.max)
        int offset = Integer.valueOf(params.offset)
        int newOffset = Integer.valueOf(params.offset)+max

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
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        savedComment = activityService.saveComment(Long.valueOf(params.activityId), params.text, user)

        render ([success: savedComment] as JSON)
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
}
