/*
 * @(#)PhysicalData.java   2011.03.18 at 09:10:33 PDT
 *
 * Copyright 2011 MBARI
 *
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
public interface PhysicalData extends AnnotationObject {

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

    Float getAltitude();

    /**
     * Get Depth
     * @return  Depth in meters
     */
    Float getDepth();

    /**
     * Get Decimal latitude
     * @return  Decimal latitude
     */
    Double getLatitude();

    /**
     * Get Light Beam Transmission PerCentage
     * @return  Light Beam Transmission PerCentage
     */
    Float getLight();

    Date getLogDate();

    /**
     * Get Decimal longitude
     * @return  Decimal longitude
     */
    Double getLongitude();

    /**
     * Get Oxygen
     * @return  Oxygen in ml/l
     */
    Float getOxygen();

    /**
     * Get Salinity
     * @return  Salinity in Practical Salinity Units (PSU)
     */
    Float getSalinity();

    /**
     * Get Temperature
     * @return  Temperature in degrees Celsius
     */
    Float getTemperature();

    /**
     * @return  The association videoFrame
     */
    VideoFrame getVideoFrame();

    void setAltitude(Float altitude);

    /**
     * Set Depth from a Float
     * @param depth  Depth in meters
     */
    void setDepth(Float depth);

    /**
     * Set Light Beam Transmission PerCentage from a float
     * @param  latitude
     */
    void setLatitude(Double latitude);

    /**
     * Set Oxygen from a float
     * @param  light
     */
    void setLight(Float light);

    /**
     * Set the date that the physical data was logged.
     * @param logDate
     */
    void setLogDate(Date logDate);

    /**
     * Set Decimal Latitude from a Float
     * @param  longitude
     */
    void setLongitude(Double longitude);

    /**
     * Set Salinity from a float
     * @param  oxygen
     */
    void setOxygen(Float oxygen);

    /**
     * Set Temperature from a float
     * @param  salinity
     */
    void setSalinity(Float salinity);

    /**
     * Set Depth from a float
     * @param  temperature
     */
    void setTemperature(Float temperature);
}
