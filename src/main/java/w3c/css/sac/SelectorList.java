/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: SelectorList.java 477010 2006-11-20 02:54:38Z mrglavas $
 */
package w3c.css.sac;

/**
 * The SelectorList interface provides the abstraction of an ordered collection
 * of selectors, without defining or constraining how this collection is
 * implemented.
 *
 * @version $Revision: 477010 $
 * @author Philippe Le Hegaret
 */
public interface SelectorList {

    /**
     * Returns the length of this selector list
     */    
    public int getLength();

    /**
     * Returns the selector at the specified index, or <code>null</code> if this
     * is not a valid index.  
     */
    public Selector item(int index);
}

