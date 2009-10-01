package vars.knowledgebase.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.mbari.vars.knowledgebase.model.Media;

public class MediaViewPanel extends JPanel implements ILockableEditor {

    /**
	 * @uml.property  name="locked"
	 */
    private boolean locked;
    private static final long serialVersionUID = 1L;
    /**
	 * @uml.property  name="urlLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JLabel urlLabel = null;
    /**
	 * @uml.property  name="typeLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JLabel typeLabel = null;
    /**
	 * @uml.property  name="captionLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JLabel captionLabel = null;
    /**
	 * @uml.property  name="creditLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JLabel creditLabel = null;
    /**
	 * @uml.property  name="urlField"
	 * @uml.associationEnd  
	 */
    private JTextField urlField = null;
    /**
	 * @uml.property  name="typeComboBox"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
    private JComboBox typeComboBox = null;
    /**
	 * @uml.property  name="captionScrollPane"
	 * @uml.associationEnd  
	 */
    private JScrollPane captionScrollPane = null;
    /**
	 * @uml.property  name="captionArea"
	 * @uml.associationEnd  
	 */
    private JTextArea captionArea = null;
    /**
	 * @uml.property  name="creditScrollPane"
	 * @uml.associationEnd  
	 */
    private JScrollPane creditScrollPane = null;
    /**
	 * @uml.property  name="creditArea"
	 * @uml.associationEnd  
	 */
    private JTextArea creditArea = null;
    /**
	 * @uml.property  name="primaryCheckBox"
	 * @uml.associationEnd  
	 */
    private JCheckBox primaryCheckBox = null;
    /**
	 * @uml.property  name="media"
	 * @uml.associationEnd  
	 */
    private Media media;
    /**
     * This is the default constructor
     */
    public MediaViewPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 1;
        gridBagConstraints8.anchor = GridBagConstraints.WEST;
        gridBagConstraints8.insets = new Insets(0, 4, 4, 0);
        gridBagConstraints8.gridy = 4;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = GridBagConstraints.BOTH;
        gridBagConstraints7.gridy = 3;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.weighty = 1.0;
        gridBagConstraints7.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints7.gridx = 1;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = GridBagConstraints.BOTH;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.weighty = 1.0;
        gridBagConstraints6.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints6.gridx = 1;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.fill = GridBagConstraints.BOTH;
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.insets = new Insets(0, 4, 0, 20);
        gridBagConstraints5.gridx = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new Insets(4, 4, 4, 20);
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints3.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints3.gridy = 3;
        creditLabel = new JLabel();
        creditLabel.setText("Credit:");
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints2.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints2.gridy = 2;
        captionLabel = new JLabel();
        captionLabel.setText("Caption:");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints1.gridy = 1;
        typeLabel = new JLabel();
        typeLabel.setText("Media Type:");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 20, 0, 0);
        gridBagConstraints.gridy = 0;
        urlLabel = new JLabel();
        urlLabel.setText("URL:");
        this.setSize(314, 203);
        this.setLayout(new GridBagLayout());
        this.add(urlLabel, gridBagConstraints);
        this.add(typeLabel, gridBagConstraints1);
        this.add(captionLabel, gridBagConstraints2);
        this.add(creditLabel, gridBagConstraints3);
        this.add(getUrlField(), gridBagConstraints4);
        this.add(getTypeComboBox(), gridBagConstraints5);
        this.add(getCaptionScrollPane(), gridBagConstraints6);
        this.add(getCreditScrollPane(), gridBagConstraints7);
        this.add(getPrimaryCheckBox(), gridBagConstraints8);
    }

    /**
	 * This method initializes urlField	
	 * @return  javax.swing.JTextField
	 * @uml.property  name="urlField"
	 */
    public JTextField getUrlField() {
        if (urlField == null) {
            urlField = new JTextField();
        }
        return urlField;
    }

    /**
	 * This method initializes typeComboBox	
	 * @return  javax.swing.JComboBox
	 * @uml.property  name="typeComboBox"
	 */
    public JComboBox getTypeComboBox() {
        if (typeComboBox == null) {
            typeComboBox = new JComboBox();
            
            // Populate with media types
            String types[] = Media.TYPES;
            for (int i = 0; i < types.length; i++) {
                typeComboBox.addItem(types[i]);
            }
            
        }
        return typeComboBox;
    }

    /**
	 * This method initializes captionScrollPane	
	 * @return  javax.swing.JScrollPane
	 * @uml.property  name="captionScrollPane"
	 */
    private JScrollPane getCaptionScrollPane() {
        if (captionScrollPane == null) {
            captionScrollPane = new JScrollPane();
            captionScrollPane.setViewportView(getCaptionArea());
        }
        return captionScrollPane;
    }

    /**
	 * This method initializes captionArea	
	 * @return  javax.swing.JTextArea
	 * @uml.property  name="captionArea"
	 */
    public JTextArea getCaptionArea() {
        if (captionArea == null) {
            captionArea = new JTextArea();
            captionArea.setPreferredSize(new Dimension(60, 60));
        }
        return captionArea;
    }

    /**
	 * This method initializes creditScrollPane	
	 * @return  javax.swing.JScrollPane
	 * @uml.property  name="creditScrollPane"
	 */
    private JScrollPane getCreditScrollPane() {
        if (creditScrollPane == null) {
            creditScrollPane = new JScrollPane();
            creditScrollPane.setViewportView(getCreditArea());
        }
        return creditScrollPane;
    }

    /**
	 * This method initializes creditArea	
	 * @return  javax.swing.JTextArea
	 * @uml.property  name="creditArea"
	 */
    public JTextArea getCreditArea() {
        if (creditArea == null) {
            creditArea = new JTextArea();
            creditArea.setPreferredSize(new Dimension(60, 60));
        }
        return creditArea;
    }

    /**
	 * This method initializes primaryCheckBox	
	 * @return  javax.swing.JCheckBox
	 * @uml.property  name="primaryCheckBox"
	 */
    public JCheckBox getPrimaryCheckBox() {
        if (primaryCheckBox == null) {
            primaryCheckBox = new JCheckBox();
            primaryCheckBox.setText("Primary");
        }
        return primaryCheckBox;
    }

    /**
	 * @param media  the media to set
	 * @uml.property  name="media"
	 */
    public void setMedia(Media media) {
        this.media = media;
        if (media != null) {
            getUrlField().setText(media.getUrl());
            getCaptionArea().setText(media.getCaption());
            getCreditArea().setText(media.getCredit());
            getTypeComboBox().setSelectedItem(media.getType());
            getPrimaryCheckBox().setSelected(media.isPrimary());
        }
        else {
            getUrlField().setText("");
            getCaptionArea().setText("");
            getCreditArea().setText("");
            getTypeComboBox().setSelectedIndex(0);
            getPrimaryCheckBox().setSelected(false);
        }
    }

    /**
	 * @return  the media
	 * @uml.property  name="media"
	 */
    public Media getMedia() {
        return media;
    }

    public boolean isLocked() {
        return locked;
    }

    /**
	 * @param locked  the locked to set
	 * @uml.property  name="locked"
	 */
    public void setLocked(boolean locked) {
        getUrlField().setEditable(!locked);
        getTypeComboBox().setEnabled(!locked);
        getCreditArea().setEditable(!locked);
        getCaptionArea().setEditable(!locked);
        getPrimaryCheckBox().setEnabled(!locked);
        this.locked = locked;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
