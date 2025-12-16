package org.metrics.visit;

import org.metrics.model.MethodInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class CollectMethodVisitor extends MethodVisitor {
    private final MethodInfo methodInfo;

    public CollectMethodVisitor(int api, MethodVisitor delegate, MethodInfo methodInfo) {
        super(api, delegate);
        this.methodInfo = methodInfo;
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if (isStoreVarOpcode(opcode)) {
            methodInfo.abcStores++;
        }
        super.visitVarInsn(opcode, varIndex);
    }
    
    private static boolean isStoreVarOpcode(int opcode) {
        return opcode == Opcodes.ISTORE
                || opcode == Opcodes.LSTORE
                || opcode == Opcodes.FSTORE
                || opcode == Opcodes.DSTORE
                || opcode == Opcodes.ASTORE;
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (opcode == Opcodes.GOTO) {
            methodInfo.abcBranches++;
        } else if (isConditionalJumpOpcode(opcode)) {
            methodInfo.abcConditions++;
        }
        super.visitJumpInsn(opcode, label);
    }

    private static boolean isConditionalJumpOpcode(int opcode) {
        return opcode == Opcodes.IFEQ
                || opcode == Opcodes.IFNE
                || opcode == Opcodes.IFLT
                || opcode == Opcodes.IFGE
                || opcode == Opcodes.IFGT
                || opcode == Opcodes.IFLE
                || opcode == Opcodes.IF_ICMPEQ
                || opcode == Opcodes.IF_ICMPNE
                || opcode == Opcodes.IF_ICMPLT
                || opcode == Opcodes.IF_ICMPGE
                || opcode == Opcodes.IF_ICMPGT
                || opcode == Opcodes.IF_ICMPLE
                || opcode == Opcodes.IF_ACMPEQ
                || opcode == Opcodes.IF_ACMPNE
                || opcode == Opcodes.IFNULL
                || opcode == Opcodes.IFNONNULL;
    }
}


