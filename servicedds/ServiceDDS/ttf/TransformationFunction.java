/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.ttf;

/**
 * The abstract class that must be extended in order to define Transformation Functions
 * to be used with TTFs.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public abstract class TransformationFunction {

	/**
	 * Generates an output array using the input array. That is, applies a transformation.
	 * Input should be kept unmodified.
	 * @param input The input to apply the transformation
	 * @return The transformed output
	 */
    public abstract Object[] transform(Object[] input);

}
