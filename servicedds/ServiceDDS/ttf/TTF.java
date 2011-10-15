/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.ttf;

import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

/**
 * A Topic Transformation Function brings Complex Event Processing concepts to ServiceDDS
 * The DDS global data space changes when a new data sample is written. This is what we
 * consider to be a simple event. Complex events are said to be inferred from multiple
 * simple events, and that is what a TTF represents. Concretely, a TTF defines a list (array)
 * of input service topics for simple event detection, a list of output service topics 
 * for complex event generation and a transformation function that makes the inference process
 * and is executed when all the simple events are detected. This function generate the samples
 * that will be written in the output service topics automatically. 
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class TTF implements ServiceTopicListener {

	/**
	 * The array of input service-topics fo simple event detection
	 */
    ServiceTopic[] inputServiceTopics;
    /**
     * The array of output service-topics form complex event generation
     */
    ServiceTopic[] outputServiceTopics;

    private boolean[] readyList;
    /**
     * The transformation function. It will be called when an on_data_available event
     * is received for each input topic (at least one event for each one).
     */
    TransformationFunction tf;
    /**
     * A condition for the execution of the TF have to be defined (even if it is always true)
     */
    Condition condition;

    private Object[] inputData;
    private Object[] outputData;

    /**
     * Creates a TTF with default qos for input topic readers and output topic writers
     * @param inputTopicTypes
     * @param ig
     * @param ip
     * @param outputTopicTypes
     * @param og
     * @param op
     * @param c
     * @param tf
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
//    public TTF(Object[] inputTopicTypes,
//               Group ig,
//               String ip,
//               Object[] outputTopicTypes,
//               Group og,
//               String op,
//               Condition c,
//               TransformationFunction tf) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//
//
//        this(inputTopicTypes,new QoSParameters[inputTopicTypes.length],ig,ip,
//             outputTopicTypes,new QoSParameters[outputTopicTypes.length],og,op,
//             c,tf);
//
//    }

    /**
     * Creates a TTF with default qos for output topic writers
     * @param inputTopicTypes
     * @param inputTopicReadersQoS
     * @param ig
     * @param ip
     * @param outputTopicTypes
     * @param og
     * @param op
     * @param c
     * @param tf
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
//    public TTF(Object[] inputTopicTypes,
//               QoSParameters[] inputTopicReadersQoS,
//               Group ig,
//               String ip,
//               Object[] outputTopicTypes,
//               Group og,
//               String op,
//               Condition c,
//               TransformationFunction tf) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//
//        this(inputTopicTypes,inputTopicReadersQoS,ig,ip,
//             outputTopicTypes,new QoSParameters[outputTopicTypes.length],og,op,
//             c,tf);
//
//    }

    /**
     * Creates a TTF with default qos for input topic readers
     * @param inputTopicTypes
     * @param ig
     * @param ip
     * @param outputTopicTypes
     * @param outputTopicWritersQoS
     * @param og
     * @param op
     * @param c
     * @param tf
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
//    public TTF(Object[] inputTopicTypes,
//               Group ig,
//               String ip,
//               Object[] outputTopicTypes,
//               QoSParameters[] outputTopicWritersQoS,
//               Group og,
//               String op,
//               Condition c,
//               TransformationFunction tf) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//
//        this(inputTopicTypes,new QoSParameters[inputTopicTypes.length],ig,ip,
//             outputTopicTypes,outputTopicWritersQoS,og,op,
//             c,tf);
//        
//    }

    /**
     * The basic constructor.
     * @param inputServiceTopics The set of input topics represented by service-topics
     * @param outputServiceTopics The set of output topics represented by service-topics
     * @param c The condition to apply the Transformation Function
     * @param tf The Transformation Function
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public TTF(ServiceTopic[] inputServiceTopics,
               ServiceTopic[] outputServiceTopics,
               Condition c,
               TransformationFunction tf) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Set condition
        this.condition = c;
        // Set transformation function
        this.tf = tf;
//        // Set group and partition
//        this.inputGroup = ig;
//        this.outputGroup = og;
//        this.inputPartition = ip;
//        this.outputPartition = op;
        // Add itself as listener
        if (inputServiceTopics != null) {
        	this.inputServiceTopics = inputServiceTopics;
            this.inputData = new Object[inputServiceTopics.length];
            this.readyList = new boolean[inputServiceTopics.length];
            for (int i=0; i<inputServiceTopics.length; i++) {
                this.inputServiceTopics[i].addListener(this);
                this.inputData[i] = this.inputServiceTopics[i].topicDataType;
                this.readyList[i] = false;
            }
        }
        // Create output data
        if (outputServiceTopics != null) {
            this.outputServiceTopics = outputServiceTopics;
            this.outputData = new Object[outputServiceTopics.length];
        }
    }

    /**
     * When every data is available and the condition evaluates to true, the
     * transformation function is executed using inputs and outputs, and the
     * resulting output objects are written into topics.
     */
    public void on_data_available(ServiceTopic serviceTopic) {
//      System.out.println("TTF.on_data_available");
        // Add to ready topics list
        newInput(serviceTopic);
        // If every input topic is ready, test condition and take action
        if (inputComplete() 
                && (
                    ((this.condition != null) && condition.eval(inputData)) 
                    || (this.condition == null)
                )
           ) {
//          System.out.println("TFF.on_data_available: entrada completa...");
            this.outputData = this.tf.transform(this.inputData);
            writeOutput();
        }
    }

    private boolean inputComplete() {
        boolean ready=true;
        for (int i=0; (ready && i<this.readyList.length); i++) {
            ready = ready && (this.readyList[i]);
        }
        return ready;
    }

    private void newInput(ServiceTopic serviceTopic) {
        Object[] data = serviceTopic.take(); // TAKE or READ???
        boolean found = false;
        for (int i=0; (i<data.length && i<1); i++) { // Takes only one
            for (int j=0; (!found && j<this.inputData.length); j++) { //Look for data type index in input
//              System.out.println("TTF.newInput: looking for data type position for new data "
//                      +data[i].getClass()+" and found in array data "+this.inputData[j].getClass());
                if (this.inputData[j].getClass().isInstance(data[i])) {
                    found = true;
                    this.inputData[j] = data[i]; // Takes the last data
                    this.readyList[j] = true;
                  //System.out.println("TTF.newInput: position found!");
                }
            }
        }
    }

    private void writeOutput() {
//      System.out.println("TTF.writeOutput: writing "+this.outputData.length+" data elements...");
        for (int i=0; i<this.outputServiceTopics.length; i++) {
//          System.out.println("TTF.writeOutput(1): writing data "+i+" of type "
//                      +this.outputData[i]
//                      +" into service topic...");
            if (this.outputData[i] != null) {
//              System.out.println("TTF.writeOutput(2): writing data of type "
//                      +this.outputData[i].getClass()
//                      +" into service topic...");
                this.outputServiceTopics[i].write(this.outputData[i]);
            }
        }
    }

	public void on_requested_deadline_missed(ServiceTopic serviceTopic) {
		// TODO Auto-generated method stub
		
	}

	public void on_sample_lost(ServiceTopic serviceTopic) {
		// TODO Auto-generated method stub
		
	}
    
}
