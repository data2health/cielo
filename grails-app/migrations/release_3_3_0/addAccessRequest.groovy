package release_3_3_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1531848682830-1") {
        createTable(tableName: "access_request") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "access_requestPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "project_owner_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "project_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "mask", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531848682830-2") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "access_request", constraintName: "FKkjrgbcdfl4nm7s7rfaet1guh3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }
}
