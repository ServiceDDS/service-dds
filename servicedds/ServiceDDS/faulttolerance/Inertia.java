package ServiceDDS.faulttolerance;

import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.ttf.Condition;
import ServiceDDS.ttf.TransformationFunction;

public abstract class Inertia {

	ServiceTopic serviceTopic;
	Condition detectionCondition;
	TransformationFunction generationDataFunction;
	
	public Inertia(ServiceTopic serviceTopic, Condition c, TransformationFunction tf) {
		
	}
	
}
