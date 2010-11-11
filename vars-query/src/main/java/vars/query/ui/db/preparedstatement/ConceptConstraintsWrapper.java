/*
 * @(#)ConceptConstraintsWrapper.java   2010.11.09 at 02:07:56 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.query.ui.db.preparedstatement;

import vars.LinkBean;
import vars.query.ui.ConceptConstraints;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @version        Enter version here..., 2010.11.09 at 02:07:40 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
class ConceptConstraintsWrapper {

    protected boolean hasConcepts = false;
    protected boolean hasAssociations = false;
    protected boolean hasLinkName = false;
    protected boolean hasToConcept = false;
    protected boolean hasLinkValue = false;
    protected final ConceptConstraints conceptConstraints;

    public ConceptConstraintsWrapper(ConceptConstraints conceptConstraints) {
        this.conceptConstraints = conceptConstraints;

        /*
         * Determine if we are constraining by concepts
         */
        Collection<String> conceptNamesAsStrings = conceptConstraints.getConceptNamesAsStrings();

        if ((conceptNamesAsStrings != null) && !conceptNamesAsStrings.isEmpty() &&
                !conceptNamesAsStrings.contains(ConceptConstraints.WILD_CARD_STRING.toLowerCase()) &&
                !conceptNamesAsStrings.contains(ConceptConstraints.WILD_CARD_STRING.toLowerCase())) {
            hasConcepts = true;
        }

        /*
         * Determine what assocation parts we are constraining by
         */
        LinkBean associationBean = conceptConstraints.getAssociationBean();

        if (associationBean != null) {
            if (!ConceptConstraints.isWildCard(associationBean.getLinkName())) {
                hasAssociations = true;
                hasLinkName = true;
            }

            if (!ConceptConstraints.isWildCard(associationBean.getToConcept()) &&
                    !associationBean.getToConcept().equalsIgnoreCase("self")) {
                hasAssociations = true;
                hasToConcept = true;
            }

            if (!ConceptConstraints.isWildCard(associationBean.getLinkValue())) {
                hasAssociations = true;
                hasLinkValue = true;
            }
        }
    }

    /**
     *
     * @param statement The preparedStatement to bind parameters to
     * @param idx The next index in the prepared statement parameters that we should start
     *      binding to
     * @return The next index to bind parameters to (After they ones in this wrapper are applied
     * @throws SQLException
     */
    public int bind(PreparedStatement statement, int idx) throws SQLException {
        LinkBean associationBean = conceptConstraints.getAssociationBean();
        if (hasConcepts) {
            List<String> conceptNamesAsStrings =  new ArrayList<String>(conceptConstraints.getConceptNamesAsStrings());
            // ---- Step 1: BIND TO conceptNamesAsStrings
            for (int i = 0; i < conceptNamesAsStrings.size(); i++) {
                statement.setString(idx, conceptNamesAsStrings.get(i));
                idx++;
            }

            // ---- Step 2: BIND TO conceptNamesAsStrings
            if (!hasToConcept) {
                for (int i = 0; i < conceptNamesAsStrings.size(); i++) {
                    statement.setString(idx, conceptNamesAsStrings.get(i));
                    idx++;
                }
            }

            // ---- Step 3: BIND TO associationBean.getToConcept()

            if (hasToConcept) {
                statement.setString(idx, associationBean.getToConcept());
                idx++;
            }
        }

        // ---- Step 4: BIND TO associationBean.getLinkName()
        if (hasLinkName) {
            statement.setString(idx, associationBean.getLinkName());
            idx++;
        }

        /*
         * ---- Step 5 : Add LinkValue constraint for association
         * BIND TO associationBean.getLinkValue()
         */
        if (hasLinkValue) {
            statement.setString(idx, associationBean.getLinkValue());
            idx++;
        }

        /*
         * ---- Step 6: Add ToConceptConstraint
         * BIND TO associationBean.getToConcept()
         */
        if (!hasConcepts && hasToConcept) {
            statement.setString(idx, associationBean.getToConcept());
            idx++;
        }

        return idx;

    }

    public String toSQL() {

        /*
        * If hothing to see here, return an empty string.
        */
        if (!hasConcepts && !hasAssociations) {
            return ConceptConstraints.EMPTY_SQL;
        }

        /*
        * Build the query here. This code follows the big ball of mud design pattern
        * Better wear your boots!
        */
        StringBuffer sb = new StringBuffer("(");

        if (hasConcepts) {

            /*
             * ---- Step 1: Place holder for comma separated list of conceptNames
             * BIND TO conceptNamesAsStrings
             */
            StringBuffer conceptNames = new StringBuffer();

            for (int i = 0; i < conceptConstraints.getConceptNamesAsStrings().size(); i++) {
                conceptNames.append("?, ");
            }

            // Remove the trailing ", "
            conceptNames.delete(conceptNames.length() - 2, conceptNames.length());
            sb.append(" ( ConceptName IN (").append(conceptNames).append(") ");

            /*
             * ---- Step 2: Placeholder for comma separated list of toConceptNames
             * BIND TO conceptNamesAsStrings
             */
            if (!hasToConcept) {
                sb.append(" OR ToConcept IN (").append(conceptNames).append(") ");
            }

            /*
             * ---- Step 3: Also need to stick in the toconcept from the association here.
             * BIND TO associationBean.getToConcept()
             */
            if (hasToConcept) {
                sb.append(" AND ToConcept = ? ");
            }

            sb.append(")");

            if (hasAssociations) {
                sb.append(" AND ");
            }
        }

        /*
         * ---- Step 4: Add LinkName constraint for association
         * BIND TO associationBean.getLinkName()
         */
        if (hasLinkName) {
            sb.append(" LinkName = ?");

            if ((!hasConcepts && hasToConcept) || hasLinkValue) {
                sb.append(" AND ");
            }
        }

        /*
         * ---- Step 5 : Add LinkValue constraint for association
         * BIND TO associationBean.getLinkValue()
         */
        if (hasLinkValue) {
            sb.append(" LinkValue = ?");

            if (!hasConcepts && hasToConcept) {
                sb.append(" AND ");
            }
        }

        /*
         * ---- Step 6: Add ToConceptConstraint
         * BIND TO associationBean.getToConcept()
         */
        if (!hasConcepts && hasToConcept) {
            sb.append(" ToConcept = ?");
        }

        sb.append(")");

        return sb.toString();
    }
}
