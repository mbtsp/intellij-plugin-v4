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
package apache.xmlgraphics.xmp.schemas.pdf;

import apache.xmlgraphics.xmp.Metadata;
import apache.xmlgraphics.xmp.XMPConstants;
import apache.xmlgraphics.xmp.XMPSchema;

public class XAPMMXMPSchema extends XMPSchema {
    public static final String NAMESPACE = XMPConstants.XAP_MM_NAMESPACE;

    /** Creates a new schema instance for Dublin Core. */
    public XAPMMXMPSchema() {
        super(NAMESPACE, "xmpMM");
    }

    /**
     * Creates and returns an adapter for this schema around the given metadata object.
     *
     * @param meta the metadata object
     * @return the newly instantiated adapter
     */
    public static XAPMMAdapter getAdapter(Metadata meta) {
        return new XAPMMAdapter(meta, NAMESPACE);
    }
}
