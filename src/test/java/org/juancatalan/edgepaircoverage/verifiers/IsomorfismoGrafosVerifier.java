package org.juancatalan.edgepaircoverage.verifiers;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.AddPrintConditionsTransformer;
import org.juancatalan.edgepaircoverage.BooleanEdge;
import org.juancatalan.edgepaircoverage.utils.AdjacencyMatrix;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM4;

public class IsomorfismoGrafosVerifier {
    static public void verify(Class clase, String nombreMetodo, DirectedPseudograph<Integer, BooleanEdge> grafoEsperado){
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

        assertFalse(metodo.isEmpty(), "El método no existe en esa clase");

        InsnList listaInstrucciones = metodo.get().instructions;
        AddPrintConditionsTransformer transformer = AddPrintConditionsTransformer.getInstance();
        transformer.addNodoToIntger(idMetodo);
        transformer.addNodoLinenumber(idMetodo);
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlFlowGraph = transformer.getControlFlowAnalyser().getControlFlowGraph(idMetodo, listaInstrucciones);
        DirectedPseudograph<Integer, BooleanEdge> controlFlowGraphInt = AddPrintConditionsTransformer.transformGraphToInteger(idMetodo, controlFlowGraph);


        assertEquals(grafoEsperado.vertexSet().size(), controlFlowGraphInt.vertexSet().size(), "El grafo obtenido no tiene el número de vertices esperado");
        assertEquals(grafoEsperado.edgeSet().size(), controlFlowGraphInt.edgeSet().size(), "El grafo obtenido no tiene el número de aristas esperado");

        AdjacencyMatrix adjacencyMatrix = AdjacencyMatrix.getAdjacencyMatrixFromGraph(controlFlowGraphInt);
        AdjacencyMatrix adjacencyMatrixEsperada = AdjacencyMatrix.getAdjacencyMatrixFromGraph(grafoEsperado);

        assertTrue(adjacencyMatrix.isAPermutationOf(adjacencyMatrixEsperada), "Los grafos no coinciden");
    }
}
