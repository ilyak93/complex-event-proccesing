package sase.pattern.condition.iteration;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

import java.util.List;

public abstract class IteratedEventExternalCondition extends DoubleEventCondition {
	
	public IteratedEventExternalCondition(EventType iteratedType, EventType nonIteratedType) {
		super(iteratedType, nonIteratedType);
	}

	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		if (firstEvent instanceof AggregatedEvent) {
			return verifyListOfEventsWithExternalEvent(((AggregatedEvent)firstEvent).getPrimitiveEvents(), 
													   secondEvent);
		}
		if (secondEvent instanceof AggregatedEvent) {
			return verifyListOfEventsWithExternalEvent(((AggregatedEvent)secondEvent).getPrimitiveEvents(), 
													   firstEvent);
		}
		throw new RuntimeException(
						String.format("Aggregated event expected, primitive events %s and %s received instead", 
						firstEvent, secondEvent));
	}

	protected abstract Double verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent);
}
