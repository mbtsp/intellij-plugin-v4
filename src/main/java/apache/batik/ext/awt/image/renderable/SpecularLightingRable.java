/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

import apache.batik.ext.awt.image.Light;

/**
 * This filter follows the specification of the feSpecularLighting filter in
 * the SVG 1.0 specification.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface SpecularLightingRable extends FilterColorInterpolation {
    /**
     * Returns the source to be filtered
     */
    Filter getSource();

    /**
     * Sets the source to be filtered
     */
    void setSource(Filter src);

    /**
     * @return Light object used for the diffuse lighting
     */
    Light getLight();

    /**
     * @param light New Light object
     */
    void setLight(Light light);

    /**
     * @return surfaceScale
     */
    double getSurfaceScale();

    /**
     * Sets the surface scale
     */
    void setSurfaceScale(double surfaceScale);

    /**
     * @return specular constant, or ks.
     */
    double getKs();

    /**
     * Sets the specular constant, or ks
     */
    void setKs(double ks);

    /**
     * @return specular exponent, or kd
     */
    double getSpecularExponent();

    /**
     * Sets the specular exponent
     */
    void setSpecularExponent(double specularExponent);

    /**
     * @return the litRegion for this filter
     */
    Rectangle2D getLitRegion();

    /**
     * Sets the litRegion for this filter
     */
    void setLitRegion(Rectangle2D litRegion);

    /**
     * Returns the min [dx,dy] distance in user space for evalutation of
     * the sobel gradient.
     */
    double [] getKernelUnitLength();

    /**
     * Sets the min [dx,dy] distance in user space for evaluation of the
     * sobel gradient. If set to zero or null then device space will be used.
     */
    void setKernelUnitLength(double [] kernelUnitLength);
}

