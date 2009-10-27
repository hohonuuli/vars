/*
 * @(#)Role.java   2008.12.30 at 01:50:52 PST
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

/**
 *
 * @author brian
 */
public interface Role {

    /**
     * Gets the name of this <code>Role</code>.
     * @return     The name of this <code>Role</code>.
     * @uml.property  name="name"
     */
    String getName();

}
