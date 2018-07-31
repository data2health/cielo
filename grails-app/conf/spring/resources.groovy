import edu.wustl.cielo.listeners.UserAccountPasswordEncoderListener
import edu.wustl.cielo.listeners.SecurityAuthSuccessEventListener
import edu.wustl.cielo.listeners.SecurityAuthFailureEventListener
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean

// Place your Spring DSL code here
beans = {
    aclCacheManager(EhCacheManagerFactoryBean) {
        shared = true
    }

    ehcacheAclCache = ref('aclCacheManager')

    userAccountPasswordEncoderListener(UserAccountPasswordEncoderListener, ref('hibernateDatastore'))
    securityAuthSuccessEventListener(SecurityAuthSuccessEventListener) {
        userAccountService = ref('userAccountService')
    }
    securityAuthFailureEventListener(SecurityAuthFailureEventListener) {
        userAccountService = ref('userAccountService')
    }
}
