/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.awt;

import com.google.common.collect.ImmutableMap;
import java.awt.RenderingHints;
import java.util.Map;

/**
 *
 * @author brian
 */
public class AWTUtilities {
    
    public static final Map<String, Object> IMAGE_INTERPOLATION_MAP = ImmutableMap.of(
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.toString(), RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR.toString(), RenderingHints.VALUE_INTERPOLATION_BILINEAR,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC.toString(), RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    
}
