
package w3c.dom.svg;

import w3c.dom.DOMException;
public interface SVGAnimatedEnumeration {
  public short getBaseVal( );
  public void           setBaseVal( short baseVal )
                       throws DOMException;
  public short getAnimVal( );
}
