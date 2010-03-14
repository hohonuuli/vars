/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.videoset;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import vars.annotation.ui.ToolBelt;

/**
 * Button for launching the {@link VideoArchiveSetEditorPanel}
 * @author brian
 */
public class VideoArchiveSetEditorButton extends JToggleButton {

    private final JFrame frame;

    public VideoArchiveSetEditorButton(ToolBelt toolBelt) {
        frame = new JFrame("VARS - VideoArchiveSet Editor");
        frame.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(new VideoArchiveSetEditorPanel(toolBelt));
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    frame.setVisible(true);
                }
                else {
                    frame.setVisible(false);
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                setSelected(false);
            }
        });
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/16px/table_edit.png")));
        setText("");
        setToolTipText("Edit VideoArchiveSet");
    }



}
