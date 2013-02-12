package vars.integration;

/**
 * @author Brian Schlining
 * @since 2013-02-11
 */
public enum MergeType {

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
     CONSERVATIVE, OPTIMISTIC, PESSIMISTIC, PRAGMATIC;
}
