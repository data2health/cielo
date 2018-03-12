package edu.wustl.cielo

import edu.wustl.cielo.enums.ActivityTypeEnum
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import grails.web.mapping.LinkGenerator
import org.apache.commons.lang.RandomStringUtils
import org.springframework.validation.ObjectError
import com.thedeanda.lorem.Lorem
import com.thedeanda.lorem.LoremIpsum

@Transactional
@Slf4j
class ProjectService {

    //Following used to allot the number of objects to add to a project
    static int numberOfTeamsPerProject = 1
    static int numberOfCodesPerProject = 2
    static int numberOfDatasPerProject = 3
    static int numberOfPublicationsPerProject   = 1
    static int numberOfAnnotationsPerProject    = 2
    static int numberOfCommentsPerProject       = 3
    static Lorem lorem                          = LoremIpsum.getInstance()
    static LinkGenerator grailsLinkGenerator
    def activityService
    def messageSource
    def springSecurityService


    /**
     * Main call used in bootstrap to generated project data
     *
     * @param numberOfProjectsPerUser number of projects to setup
     */
    void bootstrapProjects(int numberOfProjectsPerUser) {

        if (Project.list().size() == 0) {
            List<UserAccount> users = UserAccount.list()
            List<SoftwareLicense> softwareLicenses = SoftwareLicense.list()
            List<Annotation> annotations = Annotation.list()

            users?.each { UserAccount user ->
                log.info('************************************************************')
                log.info("Creating ${numberOfProjectsPerUser} projects for username ${user.username}...")
                log.info('************************************************************')

                numberOfProjectsPerUser.times {
                    Project project = new Project(
                            name: user.username + '-project-' + it,
                            description: lorem.getParagraphs(5,8).substring(0, 254),
                            shared: [true, false].get(new Random().nextInt([true, false].size())),
                            projectOwner: user,
                            license: softwareLicenses?.get(new Random().nextInt(softwareLicenses?.size())),
                            views: new Random().nextInt(6)
                    )

                    if (project.shared && (user.id.toInteger() % 2 == 0)) {
                        println "\tAdding ${numberOfTeamsPerProject} teams to shared bundle ${project.name}..."
                        def teams = Team.findAllWhere(administrator: user)
                        if (teams) {
                            ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_TEAMS
                            numberOfTeamsPerProject.times {
                                int randomId
                                while (randomId <= 0 || (project?.teams?.id?.contains(randomId))) {
                                    randomId = new Random().nextInt(teams.size())
                                }
                                Team team = teams[randomId]
                                project.addToTeams(team)
                                saveProjectAndLog(project)
                                Object[] params = [user.username, (grailsLinkGenerator.serverBaseURL?:"")
                                        + "/bundle/${project.id}", project.name]
                                activityService.saveActivityForEvent(activityTypeEnum,
                                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                        params, Locale.getDefault())
                                )
                            }
                        }
                    } else {
                        log.info("\t\tBundle ${project.name} is not shared. Not adding any teams to it...")
                    }
                    bootstrapAnnotationsForProject(project, annotations)
                    bootstrapCodesToProject(project, user.username)
                    bootstrapDatasForProject(project, user.username)
                    bootstrapPublicationsForProject(project, user.username)
                    boostrapCommentsForProject(project)
                }
            }
            log.info("\n")
        }
    }

    /**
     * Add code to a project
     *
     * @param project the project to add code to
     * @param username used to generate the name of the code
     */
    private void bootstrapCodesToProject(Project project, String username) {
        if (project) {
            /* Code for this project */
            log.info("\tCreating ${numberOfCodesPerProject} codes for bundle ${project.name}...")
            numberOfCodesPerProject.times {
                Code code = new Code(
                        name: username + '-code-' + it,
                        description: lorem.getParagraphs(5,8).substring(0, 254),
                        repository: 'http://www.github.com/',
                        revision: it,
                        url: new URL('http://www.www.org')
                )

                if (!code.save()) {
                    code.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save code ${code.name} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved code: ${code.name}")
                project.addToCodes(code)
                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_CODES
                Object[] params = [username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/bundle/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Add annotations to a project
     *
     * @param project the project to add annotations to
     * @param annotations the list of annotations to choose from
     */
    private void bootstrapAnnotationsForProject(Project project, List<Annotation> annotations) {
        def principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal?.id) : UserAccount.findByUsername("admin")

        if (project) {
            log.info("\t\tCreating ${numberOfAnnotationsPerProject} annotations for ${project.name}...\n")
            numberOfAnnotationsPerProject.times {
                Annotation annotation = (annotations?.get(new Random().nextInt(annotations?.size())))
                project.addToAnnotations(annotation)
                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_ANNOTATIONS
                Object[] params = [user.username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/bundle/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
        }
    }

    /**
     * Add data to a given project
     *
     * @param project the project to add data to
     * @param username used for generating the data name
     */
    private void bootstrapDatasForProject(Project project, String username) {
        /* Data for this bundle */
        if (project) {
            log.info("\tCreating ${numberOfDatasPerProject} data for bundle ${project.name}...")
            numberOfDatasPerProject.times {
                Data data = new Data(
                        name: username + '-data-' + it,
                        description: lorem.getParagraphs(2,5),
                        repository: 'http://www.data.gov/',
                        revision: it,
                        url: new URL('http://www.www.org')
                )

                if (!data.save()) {
                    data.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save data ${data.name} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved data: ${data.name}")
                project.addToDatas(data)
                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_DATAS
                Object[] params = [username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/bundle/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Add publications to a given project
     *
     * @param project the project to add the publications to
     * @param username used to setup the label of publication
     */
    private void bootstrapPublicationsForProject(Project project, String username) {
        /* Publications for this bundle */
        if (project) {
            log.info("\tCreating ${numberOfPublicationsPerProject} publications for bundle ${project.name}...")
            numberOfPublicationsPerProject.times {
                def publication = new Publication(label: username + '-publication-' + it,
                        url: "http://www.${RandomStringUtils.random(5, true, true)}.org",
                        issn: RandomStringUtils.random(5, true, true),
                        isbn: RandomStringUtils.random(5, true, true),
                        nbn: RandomStringUtils.random(5, true, true),
                        doi: RandomStringUtils.random(5, true, true),
                        sici: RandomStringUtils.random(5, true, true),
                        pmid: RandomStringUtils.random(5, true, true),
                        oai: RandomStringUtils.random(5, true, true)
                )

                if (!publication.save()) {
                    publication.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save publication ${publication.label} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved publication: ${publication.label}")
                project.addToPublications(publication)

                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_PUBLICATIONS
                Object[] params = [username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/bundle/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Add comments to a project
     *
     * @param project the project to add comments to
     */
    private void boostrapCommentsForProject(Project project) {
        /* Comments for this bundle */
        if (project) {
            log.info("\tCreating ${numberOfCommentsPerProject} comments for bundle ${project.name}...")
            List<UserAccount> users = UserAccount.list()

            numberOfCommentsPerProject.times {
                UserAccount user = users?.get(new Random().nextInt(users?.size()))
                Comment comment = new Comment(
                        text: lorem.getParagraphs(2,5),
                        commenter: user
                )

                if (!comment.save()) {
                    comment.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save data ${comment.text} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved comments: ${comment.text}")
                project.addToComments(comment)

                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS
                Object[] params = [user.username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/bundle/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Save the project in question and log the results
     *
     * @param project the project to save
     */
    private void saveProjectAndLog(Project project) {
        //flush here to avoid problems with subsequent saves
        if (project) {
            if (!project.save()) {
                project.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                log.error("There was an error attempting to save project ${project.name}. Exiting.")
                System.exit(-1)
            }
            log.info("\t\tSaved project: ${project.name}\n")
        }
    }

    @ReadOnly
    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    @Cacheable("most_viewed_projects")
    List<Object> getMostViewedProjects(int maxNumberOfProjects, boolean sharedOnly = false) {
        return Project.withCriteria() {
            maxResults(maxNumberOfProjects)
            eq("shared", sharedOnly)
            order("views", "desc")
            order("dateCreated", "desc")
        }.collect { Project project ->
            [
                    projectId: project.id,
                    projectOwner: project.projectOwner.fullName,
                    profilePic: project.projectOwner.profile.picture?.fileContents,
                    projectName: project.name,
                    description: project.description,
                    ownerInstitution: project.projectOwner.profile.institution.fullName
            ]
        } as List<Object>
    }
}
