package release_3_3_0

databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-1") {
        createTable(tableName: "acl_class") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "acl_classPK")
            }

            column(name: "class", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-2") {
        createTable(tableName: "acl_entry") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "acl_entryPK")
            }

            column(name: "sid", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "audit_failure", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "granting", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "acl_object_identity", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "audit_success", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "ace_order", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "mask", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-3") {
        createTable(tableName: "acl_object_identity") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "acl_object_identityPK")
            }

            column(name: "object_id_identity", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "entries_inheriting", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "object_id_class", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "owner_sid", type: "BIGINT")

            column(name: "parent_object", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-4") {
        createTable(tableName: "acl_sid") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "acl_sidPK")
            }

            column(name: "sid", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "principal", type: "BOOLEAN") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-5") {
        addUniqueConstraint(columnNames: "class", constraintName: "UC_ACL_CLASSCLASS_COL", tableName: "acl_class")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-6") {
        addUniqueConstraint(columnNames: "sid, principal", constraintName: "UK1781b9a084dff171b580608b3640", tableName: "acl_sid")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-7") {
        addUniqueConstraint(columnNames: "object_id_class, object_id_identity", constraintName: "UK56103a82abb455394f8c97a95587", tableName: "acl_object_identity")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-8") {
        addUniqueConstraint(columnNames: "acl_object_identity, ace_order", constraintName: "UKce200ed06800e5a163c6ab6c0c85", tableName: "acl_entry")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-9") {
        addForeignKeyConstraint(baseColumnNames: "parent_object", baseTableName: "acl_object_identity", constraintName: "FK4soxn7uid8qxltqps8kewftx7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "acl_object_identity")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-10") {
        addForeignKeyConstraint(baseColumnNames: "sid", baseTableName: "acl_entry", constraintName: "FK9r4mj8ewa904g3wivff0tb5b0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "acl_sid")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-11") {
        addForeignKeyConstraint(baseColumnNames: "object_id_class", baseTableName: "acl_object_identity", constraintName: "FKc06nv93ck19el45a3g1p0e58w", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "acl_class")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-12") {
        addForeignKeyConstraint(baseColumnNames: "owner_sid", baseTableName: "acl_object_identity", constraintName: "FKikrbtok3aqlrp9wbq6slh9mcw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "acl_sid")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1531319838046-13") {
        addForeignKeyConstraint(baseColumnNames: "acl_object_identity", baseTableName: "acl_entry", constraintName: "FKl39t1oqikardwghegxe0wdcpt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "acl_object_identity")
    }
}
