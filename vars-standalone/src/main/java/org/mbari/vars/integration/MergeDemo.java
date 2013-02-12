package org.mbari.vars.integration;

import vars.integration.MergeType;

/**
 * @author Brian Schlining
 * @since 2013-02-11
 */
public class MergeDemo {
    public static void main(String[] args) {
        MergeEXPDAnnotations merge = new MergeEXPDAnnotations("Ventana", 1343, false);
        merge.apply(MergeType.PRAGMATIC);
    }
}
