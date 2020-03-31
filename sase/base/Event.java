package sase.base;

import sase.pattern.EventTypesManager;

public class Event implements Comparable<Event> {
	
	private static final int signatureSize = 2;
	private static long eventCounter = 0;
	
	public static void resetCounter() {
		eventCounter = 0;
	}
	
	public static Object[] getEventSignature(Object[] eventPayload) {
		Object[] result = new Object[signatureSize];
		for (int i = 0; i < signatureSize; ++i) {
			result[i] = eventPayload[i];
		}
		return result;
	}

	protected final long sequenceNumber;
	protected EventType type;
	protected final long systemTimestamp;
	protected Object[] payload;
	protected double prob;

	public Event(EventType type, Object[] payload) {
		this.sequenceNumber = eventCounter++;
		this.type = type;
		this.systemTimestamp = System.currentTimeMillis();
		this.payload = payload == null ? null :
				EventTypesManager.getInstance().convertStringPayloadToObjectPayload(payload);
	}

	public Event(EventType type, Object[] payload, double prob) {
		this.sequenceNumber = eventCounter++;
		this.type = type;
		this.prob = prob;
		this.systemTimestamp = System.currentTimeMillis();
		this.payload = payload == null ? null :
				EventTypesManager.getInstance().convertStringPayloadToObjectPayload(payload);
	}

	private Event(Event e){
		this.sequenceNumber = e.sequenceNumber;
		this.type = e.type;
		this.prob = e.prob;
		this.systemTimestamp = e.systemTimestamp;
		Object[] newPayload = new Object[e.payload.length];
		for(int i = 0; i < e.payload.length; i++) {
			if(e.payload[i] instanceof Payload){
				newPayload[i] = ((Payload)e.payload[i]).clone();
			} else {
				newPayload[i] = e.payload[i]; //if other complicated payload, need to implement appropriate clone
			}
		}
		this.payload = newPayload;
	}
	
	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Object[] getAttributes() {
		return payload;
	}
	
	private int findAttributeByName(String attributeName) {
		for (int i = 0; i < type.getAttributes().size(); ++i) {
			if (type.getAttributes().get(i).getName().equals(attributeName)) {
				return i;
			}
		}
		return -1;
	}
	
	public Object getAttributeValue(String attributeName) {
		int attributeIndex = findAttributeByName(attributeName);
		return attributeIndex == -1 ? null : getAttributeValue(attributeIndex);
	}
	
	public Object getAttributeValue(int attributeIndex) {
		return payload[attributeIndex];
	}
	
	public String getLabel() {
		return EventTypesManager.getInstance().getEventLabel(this);
	}
	
	public long getTimestamp() {
		return EventTypesManager.getInstance().getEventTimestamp(this);
	}
	
	public Object[] getSignature() {
		return getEventSignature(payload);
	}

	public long getSystemTimestamp() {
		return systemTimestamp;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public double getProb() { return prob;	}

	public Event clone(){
		return new Event(this);
	}


	@Override
	public String toString() {
		return String.format("%s  %d  %d  %.2f", type.getName(), sequenceNumber+1, getTimestamp(), getProb());
		/*String result = String.format("%s:", type);
		for (int i = 0; i < payload.length; ++i) {
			result += payload[i];
			if (i < payload.length - 1) {
				result += ",";
			}
		}
		return result;*/
	}

	@Override
	public int compareTo(Event e) {
		return new Long(sequenceNumber - e.sequenceNumber).intValue();
	}

}
