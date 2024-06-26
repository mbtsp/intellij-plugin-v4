
package w3c.dom.svg;

import w3c.dom.events.EventTarget;

public interface SVGEllipseElement extends 
               SVGElement,
               SVGTests,
               SVGLangSpace,
               SVGExternalResourcesRequired,
               SVGStylable,
               SVGTransformable,
               EventTarget {
  public SVGAnimatedLength getCx( );
  public SVGAnimatedLength getCy( );
  public SVGAnimatedLength getRx( );
  public SVGAnimatedLength getRy( );
}
