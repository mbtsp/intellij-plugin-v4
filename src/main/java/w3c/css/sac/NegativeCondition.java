/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: NegativeCondition.java 477010 2006-11-20 02:54:38Z mrglavas $
 */
package w3c.css.sac;

/**
 * @version $Revision: 477010 $
 * @author  Philippe Le Hegaret
 * @see Condition#SAC_NEGATIVE_CONDITION
 */
public interface NegativeCondition extends Condition {

    /**
     * Returns the condition.
     */    
    public Condition getCondition();
}
