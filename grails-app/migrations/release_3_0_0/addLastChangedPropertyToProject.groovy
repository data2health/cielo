package release_3_0_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1524497401025-1") {
        addColumn(tableName: "project") {
            column(name: "last_changed", type: "timestamp", defaultValueDate: new Date().toTimestamp()) {
                constraints(nullable: "false")
            }
        }
    }
}
