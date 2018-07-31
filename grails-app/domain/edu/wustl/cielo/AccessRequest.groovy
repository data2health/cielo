package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString
import edu.wustl.cielo.enums.AccessRequestStatusEnum

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class AccessRequest {

    int mask
    Long projectId
    Long projectOwnerId
    UserAccount user
    AccessRequestStatusEnum status = AccessRequestStatusEnum.PENDING

    static constraints = {
        mask(min: 1)
        projectId(min: 1L)
        projectOwnerId(min: 1L)
        user(nullable: false)
    }
}
