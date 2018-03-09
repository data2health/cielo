import edu.wustl.cielo.listeners.UserAccountPasswordEncoderListener
import edu.wustl.cielo.listeners.SecurityAuthSuccessEventListener
import edu.wustl.cielo.listeners.SecurityAuthFailureEventListener
// Place your Spring DSL code here
beans = {
    userAccountPasswordEncoderListener(UserAccountPasswordEncoderListener, ref('hibernateDatastore'))
    securityAuthSuccessEventListener(SecurityAuthSuccessEventListener) {
        userAccountService = ref('userAccountService')
    }
    securityAuthFailureEventListener(SecurityAuthFailureEventListener) {
        userAccountService = ref('userAccountService')
    }
}
