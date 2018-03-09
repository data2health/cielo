package edu.wustl.cielo

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.validation.ObjectError

@Transactional
@Slf4j
class AnnotationService {

    /**
     * Add annotations to the DB
     *
     * @param annotationsFile file that contains list of annotations; one per line
     */
    void initializeAnnotations(File annotationsFile) {
        log.info("****************************")
        log.info("Creating annotations with file ${annotationsFile.name}...")
        log.info("****************************\n")

        annotationsFile?.readLines().each { line ->
            if (Annotation.countByLabel(line) == 0) {
                Annotation annotation = new Annotation(label: line)

                if (!annotation.save()) {
                    annotation.errors.getAllErrors().each { ObjectError err ->
                        log.error(err.toString())
                    }
                    log.error("Unable to save annotation ${annotation}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved annotation: ${annotation}")
            }
        }
        log.info("\n")
    }
}
