package sase.pattern.condition.base;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.util.List;

/**
 * Represents a condition which involves attributes of a pair of primitive events.
 */
public abstract class DoubleEventCondition extends AtomicCondition {
	
	protected final EventType firstType;
	protected final EventType secondType;
	
	public DoubleEventCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(selectivity);
		this.firstType = firstType;
		this.secondType = secondType;
		eventTypes.add(firstType);
		eventTypes.add(secondType);
		if (selectivity == null && 
			!MainConfig.isSelectivityMonitoringAllowed && 
			!MainConfig.conditionSelectivityMeasurementMode) {
				setSelectivityByEstimate();
		}
	}
	
	public DoubleEventCondition(EventType firstType, EventType secondType) {
		this(firstType, secondType, null);
	}
	
	@Override
	protected Double actuallyVerify(List<Event> events) {
		Event firstEvent = null;
		Event secondEvent = null;
		for (Event event : events) {
			if (event.getType() == eventTypes.get(0))
				firstEvent = event;
			else if (event.getType() == eventTypes.get(1))
				secondEvent = event;
			if (firstEvent != null && secondEvent != null) {
				Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.computations);
				return verifyDoubleEvent(firstEvent, secondEvent);
			}
		}
		return 0.0;
	}
	
	public EventType getLeftEventType() {
		return eventTypes.get(0);
	}
	
	public EventType getRightEventType() {
		return eventTypes.get(1);
	}

	@Override
	protected String getConditionKey() {
		return String.format("%s:%s", firstType.getName(), secondType.getName());
	}
	
	protected abstract Double verifyDoubleEvent(Event firstEvent, Event secondEvent);
}
