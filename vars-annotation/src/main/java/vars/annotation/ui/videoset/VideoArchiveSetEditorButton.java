/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.videoset;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
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

        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/16px/table_edit.png")));
        setText("");
        setToolTipText("Edit VideoArchiveSet");
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

            // When window loses focus make it look different. When it gains
            // focus reload the observations from the database
            frame.addWindowFocusListener(new WindowFocusListener() {

                WaitIndicator waitIndicator;

                public void windowGainedFocus(WindowEvent e) {
                    if (waitIndicator != null) {
                        waitIndicator.dispose();
                    }
                    VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
                    if (videoArchive != null) {
                        panel.setVideoArchiveSet(videoArchive.getVideoArchiveSet());
                    }
                    else {
                        panel.setVideoArchiveSet(null);
                    }
                }

                public void windowLostFocus(WindowEvent e) {
                    waitIndicator = new WaitIndicator(frame);
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
