package sase.pattern.condition.base;

import sase.base.Event;

import java.util.List;

/**
 * Represents an empty condition which is always satisfied.
 */
public class TrivialCondition extends AtomicCondition {

	@Override
	protected Double actuallyVerify(List<Event> events) {
		return 1.0;
	}
	
	@Override
	protected boolean shouldIgnoreSelectivityMeasurements() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("Trivial Condition");
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
