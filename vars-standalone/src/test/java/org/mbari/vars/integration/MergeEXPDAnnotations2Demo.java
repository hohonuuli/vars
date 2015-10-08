package org.mbari.vars.integration;

import vars.integration.MergeType;

/**
 * @author Brian Schlining
 * @since 2015-08-04T09:09:00
 */
public class MergeEXPDAnnotations2Demo {


    public static void main(String[] args) {
        MergeEXPDAnnotations2 m = new MergeEXPDAnnotations2("Ventana", 818, false);
        m.apply(MergeType.PRAGMATIC);
    }
}
