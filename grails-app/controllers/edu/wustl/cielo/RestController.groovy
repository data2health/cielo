package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder

class RestController {

    def restService
    def springSecurityService
    def passwordEncoder
    def projectService

    @Secured('permitAll')
    def appVersion() {
        render([version: grailsApplication.config.getProperty("info.app.version")] as JSON)
    }

    @Secured('permitAll')
    def getListOfProjects() {
        boolean filterOnSharedOnly  = params.onlyShowPublic ? Boolean.valueOf(params.onlyShowPublic) : false
        String filterTerm           = params.filterText ?: ""
        int max     = params.max    ? Integer.valueOf(params.max)    : Constants.DEFAULT_MAX
        int page    = params.offset ? Integer.valueOf(params.offset) : Constants.DEFAULT_OFFSET
        int offset  = (page * max)
        int pages   = restService.getNumberOfPages(filterOnSharedOnly, filterTerm, max)

        render([totalCount: restService.getTotalNumberOfProjects(filterOnSharedOnly, filterTerm), numberOfPages: pages,
                projects: restService.getProjectData(filterOnSharedOnly, filterTerm, offset, max)] as JSON)
    }

    @Secured('isAuthenticated() && hasRole("ROLE_API")')
    def createProject() {
        String username = UserAccountUserRole.findByUserRole(UserRole.findByAuthority('ROLE_SUPERUSER'))?.userAccount?.username
        Long licenseId  = params.licenseId ? Long.valueOf(params.licenseId) : -1L
        Long projectId = projectService.createNewProject(params.name, params.desc, licenseId, params.license, username)

        if (projectId > -1L) render([success: true, projectId: projectId] as JSON)
        else  render([success: false] as JSON)
    }

    @Secured('isAuthenticated() && hasRole("ROLE_API")')
    def changeProjectVisibility() {
        render([success: projectService.changeProjectVisibility(Long.valueOf(params.id), Boolean.valueOf(params.shared))] as JSON)
    }

    @Secured('permitAll')
    def restLogin() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            UserAccount user = UserAccount.findByUsername(params.user)

            if (user) {
                UserAccountUserRole userAccountUserRole = UserAccountUserRole.findByUserAccount(user)
                if (userAccountUserRole) {
                    if (userAccountUserRole.userRole.authority == "ROLE_API") {
                        if (passwordEncoder.isPasswordValid(user.password, params.pass, null)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    params.user, params.pass,
                                    AuthorityUtils.createAuthorityList('ROLE_API'))
                            SecurityContextHolder.context.authentication = usernamePasswordAuthenticationToken
                        } else render([error: "Invalid username or password."] as JSON)
                    } else render([error: "User is not a rest api user."] as JSON)
                } else render([error: "User doesn't have any roles. Please contact administrator."] as JSON)
            }
            render([token: session.id] as JSON)
        } else render([token: null] as JSON)
    }

    @Secured('permitAll')
    def listLicenses() {
        List<SoftwareLicense> licenses = SoftwareLicense.all.collect { [name: it.label, licenseId: it.id] }
        render(licenses as JSON)
    }
}
