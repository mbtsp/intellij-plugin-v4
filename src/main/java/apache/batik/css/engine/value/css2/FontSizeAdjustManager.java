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
package apache.batik.css.engine.value.css2;

import apache.batik.css.engine.CSSEngine;
import apache.batik.css.engine.value.AbstractValueManager;
import apache.batik.css.engine.value.FloatValue;
import apache.batik.css.engine.value.Value;
import apache.batik.css.engine.value.ValueConstants;
import apache.batik.css.engine.value.ValueManager;
import apache.batik.util.CSSConstants;
import apache.batik.util.SVGTypes;

import w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the 'font-size-adjust' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontSizeAdjustManager extends AbstractValueManager {

    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
        return true;
    }

    /**
     * Implements {@link ValueManager#isAnimatableProperty()}.
     */
    public boolean isAnimatableProperty() {
        return true;
    }

    /**
     * Implements {@link ValueManager#isAdditiveProperty()}.
     */
    public boolean isAdditiveProperty() {
        return false;
    }

    /**
     * Implements {@link ValueManager#getPropertyType()}.
     */
    public int getPropertyType() {
        return SVGTypes.TYPE_FONT_SIZE_ADJUST_VALUE;
    }

    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
        return CSSConstants.CSS_FONT_SIZE_ADJUST_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return ValueConstants.NONE_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_INHERIT:
            return ValueConstants.INHERIT_VALUE;

        case LexicalUnit.SAC_INTEGER:
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getIntegerValue());

        case LexicalUnit.SAC_REAL:
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getFloatValue());

        case LexicalUnit.SAC_IDENT:
            if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_NONE_VALUE)) {
                return ValueConstants.NONE_VALUE;
            }
            throw createInvalidIdentifierDOMException(lu.getStringValue());
        }
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value, CSSEngine engine)
        throws DOMException {
        if (type != CSSPrimitiveValue.CSS_IDENT) {
            throw createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase(CSSConstants.CSS_NONE_VALUE)) {
            return ValueConstants.NONE_VALUE;
        }
        throw createInvalidIdentifierDOMException(value);
    }

    /**
     * Implements {@link ValueManager#createFloatValue(short,float)}.
     */
    public Value createFloatValue(short type, float floatValue)
        throws DOMException {
        if (type == CSSPrimitiveValue.CSS_NUMBER) {
            return new FloatValue(type, floatValue);
        }
        throw createInvalidFloatTypeDOMException(type);
    }
}
