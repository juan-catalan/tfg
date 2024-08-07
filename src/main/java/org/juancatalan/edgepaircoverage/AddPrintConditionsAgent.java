package org.juancatalan.edgepaircoverage;

import java.lang.instrument.Instrumentation;

public class AddPrintConditionsAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("agentArgs");
        System.out.println(agentArgs);
        inst.addTransformer(AddPrintConditionsTransformer.getInstance());
    }
}
