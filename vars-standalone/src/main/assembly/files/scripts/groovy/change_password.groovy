import org.jasypt.util.password.BasicPasswordEncryptor
import mbarix4j.sql.QueryFunction
import vars.ToolBox;

if (args.size() != 2) {
    println("""
    Usage: gsh change_password <username> <newPassword>
    
    Args:
        username = The user whos password needs to be changed
        newPassword = The new password
    """)
    return
}

def name = args[0]
def password = args[1]

def toolBelt = new vars.ToolBox().toolBelt
def dao = toolBelt.miscDAOFactory.newUserAccountDAO()
dao.startTransaction()
def userAccount = dao.findByUserName(name)
if (userAccount) {
    userAccount.password = password
}
else {
    println("Unable to find user named: ${name}")
}
dao.endTransaction()


