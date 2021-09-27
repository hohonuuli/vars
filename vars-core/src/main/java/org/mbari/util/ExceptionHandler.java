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


/**
 * @author brian
 * @version $Id: ExceptionHandler.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
@Deprecated
public abstract class ExceptionHandler {

    /**
	 * @uml.property  name="exceptionClass"
	 */
    private final Class exceptionClass;

    /**
	 * This is bascially a drawer to stuff in a reference to any object that the handler might need to correctly handle the exception.
	 * @uml.property  name="object"
	 */
    private Object object;


    /**
     * Register this handler to handle a particular type of exception
     *
     * @param exceptionClass
     */
    public ExceptionHandler(final Class exceptionClass) {
        super();
        this.exceptionClass = exceptionClass;
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
        return (e != null) && exceptionClass.isInstance(e);
    }

    /**
     * This method is called by the handle method. The handle method validates that
     * this is the correct exception to process then passes the exception to this
     * method to do the actual work.
     *
     * @param e
     */
    protected abstract void doAction(Exception e);


    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="exceptionClass"
	 */
    public Class getExceptionClass() {
        return exceptionClass;
    }

    /**
	 * @return  Any object that you might need to correctly deal with an Exception in  the doAction method;
	 * @uml.property  name="object"
	 */
    public Object getObject() {
        return object;
    }



    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param e
     */
    public void handle(Exception e) {
        if (canHandle(e)) {
            doAction(e);
        }
    }


    /**
	 * This is a place to store a reference to any object that might be needed to correctly deal with the Exception
	 * @param object  Any darn thing you want. This is used to store an object  that might be needed by the doAction() method
	 * @uml.property  name="object"
	 */
    public void setObject(Object object) {
        this.object = object;
    }
}
