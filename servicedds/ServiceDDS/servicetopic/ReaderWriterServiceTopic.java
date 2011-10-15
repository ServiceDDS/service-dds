package ServiceDDS.servicetopic;

import DDS.DomainParticipant;
import DDS.Publisher;
import DDS.Subscriber;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;

public class ReaderWriterServiceTopic extends ServiceTopic {

	public ReaderWriterServiceTopic(
			Object topicData, 
			String name,
			DomainParticipant participant, 
			Subscriber subscriber,
			Publisher publisher,
			QoSParameters readerQos,
			QoSParameters writerQos) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
		super(topicData, name, participant, publisher, subscriber, readerQos, writerQos);
		this.createReader(readerQos);
		this.createWriter(writerQos);
	}
}
