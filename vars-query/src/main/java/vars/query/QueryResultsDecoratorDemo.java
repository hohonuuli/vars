package vars.query;

import com.google.inject.Injector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import mbarix4j.sql.QueryResults;
import vars.query.ui.QueryResultsFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryResultsDecoratorDemo {
    
    private static final Logger log = LoggerFactory.getLogger(QueryResultsDecoratorDemo.class);

    public static void main(String[] args) {
        String sql = "SELECT ObservationID_FK,  Associations,  ConceptName,  " +
                "Depth,  Image,  Latitude,  Longitude,  RecordedDate,  TapeTimeCode,  " +
                "videoArchiveName FROM Annotations WHERE (( ( ConceptName IN " +
                "('octopoteuthis', 'octopus-squid-family', 'octopoteuthis-deletron', " +
                "'octopoteuthidae')  OR ToConcept IN ('octopoteuthis', " +
                "'octopus-squid-family', 'octopoteuthis-deletron', 'octopoteuthidae') )))";

        try {

//            Database db = ObjectDAO.fetchDatabase();
//            db.begin();
//            Connection connection = db.getJdbcConnection();
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
//            QueryResults queryResults = new QueryResults(resultSet);
//            resultSet.close();
//            statement.close();
//            db.commit();
//            db.close();
//            
//            Injector injector = Guice.createInjector()
//
//            IQueryDAO dao
//            //QueryResultsDecorator.addHierarchy(queryResults);
//            //QueryResultsDecorator.addBasicPhylogeny(queryResults);
//            QueryResultsDecorator.addFullPhylogeny(queryResults);
//            QueryResultsDecorator.dropEmptyColumns(queryResults);
//            QueryResultsFrame f = new QueryResultsFrame(queryResults);
//            f.setVisible(true);
//            f.setDefaultCloseOperation(QueryResultsFrame.EXIT_ON_CLOSE);

        } 
        catch (Exception e) {
            log.error("Application bombed", e);
        }
    }
}
