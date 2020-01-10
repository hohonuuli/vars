package vars.shared.ui.event;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;
import org.mbari.awt.AwtUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.shared.ui.GlobalStateLookup;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * Sends an email
 *
 * @author Brian Schlining
 * @since Sep 16, 2010
 */
public class EmailErrorReporter implements ErrorReporter {

    private final JXErrorPane errorPane;

    public EmailErrorReporter(JXErrorPane errorPane) {
        this.errorPane = errorPane;
    }

    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_EMAIL = "brian@mbari.org";
    private static final String DEFAULT_USER = "VARS Error Reporter";
    
    public void reportError(ErrorInfo errorInfo) throws NullPointerException {

        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserAccount userAccount = GlobalStateLookup.getUserAccount();

            Email email = new HtmlEmail();
            email.setHostName("mbarimail.mbari.org");
            email.setFrom(getEmail(userAccount), getName(userAccount));
            email.addTo("brian@mbari.org");
            email.setSubject("VARS Error Report: " + errorInfo.getTitle());
            email.setMsg("<html><body>" + df.format(new Date()) + "\n\n" +
                    errorInfo.getDetailedErrorMessage() + "</body></html>");
            email.send();
        }
        catch (Exception e) {
            log.warn("Failed to send email error report", e);
        }

        Frame frame = AwtUtilities.getFrame(errorPane);
        frame.dispose();

    }

    private String getName(UserAccount userAccount) {
        String name = null;
        if (userAccount == null) {
            name = DEFAULT_USER;
        }
        else {
            String firstName = (userAccount.getFirstName() == null) ? "" : userAccount.getFirstName();
            String lastName = (userAccount.getLastName() == null) ? "" : userAccount.getLastName();
            if (firstName.isEmpty() && lastName.isEmpty()) {
                name = DEFAULT_USER;
            }
            else {
                name = firstName + " " + lastName;
            }
        }
        return name;
    }

    private String getEmail(UserAccount userAccount) {
        String fromEmail = (userAccount == null) ? DEFAULT_EMAIL : userAccount.getEmail();
        if (fromEmail == null) {
            fromEmail = DEFAULT_EMAIL;
        }
        return fromEmail;
    }
}
