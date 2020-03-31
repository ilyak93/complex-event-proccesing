package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.base.Payload;
import sase.pattern.condition.base.SingleEventCondition;

/**
 * Represents a condition verifying that price range of a given stock event doesn't exceed a given value.
 */
public class StockRangeCondition extends SingleEventCondition {

	private double minRange;
	
	public StockRangeCondition(EventType type, double minRange) {
		super(type);
		this.minRange = minRange;
	}

	private double getStockPricesRange(Event event) {
		double firstPrice = Double.valueOf((String)event.getAttributeValue(2));
		double lastPrice = Double.valueOf((String)event.getAttributeValue(
														event.getAttributes().length - 1));
		return Math.abs(lastPrice - firstPrice);
	}
	
	@Override
	protected Double verifySingleEvent(Event event, Payload.ConditionsGraph graph) {
		//return (getStockPricesRange(event) >= minRange);
		//TODO: use our function and type
		return 1.0;
	}
	
	@Override
	public String toString() {
		return String.format("Range condition of %f on type %s", 
							 minRange, eventTypes.get(0));
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
