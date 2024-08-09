package org.juancatalan.edgepaircoverage;

import org.apache.commons.lang3.StringUtils;

import java.lang.instrument.Instrumentation;
import java.util.*;

public class AddPrintConditionsAgent {
    static Map<String, Integer> metodosCaminosImposibles = new HashMap<>();

    private static void parse(String args){
        // Parseo de metodos a medir cobertura
        System.out.println("agentArgs");
        System.out.println(args);
        String metodos = StringUtils.substringBetween(args, "methods={", "}");
        for (String s : metodos.split(";")) {
            String[] metodoYSituacionesImposibles = s.split(":");
            metodosCaminosImposibles.put(metodoYSituacionesImposibles[0], Integer.valueOf(metodoYSituacionesImposibles[1]));
        }
        System.out.println(metodosCaminosImposibles);
        // Parseo de parametros de configuracion
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        parse(agentArgs);
        AddPrintConditionsTransformer transformer = AddPrintConditionsTransformer.getInstance();
        AddPrintConditionsTransformer.addMetodosMedir(metodosCaminosImposibles);
        inst.addTransformer(transformer);
    }
}
