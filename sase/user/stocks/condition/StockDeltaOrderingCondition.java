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
	
	private Payload calculateDelta(Event event, Payload.PayLoadUpdateGraph graph) { //TODO: use our type
		Payload firstValue = (Payload)event.getAttributeValue(
				StockEventTypesManager.firstStockMeasurementIndex);
		Payload secondValue = (Payload)event.getAttributeValue(
				StockEventTypesManager.firstStockMeasurementIndex + 1);
		return PayloadFunctional.abs(PayloadFunctional.substract(
				firstValue,secondValue, graph), graph);
	}

	@Override
	protected Boolean verifyDoubleEvent(Event firstEvent, Event secondEvent, Payload.ConditionsGraph graph) {
		//this.graph.clear();
		Payload.PayLoadUpdateGraph newGraph = new Payload.PayLoadUpdateGraph();
		newGraph.setLeftestEventType(firstEvent.getType().getName());
		newGraph.setRightestEventType(secondEvent.getType().getName());
		Payload a = calculateDelta(firstEvent, newGraph);
		newGraph.setLeftestOperand(a.getLeftestOperand());
		Payload b = calculateDelta(secondEvent, newGraph);
		newGraph.setRightestOperand(b.getLeftestOperand());
		Double result = PayloadFunctional.lessThen(a, b, newGraph); //TODO: use our function and type
		if(newGraph.notEmpty()) {
			graph.addGraph(newGraph);
		}
		return result > 0;
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
