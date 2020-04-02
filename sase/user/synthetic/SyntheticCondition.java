package sase.user.synthetic;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.input.producers.ISyntheticSelectivityProvider;
import sase.pattern.condition.base.DoubleEventCondition;

import java.util.Random;

public class SyntheticCondition extends DoubleEventCondition {

	private static int currentID = 0;
	
	public static void reset() {
		currentID = 0;
	}
	
	private final int id;
	private ISyntheticSelectivityProvider selectivityProvider = null;
	
	public SyntheticCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
		id = currentID++;
	}

	@Override
	protected Boolean verifyDoubleEvent(Event firstEvent, Event secondEvent,
                                       Payload.ConditionsGraph graph) {
		if (selectivityProvider == null) {
			throw new RuntimeException("The selectivity provider was not set yet.");
		}
		if (selectivityProvider.getConditionEvaluationDuration() != 0) {
			try {
				Thread.sleep(selectivityProvider.getConditionEvaluationDuration());
			} catch (InterruptedException e) {
			}
		}
		double currentSelectivity = selectivityProvider.getSelectivity(id, firstEvent, secondEvent);
		return (new Random().nextDouble() < currentSelectivity);
	}
	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

	public int getId() {
		return id;
	}
	
	public void setSelectivityProvider(ISyntheticSelectivityProvider selectivityProvider) {
		this.selectivityProvider = selectivityProvider;
	}

}
