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
    plus(Payload x, Payload y, Payload.PayLoadUpdateGraph graph) {
        /*
        if (x instanceof DeterministicPayload && y instanceof DeterministicPayload) {
            return subtractDD((DeterministicPayload) x, (DeterministicPayload) y);
        }
        if (x instanceof DeterministicPayload && y instanceof DiscreteDistributionPayload) {
            return subtractDDD((DeterministicPayload) x, (DiscreteDistributionPayload) y);
        }
        if (x instanceof DiscreteDistributionPayload && y instanceof DeterministicPayload) {
            return negative(subtractDDD((DeterministicPayload) y, (DiscreteDistributionPayload) x));
        }
         */
        if (x instanceof DiscreteDistributionPayload && y instanceof DiscreteDistributionPayload) {
            Payload result = plusDDDD((DiscreteDistributionPayload) x, (DiscreteDistributionPayload) y, graph);
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
        double res = (double)Math.round((a.getValue() - b.getValue()) * 10000000000l) / 10000000000l;
        return new DeterministicPayload(res);
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

    private static DiscreteDistributionPayload plusDDD(
            DiscreteDistributionPayload a, DeterministicPayload b,
            Payload.PayLoadUpdateGraph graph) {
        DiscreteDistributionPayload DDb = new DiscreteDistributionPayload(b);
        return plusDDDD(a, DDb, graph);
    }

    private static DiscreteDistributionPayload correlationDDDD(
            DiscreteDistributionPayload[] A, DiscreteDistributionPayload[] B,
            Payload.PayLoadUpdateGraph graph){
        DiscreteDistributionPayload averageA = plusDDDD(A[0], A[1], graph);
        DiscreteDistributionPayload averageB = plusDDDD(B[0], B[1], graph);
        for(int i = 2; i < A.length; i++){
            averageA = plusDDDD(averageA, A[2], graph);
        }
        for(int i = 2; i < B.length; i++){
            averageB = plusDDDD(averageB, B[2], graph);
        }
        DiscreteDistributionPayload SigmaNumerator =
                multiplyDDDD(
                        subtractDDDD(A[0], averageA, graph),
                        subtractDDDD(B[0], averageB, graph), graph);
        for(int i = 1; i < A.length; i++){
            SigmaNumerator = plusDDDD(multiplyDDDD(
                    subtractDDDD(A[i], averageA, graph),
                    subtractDDDD(B[i], averageB, graph), graph)
            ,SigmaNumerator,graph);
        }
        DiscreteDistributionPayload sqrtSigmaDenumerator1 =
                sqrtDD(
                        multiplyDDDD(
                                subtractDDDD(A[0], averageA, graph),
                                subtractDDDD(A[0], averageA, graph),
                                graph),
                        graph);
        DiscreteDistributionPayload sqrtSigmaDenumerator2 =
                sqrtDD(
                        multiplyDDDD(
                                subtractDDDD(B[0], averageB, graph),
                                subtractDDDD(B[0], averageB, graph), graph)
                , graph);
        DiscreteDistributionPayload totalDenumerator = multiplyDDDD(
                sqrtSigmaDenumerator1, sqrtSigmaDenumerator2, graph);

        DiscreteDistributionPayload corr = devideDDDD(SigmaNumerator, totalDenumerator, graph);
        return corr;
    }

    private static DiscreteDistributionPayload plusDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b,
            Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            graph.addVertex(new Payload.PayloadValue(p1), a, false);
        }
        for (Pair<Double, Double> p2 : valuesProbabilities2) {
            graph.addVertex(new Payload.PayloadValue(p2), b, false);
        }
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                Double newValue = p1.getKey() - p2.getKey();
                Double valueProb = p1.getValue() * p2.getValue();
                result.add(new Pair<>(newValue, valueProb));
                graph.addEdge(new Payload.PayloadValue(p1), a, new Payload.PayloadValue(p2), b, "plus");
            }
        }
        return new DiscreteDistributionPayload(result);
    }

    private static DiscreteDistributionPayload devideDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b,
            Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            graph.addVertex(new Payload.PayloadValue(p1), a, false);
        }
        for (Pair<Double, Double> p2 : valuesProbabilities2) {
            graph.addVertex(new Payload.PayloadValue(p2), b, false);
        }
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                assert(p2.getKey() == 0.0);
                Double newValue = p1.getKey() / p2.getKey();
                Double valueProb = p1.getValue() * p2.getValue();
                result.add(new Pair<>(newValue, valueProb));
                graph.addEdge(new Payload.PayloadValue(p1), a, new Payload.PayloadValue(p2), b, "div");
            }
        }
        return new DiscreteDistributionPayload(result);
    }

    private static DiscreteDistributionPayload multiplyDDDD(
            DiscreteDistributionPayload a, DiscreteDistributionPayload b,
            Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List<Pair<Double, Double>> valuesProbabilities2 = b.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            graph.addVertex(new Payload.PayloadValue(p1), a, false);
        }
        for (Pair<Double, Double> p2 : valuesProbabilities2) {
            graph.addVertex(new Payload.PayloadValue(p2), b, false);
        }
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                Double newValue = p1.getKey() * p2.getKey();
                Double valueProb = p1.getValue() * p2.getValue();
                result.add(new Pair<>(newValue, valueProb));
                graph.addEdge(new Payload.PayloadValue(p1), a, new Payload.PayloadValue(p2), b, "mult");
            }
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
            graph.addVertex(new Payload.PayloadValue(p1), a, false);
        }
        for (Pair<Double, Double> p2 : valuesProbabilities2) {
            graph.addVertex(new Payload.PayloadValue(p2), b, false);
        }
        for (Pair<Double, Double> p1 : valuesProbabilities1) {
            for (Pair<Double, Double> p2 : valuesProbabilities2) {
                Double newValue = p1.getKey() - p2.getKey();
                Double valueProb = p1.getValue() * p2.getValue();
                result.add(new Pair<>(newValue, valueProb));
                graph.addEdge(new Payload.PayloadValue(p1), a, new Payload.PayloadValue(p2), b, "substract");
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
            graph.addVertex(new Payload.PayloadValue(newP), righestCopy, true);
            graph.addEdge(new Payload.PayloadValue(p1), a.rightestOperand, new Payload.PayloadValue(newP), righestCopy, "abs");
        }
        return res;
    }

    private static DiscreteDistributionPayload sqrtDD(DiscreteDistributionPayload a,
                                                     Payload.PayLoadUpdateGraph graph) {
        List<Pair<Double, Double>> valuesProbabilities1 = a.getValue();
        List result = new LinkedList();
        for (Pair<Double, Double> p2 : valuesProbabilities1) {
            assert(p2.getKey() >= 0);
            Double newValue = Math.sqrt(p2.getKey());
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
            graph.addVertex(new Payload.PayloadValue(newP), righestCopy, true);
            graph.addEdge(new Payload.PayloadValue(p1), a.rightestOperand,
                    new Payload.PayloadValue(newP), righestCopy, "sqrt");
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
                graph.addEdge(new Payload.PayloadValue(p1), a.rightestOperand, new Payload.PayloadValue(p2), b.leftestOperand, "lessThen");
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
        List<Pair<Payload.PayloadValue, Payload>> roots = new LinkedList<>();
        for(Pair<Double, Double> p : leftestOperandValuesProbabilities){
            roots.add(new Pair(new Payload.PayloadValue(p), a.leftestOperand));
        }
        return graph.depthFirstTraversal(roots);
    }
}
