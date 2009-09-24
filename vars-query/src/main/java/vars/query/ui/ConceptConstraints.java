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
 * Created on Apr 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package vars.query.ui;

import vars.query.LinkBean;
import java.util.Collection;
import java.util.Iterator;
import vars.ILink;

//~--- classes ----------------------------------------------------------------

/**
 * <p>A ConceptConstraint is a wrapper for the follwong 2 items:
 * <ol>
 * <li>A collection of concept-names (as strings)</li>
 * <li>An AssociationBean</li>
 * </ol>
 * It uses these 2 items to generate a SQL fragement that constrains a query to
 * search for all concept-names in the collection that have the given association
 * </p>
 *
 * @author brian
 * @version $Id: ConceptConstraints.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ConceptConstraints {

    /** <!-- Field description --> */
    public static final String WILD_CARD_STRING = ILink.VALUE_NIL;

    /** <!-- Field description --> */
    public static final String EMPTY_SQL = "";

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="associationBean"
	 * @uml.associationEnd  
	 */
    private LinkBean associationBean;

    /**
	 * A Collection of Strings. This represents the concepts that we're attempting lookup
	 * @uml.property  name="conceptNamesAsStrings"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
    private Collection conceptNamesAsStrings;

    //~--- get methods --------------------------------------------------------

    /**
	 * @return  Returns the associationBean.
	 * @uml.property  name="associationBean"
	 */
    public LinkBean getAssociationBean() {
        return associationBean;
    }

    /**
	 * @return  Returns the conceptNamesAsStrings.
	 * @uml.property  name="conceptNamesAsStrings"
	 */
    public Collection getConceptNamesAsStrings() {
        return conceptNamesAsStrings;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    public String getSQL() {
        boolean hasConcepts = false;
        boolean hasAssociations = false;
        boolean hasLinkName = false;
        boolean hasToConcept = false;
        boolean hasLinkValue = false;

        /*
         * Determine if we are constraining by concepts
         */
        if ((conceptNamesAsStrings != null) &&
            !conceptNamesAsStrings.isEmpty() &&
            !conceptNamesAsStrings.contains(ConceptConstraints.WILD_CARD_STRING.toLowerCase()) &&
            !conceptNamesAsStrings.contains(ConceptConstraints.WILD_CARD_STRING.toLowerCase())) {
            hasConcepts = true;
        }

        /*
         * Determine what assocation parts we are constraining by
         */
        if (associationBean != null) {
            if (!isWildCard(associationBean.getLinkName())) {
                hasAssociations = true;
                hasLinkName = true;
            }

            if (!isWildCard(associationBean.getToConcept()) &&
                    !associationBean.getToConcept().equalsIgnoreCase("self")) {
                hasAssociations = true;
                hasToConcept = true;
            }

            if (!isWildCard(associationBean.getLinkValue())) {
                hasAssociations = true;
                hasLinkValue = true;
            }
        }

        /*
         * If hothing to see here, return an empty string.
         */
        if (!hasConcepts &&!hasAssociations) {
            return ConceptConstraints.EMPTY_SQL;
        }

        /*
         * Build the query here. This code follows the big ball of mud design pattern
         * Better wear your boots!
         */
        StringBuffer sb = new StringBuffer("(");
        if (hasConcepts) {

            /*
             * Make a comma separated list of the conceptNames. Each name must
             * be surronded by single quotes like: 'grimpoteuthis', 'chiroteuthis'
             */
            StringBuffer conceptNames = new StringBuffer();
            for (Iterator j = conceptNamesAsStrings.iterator(); j.hasNext(); ) {
                String conceptName = (String) j.next();
                conceptNames.append("'").append(conceptName).append("', ");
            }

            // Remove the trailing ", "
            conceptNames.delete(
                    conceptNames.length() - 2, conceptNames.length());
            sb.append(" ( ConceptName IN (").append(conceptNames).append(") ");

            if (!hasToConcept) {
                sb.append(
                        " OR ToConcept IN (").append(
                        conceptNames).append(") ");
            }

            /*
             * Also need to stick in the toconcept from the association here.
             */
            if (hasToConcept) {
                sb.append(
                        " AND ToConcept = '").append(
                        associationBean.getToConcept()).append("' ");
            }

            sb.append(")");

            if (hasAssociations) {
                sb.append(" AND ");
            }
        }

        if (hasLinkName) {
            sb.append(
                    " LinkName = '").append(
                    associationBean.getLinkName()).append("' ");

            if ((!hasConcepts && hasToConcept) || hasLinkValue) {
                sb.append(" AND ");
            }
        }

        if (hasLinkValue) {
            sb.append(
                    " LinkValue = '").append(
                    associationBean.getLinkValue()).append("' ");

            if (!hasConcepts && hasToConcept) {
                sb.append(" AND ");
            }
        }

        if (!hasConcepts && hasToConcept) {
            sb.append(
                    " ToConcept = '").append(
                    associationBean.getToConcept()).append("' ");
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param value
     *
     * @return
     */
    public static boolean isWildCard(String value) {
        boolean wildCard = false;
        if (ConceptConstraints.WILD_CARD_STRING.equalsIgnoreCase(value)) {
            wildCard = true;
        }

        return wildCard;
    }

    //~--- set methods --------------------------------------------------------

    /**
	 * @param associationBean  The associationBean to set.
	 * @uml.property  name="associationBean"
	 */
    public void setAssociationBean(LinkBean associationBean) {
        this.associationBean = associationBean;
    }

    /**
	 * @param conceptNamesAsStrings  The conceptNamesAsStrings to set.
	 * @uml.property  name="conceptNamesAsStrings"
	 */
    public void setConceptNamesAsStrings(Collection conceptNamesAsStrings) {
        this.conceptNamesAsStrings = conceptNamesAsStrings;
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
        StringBuffer sb = new StringBuffer();

        /*
         * pre-pend all the concept-names; If the set is null or contains NIL
         * then the search is not contrained by concept-names
         */
        if ((conceptNamesAsStrings == null) ||
            conceptNamesAsStrings.contains(ConceptConstraints.WILD_CARD_STRING.toLowerCase()) ||
            conceptNamesAsStrings.contains(ConceptConstraints.WILD_CARD_STRING.toUpperCase())) {
            sb.append(ConceptConstraints.WILD_CARD_STRING);
        } else {
            for (Iterator i = conceptNamesAsStrings.iterator(); i.hasNext(); ) {
                sb.append(i.next());
                sb.append(", ");
            }

            // Remove the trailing ", "
            sb.delete(sb.length() - 2, sb.length());
        }

        if (associationBean != null) {
            sb.append(": ");
            sb.append(associationBean.toString());
        }

        return sb.toString();
    }
}
