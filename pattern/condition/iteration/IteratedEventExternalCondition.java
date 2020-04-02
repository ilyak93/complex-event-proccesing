package sase.pattern.condition.iteration;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.base.DoubleEventCondition;

import java.util.List;

public abstract class IteratedEventExternalCondition extends DoubleEventCondition {
	
	public IteratedEventExternalCondition(EventType iteratedType, EventType nonIteratedType) {
		super(iteratedType, nonIteratedType);
	}

	@Override
	protected Boolean verifyDoubleEvent(Event firstEvent, Event secondEvent,
										Payload.ConditionsGraph graph) {
		if (firstEvent instanceof AggregatedEvent) {
			return verifyListOfEventsWithExternalEvent(((AggregatedEvent)firstEvent).getPrimitiveEvents(), 
													   secondEvent, graph);
		}
		if (secondEvent instanceof AggregatedEvent) {
			return verifyListOfEventsWithExternalEvent(((AggregatedEvent)secondEvent).getPrimitiveEvents(), 
													   firstEvent, graph);
		}
		throw new RuntimeException(
						String.format("Aggregated event expected, primitive events %s and %s received instead", 
						firstEvent, secondEvent));
	}

	protected abstract Boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent,
																  Payload.ConditionsGraph graph);
}
