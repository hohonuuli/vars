/*
 * @(#)AllConceptNamesComboBox.java   2009.09.24 at 08:44:47 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/*
 * Created on Apr 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package vars.shared.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.query.QueryPersistenceService;

/**
 * <h2><u>Description </u></h2>
 * <p>
 * ComboBox for displaying all concept names in the knowledgebase. This does a
 * fast lookup from the <code>KnowledgeBaseCache</code> to populate the
 * ComboBox. The combobox stores the String concept-name. Not actual Concept objects.
 * </p>
 *
 * <h2><u>UML </u></h2>
 *
 * <pre>
 *
 *  [ConceptNameComboBox]
 *        &circ;
 *        |
 *        |
 *  [AllConceptNamesComboBox]---[KnowledgeBaseCache]
 *
 * </pre>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 * @version $Id: AllConceptNamesComboBox.java,v 1.2 2004/04/30 23:47:39 brian
 *          Exp $
 */
public class AllConceptNamesComboBox extends ConceptNameComboBox {

    /**
     *
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final QueryPersistenceService queryPersistenceService;
    /**
     *
     *
     * @param conceptNameDAO
     */
    public AllConceptNamesComboBox(QueryPersistenceService queryPersistenceSerice) {
        super();
        this.queryPersistenceService = queryPersistenceSerice;
        updateConceptNames();

        /*
         * For speed we DON'T use HiearchicalConceptNameComboBox. That would
         * load all concepts right at the start.
         */
        setEditable(true);
        setMaximumRowCount(12);
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent evt) {
                getEditor().selectAll();
            }

        });



    }

    /**
     *
     * @return All Concept-names found in the knowledgebase as strings
     */
    private String[] getConceptNames() {

        // Get ALL concept names (not just primary names). This is a FAST
        // lookup.
        List<String> conceptNameList = queryPersistenceService.findAllConceptNamesAsStrings();
        String[] conceptNames = conceptNameList.toArray(new String[conceptNameList.size()]);
        return conceptNames;
    }

    public void updateConceptNames() {
        updateModel(getConceptNames());
    }
}
