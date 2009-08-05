package vars;

import java.io.InputStream;
import java.sql.Connection;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * Loads test data in the form of DbUnit XML into a database.
 * 
 * @author rcoffin
 */
public class DbUnitDataLoader
{
    private InputStream testData;
    private Connection connection;

    public DbUnitDataLoader(InputStream testData, Connection connection)
    {
        this.testData = testData;
        this.connection = connection;
    }

    /**
     * Replace existing data with test data
     * 
     * @throws Exception
     */
    public void populateTestData() throws Exception
    {
        FlatXmlDataSet dataSet = new FlatXmlDataSet(testData);

        IDatabaseConnection con = new DatabaseConnection(connection);

        DatabaseOperation.CLEAN_INSERT.execute(con, dataSet);

        con.close();
    }
}
