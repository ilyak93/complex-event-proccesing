package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.user.stocks.StockEventTypesManager;

/**
 * Represents a condition on a correlation rate between appearances of two given primitive events.
 */
public class StockFirstValueCmpCondition extends DoubleEventCondition {
	
	public enum ComparisonOperation {
		SMALLER {
            @Override
            public String toString() {
                return "<";
            }
        },
		EQUALS {
            @Override
            public String toString() {
                return "=";
            }
        },
		BIGGER {
            @Override
            public String toString() {
                return ">";
            }
        },
		ANY {
            @Override
            public String toString() {
                return "<>=";
            }
        }
	};
	
	private ComparisonOperation operation;
	
	public StockFirstValueCmpCondition(EventType firstType, EventType secondType, ComparisonOperation operation) {
		super(firstType, secondType);
		this.operation = operation;
	}

	private double getStockFirstValue(Event event) {
		return (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex);
	}
	
	@Override
	protected Double verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		int firstEventValue = ((Double)getStockFirstValue(firstEvent)).intValue();
		int secondEventValue = ((Double)getStockFirstValue(secondEvent)).intValue();
		switch (operation) {
		case BIGGER:
			//return (firstEventValue > secondEventValue);
			return 1.0;
			// TODO: use our types and functions
		case EQUALS:
			//return (firstEventValue == secondEventValue);
			return 1.0;
			// TODO: use our types and functions
		case SMALLER:
			//return (firstEventValue < secondEventValue);
			return 1.0;
			// TODO: use our types and functions
		case ANY:
			return 1.0;
		default:
			return 0.0;
		}
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of first price values of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}
}
