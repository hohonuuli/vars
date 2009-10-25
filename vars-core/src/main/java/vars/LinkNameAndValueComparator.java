/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.util.Comparator;

/**
 * For comparing links using linkName and linkValue fields.
 * @author brian
 */
public class LinkNameAndValueComparator implements Comparator<ILink> {

    public int compare(ILink o1, ILink o2) {
        final String s1 = o1.getLinkName() + ILink.DELIMITER + o1.getLinkValue();
        final String s2 = o2.getLinkName() + ILink.DELIMITER + o2.getLinkValue();

        return s1.compareTo(s2);
    }

}
