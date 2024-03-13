package org.example;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

public class Prueba {
    public static void main(String[] args) {
        AbstractInsnNode node = new JumpInsnNode(1, null);
        AddPrintConditionsTransformer.markPredicateNode("Metodo", node);
    }
}
