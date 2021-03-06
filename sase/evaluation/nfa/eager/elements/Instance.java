package sase.evaluation.nfa.eager.elements;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.evaluation.common.EventBuffer;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.NFA;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.util.List;

public class Instance {
	protected final NFA automaton;
	protected NFAState currentState;
	protected EventBuffer matchBuffer;
	protected double matchProbability; // TODO: update it everywhere is needed
	private boolean shouldGenerateInputBufferReadyEvent;
	private boolean shouldInvalidate;


	public Instance(NFA nfa, NFAState initialState) {
		this(nfa, initialState, null, 1.0);
	}

	public Instance(NFA nfa, NFAState initialState, EventBuffer eventBuffer, Double matchProb) {
		automaton = nfa;
		currentState = initialState;
		matchBuffer = eventBuffer == null ? new EventBuffer(automaton.getIterativeTypes()) : eventBuffer.clone();
		matchProbability = matchProb;
		shouldGenerateInputBufferReadyEvent = false;
		shouldInvalidate = false;
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.instanceCreations);
	}
	
	public Double getProb() {
		return this.matchProbability;
	}

	public void updateProb(Double newProb) {
		this.matchProbability *= newProb;
	}

	public Double getMatchProb(){
		return this.matchBuffer.getGraph().computeProbability();
	}

	public NFAState getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(NFAState currentState) {
		this.currentState = currentState;
	}
	
	public List<Event> getEventsFromMatchBuffer() {
		return matchBuffer.getEvents();
	}

	public Payload.ConditionsGraph getGraphFromMatchBuffer() {
		return matchBuffer.getGraph();
	}
	
	public Event getMatchBufferEventByType(EventType type) {
		return matchBuffer.getEventByType(type);
	}
	
	public Match getMatch() {
		if (!currentState.isAccepting())
			return null;

		return new Match(getEventsFromMatchBuffer(), matchBuffer.getLatestTimestamp());
	}
	
	public boolean shouldInvalidate() {
		return shouldInvalidate;
	}

	public void markAsInvalid() {
		shouldInvalidate = true;
	}
	
	protected Long getInstanceTimeWindow() {
		return automaton.getTimeWindow();
	}
	
	public boolean isExpired(long currentTime) {
		long actualCurrentTime = (currentTime > 0) ? currentTime : automaton.getLastKnownGlobalTime();
		List<Event> events = getEventsFromMatchBuffer();
		for (Event event : events) {
			Long instanceTimeWindow = getInstanceTimeWindow();
			if (instanceTimeWindow != null && event.getTimestamp() + instanceTimeWindow < actualCurrentTime)
				return true;
		}
		return false;
	}
	
	public boolean isExpired() {
		return isExpired(0);
	}

	public Boolean isTransitionPossible(Event event, Transition transition) {
		if (event.getType() != transition.getEventType())
			return false;
		EventBuffer bufferOfEventsToVerify = matchBuffer.clone();
		bufferOfEventsToVerify.addEvent(event);
		return transition.verifyCondition(bufferOfEventsToVerify.getEvents(),
				bufferOfEventsToVerify.getGraph());
	}

	protected void executeMatchBufferTransition(Event event) {
		matchBuffer.addEvent(event);
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.bufferInsertions);
	}
	
	public void executeTransition(Event event, Transition transition) {
		currentState = transition.getDestination();
		switch (transition.getAction()) {
			case TAKE:
				executeMatchBufferTransition(event);
				break;
			case IGNORE:
			default:
				break;
		}
	}
	
	public boolean shouldGenerateInputBufferReadyEvent() {
		boolean retval = shouldGenerateInputBufferReadyEvent;
		shouldGenerateInputBufferReadyEvent = false;
		return retval;
	}
	
	public long size() {
		return 4 + matchBuffer.size();
	}
	
	@Override
	public Instance clone() {
		return new Instance(automaton, currentState, matchBuffer, matchProbability);
	}

	public Instance cloneWithEvents() {
		return new Instance(automaton, currentState, matchBuffer.cloneWithEvents(), matchProbability);
	}
	
	@Override
	public String toString() {
		return String.format("Instance in state %s", currentState);
	}

	public boolean shouldDiscardWithMatch() {
		return true;
	}
	
	public boolean shouldReportMatch() {
		return true;
	}
}