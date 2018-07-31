package edu.wustl.cielo

import edu.wustl.cielo.enums.AccessRequestStatusEnum
import groovy.util.logging.Slf4j

@Slf4j
class AccessRequestCleanupJob {
    def accessRequestService

    static triggers = {
        //every 5 minutes check access requests that can be deleted
        cron name: 'accessRequestsTrigger', startDelay: 300000, cronExpression: '0 0/5 * 1/1 * ? *'
    }

    /**
     * Remove all the acknowledged requests
     *
     * @param context
     */
    def execute() {
        log.debug("Checking for access requests to delete...")

        AccessRequest.findByStatus(AccessRequestStatusEnum.ACKNOWLEDGED).each { AccessRequest accessRequest ->
            accessRequestService.deleteRequest(accessRequest)
        }
    }
}
