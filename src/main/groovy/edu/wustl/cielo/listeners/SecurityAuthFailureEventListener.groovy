package edu.wustl.cielo.listeners

import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.authentication.BadCredentialsException

/**
 * Event listener for AbstractAuthenticationFailureEvent that occurs with a failed login
 */
class SecurityAuthFailureEventListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {
    def userAccountService

    /**
     * Handle failed logins
     *
     * @param event an instance of AbstractAuthenticationFailureEvent
     */
    void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        //handle event
        switch (event.exception.class.simpleName) {
            //account is neither locked nor disabled
            case BadCredentialsException.simpleName:
                //bad password? If user exists then bad password. Principal is string when not authenticated
                userAccountService.handleBadCredentials(event.authentication.principal)
                break
        }
    }

}
