/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS;

/**
 * A ServiceDDS Group represents a DDS partition with control access and some additional information.
 * In order to interact, two Peer instances must have at least a Group in common.
 * @author JA Dianes
 * @version 0.1b, 09/24/2010
 */
public class Group {

	/***
	 * The name of the Group
	 */
    String name;

    /***
     * Creates a Group with a given name.
     * @param name The name of the Group
     */
    public Group(String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of the Group
     * @return The name of the Group
     */
    public String getName() {
        return name;
    }
    
}
