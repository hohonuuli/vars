/*
 * @(#)LinkNameComparator.java   2008.12.30 at 01:50:52 PST
 *
 * Copyright 2007 MBARI
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



package vars;

import java.util.Comparator;
import org.mbari.text.IgnoreCaseToStringComparator;

/**
 * @author brian
 * @version $Id: $
 * @since Dec 14, 2006 3:31:47 PM PST
 */
public class LinkNameComparator<T extends ILink> implements Comparator<T> {

    private static final IgnoreCaseToStringComparator stringComparator = new IgnoreCaseToStringComparator();

    /**
     * TODO: Add JavaDoc
     *
     * @param o1
     * @param o2
     * @return
     */
    public int compare(T o1, T o2) {
        return stringComparator.compare(o1.getLinkName(), o2.getLinkName());
    }
}
