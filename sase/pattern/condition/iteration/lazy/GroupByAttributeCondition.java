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
	protected Boolean verifyAggregatedEvent(AggregatedEvent event, Payload.ConditionsGraph graph) {
		List<Event> primitiveEvents = event.getPrimitiveEvents();
		if (primitiveEvents.size() < 2) {
			return true;
		}
		Object attributeValue = primitiveEvents.get(0).getAttributeValue(attributeIndex);
		for (int i = 1; i < primitiveEvents.size(); ++i) {
			Object valueToCompare = primitiveEvents.get(i).getAttributeValue(attributeIndex);
			if (!attributeValue.equals(valueToCompare)) {
				return false;
			}
		}
		return true;
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
