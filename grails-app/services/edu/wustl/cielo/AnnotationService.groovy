package edu.wustl.cielo

import grails.gorm.transactions.ReadOnly
import grails.plugin.cache.Cacheable
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.hibernate.Session
import java.sql.Timestamp

@Slf4j
class AnnotationService {

    final static int DEFAULT_ANNOTATIONS_MAX_COUNT = 20
    def sessionFactory
    def dataSource
    def utilService

    /**
     * Add annotations to the DB
     *
     * @param annotationsFile file that contains list of annotations; one per line
     */
    void initializeAnnotations(List<File> annotationsFiles) {
        Date startDateTime = new Date()

        annotationsFiles.each { File annotationsFile ->
            log.info("****************************")
            log.info("Creating annotations with file ${annotationsFile.name}...")
            log.info("****************************\n")

            BufferedReader bufferedReader = annotationsFile?.newReader()
            Iterator<String> iterator = bufferedReader.iterator()

            String line
            List keyVal

            while (iterator.hasNext()) {
                line = iterator.next()
                keyVal = line.tokenize()

                saveNewAnnotation(keyVal[1].tokenize("|"), keyVal[0])
            }
        }

        String dateTimeDiff = utilService.getDateDiff(startDateTime, null)
        log.info("*****************************************************************")
        log.info("* Mesh Terms import completed. It started ${dateTimeDiff} *")
        log.info("*****************************************************************")
        log.info("\n")
    }

    /**
     * Save annotations which are really mesh terms
     *
     * @param names the names of the annotations; name + synonyms
     * @param ui the Unique Identifier
     * @param note the note for the term
     */
    void saveNewAnnotation(List<String> names, String code) {
        final Session session = sessionFactory.currentSession
        Sql sql = new Sql(dataSource)

        names.each { String name ->
            Timestamp timestamp = new Timestamp(new Date().getTime())
            String query = "INSERT INTO annotation(version, date_created, last_updated, term, code) \n" +
                    "VALUES (0, ?, ?, ?, ?) \n" +
                    "ON CONFLICT (term) DO UPDATE SET version = annotation.version + 1;"

            sql.executeInsert(query, [timestamp, timestamp, name, code])
            log.info("\t\tSaved or updated annotation for term: ${name}")

        }
        session.flush()
    }

    @ReadOnly
    @Cacheable("filtered_annotations")
    List<Object> retrieveFilteredAnnotationsFromDB(String filterText, int pageOffset) {

        if (!filterText) return []

        int max = DEFAULT_ANNOTATIONS_MAX_COUNT
        int offsetBY = (pageOffset * max)

        Sql sql = new Sql(dataSource)
        String whereClause = "WHERE lower(term) like lower('${filterText?:''}%') "
        List params = []

        String query = """
                 SELECT
                     id,
                     term
                 FROM annotation
                 ${ whereClause } 
                 ORDER BY term
                 OFFSET ? LIMIT ?
             """
        params.add(offsetBY)
        params.add(max)

        return sql.rows(query, params).collect {
            [id: it.id, text: it.term]
        }
    }

    /**
     * Get the total number of items with filter
     *
     * @param filterText the text to filter annotations on
     *
     * @return the total number of items that match the given filter
     */
    int getFilteredAnnotationsCount(String filterText) {
        Sql sql = new Sql(dataSource)
        String whereClause = "WHERE lower(term) like lower('${filterText?:''}%') "
        List params = []

        String query = """
                SELECT
                    count(id) as cnt
                FROM annotation
                ${ whereClause } 
            """

        return sql.firstRow(query, params).cnt
    }

    /**
     * Get number of pages for results
     *
     * @param totalCount the total number of items to fit into pages
     * @param max items per page
     *
     * @return int value representing the number of pages
     */
    int getNumberOfPagesForAnnotations(int totalCount, int max) {
        if (!max || max <= 0) max = DEFAULT_ANNOTATIONS_MAX_COUNT

        if (totalCount == 0 || totalCount <= max) return 1
        else return Math.ceil(totalCount / max).toInteger().intValue()
    }
}
