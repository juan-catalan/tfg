package org.example;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class AddPrintConditionsAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("I'm the agent");
        inst.addTransformer(new AddPrintConditionsTransformer());
    }
}
