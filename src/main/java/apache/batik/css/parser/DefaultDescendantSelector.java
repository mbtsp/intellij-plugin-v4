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
package apache.batik.css.parser;

import w3c.css.sac.Selector;
import w3c.css.sac.SimpleSelector;

/**
 * This class provides an implementation for the
 * {@link w3c.css.sac.DescendantSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultDescendantSelector extends AbstractDescendantSelector {

    /**
     * Creates a new DefaultDescendantSelector object.
     */
    public DefaultDescendantSelector(Selector ancestor,
                                     SimpleSelector simple) {
        super(ancestor, simple);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * w3c.css.sac.Selector#getSelectorType()}.
     */
    public short getSelectorType() {
        return SAC_DESCENDANT_SELECTOR;
    }

    /**
     * Returns a representation of the selector.
     */
    public String toString() {
        return getAncestorSelector() + " " + getSimpleSelector();
    }
}
