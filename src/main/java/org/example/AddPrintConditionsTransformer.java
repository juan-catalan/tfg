package org.example;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class AddPrintConditionsTransformer implements ClassFileTransformer {
    static public void imprimir(String s){
        System.out.println("imprimiendo: ".concat(s));
    }

    public void addSysOutPrintIns(InsnList il, String message){
        il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out",
                Type.getObjectType("java/io/PrintStream").getDescriptor()));
        il.add(new LdcInsnNode(message));
        il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));

    }

    public void addInstructionsConditionsAndBranches(InsnList insns){
        Iterator<AbstractInsnNode> j = insns.iterator();
        while (j.hasNext()) {
            AbstractInsnNode in = j.next();
            int op = in.getOpcode();
            if (op >= IFEQ && op <= IF_ACMPNE) {
                InsnList il = new InsnList();
                addSysOutPrintIns(il, "Estoy en una condicion, index: ".concat(String.valueOf(insns.indexOf(in))));
                insns.insert(in.getPrevious(), il);
                // Busco el label de salto (donde va el programa si se evalua true)
                if (in instanceof JumpInsnNode){
                    LabelNode label = ((JumpInsnNode) in).label;
                    AbstractInsnNode target = in.getNext();
                    while (true) {
                        if (target instanceof LabelNode){
                            if (((LabelNode) target).equals(label)){
                                il.clear();
                                addSysOutPrintIns(il, "Se ha evaluado como true: ".concat(String.valueOf(insns.indexOf(target))));
                                insns.insert(target, il);
                                break;
                            }
                        }
                        target = target.getNext();
                    }
                }
                // Añado un mensaje a continuación de la comparacion: se ha debido de evaluar como false
                il.clear();
                addSysOutPrintIns(il, "Se ha evaluado como false: ".concat(String.valueOf(insns.indexOf(in.getNext()))));
                insns.insert(in, il);
            }
        }
    }


    public AbstractInsnNode findGotoDestiny(JumpInsnNode in){
        if (in.getOpcode() != GOTO) return null;
        LabelNode target = in.label;

        AbstractInsnNode nextNode = in.getNext();
        while (nextNode != null){
            if ((nextNode instanceof LabelNode) &&
                    target.equals(nextNode)
            ){
                while (nextNode.getOpcode() < 0) nextNode = nextNode.getNext();
                return nextNode;
            }
            nextNode = nextNode.getNext();
        }

        AbstractInsnNode previousNode = in.getPrevious();
        while (previousNode != null){
            if ((previousNode instanceof LabelNode) &&
                target.equals(previousNode)
            ){
                while (nextNode.getOpcode() < 0) nextNode = nextNode.getNext();
                return previousNode;
            }
            previousNode = previousNode.getPrevious();
        }

        return null;
    }

    public DirectedPseudograph<Integer, DefaultEdge> getControlFlowGraph(InsnList insns){
        DirectedPseudograph<Integer, DefaultEdge> controlGraph = new DirectedPseudograph<>(DefaultEdge.class);
        Iterator<AbstractInsnNode> j = insns.iterator();
        while (j.hasNext()) {
            AbstractInsnNode in = j.next();
            int op = in.getOpcode();
            if (op >= IFEQ && op <= IF_ACMPNE) {
                controlGraph.addVertex(insns.indexOf(in));
                // Busco el label de salto (donde va el programa si se evalua true)
                if (in instanceof JumpInsnNode){
                    LabelNode label = ((JumpInsnNode) in).label;
                    AbstractInsnNode target = in.getNext();
                    while (true) {
                        if (target instanceof LabelNode){
                            if (((LabelNode) target).equals(label)){
                                while (target != null){
                                    int opTarget = target.getOpcode();
                                    // Si es un goto cuidado revisar
                                    if (opTarget == GOTO){
                                        AbstractInsnNode destiny = this.findGotoDestiny((JumpInsnNode) target);
                                        if (destiny != null){
                                            controlGraph.addVertex(insns.indexOf(destiny));
                                            controlGraph.addEdge(insns.indexOf(in), insns.indexOf(destiny));
                                            break;
                                        }
                                    }
                                    // Si no lo es comprobar si estamos en un nodo predicado o de fin
                                    if ((opTarget >= IFEQ && opTarget <= IF_ACMPNE) ||
                                            (opTarget >= IRETURN && opTarget <= RETURN)
                                    ) {
                                        controlGraph.addVertex(insns.indexOf(target));
                                        controlGraph.addEdge(insns.indexOf(in), insns.indexOf(target));
                                        break;
                                    }
                                    target = target.getNext();
                                }
                                break;
                            }
                        }
                        target = target.getNext();
                    }
                }
                // Busco el siguiente nodo predicado
                AbstractInsnNode target = in.getNext();
                while (target != null){
                    int opTarget = target.getOpcode();
                    // Si es un goto cuidado revisar
                    if (opTarget == GOTO){
                        AbstractInsnNode destiny = this.findGotoDestiny((JumpInsnNode) target);
                        if (destiny != null){
                            controlGraph.addVertex(insns.indexOf(destiny));
                            controlGraph.addEdge(insns.indexOf(in), insns.indexOf(destiny));
                            break;
                        }
                    }
                    // Si no lo es comprobar si estamos en un nodo predicado o de fin
                    if ((opTarget >= IFEQ && opTarget <= IF_ACMPNE) ||
                            (opTarget >= IRETURN && opTarget <= RETURN)
                    ) {
                        controlGraph.addVertex(insns.indexOf(target));
                        controlGraph.addEdge(insns.indexOf(in), insns.indexOf(target));
                        break;
                    }
                    target = target.getNext();
                }
            }
        }
        return controlGraph;
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        /*
        class Node<V extends Value> extends Frame<V> {
            Set< Node<V> > successors = new HashSet< Node<V> >();
            public Node(int nLocals, int nStack) {
                super(nLocals, nStack);
            }
            public Node(Frame<? extends V> src) {
                super(src);
            }
        }
         */
        // System.out.println("I'm the ClassFileTransformer");
        ClassNode cn = new ClassNode(ASM4);
        ClassReader cr = new ClassReader(classfileBuffer);

        cr.accept(cn, 0);
        for (MethodNode mn : (List<MethodNode>) cn.methods) {

            // Check if the method is annotated with our custom one
            boolean isAnnotated = false;
            if(mn.visibleAnnotations != null) for(AnnotationNode an: mn.visibleAnnotations) {
                if (an.desc.equals(Coverage2Edge.class.descriptorString())){
                    isAnnotated = true;
                }
            }
            if (isAnnotated) {
                System.out.println("I'm transforming my own classes: ".concat(className));
                System.out.println("I'm transforming the method: ".concat(mn.name));
                if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                    continue;
                }
                InsnList insns = mn.instructions;
                if (insns.size() == 0) {
                    continue;
                }


                System.out.println(this.getControlFlowGraph(insns).toString());


                this.addInstructionsConditionsAndBranches(insns);

                /*
                Analyzer<BasicValue> a =
                    new Analyzer<BasicValue>(new BasicInterpreter()) {
                        protected Frame<BasicValue> newFrame(int nLocals, int nStack) {
                            return new Node<BasicValue>(nLocals, nStack);
                        }
                        protected Frame<BasicValue> newFrame(
                                Frame<? extends BasicValue> src) {
                            return new Node<BasicValue>(src);
                        }
                        protected void newControlFlowEdge(int src, int dst) {
                            Node<BasicValue> s = (Node<BasicValue>) getFrames()[src];
                            s.successors.add((Node<BasicValue>) getFrames()[dst]);
                        }
                    };
                try {
                    a.analyze(cn.name, mn);
                } catch (AnalyzerException e) {
                    throw new RuntimeException(e);
                }


                Frame[] frames = a.getFrames();
                int edges = 0;
                int nodes = 0;
                for (int i = 0; i < frames.length; ++i) {

                    if (frames[i] != null) {
                        int numSuccessors = ((Node) frames[i]).successors.size();
                        edges += numSuccessors;
                        nodes += 1;
                        if (numSuccessors > 1){
                            System.out.println("Frame " + i + " (" + numSuccessors + ") ");
                            //for (Node<BasicValue> sucessor: ((Node) frames[i]).successors);
                            Iterator nodeIterator = ((Node) frames[i]).successors.iterator();
                            while (nodeIterator.hasNext()){
                                Node node = (Node) nodeIterator.next();
                                node.
                            }
                            System.out.println("\nFrame " + i + " (" + numSuccessors + ") ");
                        }
                    }


                }
                */

            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        // Si queremos ver el bytecode de todas las clases que pasan por el transformer: descomentar linea
        //TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));cn.accept(cv);
        cn.accept(cw);
        return cw.toByteArray();
    }

    // Not implemented
    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return ClassFileTransformer.super.transform(module, loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }
}
