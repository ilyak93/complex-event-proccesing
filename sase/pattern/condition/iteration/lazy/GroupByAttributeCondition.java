package sase.pattern.condition.iteration.lazy;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.iteration.IteratedEventInternalCondition;

import java.util.List;

public class GroupByAttributeCondition extends IteratedEventInternalCondition {

	private final int attributeIndex;
	
	public GroupByAttributeCondition(EventType type, int attributeIndex) {
		super(type);
		this.attributeIndex = attributeIndex;
	}

	@Override
	protected Double verifyAggregatedEvent(AggregatedEvent event, Payload.ConditionsGraph graph) {
		List<Event> primitiveEvents = event.getPrimitiveEvents();
		if (primitiveEvents.size() < 2) {
			return 1.0;
		}
		Object attributeValue = primitiveEvents.get(0).getAttributeValue(attributeIndex);
		Double aggregatedProb = 1.0;
		for (int i = 1; i < primitiveEvents.size(); ++i) {
			Object valueToCompare = primitiveEvents.get(i).getAttributeValue(attributeIndex);//TODO: use our types and functions
			Double curPrimitiveEventConditionProb = 1.0; //attributeValue.equals(valueToCompare);
			if(curPrimitiveEventConditionProb > 0.0){
				aggregatedProb *= curPrimitiveEventConditionProb;
			} else return 0.0;
		}
		return aggregatedProb;
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
