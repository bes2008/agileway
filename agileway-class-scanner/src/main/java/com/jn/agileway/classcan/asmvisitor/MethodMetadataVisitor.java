package com.jn.agileway.classcan.asmvisitor;

import org.objectweb.asm.MethodVisitor;

public class MethodMetadataVisitor extends MethodVisitor {
    public MethodMetadataVisitor() {
        super(ASMApiVersion.ASM_API_VERSION);
    }
}
