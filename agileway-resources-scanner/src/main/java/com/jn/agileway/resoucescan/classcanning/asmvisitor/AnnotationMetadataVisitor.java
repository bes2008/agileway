package com.jn.agileway.resoucescan.classcanning.asmvisitor;

import org.objectweb.asm.AnnotationVisitor;

public class AnnotationMetadataVisitor extends AnnotationVisitor {
    public AnnotationMetadataVisitor() {
        super(ASMApiVersion.ASM_API_VERSION);
    }
}
