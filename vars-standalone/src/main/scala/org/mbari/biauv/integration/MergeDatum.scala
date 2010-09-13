package org.mbari.biauv.integration

import reflect.BeanProperty

/**
 * 
 * @author Brian Schlining
 * @since Sep 13, 2010
 */

case class MergeDatum(@BeanProperty time: Double,
                @BeanProperty photoNumber: Double,
                @BeanProperty latitude: Double,
                @BeanProperty longitude: Double,
                @BeanProperty depth: Double,
                @BeanProperty altitude: Double,
                @BeanProperty yaw: Double,
                @BeanProperty pitch: Double,
                @BeanProperty roll: Double,
                @BeanProperty viewHeight: Double,
                @BeanProperty viewWidth: Double)