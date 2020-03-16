package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.base.PayloadFunctional;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.user.stocks.StockEventTypesManager;

import java.util.Objects;

public class StockDeltaOrderingCondition extends DoubleEventCondition {

	public StockDeltaOrderingCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}
	
	public StockDeltaOrderingCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}
	
	private Payload calculateDelta(Event event) { //TODO: use our type
		Payload firstValue = (Payload)event.getAttributeValue(
				StockEventTypesManager.firstStockMeasurementIndex);
		Payload secondValue = (Payload)event.getAttributeValue(
				StockEventTypesManager.firstStockMeasurementIndex + 1);
		return PayloadFunctional.abs(PayloadFunctional.substract(
				firstValue,secondValue));
	}

	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		Payload a = calculateDelta(firstEvent);
		Payload b = calculateDelta(secondEvent);
		return PayloadFunctional.lessThen(a, b); //TODO: use our function and type
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of relative deltas of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StockDeltaOrderingCondition)) {
			return false;
		}
		StockDeltaOrderingCondition condition = (StockDeltaOrderingCondition)other;
		return (firstType == condition.firstType && secondType == condition.secondType);
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(firstType, secondType);
    }

}
