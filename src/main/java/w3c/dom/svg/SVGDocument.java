
package w3c.dom.svg;

import w3c.dom.Document;
import w3c.dom.events.DocumentEvent;

public interface SVGDocument extends 
               Document,
               DocumentEvent {
  public String    getTitle( );
  public String     getReferrer( );
  public String      getDomain( );
  public String      getURL( );
  public SVGSVGElement getRootElement( );
}
