/*
 * @(#)IUsage.java   2008.12.30 at 01:50:53 PST
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



package vars.knowledgebase;

import java.util.Date;

/**
 *
 * @author brian
 */
public interface IUsage {

    String PROP_EMBARGO_EXPIRATION_DATE = "embargoExpirationDate";
    String PROP_SPECIFICATION = "specification";

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="conceptDelegate"
     */
    IConceptDelegate getConceptDelegate();

    /**
     * <p><!-- Method description --></p>
     * @return
     */
    Date getEmbargoExpirationDate();


    /**
     * Gets the <code>Usage</code> 'specification' attribute.
     * @return  The String specification of the <code>Usage</code>
     */
    String getSpecification();

    /**
     * Sets the <code>Usage</code> 'embargoExpirationDTG' attribute.
     * @param  embargoExpirationDTG
     */
    void setEmbargoExpirationDate(Date embargoExpirationDTG);


    /**
     * Sets the <code>Usage</code> 'specification' attribute.
     * @param specification  String specification of the <code>Usage</code>
     * @uml.property  name="specification"
     */
    void setSpecification(String specification);
}
