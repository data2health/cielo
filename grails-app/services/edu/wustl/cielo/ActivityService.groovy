package edu.wustl.cielo

import grails.events.annotation.gorm.Listener
import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import groovy.util.logging.Slf4j
import edu.wustl.cielo.enums.ActivityTypeEnum
import org.grails.datastore.mapping.engine.EntityAccess
import org.springframework.validation.ObjectError
import grails.events.annotation.*
import org.grails.datastore.mapping.engine.event.*
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import grails.util.Environment

@Transactional
@Slf4j
class ActivityService {

    static int DEFAULT_OFFSET   = 0
    static int DEFAULT_MAX      = 5

    static def grailsCacheManager
    def springSecurityService
    def messageSource
    LinkGenerator grailsLinkGenerator

    /**
     * Persist the event (pre-update event) so we can display in the UI
     *
     * @param event the pre-update event which contains the object to be updated
     */
    @Subscriber
    void preUpdateHandler(PreUpdateEvent event) {

        if (Environment.current != Environment.TEST) {
            String domainClass = event.getEntityObject().class.simpleName
            ActivityTypeEnum activityTypeEnum
            def domainObject = event.getEntityAccess().getEntity()

            domainObject.properties.each {
                if (domainObject.hasChanged(it.key.toString())
                        && (it.key.toString() != "version" || it.key.toString() != "lastUpdated")) {
                    //need to see if we need to log the activity
                    activityTypeEnum = getUpdateActivityType(domainObject, it.key.toString())

                    if (activityTypeEnum) {
                        saveActivity(event, domainClass, activityTypeEnum)

                        //force invalidation of project cache for most viewed projects ... name or description could
                        //have changed
                        if (domainObject.class.simpleName == "Project") {
                            grailsCacheManager.getCache("most_viewed_projects").clear()
                        }
                    }
                }
            }
        }
    }

    /**
     * Persist the event (insert event) so we can display in the UI
     *
     * @param event the insert event which contains the object inserted
     */
    @Listener
    void logActivityForInsert(PostInsertEvent event) {

        if (Environment.current != Environment.TEST) {
            String domainClass = event.getEntityAccess().entity.class.simpleName
            ActivityTypeEnum activityTypeEnum = getNewActivityTypeEnum(domainClass)

            //if it's not null then we care about this activity
            if (activityTypeEnum) {
                saveActivity(event, domainClass, activityTypeEnum)
            }
        }
    }

    /**
     * Persist the event (delete event) so we can display in the UI
     *
     * @param event the insert event which contains the object inserted
     */
    @Subscriber
    void logActivityForDelete(PostDeleteEvent event) {
        //TODO: Figure out if we need this, if we do add code to handle this.
        if (Environment.current != Environment.TEST) {
            log.info("Subscriber to delete event")
        }
    }

    /**
     * Generate the event body text (on insert)
     *
     * @param user the user that initiated the event
     * @param domainClass the simple class name for the domain object
     * @param activityTypeEnum
     * @param entityId the entity id of the domain object affected
     *
     * @return the text for the event
     */
    private String generateEventBody(EntityAccess entityAccess, UserAccount user, String domainClass, ActivityTypeEnum activityTypeEnum, Long entityId) {
        Object[] params
        switch (domainClass) {
            case "UserAccount":
                params = getUserAccountActivityParams(entityAccess, entityId)
                break
            case "Profile":
                params = getUserProfileActivityParams(user, entityAccess, activityTypeEnum)
            case "Team":
            case "Data":
            case "Code":
                params = getGenericActivityParams(user, entityId, domainClass)
                break
            case "Project":
                params = getProjectActivityParams(entityAccess, user, activityTypeEnum, entityId)
                break
        }

        return messageSource.getMessage(activityTypeEnum.toString()+"_BODY_TEXT", params, Locale.getDefault())
    }

    /**
     * Generate the params to use when generating activity text for user domain object
     *
     * @param user the user that is affected by the activity
     * @param activityTypeEnum the enum describing the activity
     *
     * @return a list of objects
     */
    Object[] getUserAccountActivityParams(EntityAccess entityAccess, Long userId) {
        return  [entityAccess.getPropertyValue("username"),
                 (grailsLinkGenerator.serverBaseURL?:"") + "/user/${userId}"
        ]
    }

    /**
     * Get the activity params for profile updates
     *
     * @param user the user that initiated the profile change
     * @param entityAccess the entity that changed (this is the same as the user)
     * @param activityTypeEnum the activity type
     *
     * @return a list of objects
     */
    Object[] getUserProfileActivityParams(UserAccount user, EntityAccess entityAccess, ActivityTypeEnum activityTypeEnum) {
        Object params = []

        switch (activityTypeEnum) {
            case ActivityTypeEnum.ACTIVITY_UPDATE_USER_EMAIL_ADDRESS:
                params = [user?.username, entityAccess.getProperty("emailAddress"),
                          user.getOriginalValue("emailAddress")]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_USER_INSTITUTION:
                params = [user?.username, entityAccess.getProperty("institution"),
                          user.getOriginalValue("institution")]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_USER_INTERESTS:
                params = [user?.username, entityAccess.getProperty("interests"),
                          user.getOriginalValue("interests")]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_USER_PICTURE:
                params = [user?.username]
                break
        }

        return params
    }

    Object[] getGenericActivityParams(UserAccount user, Long entityId, String domainClass) {
        return  [user?.username,
                 (grailsLinkGenerator.serverBaseURL?:"") + "/${domainClass.toLowerCase()}/${entityId}"
        ]
    }

    /**
     * Generate the params to use when generating activity text for project
     *
     * @param user the user that is affected by the activity
     * @param activityTypeEnum the enum describing the activity
     * @param entityId the id of the project
     *
     * @return a list of objects
     */
    Object[] getProjectActivityParams(EntityAccess entityAccess, UserAccount user, ActivityTypeEnum activityTypeEnum, Long entityId){
        String entityIdentifier
        Object params = []

        switch (activityTypeEnum) {
            case ActivityTypeEnum.ACTIVITY_NEW_PROJECT:
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_CODES:
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_DATAS:
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_PUBLICATIONS:
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_ANNOTATIONS:
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS:
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_METADATAS:
                entityIdentifier = entityAccess.getPropertyValue("name")
                params = [user?.username, (grailsLinkGenerator.serverBaseURL?:"") + "/project /${entityId}", entityIdentifier]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_SHARED:
                params = [entityAccess.getPropertyValue("shared")?"shared" : "private"]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_NAME:
                params = [entityAccess.getPropertyValue("name"), ((Project)entityAccess.getEntity()).getOriginalValue("name")]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_DESCRIPTION:
                params = [entityAccess.getPropertyValue("description"), ((Project)entityAccess.getEntity()).getOriginalValue("description")]
                break
            case ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_STATUS:
                params = [entityAccess.getPropertyValue("status"), ((Project)entityAccess.getEntity()).getOriginalValue("status")]
                break
        }

        return params
    }

    /**
     * Get the activity type
     *
     * @param className simple class name of affected domain object
     * @return
     */
    private static ActivityTypeEnum getNewActivityTypeEnum(String className) {
        ActivityTypeEnum activityType

        switch (className) {
            case "Team":
                activityType = ActivityTypeEnum.ACTIVITY_NEW_TEAM
                break
            case "Project":
                activityType = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
                break
            case "UserAccount":
                activityType = ActivityTypeEnum.ACTIVITY_NEW_USER
                break
            case "Data":
                activityType = ActivityTypeEnum.ACTIVITY_NEW_UPLOAD_DATA
                break
            case "Code":
                activityType = ActivityTypeEnum.ACTIVITY_NEW_UPLOAD_CODE
                break
        }

        return activityType
    }

    /**
     * Log the activity
     *
     * @param event the abstract event
     * @param domainClass the domain class simplename of the affected object
     * @param activityTypeEnum the activity type
     */
    private void saveActivity(AbstractPersistenceEvent event, String domainClass, ActivityTypeEnum activityTypeEnum) {

        Long entityId = event.getEntityAccess().getPropertyValue('id')
        UserAccount user

        if (springSecurityService.isLoggedIn()) {
            user = UserAccount.get(springSecurityService?.principal?.id)
        } else {
            user = UserAccount.findByUsername("admin")
        }

        if (user) {
            Activity activity
            String eventText = generateEventBody(event.getEntityAccess(), user, domainClass, activityTypeEnum, entityId)
            if (!Activity.findByActivityInitiatorUserNameAndEventTextAndEventType(user.username, eventText, activityTypeEnum)) {
                Activity.withNewTransaction {
                    activity = new Activity()
                    activity.activityInitiatorUserName = user.username
                    activity.eventType = activityTypeEnum
                    activity.eventTitle = messageSource.getMessage(activityTypeEnum.toString(), null, Locale.getDefault())
                    activity.eventText = eventText

                    if (!activity.save()) {
                        activity.errors.allErrors.each { ObjectError err ->
                            log.error(err.toString())
                        }
                        log.error("Unable to save activity")
                    }
                    log.info("Saved activity")
                }
            } else log.info("Activity already exists")

        } else {
            log.error("Activity could not be logged because there was an error finding the user")
        }
    }

    /**
     * Save the activity
     *
     * @param activityTypeEnum the activity that needs to be saved
     * @param text the text of the activity
     */
    void saveActivityForEvent(ActivityTypeEnum activityTypeEnum, String eventText) {

        def principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal?.id) : UserAccount.findByUsername("admin")
        Activity activity

        if (user) {
            if (!Activity.findByActivityInitiatorUserNameAndEventTextAndEventType(user.username, eventText, activityTypeEnum)) {
                Activity.withNewTransaction {
                    activity = new Activity()
                    activity.activityInitiatorUserName = user.username
                    activity.eventType = activityTypeEnum
                    activity.eventTitle = messageSource.getMessage(activityTypeEnum.toString(), null, Locale.getDefault())
                    activity.eventText = eventText

                    if (!activity.save()) {
                        activity.errors.allErrors.each { ObjectError err ->
                            log.error(err.toString())
                        }
                        log.error("Unable to save activity")
                    }
                    log.info("Saved activity")
                }
            }

        } else {
            log.error("Activity could not be logged because there was an error finding the user")
        }
    }

    /**
     * Returns the activity type of the update event
     *
     * @param domainClass the parent domain object
     * @param property the name of the property or child object changed
     * @return the activity type or null if we do not care about the updating of the property for the domain
     */
    private static ActivityTypeEnum getUpdateActivityType(Object domainObject, String property) {
        ActivityTypeEnum activityTypeEnum
        String domainClass = domainObject.class.simpleName

        switch (domainClass) {
            case "Project":
                if (property == "shared") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_SHARED
                if (property == "name") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_NAME
                if (property == "description") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_DESCRIPTION
                if (property == "metadatas") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_METADATAS
                if (property == "status") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_STATUS
                break
            case "Profile":
                if (property == "emailAddress") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_USER_EMAIL_ADDRESS
                if (property == "picture") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_USER_PICTURE
                if (property == "interests") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_USER_INTERESTS
                if (property == "institution") activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_USER_INSTITUTION
                break
        }
        return activityTypeEnum
    }

    /**
     * Get a list of activities
     *
     * @return list of activities
     */
    List<Activity> getActivities(){

        return Activity.executeQuery("select distinct a from Activity a order by dateCreated desc",
                [max: DEFAULT_MAX, offset: DEFAULT_OFFSET])
    }

    /**
     * Get a list of activities
     *
     * @param offset the page offset
     * @param max the max number of items to retrieve
     *
     * @return list of activities
     */
    List<Activity> getActivities(int offset, int max){

        return Activity.executeQuery("select distinct a from Activity a order by dateCreated desc",
                [max: max, offset: offset])
    }

    /**
     * Determine whether there are more activities to retrieve
     *
     * @param offset the page offset
     * @param max the number of items per page
     *
     * @return true if the offset is not equal or larger than total number of pages available
     */
    boolean areThereMoreActivitiesToRetrieve(int offset, int max) {
        int totalCount = Activity.count()

        return offset < totalCount
    }

    /**
     * Save a comment for an activity
     *
     * @param activityId the id of the activity to save the comment to
     * @param comment the text of the comment
     * @param commenterUser the user commenting
     *
     * @return true if successful, false otherwise
     */
    boolean saveComment(Long activityId, String commentText, UserAccount commenterUser) {

        boolean wasSuccessful = false
        Activity activity = Activity.findById(activityId)

        if (activity) {
            Comment comment = new Comment([commenter: commenterUser, text: commentText])

            if (!comment.save()) {
                comment.getErrors().allErrors.each { ObjectError err ->
                    log.error(err.toString())
                }
            }

            //now add comment to activity
            activity.comments.add(comment)

            if (!activity.save()){
                activity.getErrors().allErrors.each { ObjectError err ->
                    log.error(err.toString())
                }
                log.error("There was an error saving comment to activity ${activity.toString()}")
            }
            else wasSuccessful = true
        }
        return wasSuccessful
    }
}
