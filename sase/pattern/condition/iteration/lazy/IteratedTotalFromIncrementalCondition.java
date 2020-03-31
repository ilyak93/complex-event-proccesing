package sase.pattern.condition.iteration.lazy;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.iteration.IteratedEventInternalCondition;
import sase.pattern.condition.iteration.eager.IteratedIncrementalCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.util.List;

public class IteratedTotalFromIncrementalCondition extends IteratedEventInternalCondition {

	private final IteratedIncrementalCondition incrementalCondition;
	
	public IteratedTotalFromIncrementalCondition(IteratedIncrementalCondition incrementalCondition) {
		super(incrementalCondition.getType());
		this.incrementalCondition = incrementalCondition;
		EventType incrementalConditionType = incrementalCondition.getType();
		if (this.type != incrementalConditionType) {
			throw new RuntimeException(String.format("Unexpected type %s accepted, %s expected",
													 incrementalConditionType, type));
		}
	}

	@Override
	protected Double verifyAggregatedEvent(AggregatedEvent event, Payload.ConditionsGraph graph) {
		//we decrease this statistic by one since this condition invokes an internal condition, which increases
		//this counter again
		Environment.getEnvironment().getStatisticsManager().decrementDiscreteStatistic(Statistics.computations);
		List<Event> primitiveEvents = event.getPrimitiveEvents();
		if (primitiveEvents.size() < 2) {
			return 1.0;
		}
		Event prevEvent = null;
		Double incrementalProb = 1.0;
		for (Event primitiveEvent : primitiveEvents) {
			if (prevEvent != null) {
				Double currentiIncrementalProb = incrementalCondition.verifyAdjacentEvents(prevEvent, primitiveEvent, graph);
				if(currentiIncrementalProb > 0.0){
					incrementalProb *= currentiIncrementalProb;
				} else return 0.0;
			}
			prevEvent = primitiveEvent;
		}
		return incrementalProb;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
