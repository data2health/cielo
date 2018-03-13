databaseChangeLog = {

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-1") {
        createSequence(sequenceName: "hibernate_sequence")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-2") {
        createTable(tableName: "activity") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "activityPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "event_text", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "activity_initiator_user_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "event_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "event_title", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-3") {
        createTable(tableName: "activity_comment") {
            column(name: "activity_comments_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "comment_id", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-4") {
        createTable(tableName: "annotation") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "annotationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-5") {
        createTable(tableName: "code") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "codePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)")

            column(name: "repository", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "revision", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-6") {
        createTable(tableName: "comment") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "commentPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "text", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "commenter_id", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-7") {
        createTable(tableName: "comment_comment") {
            column(name: "comment_responses_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "comment_id", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-8") {
        createTable(tableName: "contact_us_email") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "contact_us_emailPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "html_message", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "plain_message", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "subject", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "attempts", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-9") {
        createTable(tableName: "contact_us_email_to_addresses") {
            column(name: "contact_us_email_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "to_addresses_string", type: "VARCHAR(255)")

            column(name: "to_addresses_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-10") {
        createTable(tableName: "data") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "dataPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)")

            column(name: "repository", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "revision", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "CLOB") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-11") {
        createTable(tableName: "institution") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "institutionPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "short_name", type: "VARCHAR(16)") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "full_name", type: "VARCHAR(64)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-12") {
        createTable(tableName: "metadata") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "metadataPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "key", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-13") {
        createTable(tableName: "profile") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "profilePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "first_name", type: "VARCHAR(16)") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "interests", type: "VARCHAR(255)")

            column(name: "email_address", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "background", type: "VARCHAR(255)")

            column(name: "user_id", type: "BIGINT")

            column(name: "picture_id", type: "BIGINT")

            column(name: "user_class", type: "VARCHAR(255)")

            column(name: "last_name", type: "VARCHAR(16)") {
                constraints(nullable: "false")
            }

            column(name: "institution_id", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-14") {
        createTable(tableName: "profile_annotation") {
            column(name: "profile_annotations_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "annotation_id", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-15") {
        createTable(tableName: "profile_pic") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "profile_picPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "file_contents", type: "BYTEA") {
                constraints(nullable: "false")
            }

            column(name: "file_extension", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-16") {
        createTable(tableName: "project") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "projectPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "license_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "views", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "project_owner_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "shared", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "projects_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-17") {
        createTable(tableName: "project_annotation") {
            column(name: "project_annotations_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "annotation_id", type: "BIGINT")

            column(name: "annotations_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-18") {
        createTable(tableName: "project_code") {
            column(name: "project_codes_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code_id", type: "BIGINT")

            column(name: "codes_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-19") {
        createTable(tableName: "project_comment") {
            column(name: "project_comments_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "comment_id", type: "BIGINT")

            column(name: "comments_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-20") {
        createTable(tableName: "project_data") {
            column(name: "project_datas_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "data_id", type: "BIGINT")

            column(name: "datas_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-21") {
        createTable(tableName: "project_metadata") {
            column(name: "project_metadatas_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "metadata_id", type: "BIGINT")

            column(name: "metadatas_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-22") {
        createTable(tableName: "project_publication") {
            column(name: "project_publications_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "publication_id", type: "BIGINT")

            column(name: "publications_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-23") {
        createTable(tableName: "project_team") {
            column(name: "project_teams_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "team_id", type: "BIGINT")

            column(name: "teams_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-24") {
        createTable(tableName: "publication") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "publicationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "issn", type: "VARCHAR(255)")

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "oai", type: "VARCHAR(255)")

            column(name: "pmid", type: "VARCHAR(255)")

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)")

            column(name: "doi", type: "VARCHAR(255)")

            column(name: "nbn", type: "VARCHAR(255)")

            column(name: "sici", type: "VARCHAR(255)")

            column(name: "isbn", type: "VARCHAR(255)")

            column(name: "label", type: "VARCHAR(256)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-25") {
        createTable(tableName: "registration_code") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "registration_codePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_account_id", type: "BIGINT")

            column(name: "token", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-26") {
        createTable(tableName: "registration_email") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "registration_emailPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "html_message", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "plain_message", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "subject", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "attempts", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-27") {
        createTable(tableName: "registration_email_to_addresses") {
            column(name: "registration_email_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "to_addresses_string", type: "VARCHAR(255)")

            column(name: "to_addresses_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-28") {
        createTable(tableName: "software_license") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "software_licensePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "body", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "creator_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "custom", type: "BOOLEAN") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-29") {
        createTable(tableName: "team") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "teamPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "administrator_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(30)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-30") {
        createTable(tableName: "team_user_account") {
            column(name: "team_members_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_account_id", type: "BIGINT")

            column(name: "members_idx", type: "INT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-31") {
        createTable(tableName: "user_account") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "user_accountPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "password_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "account_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "failed_attempts", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "username", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "account_locked", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "timezone_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "password", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "last_login", type: "timestamp")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-32") {
        createTable(tableName: "user_account_user_account") {
            column(name: "user_account_connections_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_account_id", type: "BIGINT")
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-33") {
        createTable(tableName: "user_account_user_role") {
            column(name: "user_account_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_role_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-34") {
        createTable(tableName: "user_role") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "user_rolePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "authority", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-35") {
        addPrimaryKey(columnNames: "user_account_id, user_role_id", constraintName: "user_account_user_rolePK", tableName: "user_account_user_role")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-36") {
        addUniqueConstraint(columnNames: "email_address", constraintName: "UC_PROFILEEMAIL_ADDRESS_COL", tableName: "profile")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-37") {
        addUniqueConstraint(columnNames: "name", constraintName: "UC_TEAMNAME_COL", tableName: "team")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-38") {
        addUniqueConstraint(columnNames: "username", constraintName: "UC_USER_ACCOUNTUSERNAME_COL", tableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-39") {
        addUniqueConstraint(columnNames: "authority", constraintName: "UC_USER_ROLEAUTHORITY_COL", tableName: "user_role")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-40") {
        addForeignKeyConstraint(baseColumnNames: "user_account_id", baseTableName: "registration_code", constraintName: "FK2o5n2hwjcfy0d93r673p9a6gj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-41") {
        addForeignKeyConstraint(baseColumnNames: "picture_id", baseTableName: "profile", constraintName: "FK2s4gdd1w1h329i0h5r6a8lk3j", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "profile_pic")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-42") {
        addForeignKeyConstraint(baseColumnNames: "metadata_id", baseTableName: "project_metadata", constraintName: "FK3ub6yhrdc5fxwuhciwwaan7w1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "metadata")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-43") {
        addForeignKeyConstraint(baseColumnNames: "user_account_id", baseTableName: "user_account_user_role", constraintName: "FK4m6h2wlcb01oiri737ouua072", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-44") {
        addForeignKeyConstraint(baseColumnNames: "user_account_id", baseTableName: "user_account_user_account", constraintName: "FK69duwayjsvyarxy5jhnfwjphy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-45") {
        addForeignKeyConstraint(baseColumnNames: "commenter_id", baseTableName: "comment", constraintName: "FK6ma94kkto0rv5uo2cro7qjjjg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-46") {
        addForeignKeyConstraint(baseColumnNames: "data_id", baseTableName: "project_data", constraintName: "FK6y2fbb669owp2ermaqdt9e2wq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "data")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-47") {
        addForeignKeyConstraint(baseColumnNames: "project_owner_id", baseTableName: "project", constraintName: "FK72gn37j3h9hlam05cm6mt7xmc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-48") {
        addForeignKeyConstraint(baseColumnNames: "comment_responses_id", baseTableName: "comment_comment", constraintName: "FK89bfks7pbh0oo0gfpdi8glvtp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "comment")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-49") {
        addForeignKeyConstraint(baseColumnNames: "profile_annotations_id", baseTableName: "profile_annotation", constraintName: "FK8dsy8f8eqp6qysl9w43lte14", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "profile")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-50") {
        addForeignKeyConstraint(baseColumnNames: "user_account_id", baseTableName: "team_user_account", constraintName: "FK9mc9cr7di4gqthota81btxaj9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-51") {
        addForeignKeyConstraint(baseColumnNames: "institution_id", baseTableName: "profile", constraintName: "FK9op5gqb0y0dy962nbbjfc9st6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "institution")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-52") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "profile", constraintName: "FKa8kd4w5g3w9yxyu8r2ymwpv4f", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-53") {
        addForeignKeyConstraint(baseColumnNames: "comment_id", baseTableName: "project_comment", constraintName: "FKar204vwh1eo9ink3e6qdu6ku2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "comment")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-54") {
        addForeignKeyConstraint(baseColumnNames: "publication_id", baseTableName: "project_publication", constraintName: "FKbedf7v4t899j98b1fg2lb0xb6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "publication")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-55") {
        addForeignKeyConstraint(baseColumnNames: "activity_comments_id", baseTableName: "activity_comment", constraintName: "FKcgmgw60h05jaw2rx3iddkkrc2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "activity")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-56") {
        addForeignKeyConstraint(baseColumnNames: "comment_id", baseTableName: "comment_comment", constraintName: "FKdckfr4aqovn6x85lw5fkc9r94", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "comment")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-57") {
        addForeignKeyConstraint(baseColumnNames: "user_role_id", baseTableName: "user_account_user_role", constraintName: "FKfb630hc0kykfmwtxgr6qcsyyw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_role")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-58") {
        addForeignKeyConstraint(baseColumnNames: "comment_id", baseTableName: "activity_comment", constraintName: "FKfl0x7pg3psfbrkwlcl78djryo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "comment")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-59") {
        addForeignKeyConstraint(baseColumnNames: "annotation_id", baseTableName: "profile_annotation", constraintName: "FKga6q4nv1enpk5xr9csc0k5vrn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "annotation")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-60") {
        addForeignKeyConstraint(baseColumnNames: "administrator_id", baseTableName: "team", constraintName: "FKib5kbci7dmj0bpnr9c4unvd3a", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-61") {
        addForeignKeyConstraint(baseColumnNames: "annotation_id", baseTableName: "project_annotation", constraintName: "FKj5smo3knoioxudkvfm6us25ck", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "annotation")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-62") {
        addForeignKeyConstraint(baseColumnNames: "team_id", baseTableName: "project_team", constraintName: "FKje3rjxfpq3u3wj5iuvmxvk0r9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "team")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-63") {
        addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "software_license", constraintName: "FKk5x9xojhwo6opprcex5qwbr2v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-64") {
        addForeignKeyConstraint(baseColumnNames: "code_id", baseTableName: "project_code", constraintName: "FKl48egd3y4su66wfklrhfmuktv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "code")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-65") {
        addForeignKeyConstraint(baseColumnNames: "user_account_connections_id", baseTableName: "user_account_user_account", constraintName: "FKow31d8bi8ts3icd2y8r7nvneu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_account")
    }

    changeSet(author: "rickyrodriguez (generated)", id: "1520948005460-66") {
        addForeignKeyConstraint(baseColumnNames: "license_id", baseTableName: "project", constraintName: "FKr8sdwufixngr587oph3f05f5v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "software_license")
    }
}
