package sase.pattern.condition.base;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.evaluation.nfa.NFA;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a special condition, operating on a built-in primitive event reporting the status of
 * internal event buffering. The incoming event contains a counter for each event type involved in a
 * pattern, and the purpose of this condition is to check whether sorting all the counters in ascending
 * order yields the same order of event types as the one given in the constructor of the class.
 */
public class BufferedEventsRatesCondition extends SingleEventCondition {

	private List<EventType> order;
	
	public BufferedEventsRatesCondition(List<EventType> order) {
		super(NFA.inputEventType);
		this.order = order;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Double verifySingleEvent(Event event, Payload.ConditionsGraph graph) {
		if (event.getType() != NFA.inputEventType)
			return 0.0;
		Object countersObject = event.getAttributeValue(NFA.inputBufferCountersAttributeName);
		if (!(countersObject instanceof HashMap<?, ?>))
			return 0.0;
		if (order == null)
			return 0.0;
		HashMap<EventType, Integer> counters = (HashMap<EventType, Integer>)countersObject;
		for (int i = 0; i < order.size(); ++i) {
			Integer counter = counters.get(order.get(i));
			if (counter == 0)
				return 0.0;
			if (i == 0)
				continue;
			Integer prevCounter = counters.get(order.get(i - 1));
			if (prevCounter > counter)
				return 0.0;
		}
		return 1.0;
	}
	
	@Override
	public String toString() {
		if (order == null)
			return String.format("Condition on all events to appear in the buffer");
		return String.format("Condition on buffered events rates ordered as follows:\n%s", 
				order);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
