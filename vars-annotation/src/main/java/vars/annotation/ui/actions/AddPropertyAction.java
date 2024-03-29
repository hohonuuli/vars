/*
 * @(#)AddPropertyAction.java   2009.11.19 at 09:29:15 PST
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



package vars.annotation.ui.actions;

import java.util.Collection;

import org.bushe.swing.event.EventBus;
import mbarix4j.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddAssociationCmd;

/**
 * <p>
 * Base class for the various AddXXXPropActions.
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 */
public class AddPropertyAction extends ActionAdapter {

    /**  */
    public static final String NIL = ILink.VALUE_NIL;
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private String linkName;
    private String linkValue;
    private String toConcept;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    public AddPropertyAction(ToolBelt toolBelt) {
        this(toolBelt, NIL, NIL, NIL);
    }

    /**
     * Constructs ...
     *
     *
     *
     * @param toolBelt
     * @param linkName
     * @param toConcept
     * @param linkValue
     */
    public AddPropertyAction(ToolBelt toolBelt, final String linkName, final String toConcept, final String linkValue) {
        this.toolBelt = toolBelt;
        setLinkName(linkName);
        setToConcept(toConcept);
        setLinkValue(linkValue);
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
	public void doAction() {
        Collection<Observation> observations = StateLookup.getSelectedObservations();
        Association associationTemplate = toolBelt.getAnnotationFactory().newAssociation(linkName, toConcept, linkValue);
        Command command = new AddAssociationCmd(associationTemplate, observations);
        CommandEvent commandEvent = new CommandEvent(command);
        EventBus.publish(commandEvent);
    }

    /**
     * @return
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * @return
     */
    public String getLinkValue() {
        return linkValue;
    }

    /**
     * @return
     */
    public String getToConcept() {
        return toConcept;
    }

    /**
     *
     * @param linkName
     */
    public void setLinkName(final String linkName) {
        this.linkName = linkName;
    }

    /**
     *
     * @param linkValue
     */
    public void setLinkValue(final String linkValue) {
        this.linkValue = linkValue;
    }

    /**
     *
     * @param toConcept
     */
    public void setToConcept(final String toConcept) {
        this.toConcept = toConcept;
    }
}
