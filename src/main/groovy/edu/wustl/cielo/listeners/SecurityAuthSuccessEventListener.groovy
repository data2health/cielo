package edu.wustl.cielo.listeners

import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent

/**
 * Listener for successful interactive authentication
 */
class SecurityAuthSuccessEventListener implements ApplicationListener <InteractiveAuthenticationSuccessEvent> {
    def userAccountService

    /**
     * Handle extra 'stuff' that needs to happen on successful login
     *
     * @param event instance of InteractiveAuthenticationSuccessEvent
     */
    void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        userAccountService.handleOnAuthSuccess(event.authentication.principal.username)

    }
}
