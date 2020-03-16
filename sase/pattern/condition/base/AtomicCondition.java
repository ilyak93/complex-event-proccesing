package sase.pattern.condition.base;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.pattern.condition.Condition;
import sase.simulator.Environment;
import sase.statistics.ConditionSelectivityCollector;

import java.util.List;

/**
 * Represents a condition which involves a single check of primitive event(s) attribute(s).
 */
public abstract class AtomicCondition extends Condition {

	public AtomicCondition() {
		super();
	}
	
	public AtomicCondition(Double selectivity) {
		super(selectivity);
	}
	
	@Override
	public Double getSelectivity() {
		if (MainConfig.isSelectivityMonitoringAllowed) {
			return Environment.getEnvironment().getSelectivityEstimator().getSelectivityEstimate(this);
		}
		return super.getSelectivity();
	}
	
	public Double verify(List<Event> events) {
		if (shouldIgnoreSelectivityMeasurements()) {
			return actuallyVerify(events);
		}
		Event firstEvent = null;
		Event secondEvent = null;
		if (this instanceof DoubleEventCondition) {
			EventType firstEventType = ((DoubleEventCondition)this).getLeftEventType();
			firstEvent = getEventByType(events, firstEventType);
			EventType secondEventType = ((DoubleEventCondition)this).getRightEventType();
			secondEvent = getEventByType(events, secondEventType);
			Double success_prob =
				Environment.getEnvironment().getPredicateResultsCache().getConditionEvaluationResult(this,
																									 firstEvent,
																									 secondEvent);
			if (success_prob != null) {
				return success_prob;
			}
		}
		Double success_prob = actuallyVerify(events);
		if (MainConfig.conditionSelectivityMeasurementMode) {
			ConditionSelectivityCollector.getInstance().recordConditionEvaluation(getConditionKey(), success_prob);
		}
		if (MainConfig.isSelectivityMonitoringAllowed) {
			Environment.getEnvironment().getSelectivityEstimator().registerConditionVerification(this, success_prob);
		}
		if (this instanceof DoubleEventCondition) {
			Environment.getEnvironment().getPredicateResultsCache()
					.recordConditionEvaluation(this, firstEvent, secondEvent,
							success_prob);
		}
		return success_prob;
	}
	
	private Event getEventByType(List<Event> events, EventType eventType) {
		for (Event event : events) {
			if (event.getType() == eventType) {
				return event;
			}
		}
		return null;
	}
	
	protected boolean shouldIgnoreSelectivityMeasurements() {
		return false;
	}
	
	protected abstract Double actuallyVerify(List<Event> events);
	
}
