import org.jasypt.util.password.BasicPasswordEncryptor
import mbarix4j.sql.QueryFunction
import vars.ToolBox;

def toolBox = new ToolBox()
def dao = toolBox.toolBelt.knowledgebasePersistenceService
def sql = "SELECT id, Password FROM UserAccount"
def ua = dao.executeQueryFunction(sql, {rs ->
            def userAccounts = [:]
            while (rs.next()) {
                userAccounts[rs.getLong(1)] = rs.getString(2)
            }
            return userAccounts
        } as QueryFunction)
        
def encryptor = new BasicPasswordEncryptor()
ua.each { id, password ->
    dao.executeUpdate("""\
            UPDATE
                UserAccount
            SET
                Password = '${encryptor.encryptPassword(password)}'
            WHERE
            id = ${id}""".stripIndent() as String)
}