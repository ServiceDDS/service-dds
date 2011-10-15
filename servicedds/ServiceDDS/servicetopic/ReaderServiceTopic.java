package ServiceDDS.servicetopic;

import DDS.DomainParticipant;
import DDS.Subscriber;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;

/**
 * A service topic only used for reading. It does not create a default writer.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class ReaderServiceTopic extends ServiceTopic {

	public ReaderServiceTopic(
			Object topicData, 
			String name,
			DomainParticipant participant, 
			Subscriber subscriber,
			QoSParameters readerQos) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
		super(topicData, name, participant, null, subscriber, readerQos, null);
		this.createReader(readerQos);
	}

}
