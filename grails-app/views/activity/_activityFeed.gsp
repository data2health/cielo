
<g:each in="${activities}" var="activity">
    <div id="activity_post_${activity.id}" class="card activity-post col-md-4">
        <g:render template="/activity/singleActivity" model="[activity: activity, user: user]"/>
    </div>
</g:each>

<div id="nextPage" style="display: flex;justify-content: center;">
    <div id="offset" style="display: none;">${activityOffset}</div>
    <div id="max" style="display: none;">${activityMax}</div>
    <div id="loading-activity-indicator" style="display: none;"><i class="fas fa-spinner fa-spin fa-2x"></i> </div>
    <button id="loadOlderActivity" type="button" style="display: none;" class="btn btn-link"
            onclick="getOlderActivity(${activityOffset}, ${activityMax});">
        Load More...
    </button>
    <g:if test="${!showMoreActivitiesButton}">
        <div id="no-more-activity"><p class="text-secondary">Hurray! You have read all activity posts. Now get back to work!</p></div>
    </g:if>
</div>