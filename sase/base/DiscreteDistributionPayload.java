package sase.base;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DiscreteDistributionPayload extends Payload {
    //TODO: implement data_structure
    List<Pair<Double,Double>> valuesProbabilities;

    //TODO: implemet ctors and geters
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

    }

    //public DiscreteDistributionPayload(){}

    public DiscreteDistributionPayload(DeterministicPayload dPayload){
        valuesProbabilities = new LinkedList<Pair<Double,Double>>();
        valuesProbabilities.add(new Pair(dPayload.getValue(), 1.0));
    }

    public DiscreteDistributionPayload(List<Pair<Double,Double>> pairList){
        valuesProbabilities = pairList;
    }

    //TODO: implemet overriden methods
    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object apply(Object o) {
        return null;
    }

    @Override
    public String toString() {
        return "";
    }


    public  List<Pair<Double, Double>> getValue() {
        return new LinkedList<>(valuesProbabilities);
    }

    public void setValue(List<Pair<Double, Double>> valuesProbabilities) {
        this.valuesProbabilities = new LinkedList<Pair<Double, Double>>(valuesProbabilities);
    }
}