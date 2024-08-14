package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphToDotTransformer {
    public static String graphToDot(DirectedPseudograph<Integer, BooleanEdge> grafo, Map<Integer, String> nodoToLinenumberMap){
        DOTExporter<Integer, BooleanEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            // map.put("id", DefaultAttribute.createAttribute("a"));
            map.put("label", DefaultAttribute.createAttribute(nodoToLinenumberMap.getOrDefault(v, v.toString())));
            return map;
        });
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.toString()));
            return map;
        });
        Writer writer = new StringWriter();
        // exporter.exportGraph(transformGraphFromIntegerToLinenumber(metodo, grafosMetodos.get(metodo)), writer);
        exporter.exportGraph(grafo, writer);
        //System.out.println(writer);
        return writer.toString();
    }
}
