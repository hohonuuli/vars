package org.mbari.math

import org.slf4j.LoggerFactory

/**
 *
 * @author Brian Schlining
 * @since Aug 31, 2010
 */
class CoallateFunction {

    private static final log = LoggerFactory.getLogger(CoallateFunction.getClass())

    private static final doNothingClosure = { it }

    static coallate(List d0, String field0, List d1, String field1, offset) {
        coallate(d0, field0, doNothingClosure, d1, field1, doNothingClosure, offset)
    }

    /**
     * Coallates 2 different sets together based on the field you indicate. You
     * can apply a transform to each field so that they can be intercompared. For
     * example, if you want to compare timecode with time. The returned map uses
     * values form d0 as the key and the nearest value from d1, within offset distance,
     * for the value. If no value in d1 is within offset of a value within d0 then it
     * will not be included in the returned list
     *
     * @param d0 The list of 'key' objects that you wish to coallate to
     * @param field0 The field of the objects in d0 that you want to use as a coallation key
     * @param transform0 A transform applied to field0 to convert it to some numeric value (if needed)
     * @param d1 The list of value objects that will be coallated to d0
     * @param field1 The field of the objects in d1 that you want to use as a coallation key
     * @param transform1 A transform applied to field1 to convert it to some numeric value (if needed)
     * @param offset A cutoff value such that if no match is found between field0 and field1 that's within offset
     *      no value will be returned for that d0 record.
     *
     */
    static coallate(List d0, String field0, Closure transform0, List d1, String field1, Closure transform1, offset) {
        def data = [:]
        def list0 = d0.sort { transform0(it[field0]) }
        def list1 = d1.sort { transform1(it[field1]) }

        int i = 0
        list0.each { val0 ->
            def t0 = transform0(val0[field0])
            def dtBest = offset

            def goodDatum = null
            for (row in i..<list1.size()) {
                def val1 = list1[row]
                def t1 = transform1(val1[field1])
                def dt = Math.abs(t0 - t1)
                if (dt <= dtBest) {
                    dtBest = dt
                    i = row
                    goodDatum = val1
                }
                else if ((dt > dtBest) && (t1 > t0)) {
                    break
                }
            }

            if (goodDatum) {
                data[val0] = goodDatum
            }
            else {
                log.debug("No record was found to match ${val0}")
            }
        }
        return data
    }

}
