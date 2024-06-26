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

package apache.xmlgraphics.ps.dsc;

import java.io.IOException;

import apache.xmlgraphics.ps.dsc.events.DSCEvent;

/**
 * {@code DSCListener} implementation that filters certain DSC events.
 */
public class FilteringEventListener implements DSCListener {

    private DSCFilter filter;

    /**
     * Main constructor.
     * @param filter the filter
     */
    public FilteringEventListener(DSCFilter filter) {
        this.filter = filter;
    }

    /** {@inheritDoc} */
    public void processEvent(DSCEvent event, DSCParser parser) throws IOException, DSCException {
        if (!filter.accept(event)) {
            parser.next(); //skip
        }
    }

}
