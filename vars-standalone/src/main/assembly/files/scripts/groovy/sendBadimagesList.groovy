import org.apache.commons.mail.SimpleEmail
import vars.annotation.DatabaseUtility

def db = new DatabaseUtility()
def badUrls = db.listMissingStillImages()
def admins = UserAccountDAO.instance.findAdmins()

def email = new SimpleEmail()
email.setHostName("mail.shore.mbari.org")
admins.each { a ->
    email.addBcc("${a.userName}@mbari.org")
}
email.setFrom("brian@mbari.org", "Brian Schlining")
email.setSubject("VARS Annotation Report: Annotations in VARS with bogus image URLs")

def msg = """\
This report lists the image URL's that were associated with a VARS
annotation but where the image wasn't found on the web server.You are
receiving this report because you are listed as a a VARS
administrator.

Missing images:

"""

def urls = []
urls.addAll(badUrls)
urls.sort { it.toExternalForm() }

urls.each { url ->
    msg += "${url.toExternalForm()}\n"
}
email.msg = msg

if (url) {
    email.send();
}
