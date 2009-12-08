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


/*
Created on Dec 11, 2003
 */
package vars.annotation.ui.table;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * <p>A cell renderer that displays if the current observation has a frame-grab
 * or sample associated with it.</p>
 */
public class FGSCellRenderer extends JPanel implements TableCellRenderer {



    private javax.swing.JLabel lblFrameGrab = null;

    private javax.swing.JLabel lblSample = null;

    /**
     * This is the default constructor
     */
    public FGSCellRenderer() {
        super();
        initialize();
    }


    private javax.swing.JLabel getLblFrameGrab() {
        if (lblFrameGrab == null) {
            lblFrameGrab = new javax.swing.JLabel();
            lblFrameGrab.setText("");
            lblFrameGrab.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            lblFrameGrab.setDisabledIcon(
                new javax.swing.ImageIcon(getClass().getResource("/images/vars/annotation/fg_off.jpg")));
            lblFrameGrab.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/images/vars/annotation/fg_on.jpg")));
            lblFrameGrab.setName("lblFrameGrab");
        }

        return lblFrameGrab;
    }


    private javax.swing.JLabel getLblSample() {
        if (lblSample == null) {
            lblSample = new javax.swing.JLabel();
            lblSample.setText("");
            lblSample.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            lblSample.setDisabledIcon(
                new javax.swing.ImageIcon(getClass().getResource("/images/vars/annotation/s_off.jpg")));
            lblSample.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vars/annotation/s_on.jpg")));
            lblSample.setName("lblSample");
        }

        return lblSample;
    }

    /**
     * @param  table Description of the Parameter
     * @param  value Description of the Parameter
     * @param  isSelected Description of the Parameter
     * @param  hasFocus Description of the Parameter
     * @param  row Description of the Parameter
     * @param  column Description of the Parameter
     * @return  The tableCellRendererComponent value
     * @see  javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        }
        else {
            setBackground(table.getBackground());
        }


        final Integer obs = (value instanceof Integer) ? (Integer) value : -1;
        boolean hasFramegrab = false;
        boolean hasSample = false;

        // Set the framegrab icon
        if (ObservationTableModel.FRAMEGRAB.equals(obs)) {
            hasFramegrab = true;
            hasSample = false;
        }
        else if (ObservationTableModel.SAMPLE.equals(obs)) {
            hasFramegrab = false;
            hasSample = true;
        }
        else if (ObservationTableModel.FRAMEGRAB_AND_SAMPLE.equals(obs)) {
            hasFramegrab = true;
            hasSample = true;
        }

        lblFrameGrab.setEnabled(hasFramegrab);
        lblSample.setEnabled(hasSample);

        return this;
    }

    private void initialize() {
        this.setLayout(new java.awt.BorderLayout());
        this.add(getLblFrameGrab(), java.awt.BorderLayout.WEST);
        this.add(getLblSample(), java.awt.BorderLayout.EAST);
        this.setSize(32, 16);
    }
}

