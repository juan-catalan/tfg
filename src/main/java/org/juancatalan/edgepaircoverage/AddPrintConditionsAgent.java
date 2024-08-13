package org.juancatalan.edgepaircoverage;

import org.apache.commons.lang3.StringUtils;

import java.lang.instrument.Instrumentation;
import java.util.*;

public class AddPrintConditionsAgent {
    static Map<String, Integer> metodosCaminosImposibles = new HashMap<>();
    static boolean booleanAssignmentPredicateNode = false;

    private static void parse(String args){
        System.out.println("agentArgs");
        System.out.println(args);
        // Parseo de metodos a medir cobertura
        String metodos = StringUtils.substringBetween(args, "methods={", "}");
        if (metodos != null){
            for (String s : metodos.split(";;")) {
                String[] metodoYSituacionesImposibles = s.split(":");
                metodosCaminosImposibles.put(metodoYSituacionesImposibles[0], Integer.valueOf(metodoYSituacionesImposibles[1]));
            }
            System.out.println(metodosCaminosImposibles);
        }
        // Parseo de parametros de configuracion
        String booleanAssignmentPredicateNodeOption = StringUtils.substringBetween(args, "booleanAssignmentPredicateNode={", "}");
        if (booleanAssignmentPredicateNodeOption != null){
            booleanAssignmentPredicateNode = Boolean.valueOf(booleanAssignmentPredicateNodeOption);
            System.out.println("booleanAssignmentPredicateNode " + booleanAssignmentPredicateNode);
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        parse(agentArgs);
        AddPrintConditionsTransformer transformer = new AddPrintConditionsTransformer(metodosCaminosImposibles, booleanAssignmentPredicateNode);
        inst.addTransformer(transformer);
    }
}
