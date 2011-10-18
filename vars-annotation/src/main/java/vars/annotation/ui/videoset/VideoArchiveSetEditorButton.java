/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.videoset;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 * Button for launching the {@link VideoArchiveSetEditorPanel}
 * @author brian
 */
public class VideoArchiveSetEditorButton extends JToggleButton {

    private JFrame frame;
    private VideoArchiveSetEditorPanel panel;
    private final ToolBelt toolBelt;

    public VideoArchiveSetEditorButton(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;

        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    getFrame().setVisible(true);
                }
                else {
                    getFrame().setVisible(false);
                }
            }
        });

        setEnabled(false);
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/24px/table_edit.png")));
        setText("");
        setToolTipText("Edit VideoArchiveSet");

        Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                setEnabled(evt.getNewValue() != null);
            }
        });
    }

    private VideoArchiveSetEditorPanel getPanel() {
        if (panel == null) {
            panel = new VideoArchiveSetEditorPanel(toolBelt);
        }
        return panel;
    }

    private JFrame getFrame() {
        if (frame == null) {
            frame = new JFrame("VARS - VideoArchiveSet Editor");
            frame.setLayout(new BorderLayout());
            frame.add(getPanel(), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Toggle button if Frame is closed
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setSelected(false);
                }
            });


            frame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowOpened(WindowEvent e) {
                    refresh();
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                    refresh();
                }

                @Override
                public void windowActivated(WindowEvent e) {
                    refresh();
                }

            });

            frame.pack();


        }
        return frame;
    }

    private void refresh() {
        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (videoArchive != null) {
            panel.setVideoArchiveSet(videoArchive.getVideoArchiveSet());
        }
        else {
            panel.setVideoArchiveSet(null);
        }
    }




}
