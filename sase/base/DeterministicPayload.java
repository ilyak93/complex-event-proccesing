package sase.base;

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