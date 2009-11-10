/*
 * @(#)HistoryViewPanel.java   2009.10.27 at 11:32:46 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import vars.knowledgebase.History;

/**
 * @version        $date$, 2009.10.27 at 11:32:46 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class HistoryViewPanel extends JPanel {

    private static final String BLANK = "";
    private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final long serialVersionUID = 1L;
    private JTextField actionField = null;
    private JLabel actionLabel = null;
    private JTextField approvalDateField = null;
    private JLabel approvalDateLabel = null;
    private JTextField approverField = null;
    private JLabel approverLabel = null;
    private JTextField creationDateField = null;
    private JLabel creationDateLabel = null;
    private JTextField creatorField = null;
    private JLabel creatorNameLabel = null;
    private JTextField fieldField = null;
    private JLabel fieldLabel = null;
    private JTextField newValueField = null;
    private JLabel newValueLabel = null;
    private JTextField oldValueField = null;
    private JLabel oldValueLabel = null;
    private History history;

    /**
     * This is the default constructor
     */
    public HistoryViewPanel() {
        super();
        initialize();
    }

    public JTextField getActionField() {
        if (actionField == null) {
            actionField = new JTextField();
            actionField.setEditable(false);
        }

        return actionField;
    }

    public JTextField getApprovalDateField() {
        if (approvalDateField == null) {
            approvalDateField = new JTextField();
            approvalDateField.setEditable(false);
        }

        return approvalDateField;
    }

    public JTextField getApproverField() {
        if (approverField == null) {
            approverField = new JTextField();
            approverField.setEditable(false);
        }

        return approverField;
    }

    public JTextField getCreationDateField() {
        if (creationDateField == null) {
            creationDateField = new JTextField();
            creationDateField.setEditable(false);
        }

        return creationDateField;
    }

    private JTextField getCreatorField() {
        if (creatorField == null) {
            creatorField = new JTextField();
            creatorField.setEditable(false);
        }

        return creatorField;
    }

    private JTextField getFieldField() {
        if (fieldField == null) {
            fieldField = new JTextField();
            fieldField.setEditable(false);
        }

        return fieldField;
    }

    /**
     * @return  the history
     */
    public History getHistory() {
        return history;
    }

    public JTextField getNewValueField() {
        if (newValueField == null) {
            newValueField = new JTextField();
            newValueField.setEditable(false);
        }

        return newValueField;
    }

    public JTextField getOldValueField() {
        if (oldValueField == null) {
            oldValueField = new JTextField();
            oldValueField.setEditable(false);
        }

        return oldValueField;
    }

    private void initialize() {
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.fill = GridBagConstraints.BOTH;
        gridBagConstraints21.gridy = 5;
        gridBagConstraints21.weightx = 1.0;
        gridBagConstraints21.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints21.gridx = 1;
        GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
        gridBagConstraints14.gridx = 0;
        gridBagConstraints14.anchor = GridBagConstraints.WEST;
        gridBagConstraints14.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints14.gridy = 5;
        fieldLabel = new JLabel();
        fieldLabel.setText("Field:");
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        gridBagConstraints13.fill = GridBagConstraints.BOTH;
        gridBagConstraints13.gridy = 7;
        gridBagConstraints13.weightx = 1.0;
        gridBagConstraints13.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints13.gridx = 1;
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.fill = GridBagConstraints.BOTH;
        gridBagConstraints12.gridy = 6;
        gridBagConstraints12.weightx = 1.0;
        gridBagConstraints12.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints12.gridx = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.fill = GridBagConstraints.BOTH;
        gridBagConstraints11.gridy = 4;
        gridBagConstraints11.weightx = 1.0;
        gridBagConstraints11.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints11.gridx = 1;
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.fill = GridBagConstraints.BOTH;
        gridBagConstraints10.gridy = 3;
        gridBagConstraints10.weightx = 1.0;
        gridBagConstraints10.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints10.gridx = 1;
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.fill = GridBagConstraints.BOTH;
        gridBagConstraints9.gridy = 2;
        gridBagConstraints9.weightx = 1.0;
        gridBagConstraints9.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints9.gridx = 1;
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.fill = GridBagConstraints.BOTH;
        gridBagConstraints8.gridy = 1;
        gridBagConstraints8.weightx = 1.0;
        gridBagConstraints8.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints8.gridx = 1;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = GridBagConstraints.BOTH;
        gridBagConstraints7.gridy = 0;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints7.gridx = 1;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.anchor = GridBagConstraints.WEST;
        gridBagConstraints6.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints6.gridy = 7;
        oldValueLabel = new JLabel();
        oldValueLabel.setText("Old value:");
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.anchor = GridBagConstraints.WEST;
        gridBagConstraints5.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints5.gridy = 6;
        newValueLabel = new JLabel();
        newValueLabel.setText("New value:");
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.anchor = GridBagConstraints.WEST;
        gridBagConstraints4.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints4.gridy = 4;
        actionLabel = new JLabel();
        actionLabel.setText("Action:");
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints3.gridy = 3;
        approvalDateLabel = new JLabel();
        approvalDateLabel.setText("Approved on:");
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints2.gridy = 2;
        approverLabel = new JLabel();
        approverLabel.setText("Approved by:");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints1.gridy = 1;
        creationDateLabel = new JLabel();
        creationDateLabel.setText("Created on:");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints.gridy = 0;
        creatorNameLabel = new JLabel();
        creatorNameLabel.setText("Created by:");
        this.setSize(303, 279);
        this.setLayout(new GridBagLayout());
        this.add(creatorNameLabel, gridBagConstraints);
        this.add(creationDateLabel, gridBagConstraints1);
        this.add(approverLabel, gridBagConstraints2);
        this.add(approvalDateLabel, gridBagConstraints3);
        this.add(actionLabel, gridBagConstraints4);
        this.add(newValueLabel, gridBagConstraints5);
        this.add(oldValueLabel, gridBagConstraints6);
        this.add(getCreatorField(), gridBagConstraints7);
        this.add(getCreationDateField(), gridBagConstraints8);
        this.add(getApproverField(), gridBagConstraints9);
        this.add(getApprovalDateField(), gridBagConstraints10);
        this.add(getActionField(), gridBagConstraints11);
        this.add(getNewValueField(), gridBagConstraints12);
        this.add(getOldValueField(), gridBagConstraints13);
        this.add(fieldLabel, gridBagConstraints14);
        this.add(getFieldField(), gridBagConstraints21);
    }

    /**
     * @param history  the history to set
     */
    public void setHistory(History history) {


        String creator = BLANK;
        String creationDate = BLANK;
        String approver = BLANK;
        String approvalDate = BLANK;
        String action = BLANK;
        String field = BLANK;
        String newValue = BLANK;
        String oldValue = BLANK;

        if (history != null) {
            creator = history.getCreatorName();
            Date creation = history.getCreationDate();
            if (creation != null) {
                synchronized (DATEFORMAT) {
                    creationDate = DATEFORMAT.format(creation);
                }
            }

            approver = history.getProcessorName();
            Date approval = history.getProcessedDate();
            if (approval != null) {
                synchronized (DATEFORMAT) {
                    approvalDate = DATEFORMAT.format(approval);
                }

            }

            action = history.getAction();
            field = history.getField();
            newValue = history.getNewValue();
            oldValue = history.getOldValue();

            if (history.isRejected()) {
                approverLabel.setText("Rejected by:");
                approvalDateLabel.setText("Rejected on");
            }
            else {
                approverLabel.setText("Approved by:");
                approvalDateLabel.setText("Approved on");
            }
        }

        getCreatorField().setText(creator);
        getCreationDateField().setText(creationDate);
        getApproverField().setText(approver);
        getApprovalDateField().setText(approvalDate);
        getActionField().setText(action);
        getFieldField().setText(field);
        getNewValueField().setText(newValue);
        getOldValueField().setText(oldValue);

        this.history = history;
    }
}
