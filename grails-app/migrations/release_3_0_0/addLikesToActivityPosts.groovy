package release_3_0_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1523373808593-1") {
        createTable(tableName: "activity_user_account") {
            column(name: "activity_liked_by_users_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_account_id", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1523373808593-2") {
        addForeignKeyConstraint(baseColumnNames: "user_account_id", baseTableName: "activity_user_account", constraintName: "FKb14wipdw4fcpmbjqyy6db9vfd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1523373808593-3") {
        addForeignKeyConstraint(baseColumnNames: "activity_liked_by_users_id", baseTableName: "activity_user_account", constraintName: "FKf91860rd85hbi822quy5i2xy4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "activity")
    }
}
