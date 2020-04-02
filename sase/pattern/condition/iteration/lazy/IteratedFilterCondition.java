package sase.pattern.condition.iteration.lazy;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.iteration.IteratedEventExternalCondition;

import java.util.List;

public abstract class IteratedFilterCondition extends IteratedEventExternalCondition {

	public IteratedFilterCondition(EventType iteratedType, EventType nonIteratedType) {
		super(iteratedType, nonIteratedType);
	}

	@Override
	protected Boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent,
														 Payload.ConditionsGraph graph) {
		for (Event event : internalEvents) {
			if (!filterEvent(event, externalEvent, graph)) {
				return false;
			}
		}
		return true;
	}
	
	public abstract boolean filterEvent(Event iteratedEvent, Event nonIteratedEvent, Payload.ConditionsGraph graph);

}
