/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS;

import DDS.Duration_t;
import DDS.HistoryQosPolicyKind;

/**
 * The QoSParameters class purpose is to "objetize" the management of the DDS quality of service parameters.
 * Currently it only supports a subset of these parameters.
 * @author Jose Angel Dianes (jadianes@gmail.com)
 * @version 0.1b, 24/09/2010
 */
public class QoSParameters {

    public Duration_t deadline;
    public HistoryQosPolicyKind history;
    public int keep;
    
    public final static DDS.HistoryQosPolicyKind KEEP_LAST_HISTORY_QOS = DDS.HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS;

    public QoSParameters() {

    }
    
    /**
     * Creates a Duration object that will be used to set Deadline DDS parameter
     * @param sec a number of seconds for the deadline
     * @param nsec a number of nseconds for the deadline
     */
    public void setDeadline(int sec, int nsec) {
        this.deadline = new Duration_t(sec,nsec);
    }
        
    /**
     * Used to set the KIND and KEEP_LAST values of the HISTORY DDS parameter
     * @param kind The policy kind. It uses the same values that DDS.
     * @param n The number of old samples to be stored
     */
    public void setHistory(HistoryQosPolicyKind kind, int n) {
    	this.history = kind;
    	this.keep = n;
    }

}
