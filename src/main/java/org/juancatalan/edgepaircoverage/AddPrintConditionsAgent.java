package org.juancatalan.edgepaircoverage;

import java.lang.instrument.Instrumentation;

public class AddPrintConditionsAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(AddPrintConditionsTransformer.getInstance());
    }
}
