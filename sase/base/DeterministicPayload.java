package sase.base;

import java.util.Collections;
import java.util.Iterator;

public class DeterministicPayload extends Payload {
    private Double value;

    public DeterministicPayload(String raw){
        this.value = Double.parseDouble(raw);
    }

    public DeterministicPayload(Double value){
        this.value = value;
    }

    public Double getValue(){
        return value;
    }

    public void setValue(Double value){  this.value = value; }

    public Double getProb(){
        return 1.0;
    }

    @Override
    public Iterator iterator() {
        return Collections.singleton(value).iterator();
    }

    @Override
    public boolean equals(Object o){
        return this.value == ((DeterministicPayload)o).value;
    }

    @Override
    public Object apply(Object o) {
        if(o.equals(this)) return 1.0;
        return 0.0;
    }

    @Override
    public String toString() {
        return "value="+value+" probability="+"1.0";
    }

    @Override
    public void updatePayload(Object payloadValue) {}

    @Override
    public Boolean contains(Object o) {
        return null;
    }

    @Override
    public Payload clone() {
        return new DeterministicPayload(this.value);
    }
}