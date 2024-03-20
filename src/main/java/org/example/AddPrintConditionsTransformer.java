package org.example;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

import org.jgrapht.graph.DirectedPseudograph;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class AddPrintConditionsTransformer implements ClassFileTransformer {
    static private Map<String, Set<Camino2Edge>> almacenCaminos;
    static private Map<String, Map<AbstractInsnNode, Integer>> nodoToInteger;
    static private Map<String, Set<Camino2Edge>> caminosRecorridos;
    static private Map<String, Camino2Edge> caminoActual;


    private static class ShutDownHook extends Thread {
        public void run() {
            long startTime = System.nanoTime();
            AddPrintConditionsTransformer.imprimirInforme();
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
            System.out.println(duration + " ms to execute the shutdown hook");
            //AddPrintConditionsTransformer.imprimirCaminos();
            //AddPrintConditionsTransformer.imprimirCaminosRecorridos();
        }
    }

    AddPrintConditionsTransformer (){
        System.out.println("Prueba constructor");
        if (almacenCaminos == null) almacenCaminos = new HashMap<>();
        if (nodoToInteger == null) nodoToInteger = new HashMap<>();
        if (caminosRecorridos == null) caminosRecorridos = new HashMap<>();
        if (caminoActual == null) caminoActual = new HashMap<>();
        ShutDownHook jvmShutdownHook = new ShutDownHook();
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
    }

    static public void imprimirCaminos(){
        almacenCaminos.forEach((metodo, caminos) -> {
            System.out.println("Clase y metodo: " + metodo);
            System.out.println("\tNumero de caminos: " + caminos.size());
            System.out.println("\tCaminos:");
            for (Camino2Edge camino: caminos){
                System.out.println("\t\t" + camino);
            }
        });
    }

    static public void imprimirCaminosRecorridos(){
        caminosRecorridos.forEach((metodo, caminos) -> {
            System.out.println("Clase y metodo: " + metodo);
            System.out.println("\tCaminos recorridos: " + caminos.size());
            System.out.println("\t\t" + Arrays.toString(caminos.toArray()));
        });
    }

    static public void imprimirInforme(){
        System.out.println("---------------- Ejecucion terminada ----------------");
        for (String metodo: almacenCaminos.keySet()){
            Set<Camino2Edge> todosCaminos = almacenCaminos.get(metodo);
            Set<Camino2Edge> recorridosCaminos = caminosRecorridos.get(metodo);
            System.out.println("Clase y metodo: " + metodo);
            System.out.println("\tNumero de caminos total: " + todosCaminos.size());
            System.out.println("\t\tCaminos: [");
            todosCaminos.forEach((camino -> {
                System.out.println("\t\t\t" + camino);
            }));
            System.out.println("\t\t]");
            System.out.println("\tNumero de caminos cubiertos: " + recorridosCaminos.size());
            System.out.println("\t\tCaminos: [");
            recorridosCaminos.forEach((camino -> {
                System.out.println("\t\t\t" + camino);
            }));
            System.out.println("\t\t]");
            System.out.println("\tNumero de caminos sin cubrir: " + (todosCaminos.size()-recorridosCaminos.size()));
            System.out.println("\t\tCaminos: [");
            todosCaminos.forEach((camino -> {
                if (!recorridosCaminos.contains(camino)) System.out.println("\t\t\t" + camino);
            }));
            System.out.println("\t\t]");
        }
    }



    // TODO: mover a otra clase
    static public void markPredicateNode(String metodo, int nodeId) {
        Camino2Edge camino = caminoActual.get(metodo);
        camino.addNode(nodeId);
        if (camino.isComplete()){
            caminosRecorridos.get(metodo).add(camino);
            caminoActual.put(metodo, camino.nextHalf());
        }
        else {
            caminoActual.put(metodo, camino);
        }
        // System.out.print(nodeId);
    }

    static public void markEndNode(String metodo, int nodeId) {
        Camino2Edge camino = caminoActual.get(metodo);
        camino.addNode(nodeId);
        if (camino.isComplete()){
            caminosRecorridos.get(metodo).add(camino);
        }
        caminoActual.put(metodo, new Camino2Edge(null,  null, null,  null, null));
        // System.out.print(nodeId);
    }

    static public void markEdge(String metodo, String edge) {
        Camino2Edge camino = caminoActual.get(metodo);
        camino.addEdge(EdgeType.valueOf(edge));
        if (camino.isComplete()){
            caminosRecorridos.get(metodo).add(camino);
            caminoActual.put(metodo, camino.nextHalf());
        }
        else {
            caminoActual.put(metodo, camino);
        }
        // System.out.print(" ----" + edge + "--> ");
    }

    public InsnList addMarkEndNode(String metodo, Integer nodeId){
        InsnList il = new InsnList();
        il.add(new LdcInsnNode(metodo));
        il.add(new LdcInsnNode(nodeId));
        il.add(new MethodInsnNode(INVOKESTATIC, "org/example/AddPrintConditionsTransformer", "markEndNode", "(Ljava/lang/String;I)V"));
        return il;
    }

    public InsnList addMarkPredicateNode(String metodo, Integer nodeId){
        InsnList il = new InsnList();
        il.add(new LdcInsnNode(metodo));
        il.add(new LdcInsnNode(nodeId));
        il.add(new MethodInsnNode(INVOKESTATIC, "org/example/AddPrintConditionsTransformer", "markPredicateNode", "(Ljava/lang/String;I)V"));
        return il;
    }

    public InsnList addMarkEdge(String metodo, EdgeType edge){
        InsnList il = new InsnList();
        il.add(new LdcInsnNode(metodo));
        il.add(new LdcInsnNode(edge.name()));
        il.add(new MethodInsnNode(INVOKESTATIC, "org/example/AddPrintConditionsTransformer", "markEdge", "(Ljava/lang/String;Ljava/lang/String;)V"));
        return il;
    }

    public void addSysOutPrintIns(InsnList il, String message){
        il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out",
                Type.getObjectType("java/io/PrintStream").getDescriptor()));
        il.add(new LdcInsnNode(message));
        il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
    }

    public void addInstructionsConditionsAndBranches(String claseYmetodo, InsnList insns, DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo){
        Iterator<AbstractInsnNode> j = insns.iterator();
        while (j.hasNext()) {
            AbstractInsnNode in = j.next();
            if (grafo.containsVertex(in)){
                System.out.println(in + " -> " + nodoToInteger.get(claseYmetodo).get(in));
                // Si es el nodo inicial
                if (grafo.inDegreeOf(in) == 0) {
                    insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, nodoToInteger.get(claseYmetodo).get(in)));

                    // Añado el camino default
                    insns.insert(in, addMarkEdge(claseYmetodo, EdgeType.DEFAULT));
                }
                // Si son nodo predicado
                else if (grafo.outDegreeOf(in) > 0) {
                    insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, nodoToInteger.get(claseYmetodo).get(in)));
                    LabelNode label = ((JumpInsnNode) in).label;
                    AbstractInsnNode target = in.getNext();
                    while (true) {
                        if (target instanceof LabelNode){
                            if (((LabelNode) target).equals(label)){
                                insns.insert(target, addMarkEdge(claseYmetodo, EdgeType.TRUE));
                                break;
                            }
                        }
                        target = target.getNext();
                    }
                    // Añado el camino del false
                    insns.insert(in, addMarkEdge(claseYmetodo, EdgeType.FALSE));
                }
                // Si son nodos de finalizacion
                else {
                    insns.insertBefore(in, addMarkEndNode(claseYmetodo, nodoToInteger.get(claseYmetodo).get(in)));
                }
            }
            /*
            int op = in.getOpcode();
            if (op >= IFEQ && op <= IF_ACMPNE) {
                InsnList il = new InsnList();
                //addSysOutPrintIns(il, "Estoy en una condicion, index: ".concat(String.valueOf(insns.indexOf(in))));
                //insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, in));
                addSysOutPrintIns(il, "Estoy en una condicion, index: ".concat(String.valueOf(in)));
                insns.insert(in.getPrevious(), il);
                // Busco el label de salto (donde va el programa si se evalua true)
                if (in instanceof JumpInsnNode){
                    LabelNode label = ((JumpInsnNode) in).label;
                    System.out.println(in.hashCode());
                    AbstractInsnNode target = in.getNext();
                    while (true) {
                        if (target instanceof LabelNode){
                            if (((LabelNode) target).equals(label)){
                                il.clear();
                                //addSysOutPrintIns(il, "Se ha evaluado como true: ".concat(String.valueOf(insns.indexOf(target))));
                                addSysOutPrintIns(il, "Se ha evaluado como true: ".concat(String.valueOf(target)));
                                insns.insert(target, il);
                                break;
                            }
                        }
                        target = target.getNext();
                    }
                }
                // Añado un mensaje a continuación de la comparacion: se ha debido de evaluar como false
                il.clear();
                //addSysOutPrintIns(il, "Se ha evaluado como false: ".concat(String.valueOf(insns.indexOf(in.getNext()))));
                addSysOutPrintIns(il, "Se ha evaluado como false: ".concat(String.valueOf(in.getNext())));
                insns.insert(in, il);
            }

             */
        }
    }

    public boolean isPredicateNode(AbstractInsnNode in){
        int opCode = in.getOpcode();
        return ((opCode >= IFEQ && opCode <= IF_ACMPNE) ||
                (opCode >= IRETURN && opCode <= RETURN) ||
                (opCode == ATHROW));
    }

    public AbstractInsnNode findGotoDestiny(JumpInsnNode in){
        if (in.getOpcode() != GOTO) return null;
        LabelNode target = in.label;

        AbstractInsnNode nextNode = in.getNext();
        while (nextNode != null){
            if ((nextNode instanceof LabelNode) &&
                    target.equals(nextNode)
            ){
                // Avanzo si es bytecode auxiliar
                while (nextNode.getOpcode() < 0) nextNode = nextNode.getNext();
                // Busco que sea nodo predicado
                while (!isPredicateNode(nextNode)){
                    nextNode = nextNode.getNext();
                }
                return nextNode;
            }
            nextNode = nextNode.getNext();
        }

        AbstractInsnNode previousNode = in.getPrevious();
        while (previousNode != null){
            if ((previousNode instanceof LabelNode) &&
                target.equals(previousNode)
            ){
                while (previousNode.getOpcode() < 0) previousNode = previousNode.getNext();
                while (!isPredicateNode(previousNode)){
                    previousNode = previousNode.getNext();
                }
                return previousNode;
            }
            previousNode = previousNode.getPrevious();
        }

        return null;
    }

    public AbstractInsnNode findNextPredicateNode(AbstractInsnNode in){
        AbstractInsnNode target = in.getNext();
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

    public DirectedPseudograph<AbstractInsnNode, BooleanEdge> getControlFlowGraph(InsnList insns, String idMetodo){
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlGraph = new DirectedPseudograph<>(BooleanEdge.class);
        Iterator<AbstractInsnNode> j = insns.iterator();
        Integer indiceNodo = 1;
        Map<AbstractInsnNode, Integer> nodeIntegerMap = nodoToInteger.get(idMetodo);

        AbstractInsnNode in = j.next();
        int op = in.getOpcode();
        while (op < 0){
            in = j.next();
            op = in.getOpcode();
        }
        if (nodeIntegerMap.putIfAbsent(in, indiceNodo) == null) indiceNodo++;
        controlGraph.addVertex(in);
        AbstractInsnNode nextPredicate = findNextPredicateNode(in);
        if (nextPredicate != null){
            if (nodeIntegerMap.putIfAbsent(nextPredicate, indiceNodo) == null) indiceNodo++;
            controlGraph.addVertex(nextPredicate);
            controlGraph.addEdge(in, nextPredicate, new BooleanEdge(EdgeType.DEFAULT));
        }
        while (j.hasNext()) {
            in = j.next();
            op = in.getOpcode();
            if (op >= IFEQ && op <= IF_ACMPNE) {
                if (nodeIntegerMap.putIfAbsent(in, indiceNodo) == null) indiceNodo++;
                controlGraph.addVertex(in);
                // Busco el label de salto (donde va el programa si se evalua true)
                if (in instanceof JumpInsnNode){
                    LabelNode label = ((JumpInsnNode) in).label;
                    AbstractInsnNode target = in.getNext();
                    while (true) {
                        if (target instanceof LabelNode){
                            if (((LabelNode) target).equals(label)){
                                nextPredicate = findNextPredicateNode(target);
                                if (nextPredicate != null){
                                    if (nodeIntegerMap.putIfAbsent(nextPredicate, indiceNodo) == null) indiceNodo++;
                                    controlGraph.addVertex(nextPredicate);
                                    controlGraph.addEdge(in, nextPredicate, new BooleanEdge(EdgeType.TRUE));
                                }
                                break;
                            }
                        }
                        target = target.getNext();
                    }
                }
                // Busco el siguiente nodo predicado por el camino FALSE
                nextPredicate = findNextPredicateNode(in);
                if (nextPredicate != null){
                    if (nodeIntegerMap.putIfAbsent(nextPredicate, indiceNodo) == null) indiceNodo++;
                    controlGraph.addVertex(nextPredicate);
                    controlGraph.addEdge(in, nextPredicate, new BooleanEdge(EdgeType.FALSE));
                }
            }
        }
        System.out.println(nodeIntegerMap);
        return controlGraph;
    }

    public Set<Camino2Edge> pruebaObtenerCaminos(String idMetodo, DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo){
        Set<Camino2Edge> caminos = new HashSet<>();
        for (AbstractInsnNode i: grafo.vertexSet()){
            if (!grafo.incomingEdgesOf(i).isEmpty() && !grafo.outgoingEdgesOf(i).isEmpty()){
                for (BooleanEdge edgeIn: grafo.incomingEdgesOf(i)){
                    for (BooleanEdge edgeOut: grafo.outgoingEdgesOf(i)){
                        Integer nodoInicio = nodoToInteger.get(idMetodo).get(grafo.getEdgeSource(edgeIn));
                        Integer nodoMedio = nodoToInteger.get(idMetodo).get(grafo.getEdgeTarget(edgeIn));
                        Integer nodoFinal = nodoToInteger.get(idMetodo).get(grafo.getEdgeTarget(edgeOut));

                        Camino2Edge camino = new Camino2Edge(nodoInicio, edgeIn.getType(), nodoMedio,
                                edgeOut.getType(), nodoFinal);
                        caminos.add(camino);
                    }
                }
            }
        }
        return caminos;
    }

    // TODO: Para poder saber con seguridad que estoy en un nodo perteneciente al grafo, pensar si sería buena idea hacer la tarea de generar el grafo y de añadir el bytecode a la vez. Para así poder generar identificadores artificiales a los nodos y a las aristas (para no tener que usar hashcode)

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        /*
        try {
            TraceClassVisitor cv = new TraceClassVisitor(new PrintWriter(System.out));
            ClassReader cr = new ClassReader(Prueba.class.getName());
            cr.accept(cv, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

         */


        // System.out.println("I'm the ClassFileTransformer");
        ClassNode cn = new ClassNode(ASM4);
        ClassReader cr = new ClassReader(classfileBuffer);

        cr.accept(cn, 0);
        for (MethodNode mn : (List<MethodNode>) cn.methods) {
            String idMetodo = className.concat("." + mn.name).concat("." + mn.desc);


            // Check if the method is annotated with our custom one
            boolean isAnnotated = false;
            if(mn.visibleAnnotations != null) for(AnnotationNode an: mn.visibleAnnotations) {
                if (an.desc.equals(Coverage2Edge.class.descriptorString())){
                    isAnnotated = true;
                }
            }
            if (isAnnotated) {
                nodoToInteger.put(idMetodo, new HashMap<>());
                caminosRecorridos.put(idMetodo, new HashSet<>());
                caminoActual.put(idMetodo, new Camino2Edge(null, null, null, null, null));
                System.out.println("I'm transforming my own classes: ".concat(className));
                System.out.println("I'm transforming the method: ".concat(mn.name));
                if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                    continue;
                }
                InsnList insns = mn.instructions;
                if (insns.size() == 0) {
                    continue;
                }

                try{
                    DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo = this.getControlFlowGraph(insns, idMetodo);
                    System.out.println(grafo.toString());

                    Set<Camino2Edge> caminos = pruebaObtenerCaminos(idMetodo, grafo);
                    //System.out.println("Caminos de profundidad 2: " + caminos.size());
                    System.out.println(caminos);
                    almacenCaminos.put(idMetodo ,caminos);


                    this.addInstructionsConditionsAndBranches(idMetodo, insns, grafo);


                }catch (Exception e){
                    e.printStackTrace();
                }
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
