package release_3_0_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1525183009498-1") {
        addColumn(tableName: "code") {
            column(name: "blob_id", type: "bytea")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1525183009498-2") {
        addColumn(tableName: "data") {
            column(name: "blob_id", type: "bytea")
        }
    }
}
