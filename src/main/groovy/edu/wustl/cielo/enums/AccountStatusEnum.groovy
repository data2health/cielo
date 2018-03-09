package edu.wustl.cielo.enums

/**
 * Provide more detailed status of the state of an account. Spring security uses expired, locked
 */
enum AccountStatusEnum {
    ACCOUNT_UNVERIFIED, //after creating but not verified
    ACCOUNT_VERIFIED,   //verified but before first login
    ACCOUNT_ACTIVE,     //after activated and logged in
    ACCOUNT_EXPIRED,
    ACCOUNT_LOCKED,
    PASSWORD_EXPIRED
}