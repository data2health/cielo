package cielo

import edu.wustl.cielo.Annotation
import edu.wustl.cielo.SoftwareLicense
import edu.wustl.cielo.UserAccount
import edu.wustl.cielo.EmailSenderJob
import grails.plugin.springsecurity.acl.AclClass
import grails.plugin.springsecurity.acl.AclSid
import grails.util.Environment
import groovy.util.logging.Slf4j

@Slf4j
class BootStrap {
    def grailsApplication
    def userAccountService
    def institutionService
    def annotationService
    def softwareLicenseService
    def teamService
    def projectService
    def customAclService

    def webRoot

    def init = { servletContext ->
        //set default timezone. We will show local time in UI but backend will always use UTC to
        //maintain consistency
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        webRoot = servletContext.getRealPath("/")

        //setup initial user roles
        userAccountService.bootstrapUserRoles()

        //get or setup admin account
        UserAccount admin = userAccountService.bootstrapCreateOrGetAdminAccount()

        //add user role to admin if not exists
        userAccountService.bootstrapAddSuperUserRoleToUser(admin)

        //always add annotations except for integration tests
        if (Annotation.count() == 0 && Environment.current != Environment.TEST) {
            annotationService.initializeAnnotations([new File(webRoot + grailsApplication.config.annotations)])
        }

        //always bootstrap licenses
        if (SoftwareLicense.count() == 0) softwareLicenseService.bootstrapLicenses(new File(webRoot + grailsApplication.config.software.licenses.path))

        if (AclClass.count() == 0) customAclService.bootstrapAcls()

        environments {
            development {
                //here would should have calls to pertinent things that are required to effectively develop app
                log.info("In development mode...")
                setupMockDataForDev()
            }
        }

        //init email job - send on a regular interval... emails do not need to be synchronous
        EmailSenderJob.schedule(new Date())
    }

    /**
     * Setup other stuff needed to test things
     *
     */
    void setupMockDataForDev() {
        //there should only be the admin setup.
        if (UserAccount.count() == 1) {
            log.info("Going to setup environment with mock data\n")
            institutionService.setupMockInstitutions(new File(webRoot + grailsApplication.config.institutions))
            userAccountService.setupMockAppUsers(3, 5)
            teamService.bootstrapTeams(5, 5)
            userAccountService.bootstrapFollowers(5) //admin account = 1
            projectService.bootstrapProjects(2)
        }
    }
}
