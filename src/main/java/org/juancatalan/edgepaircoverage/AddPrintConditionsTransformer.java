package org.juancatalan.edgepaircoverage;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.List;
import java.util.zip.Deflater;

import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.objectweb.asm.Opcodes.*;

public class AddPrintConditionsTransformer implements ClassFileTransformer {
    static boolean DEBUG = true;
    static private Map<String, DirectedPseudograph<Integer, BooleanEdge>> grafosMetodos;
    static private Map<String, String> grafosRenderedMetodos;
    static private Map<String, Integer> caminosImposiblesMetodo;
    static private Map<String, Set<EdgePair>> almacenCaminos;
    static private Map<String, Set<EdgePair>> caminosRecorridos;
    static private Map<String, EdgePair> caminoActual;
    static private AddPrintConditionsTransformer instance;
    static private TemplateEngine templateEngine = new TemplateEngine();
    static private ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    static private ControlFlowAnalyser controlFlowAnalyser = new ControlFlowAnalyser();

    public void addNodoToIntger(String nombre){
        controlFlowAnalyser.getNodoToInteger().put(nombre, new HashMap<>());
    }

    public void addNodoLinenumber(String nombre){
        controlFlowAnalyser.getNodoToLinenumber().put(nombre, new HashMap<>());
    }

    private static class ShutDownHook extends Thread {
        public void run() {
            long startTime = System.nanoTime();
            AddPrintConditionsTransformer.imprimirInforme();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;  // Divide by 1000000 to get milliseconds.
            if (DEBUG) System.out.println(duration + " ms to execute the shutdown hook");
        }
    }

    private void initializeThymeleaf(){
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.process("report", new Context(), Writer.nullWriter());
    }

    private AddPrintConditionsTransformer (){
        new AddPrintConditionsTransformer(false);
    }

    public static AddPrintConditionsTransformer getInstance(){
        if (instance == null){
            instance = new AddPrintConditionsTransformer();
        }
        return instance;
    }

    private AddPrintConditionsTransformer (boolean debug){
        DEBUG = debug;
        if (DEBUG) System.out.println("Prueba constructor");
        if (almacenCaminos == null) almacenCaminos = new HashMap<>();
        if (caminosRecorridos == null) caminosRecorridos = new HashMap<>();
        if (caminoActual == null) caminoActual = new HashMap<>();
        if (grafosMetodos == null) grafosMetodos = new HashMap<>();
        if (grafosRenderedMetodos == null) grafosRenderedMetodos = new HashMap<>();
        if (caminosImposiblesMetodo == null) caminosImposiblesMetodo = new HashMap<>();
        initializeThymeleaf();
        ShutDownHook jvmShutdownHook = new ShutDownHook();
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
    }

    static int numeroCaminosTotal(String descriptorMetodo){
        return almacenCaminos.get(descriptorMetodo).size();
    }

    static int numeroCaminosCubiertos(String descriptorMetodo){
        return caminosRecorridos.get(descriptorMetodo).size();
    }

    static String graphToDot(String metodo){
        DOTExporter<Integer, BooleanEdge> exporter = new DOTExporter<>();
        Map<Integer, Integer> nodoToLinenumberMap = controlFlowAnalyser.getNodoToLinenumber().get(metodo);
        //nodoToLinenumberMap.getOrDefault(v, v);
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            // map.put("id", DefaultAttribute.createAttribute("a"));
            map.put("label", DefaultAttribute.createAttribute(nodoToLinenumberMap.getOrDefault(v, v)));
            return map;
        });
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.toString()));
            return map;
        });
        Writer writer = new StringWriter();
        // exporter.exportGraph(transformGraphFromIntegerToLinenumber(metodo, grafosMetodos.get(metodo)), writer);
        exporter.exportGraph(grafosMetodos.get(metodo), writer);
        //System.out.println(writer);
        return writer.toString();
    }

    static public void imprimirInforme(){
        if (DEBUG) System.out.println("---------------- Ejecucion terminada ----------------");
        List<MethodReportDTO> methodReportDTOList = new ArrayList<>();
        for (String metodo: almacenCaminos.keySet()){
            Set<EdgePair> todosCaminos = almacenCaminos.get(metodo);
            Set<EdgePair> recorridosCaminos = caminosRecorridos.get(metodo);
            Map<Integer, Integer> nodoToLinenumberMap  = controlFlowAnalyser.getNodoToLinenumber().get(metodo);
            MethodReportDTO methodReportDTO = new MethodReportDTO(metodo,
                    graphToDot(metodo),
                    grafosRenderedMetodos.get(metodo),
                    caminosImposiblesMetodo.get(metodo),
                    todosCaminos.stream().map(c -> {
                        if (!nodoToLinenumberMap.containsKey(c.nodoInicio)
                                || !nodoToLinenumberMap.containsKey(c.nodoMedio)
                                || !nodoToLinenumberMap.containsKey(c.nodoFinal))
                            return c;
                        else
                            return new EdgePair(
                                    nodoToLinenumberMap.get(c.nodoInicio),
                                    c.aristaInicioMedio,
                                    nodoToLinenumberMap.get(c.nodoMedio),
                                    c.aristaMedioFinal,
                                    nodoToLinenumberMap.get(c.nodoFinal))
                                    ;
                        }
                    ).sorted(Comparator.comparing((EdgePair c) -> c.nodoInicio).thenComparing(c -> c.nodoMedio).thenComparing(c -> c.nodoFinal)).toList(),
                    recorridosCaminos.stream().map(c -> {
                        if (!nodoToLinenumberMap.containsKey(c.nodoInicio)
                                || !nodoToLinenumberMap.containsKey(c.nodoMedio)
                                || !nodoToLinenumberMap.containsKey(c.nodoFinal)){
                            return c;
                        }
                        else {
                            return new EdgePair(nodoToLinenumberMap.get(c.nodoInicio), c.aristaInicioMedio, nodoToLinenumberMap.get(c.nodoMedio), c.aristaMedioFinal, nodoToLinenumberMap.get(c.nodoFinal));
                        }
                    }).toList(),
                    (100.0D * (recorridosCaminos.size()) / (todosCaminos.size() - caminosImposiblesMetodo.get(metodo)))
                    );
            methodReportDTOList.add(methodReportDTO);
        }
        Context context = new Context();
        context.setVariable("methods", methodReportDTOList);
        StringWriter stringWriter = new StringWriter();
        try {
            File reportDirectory = new File("coverageReport");
            if (!reportDirectory.exists()) reportDirectory.mkdirs();
            Writer fileWriter = new FileWriter(reportDirectory.getName() + "/report.html");
            templateEngine.process("report", context, fileWriter);
        } catch (IOException e) {
            templateEngine.process("report", context, stringWriter);
            System.out.println(stringWriter.toString());
        }
    }



    // TODO: mover a otra clase
    static public void markPredicateNode(String metodo, int nodeId) {
        EdgePair camino = caminoActual.get(metodo);
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
        EdgePair camino = caminoActual.get(metodo);
        camino.addNode(nodeId);
        if (camino.isComplete()){
            caminosRecorridos.get(metodo).add(camino);
        }
        caminoActual.put(metodo, new EdgePair(null,  null, null,  null, null));
        // System.out.print(nodeId);
    }

    static public void markEdge(String metodo, String edge) {
        EdgePair camino = caminoActual.get(metodo);
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
        il.add(new MethodInsnNode(INVOKESTATIC, "org/juancatalan/edgepaircoverage/AddPrintConditionsTransformer", "markEndNode", "(Ljava/lang/String;I)V"));
        return il;
    }

    public InsnList addMarkPredicateNode(String metodo, Integer nodeId){
        InsnList il = new InsnList();
        il.add(new LdcInsnNode(metodo));
        il.add(new LdcInsnNode(nodeId));
        il.add(new MethodInsnNode(INVOKESTATIC, "org/juancatalan/edgepaircoverage/AddPrintConditionsTransformer", "markPredicateNode", "(Ljava/lang/String;I)V"));
        return il;
    }

    public InsnList addMarkEdge(String metodo, EdgeType edge){
        InsnList il = new InsnList();
        il.add(new LdcInsnNode(metodo));
        il.add(new LdcInsnNode(edge.name()));
        il.add(new MethodInsnNode(INVOKESTATIC, "org/juancatalan/edgepaircoverage/AddPrintConditionsTransformer", "markEdge", "(Ljava/lang/String;Ljava/lang/String;)V"));
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
                if (DEBUG) System.out.println(in + " -> " + controlFlowAnalyser.getNodoToInteger().get(claseYmetodo).get(in));
                // Si es el nodo inicial
                if (grafo.inDegreeOf(in) == 0) {
                    insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, controlFlowAnalyser.getNodoToInteger().get(claseYmetodo).get(in)));

                    // Añado el camino default
                    insns.insert(in, addMarkEdge(claseYmetodo, EdgeType.DEFAULT));
                }
                // Si son nodo predicado
                else if (grafo.outDegreeOf(in) > 0) {
                    insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, controlFlowAnalyser.getNodoToInteger().get(claseYmetodo).get(in)));
                    AbstractInsnNode target = controlFlowAnalyser.findJumpDestiny((JumpInsnNode) in);
                    // Añado el camino del true
                    insns.insert(target, addMarkEdge(claseYmetodo, EdgeType.FALSE));
                    // Añado el camino del true
                    insns.insert(in, addMarkEdge(claseYmetodo, EdgeType.TRUE));
                }
                // Si son nodos de finalizacion
                else {
                    insns.insertBefore(in, addMarkEndNode(claseYmetodo, controlFlowAnalyser.getNodoToInteger().get(claseYmetodo).get(in)));
                }
            }
        }
    }


    static DirectedPseudograph<Integer, BooleanEdge> transformGraphToInteger(String metodo, DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo){
        DirectedPseudograph<Integer, BooleanEdge> newGraph = new DirectedPseudograph<>(BooleanEdge.class);
        Map<AbstractInsnNode, Integer> nodeIntegerMap = controlFlowAnalyser.getNodoToInteger().get(metodo);
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

    static DirectedPseudograph<Integer, BooleanEdge> transformGraphFromIntegerToLinenumber(String metodo, DirectedPseudograph<Integer, BooleanEdge> grafo){
        DirectedPseudograph<Integer, BooleanEdge> newGraph = new DirectedPseudograph<>(BooleanEdge.class);
        Map<Integer, Integer> integerLinenumberMap = controlFlowAnalyser.getNodoToLinenumber().get(metodo);
        Map<Integer, Integer> numNodosPorLinea = new HashMap<>();
        for (Integer nodo: grafo.vertexSet()){
            // Si existe
            if (numNodosPorLinea.get(nodo) != null){

            }
            newGraph.addVertex(integerLinenumberMap.get(nodo));
        }
        for (BooleanEdge edge: grafo.edgeSet()){
            newGraph.addEdge(integerLinenumberMap.get(grafo.getEdgeSource(edge)),
                    integerLinenumberMap.get(grafo.getEdgeTarget(edge)),
                    new BooleanEdge(edge.getType()));
        }
        return newGraph;
    }

    public ControlFlowAnalyser getControlFlowAnalyser(){
        return controlFlowAnalyser;
    }

    public Set<EdgePair> obtenerSituacionesPrueba(String idMetodo, DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo){
        Set<EdgePair> caminos = new HashSet<>();
        for (AbstractInsnNode i: grafo.vertexSet()){
            if (!grafo.incomingEdgesOf(i).isEmpty() && !grafo.outgoingEdgesOf(i).isEmpty()){
                for (BooleanEdge edgeIn: grafo.incomingEdgesOf(i)){
                    for (BooleanEdge edgeOut: grafo.outgoingEdgesOf(i)){
                        Integer nodoInicio = controlFlowAnalyser.getNodoToInteger().get(idMetodo).get(grafo.getEdgeSource(edgeIn));
                        Integer nodoMedio = controlFlowAnalyser.getNodoToInteger().get(idMetodo).get(grafo.getEdgeTarget(edgeIn));
                        Integer nodoFinal = controlFlowAnalyser.getNodoToInteger().get(idMetodo).get(grafo.getEdgeTarget(edgeOut));

                        EdgePair camino = new EdgePair(nodoInicio, edgeIn.getType(), nodoMedio,
                                edgeOut.getType(), nodoFinal);
                        caminos.add(camino);
                    }
                }
            }
        }
        return caminos;
    }

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
                    if (an.values != null){
                        for (int i = 0; i < an.values.size(); i++) {
                            if (an.values.get(i).equals("numCaminosImposibles")){
                                caminosImposiblesMetodo.put(idMetodo, (Integer) an.values.get(i + 1));
                                break;
                            }
                        }
                    } else {
                        caminosImposiblesMetodo.put(idMetodo, 0);
                    }
                    isAnnotated = true;
                }
            }
            if (isAnnotated) {
                controlFlowAnalyser.getNodoToInteger().put(idMetodo, new HashMap<>());
                controlFlowAnalyser.getNodoToLinenumber().put(idMetodo, new HashMap<>());
                caminosRecorridos.put(idMetodo, new HashSet<>());
                caminoActual.put(idMetodo, new EdgePair(null, null, null, null, null));
                if (DEBUG) System.out.println("I'm transforming my own classes: ".concat(className));
                if (DEBUG) System.out.println("I'm transforming the method: ".concat(mn.name));
                if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                    continue;
                }
                InsnList insns = mn.instructions;
                if (insns.size() == 0) {
                    continue;
                }

                try{
                    DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo = controlFlowAnalyser.getControlFlowGraph(insns, idMetodo);
                    if (DEBUG) System.out.println(grafo.toString());
                    grafosMetodos.put(idMetodo, transformGraphToInteger(idMetodo, grafo));
                    byte[] output = new byte[graphToDot(idMetodo).getBytes().length];
                    Deflater compresser = new Deflater();
                    compresser.setInput(graphToDot(idMetodo).getBytes("UTF-8"));
                    compresser.finish();
                    compresser.deflate(output);
                    //System.out.println("output");
                    //System.out.println(new String(output));
                    String outputB64 = Base64.getUrlEncoder().encodeToString(output);
                    grafosRenderedMetodos.put(idMetodo,"https://kroki.io/graphviz/png/" + outputB64);



                    Set<EdgePair> caminos = obtenerSituacionesPrueba(idMetodo, grafo);
                    //System.out.println("Caminos de profundidad 2: " + caminos.size());
                    if (DEBUG) System.out.println(caminos);
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
        return this.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }
}
