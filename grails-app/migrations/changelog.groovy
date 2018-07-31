import release_3_0_0.changelog_3_0_0
import release_3_3_0.changelog_3_3_0

databaseChangeLog = {
    include file: "baseSchema.groovy"
    include file: "release_3_0_0/changelog_3_0_0.groovy"
    include file: "release_3_3_0/changelog_3_3_0.groovy"
}