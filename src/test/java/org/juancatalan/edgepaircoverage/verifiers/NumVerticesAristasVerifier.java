package org.juancatalan.edgepaircoverage.verifiers;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.AddPrintConditionsTransformer;
import org.juancatalan.edgepaircoverage.BooleanEdge;
import org.juancatalan.edgepaircoverage.ControlFlowAnalyser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.objectweb.asm.Opcodes.ASM4;

public class NumVerticesAristasVerifier {
    public static void verify(Class clase, String nombreMetodo, int numVerticesEsperado, int numAristasEsperado, boolean isBooleanAssignmentsPredicateNode){
        String idMetodo = clase.getName().concat(".").concat(nombreMetodo);

        ClassNode cn = new ClassNode(ASM4);
        ClassReader cr;
        try {
            cr = new ClassReader(clase.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(cn, 0);
        Optional<MethodNode> metodo = cn.methods.stream().filter(m -> m.name.equals(nombreMetodo)).findFirst();

        assertFalse(metodo.isEmpty());

        InsnList listaInstrucciones = metodo.get().instructions;
        ControlFlowAnalyser controlFlowAnalyser = new ControlFlowAnalyser(isBooleanAssignmentsPredicateNode);
        controlFlowAnalyser.analyze(idMetodo, listaInstrucciones);
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlFlowGraph = controlFlowAnalyser.getControlFlowGraph(idMetodo);

        assertEquals(numVerticesEsperado, controlFlowGraph.vertexSet().size());
        assertEquals(numAristasEsperado, controlFlowGraph.edgeSet().size());
    }
}
