
package w3c.dom.svg;

import w3c.dom.DOMException;
import w3c.dom.events.EventTarget;
import w3c.dom.smil.ElementTimeControl;

public interface SVGAnimationElement extends 
               SVGElement,
               SVGTests,
               SVGExternalResourcesRequired,
        ElementTimeControl,
               EventTarget {
  public SVGElement getTargetElement( );

  public float getStartTime (  );
  public float getCurrentTime (  );
  public float getSimpleDuration (  )
                  throws DOMException;
}
