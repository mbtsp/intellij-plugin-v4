
package w3c.dom.svg;

import w3c.dom.DOMException;

public interface SVGPathSegLinetoVerticalAbs extends 
               SVGPathSeg {
  public float   getY( );
  public void      setY( float y )
                       throws DOMException;
}
