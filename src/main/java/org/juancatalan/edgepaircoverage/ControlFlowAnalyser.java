package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DirectedPseudograph;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IFNONNULL;

public class ControlFlowAnalyser {
    private Map<String, Map<AbstractInsnNode, Integer>> nodoToInteger;
    private Map<String, Map<Integer, Integer>> nodoToLinenumber;

    ControlFlowAnalyser(){
        if (nodoToInteger == null) nodoToInteger = new HashMap<>();
        if (nodoToLinenumber == null) nodoToLinenumber = new HashMap<>();
    }

    public Map<String, Map<AbstractInsnNode, Integer>> getNodoToInteger() {
        return nodoToInteger;
    }

    public Map<String, Map<Integer, Integer>> getNodoToLinenumber() {
        return nodoToLinenumber;
    }

    public boolean isPredicateNode(AbstractInsnNode in){
        int opCode = in.getOpcode();
        return ((opCode >= IFEQ && opCode <= IF_ACMPNE) ||
                (opCode >= IRETURN && opCode <= RETURN) ||
                (opCode >= IFNULL && opCode <= IFNONNULL) ||
                (opCode == ATHROW));
    }

    public Optional<BooleanAssignmentInfo> isBranchBooleanAssignment(AbstractInsnNode in){
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

    public boolean isBooleanAssignment(AbstractInsnNode in){
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
        /* Descomentar para permitir booleanAssignments multiples
        if (caminoSaltoBool.isPresent() && caminoNormalBool.isEmpty()){
            return isBooleanAssignment(findNextPredicateNode(in.getNext()));
        }
        */
        return caminoNormalBool.isPresent() && caminoSaltoBool.isPresent()
                && caminoNormalBool.get().value() != caminoSaltoBool.get().value()
                && caminoNormalBool.get().index() == caminoSaltoBool.get().index();
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

    public AbstractInsnNode findNextPredicateNode(AbstractInsnNode in){
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

    public Integer findLinenumber(AbstractInsnNode in){
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

    DirectedPseudograph<AbstractInsnNode, BooleanEdge> getControlFlowGraph(InsnList insns, String idMetodo){
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
        while (isBooleanAssignment(nextPredicate)){
            nextPredicate = findNextPredicateNode(nextPredicate.getNext());
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
                if (isBooleanAssignment(in)){
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
                    while (isBooleanAssignment(nextPredicate)) nextPredicate = findNextPredicateNode(nextPredicate.getNext());
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
                while (isBooleanAssignment(nextPredicate)) nextPredicate = findNextPredicateNode(nextPredicate.getNext());
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
        return controlGraph;
    }

}
