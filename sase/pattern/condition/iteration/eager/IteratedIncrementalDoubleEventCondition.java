package sase.pattern.condition.iteration.eager;

import sase.base.Event;
import sase.base.Payload;
import sase.pattern.condition.base.DoubleEventCondition;

import java.util.ArrayList;
import java.util.List;

public class IteratedIncrementalDoubleEventCondition extends IteratedIncrementalCondition {
	
	private DoubleEventCondition nestedCondition;

	public IteratedIncrementalDoubleEventCondition(DoubleEventCondition nestedCondition) {
		super(nestedCondition.getLeftEventType());
		this.nestedCondition = nestedCondition;
	}

	@Override
	public Double verifyAdjacentEvents(Event firstEvent, Event secondEvent, Payload.ConditionsGraph graph) {
		List<Event> eventsToVerify = new ArrayList<Event>();
		eventsToVerify.add(firstEvent);
		eventsToVerify.add(secondEvent);
		return nestedCondition.verify(eventsToVerify, graph);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
