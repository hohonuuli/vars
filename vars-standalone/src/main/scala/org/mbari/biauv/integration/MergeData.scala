package org.mbari.biauv.integration

import reflect.BeanProperty
import scala.collection.JavaConversions._

/**
 * 
 * @author Brian Schlining
 * @since Sep 7, 2010
 */

class MergeData(@BeanProperty val time: Array[Double],
                @BeanProperty val photoNumber: Array[Double],
                @BeanProperty val latitude: Array[Double],
                @BeanProperty val longitude: Array[Double],
                @BeanProperty val depth: Array[Double],
                @BeanProperty val altitude: Array[Double],
                @BeanProperty val yaw: Array[Double],
                @BeanProperty val pitch: Array[Double],
                @BeanProperty val roll: Array[Double],
                @BeanProperty val viewHeight: Array[Double],
                @BeanProperty val viewWidth: Array[Double])

object MergeData {

    /**
     * Convert a {@see MergeData} grouping of data into individual
     * {@see MergeDatum} objects where each object represents a row
     * of data. This makes merges easier.
     * @param mergeData A MergeData object
     * @return A java List of MergeDatum objects.
     */
    def toMergeDatumList(mergeData: MergeData): java.util.List[MergeDatum] = {
        for (i <- 0 until mergeData.time.length)
            yield MergeDatum(mergeData.time(i),
                mergeData.photoNumber(i),
                mergeData.latitude(i),
                mergeData.longitude(i),
                mergeData.depth(i),
                mergeData.altitude(i),
                mergeData.yaw(i),
                mergeData.pitch(i),
                mergeData.roll(i),
                mergeData.viewHeight(i),
                mergeData.viewWidth(i))
    }

}