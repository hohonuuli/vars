/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.knowledgebase.ui;

import vars.shared.ui.ILockableEditor;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;




/**
 * <p><!-- Class description --></p>
 *
 * @version    $Id: EditorPanel.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public abstract class EditorPanel extends JPanel implements ILockableEditor {

    protected static final Concept NULL_CONCEPT = new SimpleConceptBean() {{
        addConceptName(new SimpleConceptNameBean("", ConceptNameTypes.PRIMARY.toString()) {{
            setAuthor(ConceptName.AUTHOR_UNKNOWN);
        }});
    }};

    private Concept concept;
    private boolean locked = true;
    private final ToolBelt toolBelt;

    /**
     * This is the default constructor
     */
    public EditorPanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        initialize();
    }

    ToolBelt getToolBelt() {
        return toolBelt;
    }


    /**
     * @return  Returns the concept.
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(300, 200);
    }

    /**
     * @return
     */
    public boolean isLocked() {
        return locked;
    }

    /**
	 * @param concept  The concept to set.
	 */
    public void setConcept(Concept concept) {
        if (concept == null) {
            concept = NULL_CONCEPT;
        }

        this.concept = concept;
    }

    /**
     * @param  locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
