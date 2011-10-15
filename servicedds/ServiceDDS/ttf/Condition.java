/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.ttf;

/**
 * The abstract class that must be extended in order to define Conditions
 * to be used with TTFs.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public abstract class Condition {

	/**
	 * The condition that will be checked
	 * @param args The input for the evaluation 
	 * @return The result of the evaluation
	 */
    public abstract boolean eval(Object[] args);
    
}
