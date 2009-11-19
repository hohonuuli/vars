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
public class AddLargeSizePropAction extends AddPropertyAction {

    public AddLargeSizePropAction(ToolBelt toolBelt) {
        super(toolBelt, "relative-size", "self", "large");
    }

}
