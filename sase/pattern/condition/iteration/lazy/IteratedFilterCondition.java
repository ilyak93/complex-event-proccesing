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
	protected Double verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent,
														 Payload.ConditionsGraph graph) {
		Double filterProb = 1.0;
		for (Event event : internalEvents) {
			Double currentFilterProb = filterEvent(event, externalEvent, graph);
			if(currentFilterProb > 0.0){
				filterProb*=currentFilterProb;
			} else return 0.0;
		}
		return filterProb;
	}
	
	public abstract Double filterEvent(Event iteratedEvent, Event nonIteratedEvent, Payload.ConditionsGraph graph);

}
