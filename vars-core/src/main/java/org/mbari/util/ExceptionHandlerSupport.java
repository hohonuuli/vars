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


package org.mbari.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

//~--- classes ----------------------------------------------------------------

/**
 * @author brian
 * @version $Id: ExceptionHandlerSupport.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
@Deprecated
public class ExceptionHandlerSupport {

    /**
	 * @uml.property  name="exceptionHandlers"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="org.mbari.util.ExceptionHandler"
	 */
    private final Collection exceptionHandlers = Collections.synchronizedList(new ArrayList());

    //~--- constructors -------------------------------------------------------

    /**
     *
     */
    public ExceptionHandlerSupport() {
        super();
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param eh
     */
    public void addExceptionHandler(ExceptionHandler eh) {
        exceptionHandlers.add(eh);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param e
     *
     * @return
     */
    public boolean canHandle(Exception e) {
        boolean ok = false;

        // loop through all the handlers to see if they can handle this exception.
        for (Iterator i = exceptionHandlers.iterator(); i.hasNext(); ) {
            ExceptionHandler eh = (ExceptionHandler) i.next();
            if (eh.canHandle(e)) {
                ok = true;
                break;
            }
        }

        return ok;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param e
     */
    public void handle(Exception e) {
        for (Iterator i = exceptionHandlers.iterator(); i.hasNext(); ) {
            ExceptionHandler eh = (ExceptionHandler) i.next();

            /*
             * If the ExceptionHandler can not handle this exception it will
             * simple do nothing. (i.e internally it does a canHandle(e) check.
             */
            eh.handle(e);
        }
    }

    /**
     *
     * @return A Collection of <code>ExceptionHandlers</code>. This is a copy of the
     * internal Collection so adds or removes will have no effect on the interal copy.
     */
    public Collection listExceptionHandlers() {
        return new ArrayList(exceptionHandlers);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param eh
     */
    public void removeExceptionHandler(ExceptionHandler eh) {
        exceptionHandlers.remove(eh);
    }
}
