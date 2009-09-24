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


package vars.query.ui;

import java.util.Collection;
import java.util.Iterator;

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Insert Description --></p>
 *
 * @author Brian Schlining
 * @version $Id: SQLGenerator.java 367 2006-08-17 20:44:32Z hohonuuli $
 */
public class SQLGenerator {

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param conceptConstraints
     * @param valuePanels
     * @param allInterpretations
     * @param allAssociations
     *
     * @return
     */
    public static String getSQL(Collection conceptConstraints,
            Collection valuePanels, boolean allInterpretations,
                boolean allAssociations) {
        String whereClause = getSQLWhere(conceptConstraints, valuePanels);
        StringBuffer sb = new StringBuffer(getSQLSelect(valuePanels));
        sb.append(" FROM Annotations");

        if (allInterpretations && allAssociations) {
            if (whereClause.length() > 0) {
                sb.append(
                        " WHERE ObservationID_FK IN (SELECT ObservationID_FK FROM Annotations");
                sb.append(
                        " WHERE VideoFrameID_FK IN (SELECT VideoFrameID_FK FROM Annotations");
                sb.append(" WHERE ").append(whereClause);
                sb.append(" ))");
            }
        } else if (allInterpretations) {
            if (whereClause.length() > 0) {
                sb.append(
                        " WHERE VideoFrameID_FK IN (SELECT VideoFrameID_FK FROM Annotations");
                sb.append(" WHERE ").append(whereClause);
                sb.append(" )");
            }
        } else if (allAssociations) {
            if (whereClause.length() > 0) {
                sb.append(
                        " WHERE ObservationID_FK IN (SELECT ObservationID_FK FROM Annotations");
                sb.append(" WHERE ").append(whereClause);
                sb.append(" )");
            }
        } else {
            /*
             * This is the default if no 'all associations' or 'all
             * interpretations' is checked.
             */
            if (whereClause.length() > 0) {
                sb.append(" WHERE ").append(whereClause);
            }
        }

        return sb.toString();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param valuePanels
     *
     * @return
     */
    private static String getSQLSelect(Collection valuePanels) {
        StringBuffer sb = new StringBuffer("SELECT ObservationID_FK, ");
        for (Iterator i = valuePanels.iterator(); i.hasNext(); ) {
            ValuePanel vp = (ValuePanel) i.next();
            if (vp.getReturnCheckBox().isSelected()) {
                sb.append(" ").append(vp.getValueName()).append(", ");
            }
        }

        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param conceptConstraints
     * @param valuePanels
     *
     * @return
     */
    private static String getSQLWhere(Collection conceptConstraints,
            Collection valuePanels) {
        StringBuffer sb = new StringBuffer();

        /*
         * Add the where clauses for conceptnames first
         */
        for (Iterator i = conceptConstraints.iterator(); i.hasNext(); ) {
            ConceptConstraints cc = (ConceptConstraints) i.next();
            String sql = cc.getSQL();
            if (!sql.equals(ConceptConstraints.EMPTY_SQL)) {
                sb.append(sql).append(" OR ");
            }
        }

        /*
         * Add all the subsetting by non- ConceptName or toConcept fields
         * 
         * Wrap concept constrains with a parenthesis. If we don't do this then 
         * only the last concept constrain will be constrained by the 
         * Ancillary data fields added below.
         */
        if (sb.length() > 8) {
            sb.insert(0, "(");
            sb.delete(sb.length() - 4, sb.length()).append(") AND ");
        }

        for (Iterator i = valuePanels.iterator(); i.hasNext(); ) {
            ValuePanel vp = (ValuePanel) i.next();
            if (vp.getConstrainCheckBox().isSelected()) {
                String sql = vp.getSQL();
                if (sql.length() > 0) {
                    sb.append(" ").append(vp.getSQL()).append(" AND ");
                }
            }
        }

        if (sb.length() > 4) {
            sb.delete(sb.length() - 5, sb.length());
        }

        return sb.toString();
    }
}
