/*
 * @(#)IPhysicalData.java   2008.12.30 at 01:50:54 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation;

import java.util.Date;

/**
 *
 * @author brian
 */
public interface IPhysicalData extends IAnnotationObject {

    String PROP_DEPTH = "depth";
    String PROP_LATITUDE = "latitude";
    String PROP_LIGHT = "light";
    String PROP_LONGITUDE = "longitude";
    String PROP_OXYGEN = "oxygen";
    String PROP_SALINITY = "salinity";
    String PROP_TEMPERATURE = "temperature";

    /**
     * Indicates if the physical data object contains any data values.
     * @return <strong>false</strong> if all data values (depth, salinity, temperature, oxygen, light,
     * latitude, and longitude) are null. <strong>true</strong> if any of these values are not null.
     */
    boolean containsData();

    /**
     * Get Depth
     * @return  Depth in meters
     * @uml.property  name="depth"
     */
    Float getDepth();

    /**
     * Get Decimal latitude
     * @return  Decimal latitude
     * @uml.property  name="latitude"
     */
    Float getLatitude();

    /**
     * Get Light Beam Transmission PerCentage
     * @return  Light Beam Transmission PerCentage
     * @uml.property  name="light"
     */
    Float getLight();

    /**
     * Get Decimal longitude
     * @return  Decimal longitude
     * @uml.property  name="longitude"
     */
    Float getLongitude();

    /**
     * Get Oxygen
     * @return  Oxygen in ml/l
     * @uml.property  name="oxygen"
     */
    Float getOxygen();

    /**
     * Get Salinity
     * @return  Salinity in Practical Salinity Units (PSU)
     * @uml.property  name="salinity"
     */
    Float getSalinity();

    /**
     * Get Temperature
     * @return  Temperature in degrees Celsius
     * @uml.property  name="temperature"
     */
    Float getTemperature();

    /**
     * @return  The association videoFrame
     * @uml.property  name="videoFrame"
     */
    IVideoFrame getVideoFrame();


    /**
     * Set Depth from a Float
     * @param depth  Depth in meters
     * @uml.property  name="depth"
     */
    void setDepth(Float depth);

    /**
     * Set Light Beam Transmission PerCentage from a float
     * @param  latitude
     * @uml.property  name="latitude"
     */
    void setLatitude(Float latitude);

    /**
     * Set Oxygen from a float
     * @param  light
     * @uml.property  name="light"
     */
    void setLight(Float light);

    /**
     * Set Decimal Latitude from a Float
     * @param  longitude
     * @uml.property  name="longitude"
     */
    void setLongitude(Float longitude);


    /**
     * Set Salinity from a float
     * @param  oxygen
     * @uml.property  name="oxygen"
     */
    void setOxygen(Float oxygen);

    /**
     * Set Temperature from a float
     * @param  salinity
     * @uml.property  name="salinity"
     */
    void setSalinity(Float salinity);

    /**
     * Set Depth from a float
     * @param  temperature
     * @uml.property  name="temperature"
     */
    void setTemperature(Float temperature);

}
