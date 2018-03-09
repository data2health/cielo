package edu.wustl.cielo

import edu.wustl.cielo.enums.UserRolesEnum
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.validation.ObjectError

@Transactional
@Slf4j
class SoftwareLicenseService {

    /**
     * Add software licenses to db
     *
     * @param licensesFile the file that contains list of licenses in json format
     */
    def bootstrapLicenses(File licensesFile) {
        def jsonObject = licensesFile ? new JsonSlurper().parse(licensesFile) : null
        UserAccount superAdmin = UserAccountUserRole.findByUserRole(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))?.userAccount

        if (superAdmin) {
            log.info("**********************************************************")
            log.info("Creating licenses with file ${licensesFile.name}...")
            log.info("**********************************************************\n")

            jsonObject?.licenses.each { license ->
                if (SoftwareLicense.countByLabel(license.label) == 0) {
                    def softwareLicense = new SoftwareLicense(label: license.label, body: license.body, url: license.url,
                            creator: superAdmin)

                    if (!softwareLicense.save()) {
                        softwareLicense.errors.getAllErrors().each { ObjectError err ->
                            log.error(err.toString())
                        }
                        log.error("Unable to save software license ${softwareLicense.label}. Exiting.")
                        System.exit(-1)
                    }
                    log.info("\tSaved license: ${license.label}")
                }
            }
            log.info("\n")
        }
    }
}
