package cielo

import edu.wustl.cielo.UserAccount
import edu.wustl.cielo.EmailSenderJob
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

        //always add annotations
        annotationService.initializeAnnotations(new File(webRoot + grailsApplication.config.annotations))

        //always bootstrap licenses
        softwareLicenseService.bootstrapLicenses(new File(webRoot + grailsApplication.config.software.licenses.path))

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
        log.info("Going to setup environment with mock data\n")
        institutionService.setupMockInstitutions(new File(webRoot + grailsApplication.config.institutions))
        userAccountService.setupMockAppUsers(3, 5)
        teamService.bootstrapTeams(5, 5)
        userAccountService.bootstrapFollowers(5)
        projectService.bootstrapProjects(2)
    }
}
