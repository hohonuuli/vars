import java.text.SimpleDateFormat
import org.apache.commons.mail.SimpleEmail
import vars.ToolBox
import vars.UserAccountRoles

def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
ToolBox toolBox = new ToolBox()

def pendingHistories = toolBox.toolBelt.knowledgebaseDAOFactory.newHistoryDAO().findPendingHistories()

def admins = toolBox.toolBelt.miscDAOFactory.newUserAccountDAO().findAllByRole(UserAccountRoles.ADMINISTRATOR.roleName)

def email = new SimpleEmail()
email.setHostName("mail.shore.mbari.org")
admins.each { a ->
    email.addBcc("${a.userName}@mbari.org")
}
email.setFrom("brian@mbari.org", "Brian Schlining")
email.setSubject("VARS Knowledgebase Report: Changes pending approval")

def msg = """\
${df.format(new Date())}

This report list changes made to the VARS knowledgebase that are
pending approval by an administrator. You are receiving this report
because you are listed as a a VARS administrator. To approve a change,
open the VARS Knowledgebase application at:

http://seaspray.shore.mbari.org/webstart/varsknowledgebase.jnlp

Pending changes:

"""

pendingHistories.each { h ->
    msg += "${h.conceptDelegate.concept.primaryConceptNameAsString}: ${h.stringValue()}\n\n"
}
email.msg = msg

if (pendingHistories) {
    email.send();
}