package org.mbari.biauv.integration

import reflect.BeanProperty

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