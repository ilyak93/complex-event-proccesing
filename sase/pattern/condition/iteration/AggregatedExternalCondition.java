package sase.pattern.condition.iteration;

import sase.aggregator.VectorAggregator;
import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class AggregatedExternalCondition extends IteratedEventExternalCondition {

	private final VectorAggregator aggregator;
	private final Condition condition;
	
	public AggregatedExternalCondition(EventType iteratedType, EventType nonIteratedType,
									   VectorAggregator aggregator, Condition condition) {
		super(iteratedType, nonIteratedType);
		this.aggregator = aggregator;
		this.condition = condition;
	}

	@Override
	protected Boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent,
														 Payload.ConditionsGraph graph) {
		if (internalEvents.size() == 0) {
			throw new RuntimeException("Empty list of events encountered");
		}
		
		AggregatedEvent aggregatedEvent = new AggregatedEvent(internalEvents);
		aggregatedEvent.aggregatePrimitiveEvents(aggregator);
		
		List<Event> eventsToVerify = new ArrayList<Event>();
		eventsToVerify.add(aggregatedEvent);
		eventsToVerify.add(externalEvent);
		return condition.verify(eventsToVerify, graph);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
