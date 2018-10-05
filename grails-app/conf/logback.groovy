import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import ch.qos.logback.classic.Level
import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter
Level level = Environment.isDevelopmentMode() ? INFO : ERROR
File targetDir = BuildSettings.TARGET_DIR

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}


//standard
//logger('org.hibernate.internal.SessionImpl', level, ['STDOUT'])
//
////Custom logging entries
////Domain Objects
//logger('edu.wustl.cielo.Activity', level, ['STDOUT'])
//logger('edu.wustl.cielo.Annotation', level, ['STDOUT'])
//logger('edu.wustl.cielo.Code', level, ['STDOUT'])
//logger('edu.wustl.cielo.Comment', level, ['STDOUT'])
//logger('edu.wustl.cielo.Data', level, ['STDOUT'])
//logger('edu.wustl.cielo.Institution', level, ['STDOUT'])
//logger('edu.wustl.cielo.Profile', level, ['STDOUT'])
//logger('edu.wustl.cielo.ProfilePic', level, ['STDOUT'])
//logger('edu.wustl.cielo.Project', level, ['STDOUT'])
//logger('edu.wustl.cielo.Publication', level, ['STDOUT'])
//logger('edu.wustl.cielo.RegistrationCode', level, ['STDOUT'])
//logger('edu.wustl.cielo.SoftwareLicense', level, ['STDOUT'])
//logger('edu.wustl.cielo.Team', level, ['STDOUT'])
//logger('edu.wustl.cielo.UserAccount', level, ['STDOUT'])
//logger('edu.wustl.cielo.UserAccountUserRole', level, ['STDOUT'])
//logger('edu.wustl.cielo.UserRole', level, ['STDOUT'])
//logger('edu.wustl.cielo.meta.MetaData', level, ['STDOUT'])
//
////Services
//logger('edu.wustl.cielo.AnnotationService', level, ['STDOUT'])
//logger('edu.wustl.cielo.InstitutionService', level, ['STDOUT'])
//logger('edu.wustl.cielo.ProjectService', level, ['STDOUT'])
//logger('edu.wustl.cielo.SoftwareLicenseService', level, ['STDOUT'])
//logger('edu.wustl.cielo.TeamService', level, ['STDOUT'])
//logger('edu.wustl.cielo.UserAccountService', level, ['STDOUT'])
//
////Controllers
//logger('edu.wustl.cielo.HomeController', level, ['STDOUT'])
//
////Plugins
//logger('grails.plugins.quartz', level, ['STDOUT'])
//logger('grails.plugins.mail', level, ['STDOUT'])
//
////Jobs
//logger('edu.wustl.cielo.EmailSenderJob', level, ['STDOUT'])

//    appender("FILE", FileAppender) {
//        file = "stacktrace.log"
//        append = true
//        encoder(PatternLayoutEncoder) {
//            pattern = "%level %logger - %msg%n"
//        }
//    }
//    root(level, ['FILE'])

root(level, ['STDOUT'])