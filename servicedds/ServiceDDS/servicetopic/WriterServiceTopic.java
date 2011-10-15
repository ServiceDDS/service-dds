package ServiceDDS.servicetopic;

import DDS.DomainParticipant;
import DDS.Publisher;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;

/**
 * A service topic only used for writing. It does not create a default reader.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class WriterServiceTopic extends ServiceTopic {

	public WriterServiceTopic(Object topicData, String name,
			DomainParticipant participant, Publisher publisher,
			QoSParameters writerQos) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
		super(topicData, name, participant, publisher, null, null, writerQos);
		this.createWriter(writerQos);
	}

}
