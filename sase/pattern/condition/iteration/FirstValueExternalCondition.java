package sase.pattern.condition.iteration;

import sase.base.Event;
import sase.base.Payload;
import sase.pattern.condition.base.DoubleEventCondition;

import java.util.ArrayList;
import java.util.List;

public class FirstValueExternalCondition extends IteratedEventExternalCondition {

	private DoubleEventCondition nestedCondition;
	
	public FirstValueExternalCondition(DoubleEventCondition nestedCondition) {
		super(nestedCondition.getLeftEventType(), nestedCondition.getRightEventType());
		this.nestedCondition = nestedCondition;
	}

	@Override
	protected Boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent,
														 Payload.ConditionsGraph graph) {
		if (internalEvents.isEmpty()) {
			return false;
		}
		List<Event> eventsToVerify = new ArrayList<Event>();
		eventsToVerify.add(internalEvents.get(0));
		eventsToVerify.add(externalEvent);
		return nestedCondition.verify(eventsToVerify, graph);
	}

}
