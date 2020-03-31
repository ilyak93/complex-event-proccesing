package sase.pattern.condition.contiguity;

import sase.base.Event;
import sase.base.Payload;
import sase.pattern.condition.base.AtomicCondition;

import java.util.ArrayList;
import java.util.List;

public class TotalContiguityCondition extends AtomicCondition {

	private static final double conditionSelectivity = 1.0;
	
	public TotalContiguityCondition() {
		super(conditionSelectivity);
	}

	@Override
	protected Double actuallyVerify(List<Event> events, Payload.ConditionsGraph graph) {
		if (events.size() < 2) {
			return 1.0;
		}
		List<Event> orderedEvents = new ArrayList<Event>(events);
		orderedEvents.sort(null);
		for (int i = 0; i < orderedEvents.size() - 1; ++i) {
			long firstSequenceNumber = orderedEvents.get(i).getSequenceNumber();
			long secondSequenceNumber = orderedEvents.get(i+1).getSequenceNumber();
			if (firstSequenceNumber != secondSequenceNumber - 1) {
				return 0.0;
			}
		}
		return 1.0;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public String toString() {
		return "Total contiguity on all types";
	}

}
