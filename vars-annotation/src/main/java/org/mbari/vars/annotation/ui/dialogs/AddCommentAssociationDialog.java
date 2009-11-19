package org.mbari.vars.annotation.ui.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.mbari.swing.JFancyButton;

import vars.annotation.ui.Lookup;

public class AddCommentAssociationDialog extends JDialog {

    private JButton okButton;
    private JButton cancelButton;
    private JTextField textField;
    private JScrollPane scrollPane;
    
    private Status status;
    
    public enum Status {
        OK,
        Cancel
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {
        try {
            AddCommentAssociationDialog dialog = new AddCommentAssociationDialog();
            dialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Create the dialog
     */
    public AddCommentAssociationDialog() {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        status = Status.Cancel;
        
    }

    private void initialize() throws Exception {
        final GroupLayout groupLayout = new GroupLayout((JComponent) getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(GroupLayout.LEADING)
	        	.add(groupLayout.createSequentialGroup()
		        	.addContainerGap()
		        	.add(groupLayout.createParallelGroup(GroupLayout.TRAILING)
			        	.add(getScrollPane(), GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
			        	.add(groupLayout.createSequentialGroup()
				        	.add(getOkButton())
				        	.addPreferredGap(LayoutStyle.RELATED)
				        	.add(getCancelButton())))
		        	.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(GroupLayout.LEADING)
	        	.add(groupLayout.createSequentialGroup()
		        	.addContainerGap()
		        	.add(getScrollPane(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		        	.addPreferredGap(LayoutStyle.RELATED)
		        	.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
			        	.add(getCancelButton())
			        	.add(getOkButton()))
		        	.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        getContentPane().setLayout(groupLayout);
        setModal(true);
        setTitle("VARS - Enter Comment");
        pack();
    }

    /**
     * @return
     */
    protected JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setViewportView(getTextField());
        }
        return scrollPane;
    }

    /**
     * @return
     */
    protected JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
        }
        return textField;
    }

    /**
     * @return
     */
    protected JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JFancyButton();
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
            cancelButton.setText("");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AddCommentAssociationDialog.this.setVisible(false);
                    status = Status.Cancel;
                }
            });
        }
        return cancelButton;
    }

    /**
     * @return
     */
    public JButton getOkButton() {
        if (okButton == null) {
            okButton = new JFancyButton();
            okButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/check2.png")));
            okButton.setText("");
            getRootPane().setDefaultButton(okButton);
            addHierarchyListener(new HierarchyListener() {
                public void hierarchyChanged(final HierarchyEvent e) {
                    if (HierarchyEvent.SHOWING_CHANGED == (HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags())) {
                        AddCommentAssociationDialog.this.getRootPane().setDefaultButton(okButton);
                    }
                }
            });
        }
        return okButton;
    }
    
    public String getComment() {
        return getTextField().getText();
    }
    
    public void setComment(String comment) {
        getTextField().setText(comment);
    }
    
    public Status getStatus() {
        return status;
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            getTextField().requestFocusInWindow();
        }
    }
    
}
