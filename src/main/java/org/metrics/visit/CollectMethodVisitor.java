package org.metrics.visit;

import org.metrics.model.MethodInfo;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class CollectMethodVisitor extends MethodVisitor {
    private final MethodInfo methodInfo;

    public CollectMethodVisitor(int api, MethodVisitor delegate, MethodInfo methodInfo) {
        super(api, delegate);
        this.methodInfo = methodInfo;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (isStoreVarOpcode(opcode)) {
            methodInfo.abcStores++;
        }
        super.visitVarInsn(opcode, var);
    }

    private static boolean isStoreVarOpcode(int opcode) {
        return opcode == Opcodes.ISTORE
                || opcode == Opcodes.LSTORE
                || opcode == Opcodes.FSTORE
                || opcode == Opcodes.DSTORE
                || opcode == Opcodes.ASTORE;
    }
}


