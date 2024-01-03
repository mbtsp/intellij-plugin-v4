
package w3c.dom.svg;

import w3c.dom.DOMException;

public interface SVGNumber {
  public float getValue( );
  public void           setValue( float value )
                       throws DOMException;
}
