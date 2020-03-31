package sase.pattern.condition.iteration;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.base.SingleEventCondition;

public abstract class IteratedEventInternalCondition extends SingleEventCondition {

	private static final double defaultSelectivity = 1.0;
	
	public IteratedEventInternalCondition(EventType type) {
		super(type);
		//TODO: for now, the selectivity of the internal conditions on iterated events is not taken into account
		setSelectivity(defaultSelectivity);
	}

	@Override
	protected Double verifySingleEvent(Event event, Payload.ConditionsGraph graph) {
		if (!(event instanceof AggregatedEvent)) {
			throw new RuntimeException(
				String.format("Aggregated event of type %s expected, primitive event %s received instead", type, event));
		}
		AggregatedEvent aggregatedEvent = (AggregatedEvent)event;
		return verifyAggregatedEvent(aggregatedEvent, graph);
	}
	
	protected abstract Double verifyAggregatedEvent(AggregatedEvent event, Payload.ConditionsGraph graph);

}
