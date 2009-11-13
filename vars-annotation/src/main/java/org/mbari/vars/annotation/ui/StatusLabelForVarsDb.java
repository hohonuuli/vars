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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mbari.util.Dispatcher;
import org.mbari.vars.dao.ObjectDAO;

/**
 * <p>Indicates connection state of the database.</p>
 *
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: StatusLabelForVarsDb.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class StatusLabelForVarsDb extends StatusLabel {

    /**
     *
     */
    private static final long serialVersionUID = 1775956976270997478L;

    /**
     * Constructor
     */
    public StatusLabelForVarsDb() {
        super();
        setToolTipText("Status of the VARS database connection");
        setText(" VARS ");
        setOk(true);
        final Dispatcher d = AnnotationApp.DISPATCHER_DATABASE_STATUS;
        d.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                final boolean ok = ((Boolean) evt.getNewValue()).booleanValue();
                setOk(ok);

                if (ok) {
                    setToolTipText("Connected to: " + ObjectDAO.getUrl());
                }
                else {
                    setToolTipText("There is a problem with the database connection. Restart VARS.");
                }
            }

        });

    }

    /*
     *  (non-Javadoc)
     * @see org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param obj
     * @param changeCode
     */
    public void update(final Object obj, final Object changeCode) {

        // Do nothing. We're using a PropertyChangeListener instead of Observer.
    }
}
