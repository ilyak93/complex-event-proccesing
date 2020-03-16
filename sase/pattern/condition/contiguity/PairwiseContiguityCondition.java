package sase.pattern.condition.contiguity;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

public class PairwiseContiguityCondition extends DoubleEventCondition {
	
	private static final double conditionSelectivity = 1.0;
	private static final int defaultDifference = 1;
	
	private final int difference;

	public PairwiseContiguityCondition(EventType firstType, EventType secondType) {
		this(firstType, secondType, defaultDifference);
	}
	
	public PairwiseContiguityCondition(EventType firstType, EventType secondType, int difference) {
		super(firstType, secondType, conditionSelectivity);
		this.difference = difference;
	}

	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		Double result = (double)(firstEvent.getSequenceNumber() - (secondEvent.getSequenceNumber() - difference));
		return result == 0.0 ? 1.0 : 0.0;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public String toString() {
		return String.format("Contiguity between %s and %s (diff=%d)", firstType, secondType, difference);
	}

}
