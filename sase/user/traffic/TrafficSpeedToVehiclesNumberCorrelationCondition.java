package sase.user.traffic;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.base.DoubleEventCondition;

public class TrafficSpeedToVehiclesNumberCorrelationCondition extends DoubleEventCondition {

	private static final int minSpeedDiff = 30;
	
	public TrafficSpeedToVehiclesNumberCorrelationCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent,
                                       Payload.ConditionsGraph graph) {
		Integer firstPointID = (Integer) firstEvent.getAttributeValue(AarhusTrafficEventTypesManager.pointIDAttributeName);
		Integer secondPointID = (Integer) secondEvent.getAttributeValue(AarhusTrafficEventTypesManager.pointIDAttributeName);
		if (firstPointID == secondPointID) {
			return 0.0;
		}
		Integer firstSpeed = 
				(Integer) firstEvent.getAttributeValue(AarhusTrafficEventTypesManager.averageSpeedAttributeName);
		Integer secondSpeed = 
				(Integer) secondEvent.getAttributeValue(AarhusTrafficEventTypesManager.averageSpeedAttributeName);
		Integer firstNumberOfVehicles = 
				(Integer) firstEvent.getAttributeValue(AarhusTrafficEventTypesManager.numberOfVehiclesAttributeName);
		Integer secondNumberOfVehicles = 
				(Integer) secondEvent.getAttributeValue(AarhusTrafficEventTypesManager.numberOfVehiclesAttributeName);
		boolean requestSpeedIncrease = 
				(firstNumberOfVehicles == secondNumberOfVehicles) ? (firstPointID < secondPointID) :
																	(firstNumberOfVehicles > secondNumberOfVehicles);
		//return requestSpeedIncrease ? (firstSpeed + minSpeedDiff < secondSpeed) : (secondSpeed + minSpeedDiff < firstSpeed);
		//TODO:: use our functions and types
		return 1.0;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
