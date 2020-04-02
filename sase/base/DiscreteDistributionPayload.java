package sase.base;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class DiscreteDistributionPayload extends Payload {
    List<Pair<Double,Double>> valuesProbabilities;

    public DiscreteDistributionPayload(String raw){
        String[] valuesProbablities = (raw).split(" ");
        assert valuesProbablities.length != 0;
        valuesProbabilities = new LinkedList<Pair<Double,Double>>();
        for(int i = 1; i < valuesProbablities.length-1; i++){ // first [ and last ]
            String[] valueProb = valuesProbablities[i].split(":");
            valuesProbabilities.add(new Pair(
                    Double.parseDouble(valueProb[0]),
                    Double.parseDouble(valueProb[1])));
        }
        this.setLeftestOperand(this);
        this.setRightestOperand(this);

    }

    //public DiscreteDistributionPayload(){}

    private DiscreteDistributionPayload(DiscreteDistributionPayload ddp){
        List<Pair<Double, Double>> newValuesProbabilities =
                new LinkedList<>();
        for(Pair<Double, Double> p : ddp.valuesProbabilities){
            Pair<Double, Double> newPair = new Pair(p.getKey(), p.getValue());
            newValuesProbabilities.add(newPair);
        }
        this.valuesProbabilities = newValuesProbabilities;
        this.leftestOperand = this;
        this.rightestOperand = this;

    }

    public DiscreteDistributionPayload(DeterministicPayload dPayload){
        valuesProbabilities = new LinkedList<Pair<Double,Double>>();
        valuesProbabilities.add(new Pair(dPayload.getValue(), 1.0));
    }

    public DiscreteDistributionPayload(List<Pair<Double,Double>> pairList){
        valuesProbabilities = pairList;
    }

    /*
    public DiscreteDistributionPayload(DiscreteDistributionPayload DDP){
        valuesProbabilities = DDP.valuesProbabilities.;
    }
    */

    public  List<Pair<Double, Double>> getValue() {
        return new LinkedList<>(valuesProbabilities);
    }

    public void setValue(List<Pair<Double, Double>> valuesProbabilities) {
        this.valuesProbabilities = new LinkedList<Pair<Double, Double>>(valuesProbabilities);
    }

    public void addValue(Pair<Double, Double> pair){
        this.valuesProbabilities.add(pair);
        //TODO: assert probabilities > 1.0, maintain sum of probabilities;
    }

    @Override
    public void updatePayload(Object payloadValue) {
        valuesProbabilities.removeIf(p -> p == payloadValue);
    }

    @Override
    public Boolean contains(Object o) {
        Payload.PayloadValue toFind = (Payload.PayloadValue) o;
        for(Pair<Double, Double> p : this.valuesProbabilities){
            if(p.getKey() == toFind.NDvalue.getKey() && p.getValue() == toFind.NDvalue.getValue()) return true;
        }
        return false;
    }

    @Override
    public Payload clone() {
        return new DiscreteDistributionPayload(this);
    }
}