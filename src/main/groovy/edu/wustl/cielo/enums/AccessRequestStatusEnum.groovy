package edu.wustl.cielo.enums

enum AccessRequestStatusEnum {
    PENDING, //initial
    APPROVED, //by the owner of the object with the access request
    DENIED, //by the owner of the object with the access request
    ACKNOWLEDGED //by the user that submitted the access request
}