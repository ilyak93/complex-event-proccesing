package sase.user.speedd.fraud;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.base.DoubleEventCondition;

public class SameCreditCardIDCondition extends DoubleEventCondition {

	public SameCreditCardIDCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}

	public SameCreditCardIDCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent,
                                       Payload.ConditionsGraph graph) {
		String firstID = (String) firstEvent.getAttributeValue(CreditCardFraudEventTypesManager.creditCardIDAttributeIndex);
		String secondID = (String) secondEvent.getAttributeValue(CreditCardFraudEventTypesManager.creditCardIDAttributeIndex);
		return (firstID.equals(secondID)) ? 1.0 : 0.0;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
