/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.servicetopic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import DDS.ALIVE_INSTANCE_STATE;
import DDS.ANY_SAMPLE_STATE;
import DDS.ANY_STATUS;
import DDS.ANY_VIEW_STATE;
import DDS.ConditionSeqHolder;
import DDS.ContentFilteredTopic;
import DDS.DATAWRITER_QOS_USE_TOPIC_QOS;
import DDS.DATA_AVAILABLE_STATUS;
import DDS.DataReader;
import DDS.DataReaderListener;
import DDS.DataReaderQosHolder;
import DDS.DataWriter;
import DDS.DataWriterQosHolder;
import DDS.DomainParticipant;
import DDS.Duration_t;
import DDS.LENGTH_UNLIMITED;
import DDS.LivelinessChangedStatus;
import DDS.Publisher;
import DDS.REQUESTED_DEADLINE_MISSED_STATUS;
import DDS.ReadCondition;
import DDS.RequestedDeadlineMissedStatus;
import DDS.RequestedIncompatibleQosStatus;
import DDS.SampleInfoSeqHolder;
import DDS.SampleLostStatus;
import DDS.SampleRejectedStatus;
import DDS.Subscriber;
import DDS.SubscriptionMatchedStatus;
import DDS.Topic;
import DDS.TopicQosHolder;
import DDS.TypeSupport;
import DDS.WaitSet;
import ServiceDDS.ErrorHandler;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;

/**
 * Service-topics simplify DDS topics use and allow them to be discovered using properties
 * or meta-information. Use simplifications include wrapping the IDL-Java binding that is
 * inherited from the C language and is not complaint with Java conventions and language features.
 * Additionally, service-topics create default readers and writers, quality of service and type support
 * objects. The user only have to provide the class that represents the topic data type, an unique ID
 * and QoS parameters. If a topic instance already exists in the global data space with this
 * three parameters, a service-topic is only a handle and a container for readers and writers
 * associated with the topic instance.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public abstract class ServiceTopic implements DataReaderListener {

    /* Generic DDS entities */
    protected Topic topic;
    /**
     * The unique ID of the topic instance
     */
    public String topicID;
    /**
     * The topic data type
     */
    public Object topicDataType;
    protected TypeSupport topicTS;
    protected DomainParticipant participant;
    protected Publisher publisher;
    protected Subscriber subscriber;
    protected DataWriter defaultTopicDataWriter;
    protected DataReader defaultTopicDataReader;
    protected LinkedList<ServiceTopicListener> listenerList = new LinkedList<ServiceTopicListener>();

    /* Reflection params */
    protected String topicTypeSupportClassName;
    protected String topicTypeNameTS;
    protected String topicDataWriterClassName;
    protected String topicSeqHolderClassName;
    protected String topicSeqInfoClassName;
    protected Class writerClassDefinition;
    protected Method defaultTopicDataWriter_register_instance;
    protected Method defaultTopicDataWriter_write;
    protected Method defaultTopicDataReader_take;
    protected Method defaultTopicDataReader_return_loan;
	protected Method defaultTopicDataReader_read;

    protected String topicDataReaderClassName;
    protected Class readerClassDefinition;
    protected int status;

    // EntityQos holders
    protected TopicQosHolder defaultTopicQos = new TopicQosHolder();
    protected DataReaderQosHolder defaultReaderQos = new DataReaderQosHolder();
    protected DataWriterQosHolder defaultWriterQos = new DataWriterQosHolder();


    /**
     * Instantiate a service-topic and all the associated DDS entities. If the topic instance
     * does not exists, is created.
     * @param topicData The topic data type
     * @param name The ID
     * @param participant The DDS participant
     * @param publisher The DDS publisher
     * @param subscriber The DDS subscriber
     * @param topicQos The DDS topic QoS
     * @param readerQos The default topic data reader QoS
     * @param writerQos The default topic data writer QoS
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServiceTopic(Object topicData, String name,
    		DomainParticipant participant, Publisher publisher, Subscriber subscriber,
            QoSParameters topicQos, QoSParameters readerQos, QoSParameters writerQos)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {

//      System.out.println("ServiceTopic: creating service topic "+name+" with data type "+topicData);
        this.topicDataType = topicData;
        this.topicID = name;
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.participant = participant;
        // Set class names for reflection
        this.topicTypeSupportClassName = this.topicDataType.getClass().getName()+"TypeSupport";
//      System.out.println("ServiceTopic: "+this.topicTypeSupportClassName);
        this.topicSeqHolderClassName = this.topicDataType.getClass().getName()+"SeqHolder";
//      System.out.println("ServiceTopic: "+this.topicSeqHolderClassName);
        this.topicDataWriterClassName = this.topicDataType.getClass().getName()+"DataWriter";
//      System.out.println("ServiceTopic: "+this.topicDataWriterClassName);
        this.topicDataReaderClassName = this.topicDataType.getClass().getName()+"DataReader";
//      System.out.println("ServiceTopic: "+this.topicDataReaderClassName);


        // Register the required datatype for the topic
        Class tsClassDefinition = Class.forName(this.topicTypeSupportClassName);
        this.topicTS = (TypeSupport) tsClassDefinition.newInstance();
        ErrorHandler.checkHandle(topicTS, "new TypeSupport");
        this.topicTypeNameTS = this.topicTS.get_type_name();
//      System.out.println("ServiceTopic: "+this.topicTypeNameTS);
        this.status = this.topicTS.register_type(
                this.participant, this.topicTypeNameTS);
        ErrorHandler.checkStatus(
            this.status, "ServiceDDS.ServiceTopic.TypeSupport.register_type");

        // Get the default topic QoS
        this.status = this.participant.get_default_topic_qos(this.defaultTopicQos);
        ErrorHandler.checkStatus(status, "ServiceDDS: getting topic qos for topic "+this.topicDataType);
        // Set user desired QoS for default reader and writer
        if (topicQos != null) {
            if (topicQos.deadline != null) {
            	this.defaultTopicQos.value.deadline = new DDS.DeadlineQosPolicy(topicQos.deadline);
            }
            if (topicQos.history != null) {
            	this.defaultTopicQos.value.history = new DDS.HistoryQosPolicy(topicQos.history, topicQos.keep);
            }
        }
               
        // Create the topic
        this.topic = this.participant.create_topic(
            this.topicID,//group.getName()+"_"+this.topicDataType.getClass().getSimpleName(),
            this.topicTypeNameTS,
            this.defaultTopicQos.value,
            null,
            ANY_STATUS.value);
        if (this.topic == null) throw new ImpossibleToCreateDDSTopic(topicTypeNameTS);
//        ErrorHandler.checkHandle(
//            this.topic,
//            "ServiceDDS.ServiceTopic.DomainParticipant.create_topic ("+topicTypeNameTS+")");        
    }

    /**
     * Instantiate a service-topic and all the associated DDS entities. If the topic instance
     * does not exists, is created.
     * @param topicData The topic data type
     * @param name The ID
     * @param participant The DDS participant
     * @param publisher The DDS publisher
     * @param subscriber The DDS subscriber
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws ImpossibleToCreateDDSTopic 
     */
    public ServiceTopic(Object topicData, String name, 
    		DomainParticipant participant, Publisher publisher, Subscriber subscriber)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
        this(topicData, name, participant, publisher, subscriber, null, null);
    }

    /**
     * Instantiate a service-topic and all the associated DDS entities. If the topic instance
     * does not exists, is created.
     * @param topicData The topic data type
     * @param name The ID
     * @param participant The DDS participant
     * @param publisher The DDS publisher
     * @param subscriber The DDS subscriber
     * @param topicQos The DDS topic QoS
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServiceTopic(Object topicData, String name, 
    		DomainParticipant participant, Publisher publisher, Subscriber subscriber,
    		QoSParameters topicQos)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
        this(topicData, name, participant, publisher, subscriber, topicQos, null, null);
    }
    
    /**
     * Instantiate a service-topic and all the associated DDS entities. If the topic instance
     * does not exists, is created.
     * @param topicData The topic data type
     * @param name The ID
     * @param participant The DDS participant
     * @param publisher The DDS publisher
     * @param subscriber The DDS subscriber
     * @param readerQos The default topic data reader QoS
     * @param writerQos The default topic data writer QoS
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServiceTopic(Object topicData, String name, 
    		DomainParticipant participant, Publisher publisher, Subscriber subscriber,
    		QoSParameters readerQos, QoSParameters writerQos)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
        this(topicData, name, participant, publisher, subscriber, null, readerQos, writerQos);
    }
    
    
    /**
     * Writes data into the topic using the default writer inside the service-topic
     * @param topicData The sample to be written
     */
    public void write(Object topicData) {
        try {
        	long userHandle= ((Long) this.defaultTopicDataWriter_register_instance.invoke(
                     this.defaultTopicDataWriter, topicData
                 )).longValue();
            // Write a message using the pre-generated instance handle
            this.status = 
                ((Integer)this.defaultTopicDataWriter_write.invoke(
                    this.defaultTopicDataWriter,
                    topicData, 
                    userHandle
                )).intValue();
//          System.out.println("ServiceTopic.write: data "+topicData+" written in topic "+this.topicID+" of type "+this.topicDataType.getClass());
            ErrorHandler.checkStatus(status, "ServiceDDS.ServiceTopic.DefaultTopicDataWriter.write: "+topicData);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * TODO: Creates and returns a DataWriter with the specifies QoS if FEASIBLE
     * @return A Data Writer
     */
    public DataWriter getWriter() {
        return null;
    }

    /**
     * Adds a listener to the topic
     * @param listener
     */
    public void addListener(ServiceTopicListener listener) {
        this.listenerList.add(listener);
        // Install the listener itself
        status = this.defaultTopicDataReader.set_listener(this, 
                REQUESTED_DEADLINE_MISSED_STATUS.value
              | DATA_AVAILABLE_STATUS.value);
        ErrorHandler.checkStatus(
            status, "ServiceDDS.ServiceTopic.DefaultTopicDataReader.addListener");

    }

    /**
     * TODO
     * @return The sample read
     */
    public Object[] read() {
        Object[] res = null;
        try {
            Object msgSeq = Class.forName(this.topicSeqHolderClassName).newInstance();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

            this.status = 
                ((Integer)this.defaultTopicDataReader_read.invoke(
                    this.defaultTopicDataReader,
                    msgSeq, 
                    infoSeq, 
                    LENGTH_UNLIMITED.value, 
                    ANY_SAMPLE_STATE.value, 
                    ANY_VIEW_STATE.value, 
                    ALIVE_INSTANCE_STATE.value
                )).intValue();
            ErrorHandler.checkStatus(status, "ServiceDDS.ServiceTopic.DefaultTopicDataReader.read");
            res = (Object[]) msgSeq.getClass().getField("value").get(msgSeq);
//          System.out.println("ServiceTopic.take: obtained "+res.length+" samples");
 
            this.status = 
                ((Integer)this.defaultTopicDataReader_return_loan.invoke(
                    this.defaultTopicDataReader,
                    msgSeq, 
                    infoSeq
                )).intValue();
            ErrorHandler.checkStatus(status, "DDSLWS.ServiceTemplateDataReader.DefaultTopicDataReader.return_loan");

        } catch (NoSuchFieldException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        }

        return res;
    }


    /**
     * Take a sample (if available) from the topic handled by this service-topic
     * @return A sample or null if there are no samples available
     */
    public Object[] take() {
        Object[] res = null;
        try {
            Object msgSeq = Class.forName(this.topicSeqHolderClassName).newInstance();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

            this.status = 
                ((Integer)this.defaultTopicDataReader_take.invoke(
                    this.defaultTopicDataReader,
                    msgSeq, 
                    infoSeq, 
                    LENGTH_UNLIMITED.value, 
                    ANY_SAMPLE_STATE.value, 
                    ANY_VIEW_STATE.value, 
                    ALIVE_INSTANCE_STATE.value
                )).intValue();
            ErrorHandler.checkStatus(status, "ServiceDDS.ServiceTopic.DefaultTopicDataReader.take");
            res = (Object[]) msgSeq.getClass().getField("value").get(msgSeq);
//          System.out.println("ServiceTopic.take: obtained "+res.length+" samples");

            this.status = 
                ((Integer)this.defaultTopicDataReader_return_loan.invoke(
                    this.defaultTopicDataReader,
                    msgSeq, 
                    infoSeq
                )).intValue();
            ErrorHandler.checkStatus(status, "DDSLWS.ServiceTemplateDataReader.DefaultTopicDataReader.return_loan");
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /**
     * Waits until new data is available
     */
    public void waitData(QoSParameters qos) {
        WaitSet waitSet = new WaitSet();
        ReadCondition readerCondition =
                this.defaultTopicDataReader.create_readcondition(
                    DDS.NOT_READ_SAMPLE_STATE.value,
                    DDS.NEW_VIEW_STATE.value,
                    DDS.ALIVE_INSTANCE_STATE.value);
        waitSet.attach_condition(readerCondition);

        // Set QoS
        if (qos!=null) {
	        ConditionSeqHolder csh = new ConditionSeqHolder();
	        Duration_t duration = qos.deadline;
	        waitSet._wait(csh, duration);
        } else { // Throw proper exception
        	System.err.println("ServiceTopic.waitData: no deadline defined for waiting data in service topic "+this.topicID);
        }
    }

    @Override
    protected void finalize() throws Throwable {

        // Remove the DataWriter
        status = publisher.delete_datawriter(this.defaultTopicDataWriter);
        ErrorHandler.checkStatus(
            status, "DDS.Subscriber.delete_datawriter");


       // Remove the DataReader
        status = this.subscriber.delete_datareader(this.defaultTopicDataReader);
        ErrorHandler.checkStatus(
            status, "DDS.Subscriber.delete_datareader");

        // Remove the Topics
        status = participant.delete_topic(this.topic);
        ErrorHandler.checkStatus(
            status, "DDS.DomainParticipant.delete_topic (Topic)");

      super.finalize(); //not necessary if extending Object.

    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
//      System.out.println("ServiceTopic.on_requested_deadline_missed(): in topic "+this.topicDataType);
      Iterator<ServiceTopicListener> it = this.listenerList.iterator();
      while (it.hasNext()) {
          ServiceTopicListener newListener = it.next();
          newListener.on_requested_deadline_missed(this);
      }
    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_data_available(DataReader arg0) {
//      System.out.println("ServiceTopic.on_data_available(): new sample available for topic "+this.topicDataType);
        Iterator<ServiceTopicListener> it = this.listenerList.iterator();
        while (it.hasNext()) {
            ServiceTopicListener newListener = it.next();
            newListener.on_data_available(this);
        }
    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Event handler for the DDS topic event
     */
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
//      System.out.println("ServiceTopic.on_sample_lost(): sample lost for topic "+this.topicDataType);
        Iterator<ServiceTopicListener> it = this.listenerList.iterator();
        while (it.hasNext()) {
            ServiceTopicListener newListener = it.next();
            newListener.on_sample_lost(this);
        }
    }

    /**
     * Event handler for the DDS topic event
     */
    public boolean isTopicType(Object topicType) {
        return (topicType.getClass().isInstance(this.topicDataType));
    }
    
    protected void createWriter(QoSParameters writerQos) throws ClassNotFoundException {
        // Create a DataWriter for the Topic
        DataWriter parentWriter = this.publisher.create_datawriter(
            this.topic,
            DATAWRITER_QOS_USE_TOPIC_QOS.value,
            null,
            ANY_STATUS.value);
        ErrorHandler.checkHandle(
            parentWriter, "ServiceDDS.ServiceTopic.Publisher.create_datawriter (serviceTemplateTopic)");

        // Narrow the abstract parent into its typed representative
        this.writerClassDefinition = Class.forName(this.topicDataWriterClassName);
        this.defaultTopicDataWriter = (DataWriter) writerClassDefinition.cast(parentWriter);

        // Preload register_instance and write methods for late invocation
        try {
            this.defaultTopicDataWriter_register_instance =
                this.writerClassDefinition.getMethod(
                    "register_instance",
                    new Class[]{this.topicDataType.getClass()}
            );
//          System.out.println("ServiceTopic: topicType="+this.topicDataType.getClass());
            this.defaultTopicDataWriter_write =
                this.writerClassDefinition.getMethod(
                    "write",
                    new Class[] {this.topicDataType.getClass(), long.class}
            );
            
           
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Set user desired QoS for default writer
        if (writerQos != null) {
    		this.status = this.defaultTopicDataWriter.get_qos(this.defaultWriterQos);
    		ErrorHandler.checkStatus(status, "ServiceDDS: getting writer qos for topic "+this.topicDataType);
        	if (writerQos.deadline != null) {
        		this.defaultWriterQos.value.deadline = new DDS.DeadlineQosPolicy(writerQos.deadline);
        	}
 /*       	if (writerQos.history != null) {
        		this.defaultWriterQos.value.history = new DDS.HistoryQosPolicy(writerQos.history, writerQos.keep);
        	}        	*/
            this.status = this.defaultTopicDataWriter.set_qos(this.defaultWriterQos.value);
            ErrorHandler.checkStatus(status, "ServiceDDS: setting writer qos for topic "+this.topicDataType);
        }
    }
    
    void createReader(QoSParameters readerQos) throws ClassNotFoundException {
        // Create a DataReader for the topicDataType (using the default QoS)
        DataReader parentReader = this.subscriber.create_datareader(
            this.topic,
            DDS.DATAREADER_QOS_USE_TOPIC_QOS.value,
            null,
            ANY_STATUS.value);
        ErrorHandler.checkHandle(
            parentReader, "ServiceDDS.ServiceTopic.Subscriber.create_datareader");

        // Narrow the abstract parent into its typed representative
        this.readerClassDefinition = Class.forName(this.topicDataReaderClassName);
        this.defaultTopicDataReader = (DataReader) this.readerClassDefinition.cast(parentReader);

        // Preload take and return_loan methods for late invocation
        try {
            this.defaultTopicDataReader_take =
                this.readerClassDefinition.getMethod(
                    "take",
                    new Class[]{Class.forName(this.topicSeqHolderClassName),
                                SampleInfoSeqHolder.class,
                                int.class,
                                int.class,
                                int.class,
                                int.class}
            );
            this.defaultTopicDataReader_read =
                this.readerClassDefinition.getMethod(
                    "read",
                    new Class[]{Class.forName(this.topicSeqHolderClassName),
                                SampleInfoSeqHolder.class,
                                int.class,
                                int.class,
                                int.class,
                                int.class}
            );
            this.defaultTopicDataReader_return_loan =
                this.readerClassDefinition.getMethod(
                    "return_loan",
                    new Class[] {Class.forName(this.topicSeqHolderClassName),
                                SampleInfoSeqHolder.class}
            );

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        // Set user desired QoS for default reader
        if (readerQos != null) {
    		this.status = this.defaultTopicDataReader.get_qos(this.defaultReaderQos);
    		ErrorHandler.checkStatus(status, "ServiceDDS: getting reader qos for topic "+this.topicDataType);
        	if (readerQos.deadline != null) {
        		this.defaultReaderQos.value.deadline = new DDS.DeadlineQosPolicy(readerQos.deadline);
        	}
/*        	if (readerQos.history != null) {
        		this.defaultReaderQos.value.history = new DDS.HistoryQosPolicy(readerQos.history, readerQos.keep);
        	}     **/   	
    		this.status = this.defaultTopicDataReader.set_qos(this.defaultReaderQos.value);
            ErrorHandler.checkStatus(status, "ServiceDDS: setting reader qos for topic "+this.topicDataType);
        }
        
    }
    
	protected ContentFilteredTopic createContentFilteredTopic(
			String name, String expression, String[] args) {
        // Create the topic
//		System.out.println("ServiceTopic.createContentFilteredTopic: " +
//				"name="+name+
//				" topic="+this.topic+
//				" expression="+expression+
//				" args="+args);
        ContentFilteredTopic filteredTopic = this.participant.create_contentfilteredtopic(
        		name, 
        		this.topic,
        		expression,
        		args);
        //if (filteredTopic == null) throw new ImpossibleToCreateDDSTopic(topicTypeNameTS);		
//        System.out.println("ServiceTopic.createContentFilteredTopic: "+filteredTopic+" created with expresion "+expression);
        
        ErrorHandler.checkHandle(
        		filteredTopic,
        		"ServiceDDS.ServiceTopic.DomainParticipant.create_contentfilteredtopic ("+topicTypeNameTS+")");        

        return filteredTopic;
	}
    
}
