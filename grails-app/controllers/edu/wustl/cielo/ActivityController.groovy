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

        render(template: "/templates/activityFeed",
                model:[activities: activityService.getActivities(offset, max), activityOffset: newOffset, activityMax: max,
                       showMoreActivitiesButton: activityService.areThereMoreActivitiesToRetrieve(newOffset, max),
                       username: user?.username
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
}
