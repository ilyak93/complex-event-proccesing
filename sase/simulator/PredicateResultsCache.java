package sase.simulator;

import sase.base.Event;
import sase.pattern.condition.base.AtomicCondition;

import java.util.HashMap;

//TODO: for now, only two-operand predicates will be supported!
public class PredicateResultsCache {

	private class DoubleEventPredicateCache {
	
		private HashMap<Event, HashMap<Event, Double>> cache;
		
		public DoubleEventPredicateCache() {
			cache = new HashMap<Event, HashMap<Event,Double>>();
		}
		
		public void recordConditionEvaluation(Event firstEvent, Event secondEvent, Double result) {
			HashMap<Event, Double> internalCache = cache.get(firstEvent);
			boolean internalCacheExists = (internalCache != null);
			if (!internalCacheExists) {
				internalCache = new HashMap<Event, Double>();
			}
			internalCache.put(secondEvent, result);
			if (!internalCacheExists) {
				cache.put(firstEvent, internalCache);
			}
		}
		
		public Double getConditionEvaluationResult(Event firstEvent, Event secondEvent) {
			HashMap<Event, Double> internalCache = cache.get(firstEvent);
			if (internalCache == null) {
				return null;
			}
			return internalCache.get(secondEvent);
		}
	
	}
	
	private HashMap<AtomicCondition, DoubleEventPredicateCache> cache;
	
	public PredicateResultsCache() {
		cache = new HashMap<AtomicCondition, DoubleEventPredicateCache>();
	}
	
	public void recordConditionEvaluation(AtomicCondition condition,
										  Event firstEvent, Event secondEvent,
										  Double result) {
		DoubleEventPredicateCache predicateCache = cache.get(condition);
		boolean predicateCacheExists = (predicateCache != null);
		if (!predicateCacheExists) {
			predicateCache = new DoubleEventPredicateCache();
		}
		predicateCache.recordConditionEvaluation(firstEvent, secondEvent, result);
		if (!predicateCacheExists) {
			cache.put(condition, predicateCache);
		}
	}
	
	public Double getConditionEvaluationResult(AtomicCondition condition, Event firstEvent, Event secondEvent) {
		DoubleEventPredicateCache predicateCache = cache.get(condition);
		if (predicateCache == null) {
			return null;
		}
		return predicateCache.getConditionEvaluationResult(firstEvent, secondEvent);
	}
	
	public void clear() {
		cache.clear();
	}

}
