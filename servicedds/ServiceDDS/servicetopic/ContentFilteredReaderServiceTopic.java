package ServiceDDS.servicetopic;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDS.ANY_STATUS;
import DDS.ContentFilteredTopic;
import DDS.DataReader;
import DDS.DomainParticipant;
import DDS.SampleInfoSeqHolder;
import DDS.Subscriber;
import ServiceDDS.ErrorHandler;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;

public class ContentFilteredReaderServiceTopic extends ServiceTopic {

	ContentFilteredTopic cftopic;
	
	public ContentFilteredReaderServiceTopic(
			Object topicData, 
			String name,
			String expression, 
			String[] arguments,
			DomainParticipant participant, 
			Subscriber subscriber,
			QoSParameters readerQos) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
		super(topicData, name, participant, null, subscriber, readerQos, null);
		System.out.println("ContentFilteredReaderServiceTopic: created ServiceTopic for topic "+name);
		this.cftopic = super.createContentFilteredTopic(name+"Filtered", expression, arguments);
		System.out.println("ContentFilteredReaderServiceTopic: created content filtered topic for topic "+name+" and expression "+expression);
		this.createReader(readerQos);
		System.out.println("ContentFilteredReaderServiceTopic: created reader for topic "+name);
	}
	
	void createReader(QoSParameters readerQos) throws ClassNotFoundException {
		System.out.println("ContentFilteredReaderServiceTopic.createReader: starting the process... for subscriber = "+this.subscriber+" and cf = "+this.cftopic+ " and topicQoS = "+DDS.DATAREADER_QOS_USE_TOPIC_QOS.value+" and status = "+ANY_STATUS.value);
		// Create a DataReader for the topicDataType (using the default QoS)
	    DataReader parentReader = this.subscriber.create_datareader(
	        this.cftopic,
	        DDS.DATAREADER_QOS_USE_TOPIC_QOS.value,
	        null,
	        ANY_STATUS.value);
	    System.out.println("ContentFilteredReaderServiceTopic.createReader: got parent reader = "+parentReader);
        ErrorHandler.checkHandle(
            parentReader, "ServiceDDS.ServiceTopic.Subscriber.create_datareader");
        System.out.println("ContentFilteredReaderServiceTopic.createReader: parent reader created");
        
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
            System.out.println("ContentFilteredReaderServiceTopic.createReader: take preloaded");
            
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
            System.out.println("ContentFilteredReaderServiceTopic.createReader: read preloaded");
            
            this.defaultTopicDataReader_return_loan =
                this.readerClassDefinition.getMethod(
                    "return_loan",
                    new Class[] {Class.forName(this.topicSeqHolderClassName),
                                SampleInfoSeqHolder.class}
            );
            System.out.println("ContentFilteredReaderServiceTopic.createReader: return loan preloaded");
            
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        // Set user desired QoS for default reader
        if (readerQos != null) {
        	System.out.println("ContentFilteredReaderServiceTopic.createReader: setting QoS...");
    		int status = this.defaultTopicDataReader.get_qos(this.defaultReaderQos);
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
}
