import org.slf4j.LoggerFactory
def log = LoggerFactory.getLogger(this.class);
log.info("Opening interactive Groovy console for VARS processing")
org.codehaus.groovy.tools.shell.Main.main()