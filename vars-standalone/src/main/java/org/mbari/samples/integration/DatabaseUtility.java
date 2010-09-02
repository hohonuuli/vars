/*
 * @(#)DatabaseUtility.java   2010.09.02 at 01:07:09 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.samples.integration;

import com.google.inject.Injector;
import org.mbari.sql.IQueryable;
import org.mbari.sql.QueryableImpl;
import vars.ToolBelt;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ui.Lookup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Brian Schlining
 * @since Sep 2, 2010
 */
class DatabaseUtility {

    /**  */
    public final String jdbcDriver = "net.sourceforge.jtds.jdbc.Driver";

    /**  */
    public final String jdbcPassword = "samp";

    /**  */
    public final String jdbcUrl = "jdbc:jtds:sqlserver://solstice.shore.mbari.org:1433/MBARI_Samples";

    /**  */
    public final String jdbcUsername = "samp";
    public final IQueryable samplesDatabase = new QueryableImpl(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
    protected final ToolBelt toolBelt;

    /**
     * Constructs ...
     */
    public DatabaseUtility() {
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();

        toolBelt = injector.getInstance(ToolBelt.class);
    }

    /**
     *  Gets the keywords attribute of the GetVimsKeywords object
     *
     * @param  conceptName Description of the Parameter
     * @return  The keywords value
     */
    public List<String> getKeywords(String conceptName) {
        List<String> keywords = new LinkedList<String>();
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        Concept concept = dao.findByName(conceptName);

        while (concept != null) {
            Set<ConceptName> conceptNames = concept.getConceptNames();

            for (ConceptName name : conceptNames) {
                StringBuilder sb = new StringBuilder(name.getName().replace('-', ' '));

                // Capitalize 1st Letter
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                keywords.add(sb.toString());
            }

            concept = concept.getParentConcept();
        }

        return keywords;
    }

    /**
     * @return
     */
    public Connection newConnection() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return connection;
    }
}
