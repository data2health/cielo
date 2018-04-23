package release_3_0_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1524497401025-1") {
        addColumn(tableName: "project") {
            column(name: "last_changed", type: "timestamp") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1524497401025-2") {
                sql("UPDATE public.project SET last_changed=last_updated WHERE last_changed is null;")
    }
}
