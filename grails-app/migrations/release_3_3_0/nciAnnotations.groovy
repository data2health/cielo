package release_3_3_0

databaseChangeLog = {


    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-1") {
        sql("DELETE FROM project_annotation;")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-2") {
        sql("DELETE FROM profile_annotation;")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-3") {
        sql("DELETE FROM annotation;")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-4") {
        addColumn(tableName: "annotation") {
            column(name: "code", type: "varchar(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-5") {
        addColumn(tableName: "annotation") {
            column(name: "term", type: "text") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-6") {
        addUniqueConstraint(columnNames: "term", constraintName: "UC_ANNOTATIONTERM_COL", tableName: "annotation")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1538663010423-7") {
        dropColumn(columnName: "label", tableName: "annotation")
    }
}
