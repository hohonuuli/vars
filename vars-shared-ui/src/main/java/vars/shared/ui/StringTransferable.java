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


package vars.shared.ui;

import com.google.common.collect.ImmutableList;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;

/**
 * <p>This class represents the &quot;transferable&quot; object that
 * is used in a drag and drop operation in the VARS annotation application.
 * It currently only contains a string as the data being transferred, but
 * it has the structure to do more than that in the future.</p><hr>
 *
 */
public class StringTransferable implements Transferable {


    public final static DataFlavor LOCAL_STRING_FLAVOR = DataFlavor.stringFlavor;

    /**
     * Although these declarations are not really necessary, they serve more as a placeholder
     * for our own flavors that we can implement later to do more interesting
     * data transfers with.
     */
    private final static List<DataFlavor> flavorList = ImmutableList.of(StringTransferable.LOCAL_STRING_FLAVOR);

    public final static DataFlavor[] flavors = flavorList.toArray(new DataFlavor[0]);

    private String string;

    /**
     * The default constructor that simply initializes the instance
     *
     * @param  string is the String that will be transferred as data in the operation
     */
    public StringTransferable(final String string) {
        this.string = string;
    }

    /**
     * This is the method that is used to get the transferred data object.  Once
     * the object is retrieved, it should be cast into it appropriate class and
     * then it can be used
     *
     * @param  flavor This is the flavor of data that is being requested from the
     * operation.  Usually the caller will call <code>isDataFlavorSupported</code>
     * first to see if the data can be retrieved in that flavor.  If it is supported,
     * the user can then call this method with that DataFlavor, get the returned object
     * and cast it into what they were expecting
     * @return  The <code>Object</code> that contains the data that is transferred
     * @exception  UnsupportedFlavorException Description of the Exception
     */
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
        if (StringTransferable.LOCAL_STRING_FLAVOR.equals(flavor)) {
            return this.string;
        }
        else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * A method to return the data flavors that are being transferred with this
     * transferable class
     *
     * @return  An array of DataFlavor classes that tell what kind of data is transferred with this class
     */
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * A method to use to determine if this class supports a certain data flavor for
     * transfer.
     *
     * @param  flavor Is the DataFlavor that is checked for transferrability
     * @return  a boolean to indicate if the DataFlavor can be transferred (true) or not (false)
     */
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return (flavorList.contains(flavor));
    }

    /**
     * Simply overriding the toString() method
     *
     * @return  Description of the Return Value
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + this.string + ")";
    }
}
