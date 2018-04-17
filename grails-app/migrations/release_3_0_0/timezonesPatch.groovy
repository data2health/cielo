package release_3_0_0

databaseChangeLog = {
    changeSet(author: "rickyrodriguez (generated)", id: "1523985708000-1") {
        sql("UPDATE user_account set timezone_id='US/Eastern' where timezone_id='EST';")
        sql("UPDATE user_account set timezone_id='US/Eastern' where timezone_id='US/Indiana-Starke';")
    }
}