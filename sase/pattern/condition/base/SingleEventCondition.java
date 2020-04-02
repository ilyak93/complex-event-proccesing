package sase.pattern.condition.base;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.util.List;

/**
 * Represents a condition which only involves attributes of a single primitive event.
 */
public abstract class SingleEventCondition extends AtomicCondition {
	
	protected final EventType type;
	
	public SingleEventCondition(EventType type) {
		super();
		this.type = type;
		eventTypes.add(type);
	}
	
	@Override
	protected Boolean actuallyVerify(List<Event> events, Payload.ConditionsGraph graph) {
		for (Event event : events) {
			if (event.getType() == type) {
				Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.computations);
				return verifySingleEvent(event, graph);
			}
		}
		return false;
	}
	
	public EventType getType() {
		return type;
	}

	protected abstract Boolean verifySingleEvent(Event event, Payload.ConditionsGraph graph);
}