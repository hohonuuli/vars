/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui.actions;

import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class AddRedSurfaceColorPropAction extends AddPropertyAction {

    public AddRedSurfaceColorPropAction(ToolBelt toolBelt) {
        super(toolBelt, "surface-color", "self", "red");
    }

}
