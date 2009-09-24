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


package vars.query;

import vars.ILink;


/**
 * <p>Convience class used for lightweight representation of Associations. </p>
 *
 * @author Brian Schlining
 * @version $Id: AssociationBean.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class LinkBean implements ILink {

    /**
	 * @uml.property  name="linkName"
	 */
    private String linkName;
    /**
	 * @uml.property  name="linkValue"
	 */
    private String linkValue;
    /**
	 * @uml.property  name="toConcept"
	 */
    private String toConcept;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public LinkBean() {
        super();
    }

    /**
     * Constructs ...
     *
     *
     * @param linkName
     * @param toConcept
     * @param linkValue
     */
    public LinkBean(String linkName, String toConcept,
            String linkValue) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkName;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param obj
     *
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj != null) {
            if (obj == this) {
                equal = true;
            } else if (obj.getClass().equals(this.getClass())) {
                if ((obj.hashCode() - this.hashCode()) == 0) {
                    equal = true;
                }
            }
        }

        return equal;
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * @return  Returns the linkName.
	 * @uml.property  name="linkName"
	 */
    public String getLinkName() {
        return linkName;
    }

    /**
	 * @return  Returns the linkValue.
	 * @uml.property  name="linkValue"
	 */
    public String getLinkValue() {
        return linkValue;
    }

    /**
	 * @return  Returns the toConcept.
	 * @uml.property  name="toConcept"
	 */
    public String getToConcept() {
        return toConcept;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    @Override
    public int hashCode() {
        return toString().hashCode() * 3;
    }

    //~--- set methods --------------------------------------------------------

    /**
	 * @param linkName  The linkName to set.
	 * @uml.property  name="linkName"
	 */
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    /**
	 * @param linkValue  The linkValue to set.
	 * @uml.property  name="linkValue"
	 */
    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }

    /**
	 * @param toConcept  The toConcept to set.
	 * @uml.property  name="toConcept"
	 */
    public void setToConcept(String toConcept) {
        this.toConcept = toConcept;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    @Override
    public String toString() {
        return linkName + " | " + toConcept + " | " + linkValue;
    }

    public String getFromConcept() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
