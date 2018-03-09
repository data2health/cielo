package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.validation.ObjectError

@Transactional
@Slf4j
class InstitutionService {

    /**
     * Setup institutions based on a json file
     *
     * @param institutions the json file with list of institutions
     */
    void setupMockInstitutions(File institutions) {
        def jsonObject = institutions ? new JsonSlurper().parse(institutions) : null

        log.info("****************************")
        log.info("Creating mock institutions using ${institutions.name}...")
        log.info("****************************\n")

        jsonObject?.institutions.each { institution ->
            if (Institution.countByFullNameAndShortName(institution.fullName, institution.shortName) == 0) {
                def institute = new Institution(fullName: institution.fullName,
                        shortName: institution.shortName)

                if (!institute.save()) {
                    institute.errors.getAllErrors().each { ObjectError err ->
                        log.error(err.toString())
                    }
                    log.error("Unable to save institue ${institute.fullName}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved institution with name: ${institute.fullName}")
            }
        }
        log.info("\n")
    }

    /**
     * Get a list of all available institutions
     *
     * @return a list of institutions with minimal
     */
    List<Institution> getAvailableInstitutions() {
        return Institution.list()
    }
}
