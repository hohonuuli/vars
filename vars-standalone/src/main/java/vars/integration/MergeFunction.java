/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.integration;

/**
 *
 * @author brian
 */
public interface MergeFunction<T> {
    
    /**
     * CONSERVATIVE - Match by date, then any that aren't matched, match by
     *                timecode. No recordedDates are changed.
     *
     * OPTIMISTIC - Match by date only
     *
     * PESSIMISTIC - Match by timecode, update recordedDates to values from 
     *               EXPD camera data
     *
     * PRAGMATIC - Match by date, then any that aren't matched, match by
     *             timecode. Change bogus recordedDates to those used in EXPD.
     */
    enum MergeType { CONSERVATIVE, OPTIMISTIC, PESSIMISTIC, PRAGMATIC };

    /**
     * Combines coallate and update into a single step
     * @param mergeType
     * @return
     */
    T apply(MergeType mergeType);

    /**
     * Commits the merged data to the database based on the mergetype
     * 
     * @param data
     * @param mergeType
     */
    void update(T data, MergeType mergeType);

    /**
     * Coallates the data. Associating the source data with other info. No
     * values are changed anywhere!
     * 
     * @param mergeType
     * @return
     */
    T coallate(MergeType mergeType);


}
