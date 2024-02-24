package org.example;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

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
                                addSysOutPrintIns(il, "Se ha evaluado como true");
                                insns.insert(target, il);
                                break;
                            }
                        }
                        target = target.getNext();
                    }
                }
                // Añado un mensaje a continuación de la comparacion: se ha debido de evaluar como false
                il.clear();
                addSysOutPrintIns(il, "Se ha evaluado como false");
                insns.insert(in, il);
            }
        }
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
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

                this.addInstructionsConditionsAndBranches(insns);
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
