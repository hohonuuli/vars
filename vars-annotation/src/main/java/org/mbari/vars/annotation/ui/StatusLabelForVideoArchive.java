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
Created on Dec 1, 2003
 */
package org.mbari.vars.annotation.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import org.mbari.vars.annotation.ui.actions.ShowOpenVideoArchiveDialogAction;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import vars.annotation.IVideoArchive;

/**
 * <p>Indicates which videoarchive the annotator is editing. Clicking on the
 * label opens a dialog allowing the user to change the videoarchive being
 * edited.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: StatusLabelForVideoArchive.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class StatusLabelForVideoArchive extends StatusLabel {

    /**
     *
     */
    private static final long serialVersionUID = -3645686292355814856L;

    /**
     *     @uml.property  name="action"
     *     @uml.associationEnd
     */
    private ShowOpenVideoArchiveDialogAction action;

    /**
     * Constructor for the StatusLabelForVideoArchive object
     */
    public StatusLabelForVideoArchive() {
        super();
        Dispatcher dispatcher = PredefinedDispatcher.VIDEOARCHIVE.getDispatcher();
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                update((IVideoArchive) evt.getNewValue());
            }

        });
        update((IVideoArchive) dispatcher.getValueObject());

        /*
         * On click show a dialog allowing a user to open a VideoArchive
         */
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(final MouseEvent me) {
                SwingUtils.flashJComponent(StatusLabelForVideoArchive.this, 2);
                final JDialog dialog = getAction().getDialog();

                /*
                 * Centers the dialog on screen
                 */
                dialog.setLocationRelativeTo(null);
                getAction().doAction();
            }

        });
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="action"
     */
    private ShowOpenVideoArchiveDialogAction getAction() {
        if (action == null) {
            action = new ShowOpenVideoArchiveDialogAction();
        }

        return action;
    }

    /**
     * Sets the videoArchive registered with the label. In general, you don't need
     * to call this. This status label registers with
     * PredefinedDispatcher.VIDEOARCHIVE and listens for when the videoArchive
     * is set there.
     *
     * @param videoArchive Sets the videoArchive to be registered with the label
     */
    public void update(final IVideoArchive videoArchive) {
        boolean ok = false;
        String text = "NONE";
        String toolTip = text;
        if (videoArchive != null) {
            text = videoArchive.getVideoArchiveName() + "";
            toolTip = text;
            
            if (text.length() > 20 && 
                    (text.toLowerCase().startsWith("http:") || 
                    text.toLowerCase().startsWith("file:"))) {
                String[] parts = text.split("/");
                if (parts.length > 0) {
                    text = ".../" + parts[parts.length - 1];
                }
                
            }
            ok = true;
        }

        setText("Video: " + text);
        setToolTipText(toolTip);
        setOk(ok);
    }

    /**
     * Method description
     *
     *
     * @param arg0
     * @param arg1
     */
    public void update(Object arg0, Object arg1) {
        update((IVideoArchive) arg0);
    }
}
