package org.juancatalan.edgepaircoverage.controlFlow;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.graphs.BooleanEdge;
import org.juancatalan.edgepaircoverage.graphs.EdgeType;
import org.juancatalan.edgepaircoverage.utils.MapUtils;
import org.objectweb.asm.tree.*;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IFNONNULL;

public class ControlFlowAnalyser {
    private boolean isBooleanAssignmentPredicateNode;
    private Map<String, DirectedPseudograph<AbstractInsnNode, BooleanEdge>> controlGraphs = new HashMap<>();
    private Map<String, Map<AbstractInsnNode, Integer>> nodoToInteger = new HashMap<>();
    private Map<String, Map<Integer, Integer>> nodoToLinenumber = new HashMap<>();

    public ControlFlowAnalyser(boolean isBooleanAssignmentPredicateNode) {
        this.isBooleanAssignmentPredicateNode = isBooleanAssignmentPredicateNode;
    }


    public Map<String, Map<AbstractInsnNode, Integer>> getNodoToInteger() {
        return nodoToInteger;
    }

    public Map<String, Map<Integer, Integer>> getNodoToLinenumber() {
        return nodoToLinenumber;
    }

    public Map<Integer, String> getMappingFromNodoToLinenumber(String metodo) {
        Map<Integer, String> map = new HashMap<>();
        Map<Integer, Integer> nodoToLineNumberMap = nodoToLinenumber.get(metodo);
        Map<Integer, List<Integer>> valuesToKeysMap = MapUtils.invertMap(nodoToLineNumberMap);

        for (Integer key : nodoToLineNumberMap.keySet()) {
            Integer value = nodoToLineNumberMap.get(key);
            if (valuesToKeysMap.get(value).size() > 1){
                map.put(key, value.toString() + "." + (valuesToKeysMap.get(value).indexOf(key) + 1));
            }
            else map.put(key, value.toString());
        }
        return map;
    }

    private boolean isPredicateNode(AbstractInsnNode in){
        int opCode = in.getOpcode();
        return ((opCode >= IFEQ && opCode <= IF_ACMPNE) ||
                (opCode >= IRETURN && opCode <= RETURN) ||
                (opCode >= IFNULL && opCode <= IFNONNULL) ||
                (opCode == ATHROW));
    }

    private Optional<BooleanAssignmentInfo> isBranchBooleanAssignment(AbstractInsnNode in){
        AbstractInsnNode auxNode = in;
        boolean value;
        // Paso del bytecode auxiliar
        while (auxNode.getOpcode() < 0){
            auxNode = auxNode.getNext();
        }
        if (auxNode.getOpcode() == ICONST_0) value = false;
        else if (auxNode.getOpcode() == ICONST_1) value = true;
        else return Optional.empty();
        //System.out.println("ICONST");
        auxNode = auxNode.getNext();
        // Paso del bytecode auxiliar
        while (auxNode.getOpcode() < 0){
            auxNode = auxNode.getNext();
        }
        if (auxNode.getOpcode() == GOTO) {
            auxNode = findJumpDestiny((JumpInsnNode) auxNode);
            //System.out.println("GOTO");
        }
        while (auxNode.getOpcode() < 0){
            auxNode = auxNode.getNext();
        }
        if (auxNode.getOpcode() == ISTORE) {
            int index = ((VarInsnNode) auxNode).var;
            return Optional.of(new BooleanAssignmentInfo(value, index));
        }
        else return Optional.empty();
    }

    private boolean isBooleanAssignment(AbstractInsnNode in){
        if (!(in instanceof JumpInsnNode)) return false;
        Optional<BooleanAssignmentInfo> caminoSaltoBool, caminoNormalBool;
        //System.out.println("Checkeando boolean assignment");
        AbstractInsnNode auxNode = findJumpDestiny((JumpInsnNode) in);
        //System.out.println("Por salto");
        //System.out.println(findLinenumber(in));
        caminoSaltoBool = isBranchBooleanAssignment(auxNode);
        //System.out.println("Por normal");
        //System.out.println(findLinenumber(in.getNext()));
        caminoNormalBool = isBranchBooleanAssignment(in.getNext());
        // Si queremos identificar puertas logicas anidadas
        if (caminoSaltoBool.isPresent() && caminoNormalBool.isEmpty()){
            return isBooleanAssignment(findNextPredicateNode(in.getNext()));
        }
        return caminoNormalBool.isPresent() && caminoSaltoBool.isPresent()
                && caminoNormalBool.get().value() != caminoSaltoBool.get().value()
                && Objects.equals(caminoNormalBool.get().index(), caminoSaltoBool.get().index());
    }

    public AbstractInsnNode findJumpDestiny(JumpInsnNode in){
        LabelNode target = in.label;

        AbstractInsnNode nextNode = in.getNext();
        while (nextNode != null){
            if ((nextNode instanceof LabelNode) &&
                    target.equals(nextNode)
            ){
                // Avanzo si es bytecode auxiliar
                //while (nextNode.getOpcode() < 0) nextNode = nextNode.getNext();
                return nextNode;
            }
            nextNode = nextNode.getNext();
        }

        AbstractInsnNode previousNode = in.getPrevious();
        while (previousNode != null){
            if ((previousNode instanceof LabelNode) &&
                    target.equals(previousNode)
            ){
                return previousNode;
            }
            previousNode = previousNode.getPrevious();
        }

        return null;
    }

    public AbstractInsnNode findGotoDestiny(JumpInsnNode in){
        AbstractInsnNode destiny = findJumpDestiny(in);
        if (destiny != null) {
            // Busco que sea nodo predicado
            while (!isPredicateNode(destiny)){
                if (destiny.getOpcode() == GOTO){
                    return findGotoDestiny((JumpInsnNode) destiny);
                }
                destiny = destiny.getNext();
            }
        }
        return destiny;
    }

    private AbstractInsnNode findNextPredicateNode(AbstractInsnNode in){
        AbstractInsnNode target = in;
        while (target != null){
            int opTarget = target.getOpcode();
            // Si es un goto cuidado revisar
            if (opTarget == GOTO){
                AbstractInsnNode destiny = this.findGotoDestiny((JumpInsnNode) target);
                if (destiny != null){
                    return destiny;
                }
            }
            // Si no lo es comprobar si estamos en un nodo predicado o de fin
            if (isPredicateNode(target)
            ) {
                return target;
            }
            target = target.getNext();
        }
        return null;
    }

    private Integer findLinenumber(AbstractInsnNode in){
        AbstractInsnNode target = in.getPrevious();
        while (target != null){
            if (target instanceof LineNumberNode){
                return ((LineNumberNode) target).line;
            }
            target = target.getPrevious();
        }
        target = in.getNext();
        while (target != null){
            if (target instanceof LineNumberNode){
                return ((LineNumberNode) target).line;
            }
            target = target.getNext();
        }
        System.out.println("No encuentra");
        return null;
    }

    public void analyze(String idMetodo, InsnList insns){
        if (!nodoToInteger.containsKey(idMetodo)) nodoToInteger.put(idMetodo, new HashMap<>());
        if (!nodoToLinenumber.containsKey(idMetodo)) nodoToLinenumber.put(idMetodo, new HashMap<>());

        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlGraph = new DirectedPseudograph<>(BooleanEdge.class);
        Iterator<AbstractInsnNode> j = insns.iterator();
        Integer indiceNodo = 1;
        Map<AbstractInsnNode, Integer> nodeIntegerMap = nodoToInteger.get(idMetodo);
        Map<Integer, Integer> nodeLinenumberMap = nodoToLinenumber.get(idMetodo);

        AbstractInsnNode in = j.next();
        /*
        while(in.getOpcode() < 0){
            in = in.getNext();
        }

         */
        if (nodeIntegerMap.putIfAbsent(in, indiceNodo) == null){
            nodeLinenumberMap.put(indiceNodo, findLinenumber(in));
            indiceNodo++;
        }
        controlGraph.addVertex(in);
        AbstractInsnNode nextPredicate;
        nextPredicate = findNextPredicateNode(in);
        if (!isBooleanAssignmentPredicateNode){
            while (isBooleanAssignment(nextPredicate)){
                nextPredicate = findNextPredicateNode(nextPredicate.getNext());
            }
        }
        if (nextPredicate != null){
            if (nodeIntegerMap.putIfAbsent(nextPredicate, indiceNodo) == null){
                nodeLinenumberMap.put(indiceNodo, findLinenumber(nextPredicate));
                indiceNodo++;
            }
            controlGraph.addVertex(nextPredicate);
            controlGraph.addEdge(in, nextPredicate, new BooleanEdge(EdgeType.DEFAULT));
        }
        while (j.hasNext()) {
            in = j.next();
            int op = in.getOpcode();
            if (op >= IFEQ && op <= IF_ACMPNE || op >= IFNULL && op <= IFNONNULL) {
                if (!isBooleanAssignmentPredicateNode && isBooleanAssignment(in)){
                    continue;
                }
                if (nodeIntegerMap.putIfAbsent(in, indiceNodo) == null) {
                    nodeLinenumberMap.put(indiceNodo, findLinenumber(in));
                    indiceNodo++;
                }
                controlGraph.addVertex(in);
                // Busco el label de salto (donde va el programa si se evalua true)
                if (in instanceof JumpInsnNode){
                    AbstractInsnNode target = findJumpDestiny((JumpInsnNode) in);
                    nextPredicate = findNextPredicateNode(target);
                    if (!isBooleanAssignmentPredicateNode){
                        while (isBooleanAssignment(nextPredicate)) nextPredicate = findNextPredicateNode(nextPredicate.getNext());
                    }
                    if (nextPredicate != null){
                        if (nodeIntegerMap.putIfAbsent(nextPredicate, indiceNodo) == null) {
                            nodeLinenumberMap.put(indiceNodo, findLinenumber(nextPredicate));
                            indiceNodo++;
                        }
                        controlGraph.addVertex(nextPredicate);
                        controlGraph.addEdge(in, nextPredicate, new BooleanEdge(EdgeType.FALSE));
                    }
                }
                // Busco el siguiente nodo predicado por el camino FALSE
                nextPredicate = findNextPredicateNode(in.getNext());
                if (!isBooleanAssignmentPredicateNode){
                    while (isBooleanAssignment(nextPredicate)) nextPredicate = findNextPredicateNode(nextPredicate.getNext());
                }
                if (nextPredicate != null){
                    if (nodeIntegerMap.putIfAbsent(nextPredicate, indiceNodo) == null) {
                        nodeLinenumberMap.put(indiceNodo, findLinenumber(nextPredicate));
                        indiceNodo++;
                    }
                    controlGraph.addVertex(nextPredicate);
                    controlGraph.addEdge(in, nextPredicate, new BooleanEdge(EdgeType.TRUE));
                }
            }
        }
        controlGraphs.put(idMetodo, controlGraph);
    }

    public DirectedPseudograph<AbstractInsnNode, BooleanEdge> getControlFlowGraph(String idMetodo){
        return controlGraphs.get(idMetodo);
    }

    public DirectedPseudograph<Integer, BooleanEdge> getControlFlowGraphAsIntegerGraph(String idMetodo){
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo = controlGraphs.get(idMetodo);
        DirectedPseudograph<Integer, BooleanEdge> newGraph = new DirectedPseudograph<>(BooleanEdge.class);
        Map<AbstractInsnNode, Integer> nodeIntegerMap = nodoToInteger.get(idMetodo);
        for (AbstractInsnNode nodo: grafo.vertexSet()){
            newGraph.addVertex(nodeIntegerMap.get(nodo));
        }
        for (BooleanEdge edge: grafo.edgeSet()){
            newGraph.addEdge(nodeIntegerMap.get(grafo.getEdgeSource(edge)),
                    nodeIntegerMap.get(grafo.getEdgeTarget(edge)),
                    new BooleanEdge(edge.getType()));
        }
        return newGraph;
    }

    public DirectedPseudograph<Integer, BooleanEdge> getControlFlowGraphAsLinenumberGraph(String idMetodo){
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo = controlGraphs.get(idMetodo);
        DirectedPseudograph<Integer, BooleanEdge> newGraph = new DirectedPseudograph<>(BooleanEdge.class);
        Map<AbstractInsnNode, Integer> nodeIntegerMap = nodoToInteger.get(idMetodo);
        Map<Integer, Integer> integerLinenumberMap = nodoToLinenumber.get(idMetodo);
        for (AbstractInsnNode nodo: grafo.vertexSet()){
            newGraph.addVertex(
                    integerLinenumberMap.get(nodeIntegerMap.get(nodo)
                    ));
        }
        for (BooleanEdge edge: grafo.edgeSet()){
            newGraph.addEdge(
                    integerLinenumberMap.get(nodeIntegerMap.get(grafo.getEdgeSource(edge))),
                    integerLinenumberMap.get(nodeIntegerMap.get(grafo.getEdgeTarget(edge))),
                    new BooleanEdge(edge.getType()));
        }
        return newGraph;
    }

    public Set<EdgePair> obtenerSituacionesPrueba(String idMetodo){
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo = controlGraphs.get(idMetodo);
        Map<AbstractInsnNode, Integer> nodoToIntegerMap = nodoToInteger.get(idMetodo);
        Set<EdgePair> caminos = new HashSet<>();
        for (AbstractInsnNode i: grafo.vertexSet()){
            if (!grafo.incomingEdgesOf(i).isEmpty() && !grafo.outgoingEdgesOf(i).isEmpty()){
                for (BooleanEdge edgeIn: grafo.incomingEdgesOf(i)){
                    for (BooleanEdge edgeOut: grafo.outgoingEdgesOf(i)){
                        Integer nodoInicio = nodoToIntegerMap.get(grafo.getEdgeSource(edgeIn));
                        Integer nodoMedio = nodoToIntegerMap.get(grafo.getEdgeTarget(edgeIn));
                        Integer nodoFinal = nodoToIntegerMap.get(grafo.getEdgeTarget(edgeOut));

                        EdgePair camino = new EdgePair(nodoInicio, edgeIn.getType(), nodoMedio,
                                edgeOut.getType(), nodoFinal);
                        caminos.add(camino);
                    }
                }
            }
        }
        return caminos;
    }
}
