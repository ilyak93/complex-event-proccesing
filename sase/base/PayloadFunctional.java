package sase.base;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public abstract class PayloadFunctional {

    public static Payload
    substract(Payload x, Payload y, Payload.PayLoadUpdateGraph graph) {
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
            Payload result = subtractDDDD((DiscreteDistributionPayload) x, (DiscreteDistributionPayload) y, graph);
            result.setRightestOperand(y.getRightestOperand());
            result.setLeftestOperand(x.getLeftestOperand());
            return result;
        }
        return null;
    }

    public static Payload
    abs(Payload x, Payload.PayLoadUpdateGraph graph) {
        if (x instanceof DeterministicPayload) {
            return absD((DeterministicPayload) x);
        }
        if (x instanceof DiscreteDistributionPayload) {
            return absDD((DiscreteDistributionPayload) x, graph);
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
    lessThen(Payload x, Payload y, Payload.PayLoadUpdateGraph graph) {
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
            return lessThenDDDD((DiscreteDistributionPayload) x, (DiscreteDistributionPayload) y, graph);
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
            DiscreteDistributionPayload a, DiscreteDistributionPayload b,
            Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            graph.addVertex(p1, a);
        }
        for (Pair<Double, Double> p2 : valuesProbabilities2) {
            graph.addVertex(p2, b);
        }
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                Double newValue = p1.getKey() - p2.getKey();
                Double valueProb = p1.getValue() * p2.getValue();
                result.add(new Pair<>(newValue, valueProb));
                graph.addEdge(p1, a, p2, b, "substract");
            }
        }
        return new DiscreteDistributionPayload(result);
    }

    private static DeterministicPayload absD(DeterministicPayload a) {
        return new DeterministicPayload(Math.abs(a.getValue()));
    }

    private static DiscreteDistributionPayload absDD(DiscreteDistributionPayload a,
                                                     Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            Double newValue = Math.abs(p2.getKey());
            Double valueProb = p2.getValue();
            result.add(new Pair<>(newValue, valueProb));
        }
        DiscreteDistributionPayload res = new DiscreteDistributionPayload(result);
        DiscreteDistributionPayload righestCopy = new DiscreteDistributionPayload(
                ((DiscreteDistributionPayload)a.rightestOperand.getRightestOperand()).valuesProbabilities);
        res.setRightestOperand(righestCopy);
        res.setLeftestOperand(a.getLeftestOperand());
        for (Pair<Double, Double> p1 : ((DiscreteDistributionPayload)a.getRightestOperand()).valuesProbabilities) {
            Pair<Double, Double> newP = new Pair(p1.getKey(), p1.getValue());
            graph.addVertex(newP, righestCopy);
            graph.addEdge(p1, a.rightestOperand, newP, righestCopy, "abs");
        }
        return res;
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

    private static Double lessThenDD(DeterministicPayload a,
                                     DeterministicPayload b) {
        return a.getValue() < b.getValue() ? 1.0 : 0.0;
    }

    private static Double lessThenDDD(DeterministicPayload a,
                                      DiscreteDistributionPayload b) {
        Double value = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities1 = b.getValue();
        Double lessProb = 0.0;
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            if (value < p2.getKey()) lessProb += p2.getValue();
        }
        return lessProb;
    }

    private static Double lessThenDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b,
            Payload.PayLoadUpdateGraph graph) {
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
        List<Pair<Double, Double>> rightestOperandValuesProbabilities =
                ((DiscreteDistributionPayload)a.getRightestOperand()).
                        getValue();
        List<Pair<Double, Double>> leftestOperandValuesProbabilities =
                ((DiscreteDistributionPayload)b.getLeftestOperand()).
                        getValue();
        for(Pair<Double, Double> p1 : rightestOperandValuesProbabilities){
            for(Pair<Double, Double> p2 : leftestOperandValuesProbabilities){
                graph.addEdge(p1, a.rightestOperand, p2, b.leftestOperand, "lessThen");
            }
        }
        Double res = updateLists(a, b, graph);
        assert res != lessProb;
        return lessProb;
    }

    /*
    private static Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>>
    updateLists(DiscreteDistributionPayload a, DiscreteDistributionPayload b) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List<Double> valuesToRemove = new LinkedList<Double>();
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
     */

    private static Double
    updateLists(DiscreteDistributionPayload a, DiscreteDistributionPayload b,
                Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> leftestOperandValuesProbabilities =
                ((DiscreteDistributionPayload)a.getLeftestOperand()).
                        getValue();
        List<Pair<Object, Payload>> roots = new LinkedList<>();
        for(Pair<Double, Double> p : leftestOperandValuesProbabilities){
            roots.add(new Pair(p, a.leftestOperand));
        }
        return graph.depthFirstTraversal(roots);
    }
}
