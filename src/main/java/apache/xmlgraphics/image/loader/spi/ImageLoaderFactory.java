/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package apache.xmlgraphics.image.loader.spi;

import apache.xmlgraphics.image.loader.ImageFlavor;
import apache.xmlgraphics.image.loader.ImageInfo;

/**
 * This interface is implemented to provide information about an ImageLoader and to create new
 * instances. A separate factory allows implementation to dynamically detect if the underlying
 * libraries are available in the classpath so the caller can skip this implementation if it's
 * not functional.
 */
public interface ImageLoaderFactory {

    /**
     * Returns an array of MIME types supported by this implementation.
     * @return the MIME type array
     */
    String[] getSupportedMIMETypes();

    /**
     * Returns an array of ImageFlavors that are supported by this implementation for a given
     * MIME type.
     * @param mime the MIME type
     * @return the ImageFlavor array
     */
    ImageFlavor[] getSupportedFlavors(String mime);

    /**
     * Indicates whether the given image (represented by an {@link ImageInfo} object) is supported
     * by the loader. By default, implementations return true assuming all images of the supported
     * MIME types can be processed correctly. In some cases, however, an ImageLoader may only
     * support a subset of a format because it offers an optimized way to embed the image in
     * the target format (for example: CCITT compressed TIFF files in PDF and PostScript). For
     * this to work, the preloader must register some information in the ImageInfo's custom
     * objects so the factory can identify if an image may or may not be supported.
     * @param imageInfo the image info object
     * @return true if the image is supported by the loaders generated by this factory
     */
    boolean isSupported(ImageInfo imageInfo);

    /**
     * Creates and returns a new ImageLoader instance.
     * @param targetFlavor the target image flavor to produce
     * @return a new ImageLoader instance
     */
    ImageLoader newImageLoader(ImageFlavor targetFlavor);

    /**
     * Returns the usage penalty for a particular ImageLoader. This is used to select the best
     * ImageLoader implementation for loading an image.
     * @param mime the MIME type
     * @param flavor the target image flavor
     * @return the usage penalty (must be a non-negative integer)
     * @deprecated Redundancy with {@link ImageLoader#getUsagePenalty()}
     */
    int getUsagePenalty(String mime, ImageFlavor flavor);

    /**
     * Indicates whether the underlying libraries needed by the implementation are available.
     * @return true if the implementation is functional.
     */
    boolean isAvailable();

}
