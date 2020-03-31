package sase.user.speedd.traffic;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.base.DoubleEventCondition;

public class TrafficSimilarVehicleIDCondition extends DoubleEventCondition {
	
	private static final long maxDiff = 0;

	public TrafficSimilarVehicleIDCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}

	public TrafficSimilarVehicleIDCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent, Payload.ConditionsGraph graph) {
		Long firstID = (Long) firstEvent.getAttributeValue(TrafficEventTypesManager.vehicleIDAttributeIndex);
		Long secondID = (Long) secondEvent.getAttributeValue(TrafficEventTypesManager.vehicleIDAttributeIndex);
		//return Math.abs(firstID - secondID) <= maxDiff;
		//TODO: use our functions and types
		return 1.0;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
