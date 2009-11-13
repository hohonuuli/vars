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


package org.mbari.vars.annotation.ui.dispatchers;

import org.mbari.util.Dispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;

/**
 * <p>Singleton access to the ObservationTable used in the annotation applicaiton.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ObservationTableDispatcher.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ObservationTableDispatcher {

    /** Dispatcher used for managing ObservationTable references */
    public static final Dispatcher DISPATCHER = PredefinedDispatcher.OBSERVATIONTABLE.getDispatcher();
    private static ObservationTableDispatcher cod;

    /**
     * Singleton
     *
     */
    ObservationTableDispatcher() {
        super();
    }

    /**
     * @return   Returns the observationTableModel.
     */
    public ObservationTable getObservationTable() {
        return (ObservationTable) DISPATCHER.getValueObject();
    }

    /**
     * @param observationTable  The new observationTable value
     */
    public void setObservationTable(ObservationTable observationTable) {
        DISPATCHER.setValueObject(observationTable);
    }

    /**
     * Singleton access. You can retrieve an instance here.
     * @return  An instance of ObservationTableDispatcher
     */
    public static ObservationTableDispatcher getInstance() {
        if (cod == null) {
            synchronized (ObservationTableDispatcher.class) {
                cod = new ObservationTableDispatcher();
            }
        }

        return cod;
    }
}
