
package w3c.dom.svg;

import w3c.dom.events.EventTarget;

public interface SVGAElement extends 
               SVGElement,
               SVGURIReference,
               SVGTests,
               SVGLangSpace,
               SVGExternalResourcesRequired,
               SVGStylable,
               SVGTransformable,
               EventTarget {
  public SVGAnimatedString getTarget( );
}
