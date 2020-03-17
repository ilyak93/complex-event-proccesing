package sase.base;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class PayloadFunctional {

    public static Payload
    substract(Payload x, Payload y) {
        if (x instanceof DeterministicPayload && y instanceof DeterministicPayload) {
            return subtractDD((DeterministicPayload) x, (DeterministicPayload) y);
        }
        if (x instanceof DeterministicPayload && y instanceof DiscreteDistributionPayload) {
            return subtractDDD((DeterministicPayload) x, (DiscreteDistributionPayload) y);
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DeterministicPayload) {
            return negative(subtractDDD((DeterministicPayload) y, (DiscreteDistributionPayload) x));
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DiscreteDistributionPayload) {
            return subtractDDDD((DiscreteDistributionPayload) y, (DiscreteDistributionPayload) x);
        }
        return null;
    }

    public static Payload
    abs(Payload x) {
        if (x instanceof DeterministicPayload) {
            return absD((DeterministicPayload) x);
        }
        if (x instanceof DiscreteDistributionPayload) {
            return absDD((DiscreteDistributionPayload) x);
        }
        return null;
    }

    public static Double
    equals(Payload x, Payload y) {
        if (x instanceof DeterministicPayload && y instanceof DeterministicPayload) {
            return equalsDD((DeterministicPayload) x, (DeterministicPayload) y);
        }
        if (x instanceof DeterministicPayload && y instanceof DiscreteDistributionPayload) {
            return equalsDDD((DeterministicPayload) x, (DiscreteDistributionPayload) y);
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DeterministicPayload) {
            return equalsDDD((DeterministicPayload) y, (DiscreteDistributionPayload) x);
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DiscreteDistributionPayload) {
            return equalsDDDD((DiscreteDistributionPayload) y, (DiscreteDistributionPayload) x);
        }
        return null;
    }

    public static Double
    lessThen(Payload x, Payload y) {
        if (x instanceof DeterministicPayload && y instanceof DeterministicPayload) {
            return lessThenDD((DeterministicPayload) x, (DeterministicPayload) y);
        }
        if (x instanceof DeterministicPayload && y instanceof DiscreteDistributionPayload) {
            return lessThenDDD((DeterministicPayload) x, (DiscreteDistributionPayload) y);
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DeterministicPayload) {
            return 1.0 - lessThenDDD((DeterministicPayload) y, (DiscreteDistributionPayload) x) - equals(x, y);
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DiscreteDistributionPayload) {
            return lessThenDDDD((DiscreteDistributionPayload) x, (DiscreteDistributionPayload) y);
        }
        return null;
    }

    private static DeterministicPayload negative(DeterministicPayload a) {
        return new DeterministicPayload(-a.getValue());
    }

    private static DiscreteDistributionPayload negative(DiscreteDistributionPayload b) {
        List<Pair<Double, Double>> valuesProbabilities1 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            Double newValue = -p2.getKey();
            Double valueProb = p2.getValue();
            result.add(new Pair<>(newValue, valueProb));
        }
        return new DiscreteDistributionPayload(result);
    }

    private static DeterministicPayload subtractDD(DeterministicPayload a, DeterministicPayload b) {
        return new DeterministicPayload(a.getValue() - b.getValue());
    }

    private static DiscreteDistributionPayload subtractDDD(DeterministicPayload a, DiscreteDistributionPayload b) {
        Double value = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities1 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            Double newValue = value - p2.getKey();
            Double valueProb = p2.getValue();
            result.add(new Pair<>(newValue, valueProb));
        }
        return new DiscreteDistributionPayload(result);
    }

    private static DiscreteDistributionPayload subtractDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                Double newValue = p1.getKey() - p2.getKey();
                Double valueProb = p1.getValue() * p2.getValue();
                result.add(new Pair<>(newValue, valueProb));
            }
        }
        return new DiscreteDistributionPayload(result);
    }

    private static DeterministicPayload absD(DeterministicPayload a) {
        return new DeterministicPayload(Math.abs(a.getValue()));
    }

    private static DiscreteDistributionPayload absDD(DiscreteDistributionPayload a) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            Double newValue = Math.abs(p2.getKey());
            Double valueProb = p2.getValue();
            result.add(new Pair<>(newValue, valueProb));
        }
        return new DiscreteDistributionPayload(result);
    }

    private static Double equalsDD(DeterministicPayload a, DeterministicPayload b) {
        return a.getValue() == b.getValue() ? 1.0 : 0.0;
    }

    private static Double equalsDDD(DeterministicPayload a, DiscreteDistributionPayload b) {
        Double value = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities1 = b.getValue();

        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            if (value == p2.getKey()) return p2.getValue();
        }
        return 0.0;
    }

    private static Double equalsDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                if (p1.getKey() == p2.getKey()) return p1.getValue() * p2.getValue();
            }
        }
        return 0.0;
    }

    private static Double lessThenDD(DeterministicPayload a, DeterministicPayload b) {
        return a.getValue() < b.getValue() ? 1.0 : 0.0;
    }

    private static Double lessThenDDD(DeterministicPayload a, DiscreteDistributionPayload b) {
        Double value = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities1 = b.getValue();
        Double lessProb = 0.0;
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            if (value < p2.getKey()) lessProb += p2.getValue();
        }
        return lessProb;
    }

    private static Double lessThenDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        Double lessProb = 0.0;
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                if (p1.getKey() < p2.getKey()) {
                    lessProb += p1.getValue() * p2.getValue();
                }
            }
        }
        Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>> res =
                updateLists(a, b);
        a.setValue(res.getKey());
        b.setValue(res.getValue());
        return lessProb;
    }

    private static Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>>
    updateLists(DiscreteDistributionPayload a, DiscreteDistributionPayload b) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        Set<Double> valuesToRemove = new HashSet<Double>();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            Boolean keppCurValue = false;
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                if (p1.getKey() < p2.getKey()) {
                    keppCurValue = true;
                }
            }
            if (keppCurValue == false) {
                valuesToRemove.add(p1.getKey());
            }
        }
        for(Double valueToRemove : valuesToRemove){
            valuesProbabilities1.removeIf(pair -> pair.getKey() == valueToRemove);
        }
        valuesToRemove.clear();
        for (Pair<Double, Double> p2 : valuesProbabilities2) {
            Boolean keppCurValue = false;
            for (Pair<Double, Double> p1 : valuesProbabilities1) {
                if (p2.getKey() > p1.getKey()) {
                    keppCurValue = true;
                }
            }
            if (keppCurValue == false) {
                valuesToRemove.add(p2.getKey());
            }
        }
        for(Double valueToRemove : valuesToRemove){
            valuesProbabilities2.removeIf(pair -> pair.getKey() == valueToRemove);
        }
        return new Pair(valuesProbabilities1, valuesProbabilities2);
    }
}
