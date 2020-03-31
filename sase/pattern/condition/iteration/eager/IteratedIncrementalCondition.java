package sase.pattern.condition.iteration.eager;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.iteration.IteratedEventInternalCondition;

import java.util.List;

public abstract class IteratedIncrementalCondition extends IteratedEventInternalCondition {

	public IteratedIncrementalCondition(EventType type) {
		super(type);
	}

	@Override
	protected Double verifyAggregatedEvent(AggregatedEvent event, Payload.ConditionsGraph graph) {
		List<Event> primitiveEvents = event.getPrimitiveEvents();
		if (primitiveEvents.size() < 2) {
			return 1.0;
		}
		Event lastEvent = primitiveEvents.get(primitiveEvents.size() - 1);
		Event eventBeforeLast = primitiveEvents.get(primitiveEvents.size() - 2);
		return verifyAdjacentEvents(lastEvent, eventBeforeLast, graph);
	}

	public abstract Double verifyAdjacentEvents(Event firstEvent, Event secondEvent, Payload.ConditionsGraph graph);
}
