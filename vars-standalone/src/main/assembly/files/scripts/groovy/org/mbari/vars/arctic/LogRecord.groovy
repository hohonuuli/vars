package org.mbari.vars.arctic

/**
 * Created by brian on 1/29/14.
 */
class LogRecord {

    def conductivity
    def depth
    def latitude
    def longitude
    def temperature
    def time

    LogRecord(conductivity, depth, latitude, longitude, temperature, time) {
        if (!conductivity || !depth || !latitude || !longitude || !temperature || !time) {
            throw new IllegalArgumentException("No argument can be null")
        }
        this.conductivity = conductivity
        this.depth = depth
        this.latitude = latitude
        this.longitude = longitude
        this.temperature = temperature
        this.time = time

    }
}
