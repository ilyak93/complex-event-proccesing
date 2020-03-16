package sase.evaluation.nfa.lazy.optimizations;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.nfa.lazy.elements.LazyInstance;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.iteration.lazy.IteratedFilterCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BufferFilter extends BufferPreprocessor {
	
	private class Activator {
		private final HashMap<IteratedFilterCondition, Event> conditionToExternalEvent;
		
		public Activator(LazyInstance instance) {
			conditionToExternalEvent = new HashMap<IteratedFilterCondition, Event>();
			for (IteratedFilterCondition filter : conditionToType.keySet()) {
				EventType eventType = conditionToType.get(filter);
				Event event = instance.getMatchBufferEventByType(eventType);
				conditionToExternalEvent.put(filter, event);
			}
		}
		
		public Double applyFilter(Event eventToFilter) { //TODO: make sue with Ilya what is that
			Double filterConditionProb = 1.0;
			for (AtomicCondition atomicCondition : filterCondition.getAtomicConditions()) {
				IteratedFilterCondition atomicFilterCondition = (IteratedFilterCondition)atomicCondition;
				Event externalEventToVerifyWith = conditionToExternalEvent.get(atomicFilterCondition);
				Double currentFilterConditionProb = atomicFilterCondition.filterEvent(eventToFilter, externalEventToVerifyWith);
				if (currentFilterConditionProb > 0.0) {
					filterConditionProb *= currentFilterConditionProb;
				} else return 0.0;
			}
			return filterConditionProb;
		}
	};

	private final HashMap<IteratedFilterCondition, EventType> conditionToType;
	
	public BufferFilter(Condition condition) {
		super(condition);
		conditionToType = new HashMap<IteratedFilterCondition, EventType>();
		for (AtomicCondition atomicCondition : filterCondition.getAtomicConditions()) {
			IteratedFilterCondition atomicFilterCondition = (IteratedFilterCondition)atomicCondition;
			conditionToType.put(atomicFilterCondition, atomicFilterCondition.getRightEventType());
		}
	}

	//TODO:: to check how it should work in non deterministic way and fix code appropriately
	@Override
	public List<Event> preprocessEvents(LazyInstance instance, List<Event> events, boolean isLastPreprocessor) {
		List<Event> filteredEvents = new ArrayList<Event>();
		Activator filterActivator = new Activator(instance);
		Double preprocessConditionsProb = 1.0;
		for (Event event : events) {
			Double currentPreprocessConditionsProb = 1.0;
			if (currentPreprocessConditionsProb > 0.0) {
				filteredEvents.add(event);
				preprocessConditionsProb *= currentPreprocessConditionsProb;
			}
		}
		instance.updateProb(preprocessConditionsProb);
		if (isLastPreprocessor) {
			return BufferPreprocessor.createAggregatedEventsFromBufferedEvents(filteredEvents);
		}
		else {
			return filteredEvents;
		}
	}

	@Override
	protected void stripPreprocessorConditions(CNFCondition condition) {
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			if (atomicCondition instanceof IteratedFilterCondition) {
				filterCondition.addAtomicCondition(atomicCondition);
			}
		}
		condition.removeAtomicConditions(filterCondition);
	}
}
