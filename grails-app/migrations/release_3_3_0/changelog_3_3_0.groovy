package release_3_3_0

databaseChangeLog = {
    include file: "release_3_3_0/addedSpringSecurityAcl.groovy"
    include file: "release_3_3_0/addAccessRequest.groovy"
    include file: "release_3_3_0/addAPIRoleToAdminUsers.groovy"
    include file: "release_3_3_0/nciAnnotations.groovy"
}