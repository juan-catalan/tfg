package org.example;

import org.example.ejerciciosExamen.curso1617.Convocatoria1;
import org.jgrapht.graph.DirectedPseudograph;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM4;

class AddPrintConditionsTransformerTest {

    @Test
    void getControlFlowGraphExample() {
        ClassNode cn = new ClassNode(ASM4);
        ClassReader cr;
        try {
            cr = new ClassReader(Convocatoria1.class.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(cn, 0);
        Optional<MethodNode> metodo = cn.methods.stream().filter(m -> {
                return m.name.equals("buscar");
        }).findFirst();

        assertFalse(metodo.isEmpty());

        InsnList listaInstrucciones = metodo.get().instructions;
        AddPrintConditionsTransformer transformer = new AddPrintConditionsTransformer(false);
        transformer.addNodoToIntger("a");
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlFlowGraph = transformer.getControlFlowGraph(listaInstrucciones, "a");
        DirectedPseudograph<Integer, BooleanEdge> controlFlowGraphInt = transformer.transformGraphToInteger("a", controlFlowGraph);

        assertEquals(6, controlFlowGraphInt.vertexSet().size());
        assertEquals(7, controlFlowGraphInt.edgeSet().size());
    }
}