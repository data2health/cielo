package release_3_0_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1523372508172-1") {
        createTable(tableName: "comment_user_account") {
            column(name: "comment_liked_by_users_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_account_id", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1523372508172-2") {
        addForeignKeyConstraint(baseColumnNames: "user_account_id", baseTableName: "comment_user_account", constraintName: "FK7lorueemlsi9gk4xw6s12jtti", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1523372508172-3") {
        addForeignKeyConstraint(baseColumnNames: "comment_liked_by_users_id", baseTableName: "comment_user_account", constraintName: "FKra753ijkkoybjclwda0p5uhv7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "comment")
    }
}
