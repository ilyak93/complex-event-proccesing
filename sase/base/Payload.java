package sase.base;

import javafx.util.Pair;

import java.util.*;

public abstract class Payload {

    protected Payload rightestOperand;
    protected Payload leftestOperand;

    public abstract void updatePayload(Object payloadValue);

    public abstract Boolean contains(Object o);

    //clone must be deep clone
    public abstract Payload clone();

    public Payload getRightestOperand(){
        return rightestOperand;
    }

    public void setRightestOperand(Payload payload){
        this.rightestOperand = payload;
    }

    public Payload getLeftestOperand(){
        return leftestOperand;
    }

    public void setLeftestOperand(Payload payload){
        this.leftestOperand = payload;
    }

    public Payload(){}

    public Payload(Payload p){
        this.leftestOperand = p.leftestOperand;
        this.rightestOperand = p.rightestOperand;
    }
    public static class PayloadValue{
        Pair<Double, Double> NDvalue;

        public PayloadValue(Pair<Double, Double> p) {
            NDvalue = p;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(NDvalue);
        }
    }

    public static class ConditionsGraph {
        List<PayLoadUpdateGraph> graphs;

        public ConditionsGraph(){
            graphs = new LinkedList<>();
        }


        private ConditionsGraph(ConditionsGraph cg,
                               Map<Pair<PayloadValue, Payload>, Pair<PayloadValue, Payload>> originCloneVertices){
            List<PayLoadUpdateGraph> newGraphs = new LinkedList<>();
            for(PayLoadUpdateGraph graph : cg.graphs){
                PayLoadUpdateGraph copy = graph.clone(originCloneVertices);
                newGraphs.add(copy);
            }
            this.graphs = newGraphs;
        }

        public Boolean empty(){
            return this.graphs.size() == 0;
        }

        public void addGraph(PayLoadUpdateGraph graph){
            this.graphs.add(graph);
        }

         private List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>>
        computeSelfPaths(Map<String, Map<Deque<PayLoadUpdateGraph.Vertex>,
                 List<Deque<PayLoadUpdateGraph.Vertex>>>> newPaths,
                         String firstEventType, String secondEventType){
             List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> newList = new LinkedList<>();
                for(Deque<PayLoadUpdateGraph.Vertex> firstPath :
                        newPaths.get(firstEventType).keySet()){
                    for(Deque<PayLoadUpdateGraph.Vertex> secondPath :
                            newPaths.get(firstEventType).get(firstPath)){
                        Map<String, Deque<PayLoadUpdateGraph.Vertex>> newMap = new HashMap<>();
                        newMap.put(firstEventType, firstPath);
                        newMap.put(secondEventType, secondPath);
                        newList.add(newMap);
                    }
                }
            return newList;
         }

        private List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>>
        computeFullCross(List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> allPaths1,
                         List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> allPaths2){
            List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> newList = new LinkedList<>();
            for(Map<String, Deque<PayLoadUpdateGraph.Vertex>> paths1 : allPaths1){
                for(Map<String, Deque<PayLoadUpdateGraph.Vertex>> paths2 : allPaths2){
                    Map<String, Deque<PayLoadUpdateGraph.Vertex>> newMap = new HashMap<>();
                    for(String oldEventType : paths1.keySet()){
                        newMap.put(oldEventType, paths1.get(oldEventType));
                    }
                    for(String newEventTypeName : paths2.keySet()){
                        newMap.put(newEventTypeName, paths2.get(newEventTypeName));
                    }
                    newList.add(newMap);
                }
            }
            return newList;
        }


        private List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>>
        computeConditionalCross(List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> allPaths1,
                                Map<String, Map<Deque<PayLoadUpdateGraph.Vertex>,
                                        List<Deque<PayLoadUpdateGraph.Vertex>>>> newPaths,
                                String leftEventTypeName,
                                String rightEventTypeName,
                                Boolean leftParticipating){
            List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> newList = new LinkedList<>();
            String common;
            String newEventTypeName;
            if(leftParticipating == false){
                common = leftEventTypeName;
                newEventTypeName = rightEventTypeName;
            } else {
                common = rightEventTypeName;
                newEventTypeName = leftEventTypeName;
            }
            for(Map<String, Deque<PayLoadUpdateGraph.Vertex>> paths1 : allPaths1){
                Map<Deque<PayLoadUpdateGraph.Vertex>,
                        List<Deque<PayLoadUpdateGraph.Vertex>>> pathsOfCommon = newPaths.get(common);
                for(Deque<PayLoadUpdateGraph.Vertex> path : pathsOfCommon.keySet()) {
                    if (path.equals(paths1.get(common))) {
                        for(Deque<PayLoadUpdateGraph.Vertex> newPath :  pathsOfCommon.get(path)) {
                            Map<String, Deque<PayLoadUpdateGraph.Vertex>> newMap = new HashMap<>();
                            for (String oldEventType : paths1.keySet()) {
                                newMap.put(oldEventType, paths1.get(oldEventType));
                            }
                            newMap.put(newEventTypeName, newPath);
                            newList.add(newMap);
                        }
                    }
                }
            }
            return newList;
        }


        private List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>>
        computeAllPaths(List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> allPaths,
                        Map<String, Map<Deque<PayLoadUpdateGraph.Vertex>,
                                List<Deque<PayLoadUpdateGraph.Vertex>>>> newPaths,
                        Boolean leftParticipating, Boolean rightParticipating,
                        String leftEventTypeName, String rightEventTypeName){
            if(leftParticipating == true && rightParticipating == true){
                List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>>
                        newPathesSeparated = computeSelfPaths(newPaths,
                                    leftEventTypeName, rightEventTypeName);
                if(allPaths.size() == 0){
                    allPaths = newPathesSeparated;
                } else{
                    allPaths = computeFullCross(allPaths, newPathesSeparated);
                }
            } else if(leftParticipating == true){
                allPaths = computeConditionalCross(allPaths, newPaths,
                        leftEventTypeName, rightEventTypeName, leftParticipating);
            } else if(rightParticipating == true){
                allPaths = computeConditionalCross(allPaths, newPaths,
                        leftEventTypeName, rightEventTypeName, leftParticipating);
            }
        return allPaths;
        }

        private Double computeFinalProb(List<Map<String,
                Deque<PayLoadUpdateGraph.Vertex>>> allPaths){
            Double finalProb = 0.0;
            for(Map<String,
                    Deque<PayLoadUpdateGraph.Vertex>> path : allPaths){
                Double curMult = 1.0;
                for(String key : path.keySet()){
                    Double pathMult = 1.0;
                    for(PayLoadUpdateGraph.Vertex p : path.get(key)) {
                        if(p.copy == true) continue; // it is a copy of a vertex because unary op
                        pathMult *= p.payloadValue.NDvalue.getValue();
                    }
                    curMult *= pathMult;
                }
                finalProb += curMult;
            }
            return finalProb;
        }

        public Double computeProbability(){
            if(this.graphs.isEmpty()) return 1.0;
            Double finalProb = 1.0;
            List<Map<String, Deque<PayLoadUpdateGraph.Vertex>>> allPaths = new LinkedList<>();
            Set<String> EventTypes = new HashSet<>();
            for(PayLoadUpdateGraph graph : this.graphs){
                Boolean leftParticipatingInComputation = true;
                Boolean rightParticipatingInComputation = true;
                if(EventTypes.contains(graph.leftEventType) &&
                        EventTypes.contains(graph.rightEventType)) continue;
                if(EventTypes.contains(graph.leftEventType)) {
                    leftParticipatingInComputation = false;
                }
                if(EventTypes.contains(graph.rightEventType)){
                    rightParticipatingInComputation = false;
                }
                List<Pair<PayloadValue, Payload>> roots = new LinkedList<>();
                List<Pair<Double, Double>> OperandValuesProbabilities = null;
                Payload father = null;
                OperandValuesProbabilities =
                            ((DiscreteDistributionPayload) graph.leftestOperand).
                                    getValue();
                father = graph.leftestOperand;

                for(Pair<Double, Double> p : OperandValuesProbabilities){
                    roots.add(new Pair(new Payload.PayloadValue(p), father));
                }

                Map<String, Map<Deque<PayLoadUpdateGraph.Vertex>,
                        List<Deque<PayLoadUpdateGraph.Vertex>>>> newPaths =
                        graph.depthFirstTraversalCompute(roots,
                        graph.leftEventType, graph.rightEventType);
                EventTypes.add(graph.leftEventType);
                EventTypes.add(graph.rightEventType);
                allPaths = computeAllPaths(allPaths, newPaths,
                        leftParticipatingInComputation,
                        rightParticipatingInComputation, graph.leftEventType,
                        graph.rightEventType);
                //finalProb*=prob;
            }
            return computeFinalProb(allPaths);
        }

        public ConditionsGraph clone(Map<Pair<PayloadValue, Payload>, Pair<PayloadValue, Payload>> originCloneVertices){
            return new ConditionsGraph(this, originCloneVertices);
        }


    }

    public static class PayLoadUpdateGraph{
        public void clear() {
            this.vertices.clear();
            this.adjVertices.clear();
            this.edges.clear();
        }

        static int id = 0;

        class Vertex {
            PayloadValue payloadValue;
            Payload payloadOfValue;
            Boolean visited;
            Boolean removed;
            Boolean copy;
            Vertex(PayloadValue payloadValue, Payload payloadOfValue) {
                this.payloadValue = payloadValue;
                this.payloadOfValue = payloadOfValue;
                visited = false;
                removed = false;
                copy = false;
            }
            // equals and hashCode
            @Override
            public boolean equals(Object obj) {
                return payloadValue.NDvalue.getKey() ==
                        ((((Vertex)obj).payloadValue)).NDvalue.getKey() &&
                        (payloadValue.NDvalue.getValue() ==
                                ((((Vertex)obj).payloadValue)).NDvalue.getValue());
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.payloadValue, this.payloadOfValue);
            }
        }

        class Edge {
            Vertex v1;
            Vertex v2;
            String operation;
            Edge(Vertex v1, Vertex v2, String operation){
                this.v1 = v1;
                this.v2 = v2;
                this.operation = operation;
            }
            // equals and hashCode


            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }
        private Vector<Vertex> vertices;
        private Map<Vertex, List<Vertex>> adjVertices;
        private Map<Pair<Vertex,Vertex>, Edge> edges;
        private Payload leftestOperand;
        private Payload rightestOperand;
        private String leftEventType;
        private String rightEventType;


        public PayLoadUpdateGraph(){
            this.vertices = new Vector();
            this.adjVertices = new HashMap<>();
            this.edges = new HashMap<>();
            this.leftestOperand = null;
            this.rightestOperand = null;
            this.leftEventType = null;
            this.rightEventType = null;
        }

        public Boolean notEmpty(){
            return !(this.vertices.isEmpty());
        }

        public void setLeftestEventType(String leftEventType) {
            this.leftEventType = leftEventType;
        }

        public void setRightestEventType(String rightEventType) {
            this.rightEventType = rightEventType;
        }

        public String getLeftestEventType(Payload leftestOperand) {
            return this.leftEventType;
        }

        public String getRightestEventType(Payload rightestOperand) {
            return this.rightEventType;
        }

        public void setLeftestOperand(Payload leftestOperand) {
            this.leftestOperand = leftestOperand;
        }

        public void setRightestOperand(Payload rightestOperand) {
            this.rightestOperand = rightestOperand;
        }

        private PayLoadUpdateGraph(Vector<Vertex> newVertices,
                                   Map<Vertex, List<Vertex>> newAdjVertices,
                                   Map<Pair<Vertex,Vertex>, Edge> newEdges,
                                   Payload left, Payload right,
                                   String leftEventType, String rightEventType){
            this.vertices = newVertices;
            this.adjVertices = newAdjVertices;
            this.edges = newEdges;
            this.leftestOperand = left;
            this.rightestOperand = right;
            this.leftEventType = leftEventType;
            this.rightEventType = rightEventType;
        }


        void addVertex(PayloadValue payloadValue, Payload payloadOfValue, Boolean copy) {
            Vertex toAdd = new Vertex(payloadValue, payloadOfValue);
            int idx = vertices.indexOf(toAdd);
            if(idx >= 0 && vertices.get(idx).hashCode() == payloadValue.hashCode()) {
                toAdd = vertices.get(idx);
            }
            toAdd.copy = copy;
            vertices.add(toAdd);
            Object result = adjVertices.putIfAbsent(toAdd, new ArrayList<>());
        }
        /*
        void removeVertex(Object payloadValue, Payload payloadOfValue) {
            Vertex v = new Vertex(payloadValue, payloadOfValue);
            adjVertices.values().stream().forEach(e -> e.remove(v));
            adjVertices.remove(payloadValue, payloadOfValue);
            List<Pair<Vertex, Vertex>> toRemove = new LinkedList<>();
            for(Pair<Vertex, Vertex> p : edges.keySet()){
                if (p.getKey().equals(new Vertex(payloadValue, payloadOfValue)) ||
                p.getValue().equals(new Vertex(payloadValue, payloadOfValue))){
                    toRemove.add(p);
                }
            }
            //edges.removeIf; // TODO: remove all edges
            for(Pair<Vertex, Vertex> p : edges.keySet()){
        }
        */

        private Vertex find(Vertex toFind){
            Vertex toReturn = null;
            for(Vertex v : this.vertices){
                if(v.equals(toFind) && v.payloadOfValue.hashCode() == toFind.payloadOfValue.hashCode()){
                    toReturn = v;
                }
            }
            return toReturn;
        }

        void addEdge(PayloadValue payloadValue1, Payload payloadOfValue1,
                     PayloadValue payloadValue2, Payload payloadOfValue2,
                     String operation) {
            Vertex v1 = find(new Vertex(payloadValue1, payloadOfValue1));
            assert(v1 != null);
            Vertex v2 = find(new Vertex(payloadValue2, payloadOfValue2));
            assert(v2 != null);
            List<Vertex> l = adjVertices.get(v1);
            l.add(v2);
            edges.put(new Pair<>(v1, v2), new Edge(v1, v2, operation)); // TODO: optimization: mark and remove when not marked
        }
        /*
        void removeEdge(Object payloadValue1, Payload payloadOfValue1,
                        Object payloadValue2, Payload payloadOfValue2) {
            Vertex v1 = new Vertex(payloadValue1, payloadOfValue1);
            Vertex v2 = new Vertex(payloadValue2, payloadOfValue2);
            List<Vertex> eV1 = adjVertices.get(v1);
            List<Vertex> eV2 = adjVertices.get(v2);
            if (eV1 != null)
                eV1.remove(v2);
            if (eV2 != null)
                eV2.remove(v1);
            edges = null; //TODO: remove edge
        }
        */
        List<Vertex> getAdjVertices(PayloadValue payloadValue, Payload payloadOfValue) {
            return adjVertices.get(new Vertex(payloadValue, payloadOfValue));
        }

        private boolean operatinIsMain(String operation){
            switch(operation){
                case "lessThen":
                    return true;
                default:
                    return false;
            }
        }
        /*
        void depthFirstTraversal(List<Pair<Object, Payload>> roots) {
            Set<Pair<Object, Payload>> visited = new LinkedHashSet<Pair<Object, Payload>>();
            Stack<Pair<Object, Payload>> stack1 = new Stack<Pair<Object, Payload>>();
            Stack<Double> stack2 = new Stack<Double>();
            for(Pair<Object, Payload> root : roots) {
                stack1.push(root);
                Double firstValue = 0.0;
                Double secondValue = 0.0;
                //String operation = null;
                int operationCount = 0;
                int curOperationCount = 0;
                while (!stack1.isEmpty()) {
                    Pair<Object, Payload> curP = stack1.pop();
                    Object payloadValue = curP.getKey();
                    Payload payloadOfValue = curP.getValue();
                    boolean sink = true;
                     if (!visited.contains(curP)) {
                        visited.add(curP);
                        for (Vertex v : this.getAdjVertices(payloadValue, payloadOfValue)) {
                            stack1.push(new Pair(v.payloadValue, v.payloadOfValue));
                            sink = false;
                            Edge edge = null;
                            for(Edge e: edges){
                                if(e.v1.payloadValue == payloadValue &&
                                        e.v1.payloadOfValue == payloadOfValue &&
                                        e.v2.payloadValue == v.payloadValue &&
                                        e.v2.payloadOfValue == v.payloadOfValue
                                ){
                                    edge = e;
                                }
                            }
                            if(operationCount == curOperationCount+1 && operationIsMain(edge.operation)){
                                secondValue
                            }
                        }
                        if(sink == true){
                            //compute first value
                            //compute second value
                            //make comparasion
                            //if true mark edges
                        }
                    }
                }
            }
        }
        */

        private Boolean isUnary(String operation){
            switch (operation){
                case "abs":
                    return true;
                default:
                    return false;
            }
        }

        private Boolean isBinary(String operation){
            switch (operation){
                case "substract":
                    return true;
                default:
                    return false;
            }
        }


        private Pair<Double, Double> evaluateBinaryOperation(Vertex v1, Vertex v2, String operation){
            switch(operation){
                case "substract":

                    Double value1 = v1.payloadValue.NDvalue.getKey();
                    Double value2 = (v2.payloadValue).NDvalue.getKey();
                    Double result = value1 - value2;
                    result = (double)Math.round(result * 10000000000l) / 10000000000l;
                    return new Pair(result,
                            (v1.payloadValue).NDvalue.getValue() *
                                    (v2.payloadValue).NDvalue.getValue());
                default:
                    return null;
            }
        }

        private Pair<Double, Double> evaluateUnaryOperation(Pair<Double, Double> p, String operation){
            switch(operation){
                case "abs":
                    return new Pair(Math.abs(p.getKey()), p.getValue()) ;
                default:
                    return null;
            }
        }

        private Boolean evaluateMainOperation(Pair<Double,Double> p1,
                                          Pair<Double,Double> p2,
                                          String operation){
            switch(operation){
                case "lessThen":
                    Double value1 = p1.getKey();
                    Double value2 = p2.getKey();
                    return value1 < value2 && p1.getValue() *p2.getValue() != 0.0;
                default:
                    return null;
            }
        }

        Double depthFirstTraversal(List<Pair<PayloadValue, Payload>> roots) {
            Set<Pair<PayloadValue, Payload>> visited = new LinkedHashSet<Pair<PayloadValue, Payload>>();
            Stack<Vertex> stackOfVisited = new Stack<>();
            Double result = new Double(0.0);
            for(Pair<PayloadValue, Payload> root : roots) {
                Pair<Double,Double> firstValue = new Pair(0.0, 1.0);
                Pair<Double,Double> secondValue = new Pair(0.0, 1.0);
                PayloadValue pv = root.getKey();
                Payload p = root.getValue();
                Vertex rt = new Vertex(root.getKey(), root.getValue());
                for( Vertex v : this.adjVertices.keySet()){
                    if(v!= null && v.equals(rt)){
                        rt = v;
                    }
                }
                if(rt.removed == true) continue;
                stackOfVisited.push(rt);
                result += depthFirstTraversalAux(rt, firstValue, secondValue, null, stackOfVisited);
                stackOfVisited.pop();

            }
            for(Vertex v : adjVertices.keySet()){
                if(v!= null && v.visited == false){
                    v.payloadOfValue.updatePayload(v.payloadValue);
                    v.removed = true;
                }
            }
        return result;
        }

        private Double depthFirstTraversalAux(Vertex root,
                                    Pair<Double, Double> firstValue,
                                    Pair<Double, Double> secondValue,
                                    String operation,
                                    Stack<Vertex> stackOfVisited) {
            Double fResult = 0.0;
            boolean sink = true;
            List<Vertex> adjs = this.getAdjVertices(root.payloadValue, root.payloadOfValue);
            if(adjs == null) return 0.0;
            for (Vertex adj : adjs) {
                if(adj == null || adj.removed == true || adj.payloadOfValue.contains(adj.payloadValue) == false) continue;
                sink = false;
                Edge e = edges.get(new Pair(root, adj));
                if((operation != null) || operatinIsMain(e.operation)){
                    if(operatinIsMain(e.operation)){
                        stackOfVisited.push(adj);
                        fResult += depthFirstTraversalAux(adj, firstValue, secondValue, e.operation, stackOfVisited);
                        stackOfVisited.pop();
                    } else {
                        Pair<Double, Double> newSecondValue = null;
                        if(isBinary(e.operation)) {
                            newSecondValue = evaluateBinaryOperation(root, adj, e.operation);
                            newSecondValue = new Pair<>(
                                    newSecondValue.getKey() + secondValue.getKey(),
                                    newSecondValue.getValue() * secondValue.getValue());
                        } else if(isUnary(e.operation)){
                            newSecondValue = evaluateUnaryOperation(secondValue, e.operation);
                        }
                        stackOfVisited.push(adj);
                        fResult += depthFirstTraversalAux(adj, firstValue, newSecondValue, operation, stackOfVisited);
                        stackOfVisited.pop();
                    }

                } else {
                    Pair<Double, Double> newFirstValue = null;
                    if(isBinary(e.operation)) {
                        newFirstValue = evaluateBinaryOperation(root, adj, e.operation);
                        newFirstValue = new Pair<Double, Double>(
                                newFirstValue.getKey() + firstValue.getKey(),
                                newFirstValue.getValue() * firstValue.getValue());
                    } else if(isUnary(e.operation)){
                        newFirstValue = evaluateUnaryOperation(firstValue, e.operation);
                    }
                    stackOfVisited.push(adj);
                    fResult += depthFirstTraversalAux(adj, newFirstValue, secondValue, operation, stackOfVisited);
                    stackOfVisited.pop();
                }
            }
            if(sink == true){
                if(operation == null) return 0.0;
                if(evaluateMainOperation(firstValue,secondValue, operation) == true) {
                    for(Vertex v : stackOfVisited){
                        v.visited = true;
                    }
                    Double pathEvaluationResult = firstValue.getValue() *
                            secondValue.getValue();
                    return pathEvaluationResult;
                }
            }
            return fResult;
        }

        public PayLoadUpdateGraph clone(Map<Pair<PayloadValue, Payload>, Pair<PayloadValue, Payload>> originCloneVertices) {
            Vector<Vertex> newVertices = new Vector<>();
            Map<Vertex, Vertex> newVerticesAux = new HashMap<>();
            Map<Vertex, List<Vertex>> newAdjVertices = new HashMap<>();
            Map<Pair<Vertex,Vertex>, Edge> newEdges = new HashMap<>();

            Map<Payload, Payload> copies = new HashMap<>();

            for(Vertex v: this.vertices){
                if(v.removed == true) continue;
                Pair<PayloadValue, Payload> newVertexData = originCloneVertices.get(new Pair(v.payloadValue, v.payloadOfValue));
                if(newVertexData == null ){ //that means its a replica (of unary operator for exmaple) an not real payload pair, then create it now
                    Payload p = copies.get(v.payloadOfValue);
                    DiscreteDistributionPayload payloadOfValue = null;
                    if(p != null){
                       payloadOfValue = (DiscreteDistributionPayload)(p);
                    } else {
                        payloadOfValue =
                                new DiscreteDistributionPayload(new LinkedList<>());
                    }
                    Pair<Double, Double> newPair = new Pair<>(
                            (v.payloadValue).NDvalue.getKey(),
                            (v.payloadValue).NDvalue.getValue());
                    payloadOfValue.addValue(newPair);
                    copies.put(v.payloadOfValue, payloadOfValue);
                    newVertexData = new Pair(new PayloadValue(newPair), payloadOfValue);

                }
                Vertex newVertex = new Vertex(newVertexData.getKey(), newVertexData.getValue());
                newVertex.copy = v.copy;
                newVertices.add(newVertex);
                newVerticesAux.put(v, newVertex);
            }

            for(Vertex v: this.adjVertices.keySet()){
                Vertex newVertex = newVerticesAux.get(v);
                List<Vertex> adjs = this.adjVertices.get(v);
                List<Vertex> newAdjs = new LinkedList<>();
                for(Vertex adj : adjs){
                    Vertex newAdj = newVerticesAux.get(adj);
                    newAdjs.add(newAdj);
                    Edge e = edges.get(new Pair(v, adj));
                    String operation = e != null ? e.operation : null;
                    newEdges.put(new Pair(newVertex, newAdj), new Edge(newVertex, newAdj, operation));
                }
                newAdjVertices.put(newVertex, newAdjs);
            }
            return new PayLoadUpdateGraph(newVertices, newAdjVertices,
                    newEdges, this.leftestOperand, this.rightestOperand,
                    this.leftEventType, this.rightEventType);
        }

        Map<String, Map<Deque<Vertex>, List<Deque<Vertex>>>>
        depthFirstTraversalCompute(List<Pair<PayloadValue, Payload>> roots,
                                   String leftEventType,
                                   String rightEventType) {
            Deque<Vertex> stackOfVisited = new LinkedList<>();
            Deque<Vertex> stackOfVisited1 = new LinkedList<>();
            Deque<Vertex> stackOfVisited2 = new LinkedList<>();
            Map<String,
                    Map<Deque<Vertex>,
                            List<Deque<Vertex>>>> newPaths = new HashMap<>();
            Map<Deque<Vertex>,
                    List<Deque<Vertex>>> newMap1 = new HashMap<>();
            Map<Deque<Vertex>,
                    List<Deque<Vertex>>> newMap2 = new HashMap<>();
            newPaths.put(leftEventType, newMap1);
            newPaths.put(rightEventType, newMap2);
            for(Pair<PayloadValue, Payload> root : roots) {
                Pair<Double,Double> firstValue = new Pair(0.0, 1.0);
                Pair<Double,Double> secondValue = new Pair(0.0, 1.0);
                Vertex rt = new Vertex(root.getKey(), root.getValue());
                for( Vertex v : this.adjVertices.keySet()){
                    if(v!= null && v.equals(rt)){
                        rt = v;
                    }
                }
                if(rt.removed == true) continue;
                stackOfVisited.push(rt);
                stackOfVisited1.push(rt);
                depthFirstTraversalComputeAux(rt, firstValue, secondValue,
                        null, stackOfVisited, stackOfVisited1,
                        stackOfVisited2,
                        leftEventType, rightEventType,
                        newPaths);
                stackOfVisited.pop();
                stackOfVisited1.pop();

            }

            for(Vertex v : adjVertices.keySet()){
                if(v!= null && v.visited == false){
                    v.payloadOfValue.updatePayload(v.payloadValue);
                    v.removed = true;
                }
            }
            return newPaths;
        }

        private void depthFirstTraversalComputeAux(
                Vertex root, Pair<Double, Double> firstValue,
                Pair<Double, Double> secondValue,
                String operation, Deque<Vertex> stackOfVisited,
                Deque<Vertex> stackOfVisited1, Deque<Vertex> stackOfVisited2,
                String leftEventType, String rightEventType,
                Map<String, Map<Deque<Vertex>, List<Deque<Vertex>>>> newPaths) {

            boolean sink = true;
            List<Vertex> adjs = this.getAdjVertices(root.payloadValue, root.payloadOfValue);
            if(adjs == null) return; // TODO: remove and fix as appropriate
            for (Vertex adj : adjs) {
                if(adj == null || adj.removed == true || adj.payloadOfValue.contains(adj.payloadValue) == false) continue;
                sink = false;
                Edge e = edges.get(new Pair(root, adj));
                if((operation != null) || operatinIsMain(e.operation)){
                    if(operatinIsMain(e.operation)){
                        Deque<Vertex> newStackOfVisited1 = new LinkedList<>((LinkedList)(stackOfVisited1));
                        Deque<Vertex> newPath= newStackOfVisited1;
                        stackOfVisited.push(adj);
                        stackOfVisited2.push(adj);
                        depthFirstTraversalComputeAux(adj, firstValue, secondValue, e.operation, stackOfVisited,
                                stackOfVisited1, stackOfVisited2,
                                leftEventType, rightEventType, newPaths);
                        stackOfVisited.pop();
                        stackOfVisited2.pop();

                    } else {
                        Pair<Double, Double> newSecondValue = null;
                        if(isBinary(e.operation)) {
                            newSecondValue = evaluateBinaryOperation(root, adj, e.operation);
                            newSecondValue = new Pair<>(
                                    newSecondValue.getKey() + secondValue.getKey(),
                                    newSecondValue.getValue() * secondValue.getValue());
                        } else if(isUnary(e.operation)){
                            newSecondValue = evaluateUnaryOperation(secondValue, e.operation);
                        }
                        stackOfVisited.push(adj);
                        stackOfVisited2.push(adj);
                        depthFirstTraversalComputeAux(adj, firstValue,
                                newSecondValue, operation, stackOfVisited,
                                stackOfVisited1, stackOfVisited2,
                                leftEventType, rightEventType, newPaths);
                        stackOfVisited.pop();
                        stackOfVisited2.pop();
                    }

                } else {
                    Pair<Double, Double> newFirstValue = null;
                    if(isBinary(e.operation)) {
                        newFirstValue = evaluateBinaryOperation(root, adj, e.operation);
                        newFirstValue = new Pair<Double, Double>(
                                newFirstValue.getKey() + firstValue.getKey(),
                                newFirstValue.getValue() * firstValue.getValue());
                    } else if(isUnary(e.operation)){
                        newFirstValue = evaluateUnaryOperation(firstValue, e.operation);
                    }
                    stackOfVisited.push(adj);
                    stackOfVisited1.push(adj);
                    depthFirstTraversalComputeAux(adj, newFirstValue,
                            secondValue, operation, stackOfVisited,
                            stackOfVisited1, stackOfVisited2,
                            leftEventType, rightEventType, newPaths);
                    stackOfVisited.pop();
                    stackOfVisited1.pop();
                }
            }
            if(sink == true){
                if(operation == null) return;
                if(evaluateMainOperation(firstValue, secondValue, operation) == true) {
                    for(Vertex v : stackOfVisited){
                        v.visited = true;
                    }

                    Deque<Vertex> newStackOfVisited1 =
                            new LinkedList<>((LinkedList)(stackOfVisited1));

                    List<Deque<Vertex>> leftListOfPathes = newPaths.get(leftEventType).get(stackOfVisited1);
                    if(leftListOfPathes == null){
                        leftListOfPathes = new LinkedList<>();
                        newPaths.get(leftEventType).put(
                                newStackOfVisited1, leftListOfPathes);
                    }


                    Deque<Vertex> newStackOfVisited2 =
                            new LinkedList<>((LinkedList)(stackOfVisited2));

                    newPaths.get(leftEventType).get(
                            stackOfVisited1).add(newStackOfVisited2);


                    List<Deque<Vertex>> rightListOfPathes = newPaths.get(rightEventType).get(stackOfVisited2);
                    if(rightListOfPathes == null){
                        rightListOfPathes = new LinkedList<>();
                        newPaths.get(rightEventType).put(
                                newStackOfVisited2, rightListOfPathes);
                    }

                    newPaths.get(rightEventType).get(
                            stackOfVisited2).add(newStackOfVisited1);
                }
            }
        }

    }
}

