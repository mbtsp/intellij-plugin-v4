
package w3c.dom.svg;

import w3c.dom.DOMException;
public interface SVGICCColor {
  public String      getColorProfile( );
  public void      setColorProfile( String colorProfile )
                       throws DOMException;
  public SVGNumberList getColors( );
}
