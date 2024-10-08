package org.juancatalan.edgepaircoverage;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.charset.StandardCharsets;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.List;
import java.util.zip.Deflater;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.DTO.EdgePairDTO;
import org.juancatalan.edgepaircoverage.DTO.MethodReportDTO;
import org.juancatalan.edgepaircoverage.controlFlow.ControlFlowAnalyser;
import org.juancatalan.edgepaircoverage.controlFlow.EdgePair;
import org.juancatalan.edgepaircoverage.graphs.BooleanEdge;
import org.juancatalan.edgepaircoverage.graphs.EdgeType;
import org.juancatalan.edgepaircoverage.graphs.GraphToDotTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


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
    static private ControlFlowAnalyser controlFlowAnalyser;
    static private Set<String> metodosAMedir;

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

    public AddPrintConditionsTransformer (Map<String, Integer> metodosCaminosImposibles, boolean isBooleanAssignmentPredicateNode){
        new AddPrintConditionsTransformer(metodosCaminosImposibles, isBooleanAssignmentPredicateNode, false);
    }


    private AddPrintConditionsTransformer (Map<String, Integer> metodosCaminosImposibles, boolean isBooleanAssignmentPredicateNode, boolean debug){
        DEBUG = debug;
        if (DEBUG) System.out.println("Prueba constructor");
        if (almacenCaminos == null) almacenCaminos = new HashMap<>();
        if (caminosRecorridos == null) caminosRecorridos = new HashMap<>();
        if (caminoActual == null) caminoActual = new HashMap<>();
        if (grafosMetodos == null) grafosMetodos = new HashMap<>();
        if (grafosRenderedMetodos == null) grafosRenderedMetodos = new HashMap<>();
        if (caminosImposiblesMetodo == null) caminosImposiblesMetodo = new HashMap<>();
        if (metodosCaminosImposibles!=null){
            metodosAMedir = metodosCaminosImposibles.keySet();
            caminosImposiblesMetodo.putAll(metodosCaminosImposibles);
        }
        controlFlowAnalyser = new ControlFlowAnalyser(isBooleanAssignmentPredicateNode);
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


    static private double calcularCobertura(int situacionesEjecutadas, int totalSituaciones, int situacionesImposibles){
        if (totalSituaciones == 0) return 100;
        double cobertura = (100.0D * ((double) situacionesEjecutadas / (totalSituaciones - situacionesImposibles)));
        return (cobertura < 100) ? cobertura : 100.0D;
    }

    static private MethodReportDTO obtenerMethodReportDTO(String metodo){
        Set<EdgePair> todosCaminos = almacenCaminos.get(metodo);
        Set<EdgePair> recorridosCaminos = caminosRecorridos.get(metodo);
        Map<Integer, String> nodoToLinenumberMap  = controlFlowAnalyser.getMappingFromNodoToLinenumber(metodo);
        return new MethodReportDTO(
                metodo,
                GraphToDotTransformer.graphToDot(controlFlowAnalyser.getControlFlowGraphAsIntegerGraph(metodo), controlFlowAnalyser.getMappingFromNodoToLinenumber(metodo)),
                grafosRenderedMetodos.get(metodo),
                caminosImposiblesMetodo.get(metodo),
                todosCaminos.stream().map(c -> {
                            if (!nodoToLinenumberMap.containsKey(c.nodoInicio)
                                    || !nodoToLinenumberMap.containsKey(c.nodoMedio)
                                    || !nodoToLinenumberMap.containsKey(c.nodoFinal))
                                return new EdgePairDTO(
                                        c.nodoInicio.toString(),
                                        c.aristaInicioMedio,
                                        c.nodoMedio.toString(),
                                        c.aristaMedioFinal,
                                        c.nodoFinal.toString()
                                );
                            else
                                return new EdgePairDTO(
                                        nodoToLinenumberMap.get(c.nodoInicio),
                                        c.aristaInicioMedio,
                                        nodoToLinenumberMap.get(c.nodoMedio),
                                        c.aristaMedioFinal,
                                        nodoToLinenumberMap.get(c.nodoFinal))
                                        ;
                        }
                ).sorted(Comparator.comparing((EdgePairDTO c) -> c.nodoInicio).thenComparing(c -> c.nodoMedio).thenComparing(c -> c.nodoFinal)).toList(),
                recorridosCaminos.stream().map(c -> {
                            if (!nodoToLinenumberMap.containsKey(c.nodoInicio)
                                    || !nodoToLinenumberMap.containsKey(c.nodoMedio)
                                    || !nodoToLinenumberMap.containsKey(c.nodoFinal))
                                return new EdgePairDTO(
                                        c.nodoInicio.toString(),
                                        c.aristaInicioMedio,
                                        c.nodoMedio.toString(),
                                        c.aristaMedioFinal,
                                        c.nodoFinal.toString()
                                );
                            else
                                return new EdgePairDTO(
                                        nodoToLinenumberMap.get(c.nodoInicio),
                                        c.aristaInicioMedio,
                                        nodoToLinenumberMap.get(c.nodoMedio),
                                        c.aristaMedioFinal,
                                        nodoToLinenumberMap.get(c.nodoFinal))
                                        ;
                        }
                ).sorted(Comparator.comparing((EdgePairDTO c) -> c.nodoInicio).thenComparing(c -> c.nodoMedio).thenComparing(c -> c.nodoFinal)).toList(),
                calcularCobertura(recorridosCaminos.size(), todosCaminos.size(), caminosImposiblesMetodo.get(metodo))
        );
    }

    static public void imprimirInforme(){
        if (DEBUG) System.out.println("---------------- Ejecucion terminada ----------------");
        List<MethodReportDTO> methodReportDTOList = new ArrayList<>();
        for (String metodo: almacenCaminos.keySet()){
            MethodReportDTO methodReportDTO = obtenerMethodReportDTO(metodo);
            methodReportDTOList.add(methodReportDTO);
        }
        // Genero el report.html
        Context context = new Context();
        context.setVariable("methods", methodReportDTOList);
        StringWriter stringWriter = new StringWriter();
        try {
            File reportDirectory = new File(".edgePairCoverage");
            if (!reportDirectory.exists()) reportDirectory.mkdirs();
            Writer fileWriter = new FileWriter(reportDirectory.getName() + "/report.html");
            templateEngine.process("report", context, fileWriter);
        } catch (IOException e) {
            templateEngine.process("report", context, stringWriter);
            System.out.println(stringWriter.toString());
        }
        // Genero el report.json
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            File reportDirectory = new File(".edgePairCoverage");
            if (!reportDirectory.exists()) reportDirectory.mkdirs();
            Writer fileWriter = new FileWriter(reportDirectory.getName() + "/report.json");
            String json = ow.writeValueAsString(methodReportDTOList);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void addInstructionsConditionsAndBranches(String claseYmetodo, InsnList insns, DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo, Map<AbstractInsnNode, Integer> abstractInsnNodeToIntegerMap){
        Iterator<AbstractInsnNode> j = insns.iterator();
        while (j.hasNext()) {
            AbstractInsnNode in = j.next();
            if (grafo.containsVertex(in)){
                if (DEBUG) System.out.println(in + " -> " + abstractInsnNodeToIntegerMap.get(in));
                // Si es el nodo inicial
                if (grafo.inDegreeOf(in) == 0) {
                    insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, abstractInsnNodeToIntegerMap.get(in)));

                    // Añado el camino default
                    insns.insert(in, addMarkEdge(claseYmetodo, EdgeType.DEFAULT));
                }
                // Si son nodo predicado
                else if (grafo.outDegreeOf(in) > 0) {
                    insns.insertBefore(in, addMarkPredicateNode(claseYmetodo, abstractInsnNodeToIntegerMap.get(in)));
                    AbstractInsnNode target = controlFlowAnalyser.findJumpDestiny((JumpInsnNode) in);
                    // Añado el camino del false
                    insns.insert(target, addMarkEdge(claseYmetodo, EdgeType.FALSE));
                    // Añado el camino del true
                    insns.insert(in, addMarkEdge(claseYmetodo, EdgeType.TRUE));
                }
                // Si son nodos de finalizacion
                else {
                    insns.insertBefore(in, addMarkEndNode(claseYmetodo, abstractInsnNodeToIntegerMap.get(in)));
                }
            }
        }
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
            if (isAnnotated || metodosAMedir.contains(idMetodo)) {
                caminosRecorridos.put(idMetodo, new HashSet<>());
                caminoActual.put(idMetodo, new EdgePair(null, null, null, null, null));
                if (DEBUG) System.out.println("I'm transforming my own classes: ".concat(className));
                if (DEBUG) System.out.println("I'm transforming the method: ".concat(mn.name));
                InsnList insns = mn.instructions;


                controlFlowAnalyser.analyze(idMetodo, insns);
                DirectedPseudograph<AbstractInsnNode, BooleanEdge> grafo = controlFlowAnalyser.getControlFlowGraph(idMetodo);
                DirectedPseudograph<Integer, BooleanEdge> grafoAsInteger = controlFlowAnalyser.getControlFlowGraphAsIntegerGraph(idMetodo);
                if (DEBUG) System.out.println(grafo.toString());
                grafosMetodos.put(idMetodo, grafoAsInteger);
                String graphDot = GraphToDotTransformer.graphToDot(grafoAsInteger, controlFlowAnalyser.getMappingFromNodoToLinenumber(idMetodo));
                byte[] output = new byte[graphDot.getBytes().length];
                Deflater compresser = new Deflater();
                compresser.setInput(
                        graphDot
                                .getBytes(StandardCharsets.UTF_8));
                compresser.finish();
                compresser.deflate(output);
                //System.out.println("output");
                //System.out.println(new String(output));
                String outputB64 = Base64.getUrlEncoder().encodeToString(output);
                grafosRenderedMetodos.put(idMetodo,"https://kroki.io/graphviz/png/" + outputB64);



                Set<EdgePair> caminos = controlFlowAnalyser.obtenerSituacionesPrueba(idMetodo);
                //System.out.println("Caminos de profundidad 2: " + caminos.size());
                if (DEBUG) System.out.println(caminos);
                almacenCaminos.put(idMetodo ,caminos);


                this.addInstructionsConditionsAndBranches(idMetodo, insns, grafo, controlFlowAnalyser.getNodoToInteger().get(idMetodo));
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
