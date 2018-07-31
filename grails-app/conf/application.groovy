

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

environments {
	development {
		annotations="/WEB-INF/startup/shorter_mshd2014.txt"
	}
	test {
		annotations="/WEB-INF/startup/shorter_mshd2014.txt"
	}
	production {
		annotations="/WEB-INF/startup/shorter_mshd2014.txt"
	}
}

software.licenses.path="/WEB-INF/startup/licenses.json"
institutions="/WEB-INF/startup/intsitutions.json"

//email settings
mailjet.publicKey="6d9a8bd2361ca922b3ee2ff263655f9c"
mailjet.secretKey="e7e5a40f55babdfd6540d2ab2e438e5b"
mailjet.from = "CIELO"
mailjet.fromAddress = "cd2h.cielo@wustl.edu"

//migration settings
grails.plugin.databasemigration.updateOnStart 			= true
grails.plugin.databasemigration.updateOnStartFileName 	= 'changelog.groovy'
grails.plugin.databasemigration.autoMigrateScripts 		= ['RunApp', 'DebugApp']