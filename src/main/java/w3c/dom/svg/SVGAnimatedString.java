
package w3c.dom.svg;

import w3c.dom.DOMException;
public interface SVGAnimatedString {
  public String getBaseVal( );
  public void           setBaseVal( String baseVal )
                       throws DOMException;
  public String getAnimVal( );
}
