package com.jn.agileway.resoucescan.classcanning.asmvisitor;

import org.objectweb.asm.FieldVisitor;

public class FieldMetadataVisitor extends FieldVisitor {
    public FieldMetadataVisitor() {
        super(ASMApiVersion.ASM_API_VERSION);
    }
}
