package sase.pattern.condition.base;

import sase.base.Event;
import sase.base.Payload;

import java.util.List;

/**
 * Represents an empty condition which is always satisfied.
 */
public class TrivialCondition extends AtomicCondition {

	@Override
	protected Boolean actuallyVerify(List<Event> events, Payload.ConditionsGraph graph) {
		return true;
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
