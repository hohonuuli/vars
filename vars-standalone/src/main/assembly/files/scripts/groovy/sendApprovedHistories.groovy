import java.text.SimpleDateFormat
import org.apache.commons.mail.SimpleEmail
import vars.ToolBox
import vars.UserAccountRoles

def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
def startDate = args.size() ? df.parse(args[0]) : (new Date()) - 7
def endDate = args.size() > 1 ? def.parse(args[1]) : new Date()

def toolBox = new ToolBox()
def historyDao = toolBox.toolBelt.knowledgebaseDAOFactory.newHistoryDAO()
def approvedHistories = historyDao.findApprovedHistories()
approvedHistories = approvedHistories.findAll { h ->
    h.approvalDate >= startDate && h.approvalDate <= endDate
}

def userAccountDao = toolBox.toolBelt..miscDAOFactory.newUserAccountDAO()
def admins = userAccountDao.findAllByRole(UserAccountRoles.ADMINISTRATOR.toString())

def email = new SimpleEmail()
email.setHostName("mail.shore.mbari.org")
admins.each { a ->
    email.addBcc(a.email)
}
email.setFrom("brian@mbari.org", "Brian Schlining")
email.setSubject("VARS Knowledgebase Report: Recently approved changes")

def msg = """\
This report list changes made to the VARS knowledgebase that were
approved between ${df.format(startDate)} and ${df.format(endDate)}. You are
receiving this report because you are listed as a a VARS administrator.
You may modify these changes using the VARS Knowledgebase application
at:

http://seaspray.shore.mbari.org/webstart/varsknowledgebase.jnlp

Recently approved changes:

"""

approvedHistories.each { h ->
    msg += "${h.conceptDelegate.concept.primaryConceptNameAsString}: ${h.stringValue()}\n\n"
}
email.msg = msg

if (approvedHistories) {
    email.send();
}