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


package vars.annotation.ui;

import javax.swing.JComboBox;
import vars.annotation.CameraDirections;

/**
 * <p>A combobox that sets the Camera direction. A convience class.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class CameraDirectionComboBox extends JComboBox {

    /**
     * Constructs ...
     *
     */
    public CameraDirectionComboBox() {
        initialize();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void initialize() {

        // Add acceptable values
        final CameraDirections[] values = CameraDirections.values();
        for (int i = 0; i < values.length; i++) {
            addItem(values[i].toString());
        }

        // Set the selected item
        if (values.length > 0) {
            setSelectedIndex(0);
        }
    }
}
