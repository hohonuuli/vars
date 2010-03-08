/*
 * @(#)LinkSelectionDialog.java   2010.03.05 at 04:42:33 PST
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



package vars.shared.ui.dialogs;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import vars.ILink;
import vars.LinkBean;
import vars.LinkComparator;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 *
 *
 * @version        Enter version here..., 2010.03.05 at 04:42:33 PST
 * @author         Brian Schlining [brian@mbari.org]    
 */
public class LinkSelectionDialog extends StandardDialog {

    private JPanel mainPanel;
    private final List<ILink> links = new Vector<ILink>();
    private final ILink DEFAULT_LINK = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
    private JLabel lblSearchFor;
    private JTextField textField;
    private JComboBox comboBox;
    private JLabel lblLinkName;
    private JTextField textField_1;
    private JLabel lblToConcept;
    private JComboBox comboBox_1;
    private JLabel lblLinkValue;
    private JTextField textField_2;

    /**
     * Create the dialog.
     */
    public LinkSelectionDialog() {
        initialize();
    }

    protected void initialize() {
        setBounds(100, 100, 450, 300);
        getContentPane().add(getMainPanel(), BorderLayout.CENTER);
    }
    
    private JPanel getMainPanel() {
    	if (mainPanel == null) {
    		mainPanel = new JPanel();
    		GroupLayout groupLayout = new GroupLayout(mainPanel);
    		groupLayout.setHorizontalGroup(
    			groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addContainerGap()
    					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    						.addComponent(getComboBox(), 0, 438, Short.MAX_VALUE)
    						.addGroup(groupLayout.createSequentialGroup()
    							.addComponent(getLblSearchFor())
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(getTextField(), GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
    						.addGroup(groupLayout.createSequentialGroup()
    							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    								.addComponent(getLblLinkName())
    								.addComponent(getLblToConcept())
    								.addComponent(getLblLinkValue()))
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    								.addComponent(getComboBox_1(), 0, 360, Short.MAX_VALUE)
    								.addComponent(getTextField_1(), GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
    								.addComponent(getTextField_2(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))))
    					.addContainerGap())
    		);
    		groupLayout.setVerticalGroup(
    			groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addContainerGap()
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblSearchFor())
    						.addComponent(getTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addComponent(getComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblLinkName())
    						.addComponent(getTextField_1(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblToConcept())
    						.addComponent(getComboBox_1(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblLinkValue())
    						.addComponent(getTextField_2(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addContainerGap(70, Short.MAX_VALUE))
    		);
    		mainPanel.setLayout(groupLayout);
    	}
    	return mainPanel;
    }
    
    public void setLinks(Collection<ILink> links) {
    	this.links.clear();
    	this.links.addAll(links);
    	this.links.add(DEFAULT_LINK);
    	Collections.sort(this.links, new LinkComparator());
    	// TODO trigger redraw
    }
    
    public ILink getLink() {
    	throw new UnsupportedOperationException("Implement me!!");
    }
	private JLabel getLblSearchFor() {
		if (lblSearchFor == null) {
			lblSearchFor = new JLabel("Search For");
		}
		return lblSearchFor;
	}
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
		}
		return textField;
	}
	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
		}
		return comboBox;
	}
	private JLabel getLblLinkName() {
		if (lblLinkName == null) {
			lblLinkName = new JLabel("Link Name");
		}
		return lblLinkName;
	}
	private JTextField getTextField_1() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
			textField_1.setEditable(false);
			textField_1.setColumns(10);
		}
		return textField_1;
	}
	private JLabel getLblToConcept() {
		if (lblToConcept == null) {
			lblToConcept = new JLabel("To Concept");
		}
		return lblToConcept;
	}
	private JComboBox getComboBox_1() {
		if (comboBox_1 == null) {
			comboBox_1 = new JComboBox();
		}
		return comboBox_1;
	}
	private JLabel getLblLinkValue() {
		if (lblLinkValue == null) {
			lblLinkValue = new JLabel("Link Value");
		}
		return lblLinkValue;
	}
	private JTextField getTextField_2() {
		if (textField_2 == null) {
			textField_2 = new JTextField();
			textField_2.setColumns(10);
		}
		return textField_2;
	}
}
