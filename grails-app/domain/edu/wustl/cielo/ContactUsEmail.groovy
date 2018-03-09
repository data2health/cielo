package edu.wustl.cielo

class ContactUsEmail {

    int attempts = 0
    String subject
    String plainMessage
    String htmlMessage
    List<String> toAddresses

    static mapping = {
        plainMessage type: 'text'
        htmlMessage type: 'text'
    }
    static constraints = {
        attempts(min: 0, max: 2)
        subject(nullable: false)
        plainMessage(nullable: false)
        htmlMessage(nullable: false)
        toAddresses(nulllable: false)
    }
}
