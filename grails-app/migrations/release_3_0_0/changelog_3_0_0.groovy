package release_3_0_0

databaseChangeLog = {
    include file: "release_3_0_0/addLikesToComments.groovy"
    include file: "release_3_0_0/addLikesToActivityPosts.groovy"
    include file: "release_3_0_0/timezonesPatch.groovy"
    include file: "release_3_0_0/addLastChangedPropertyToProject.groovy"
}