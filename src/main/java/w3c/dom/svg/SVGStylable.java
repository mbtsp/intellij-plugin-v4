
package w3c.dom.svg;

import w3c.dom.css.CSSStyleDeclaration;
import w3c.dom.css.CSSValue;

public interface SVGStylable {
  public SVGAnimatedString getClassName( );
  public CSSStyleDeclaration getStyle( );

  public CSSValue getPresentationAttribute ( String name );
}
