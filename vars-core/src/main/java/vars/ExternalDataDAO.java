/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.util.Date;
import java.util.List;

/**
 * Interface for linking into data that is needed by VARS but which exists
 * outside of the VARS database. This would normally be EXPD data (position,
 * salinity, temperature, etc.)
 * 
 * @author brian
 */
public interface ExternalDataDAO {

    IVideoMoment interpolateTimecodeByDate(String cameraIdentifier, Date date, int millisecTolerance, double frameRate);

    List<IVideoMoment> findTimecodesNearDate(String platform, Date date, int millisecTolerance);

    IVideoMoment findTimecodeNearDate(String platform, Date date, int millisecTolerance);

}
