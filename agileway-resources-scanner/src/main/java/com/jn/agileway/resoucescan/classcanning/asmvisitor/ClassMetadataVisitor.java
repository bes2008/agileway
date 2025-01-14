package com.jn.agileway.resoucescan.classcanning.asmvisitor;

import org.objectweb.asm.ClassVisitor;

public class ClassMetadataVisitor extends ClassVisitor {
    public ClassMetadataVisitor() {
        super(ASMApiVersion.ASM_API_VERSION);
    }
}
