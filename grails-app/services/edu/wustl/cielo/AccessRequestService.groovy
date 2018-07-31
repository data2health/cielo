package edu.wustl.cielo

import edu.wustl.cielo.enums.AccessRequestStatusEnum
import grails.gorm.transactions.Transactional
import org.springframework.validation.ObjectError

@Transactional
class AccessRequestService {

    def groovyPageRenderer

    /**
     * Create a new access request
     *
     * @param projectId the id of the project we want to create a request for
     * @param user the user requesting access
     * @param bitMask the mask representing the access requested
     *
     * @return true if successful, false otherwise
     */
    boolean createRequest(Long projectId, UserAccount user, int bitMask) {
        AccessRequest accessRequest     = new AccessRequest()
        accessRequest.projectId         = projectId
        accessRequest.user              = user
        accessRequest.mask              = bitMask
        accessRequest.projectOwnerId    = Project.findById(projectId)?.projectOwner?.id

        if (!accessRequest.save()) {
            accessRequest.errors.allErrors.each { ObjectError error ->
                log.error(error.toString())
            }
            return false
        }
        return true
    }

    /**
     * Deny access request
     *
     * @param accessRequestId the id of the access request
     * @param userAccountId the id of user account denying access
     *
     * @return true if successful, false otherwise
     */
    boolean denyAccess(Long accessRequestId, Long userAccountId) {
        AccessRequest accessRequest = AccessRequest.findById(accessRequestId)

        if ( accessRequest ) {
            if ( accessRequest.projectOwnerId == userAccountId ) {

                accessRequest.status = AccessRequestStatusEnum.DENIED

                if ( !accessRequest.save() ) {
                    accessRequest.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    return false
                }
                return true
            }
        }
        return false
    }

    /**
     * Approve the request
     *
     * @param accessRequest the access request to approve
     *
     * @return true if successful, false otherwise
     */
    boolean approveAccessRequest(AccessRequest  accessRequest) {
        accessRequest.status = AccessRequestStatusEnum.APPROVED

        if (!accessRequest.save()) {
            accessRequest.errors.allErrors.each { ObjectError objectError ->
                log.error(objectError.toString())
            }
            return false
        }
        return true
    }

    /**
     * Delete a given access request
     *
     * @param accessRequest the access request to delete
     */
    void deleteRequest(AccessRequest accessRequest) {
        accessRequest.delete()
    }

    /**
     * Return a list of access request messages that belong to the user
     *
     * @param userAccount the user that we need to retrieve list for
     *
     * @return a list of access requests or null
     */
    List<AccessRequest> getAccessRequests(UserAccount userAccount, int max, int offset) {
        return AccessRequest.findAllByProjectOwnerId(userAccount.id, [max: max, offset: offset])
    }

    /**
     * Get total number of access requests for a given user
     *
     * @param userAccount the user to count requests for
     *
     * @return int value representing the total number of requests for user
     */
    int countAccessRequests(UserAccount userAccount) {
        return AccessRequest.countByProjectOwnerId(userAccount.id)
    }

    /**
     * Get the total number of pages for requests given a max count
     *
     * @param userAccount the user that owns the requests
     * @param max the max count of requests per page
     *
     * @return the number of pages for the user and a given max value
     */
    int getPagesCount(UserAccount userAccount, int max) {
        if (!max || max <= 0) max = Constants.DEFAULT_MAX

        int totalCount = countAccessRequests(userAccount)

        if (totalCount == 0 || totalCount <= max) return 1
        else return Math.ceil(totalCount / max).toInteger().intValue()
    }

    /**
     * Given a model render the row for access request table
     *
     * @param model which really just contains the list of access requests
     *
     * @return the rendered html
     */
    String renderTableRows(Map model) {
        return groovyPageRenderer.render(template: "/accessRequest/requestTableRows", model: model)
    }
}
