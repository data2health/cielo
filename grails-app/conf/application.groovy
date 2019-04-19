

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'edu.wustl.cielo.UserAccount'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'edu.wustl.cielo.UserAccountUserRole'
grails.plugin.springsecurity.authority.className = 'edu.wustl.cielo.UserRole'
grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.userLookup.usernameIgnoreCase = true

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/errors/**',          access: ['permitAll']],
	[pattern: '/login/auth',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['isAuthenticated()']],
	[pattern: '/assets/**',      access: ['isAuthenticated()']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/templates/**', access: ['permitAll']],
	[pattern: '/favicon.ico', access: ['permitAll']],
	[pattern: '/**/cielo_icon.png', access: ['permitAll']],
	[pattern: '/verification', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]


//migration settings
grails.plugin.databasemigration.updateOnStart 			= true
grails.plugin.databasemigration.updateOnStartFileName 	= 'changelog.groovy'
grails.plugin.databasemigration.autoMigrateScripts 		= ['RunApp']
grails.plugin.databasemigration.updateOnStartContexts 	= ['development', 'production']

environments {
	development {
		annotations="/WEB-INF/startup/NCI_Thesaurus_terms.txt"
	}
	test {
		annotations="/WEB-INF/startup/NCI_Thesaurus_terms_shorter.txt"
		grails.plugin.databasemigration.updateOnStart = false
	}
	production {
		annotations="/WEB-INF/startup/NCI_Thesaurus_terms.txt"
	}
}

software.licenses.path="/WEB-INF/startup/licenses.json"
institutions="/WEB-INF/startup/intsitutions.json"
