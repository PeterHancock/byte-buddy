package com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.stack;

import com.blogspot.mydailyjava.bytebuddy.instrumentation.Instrumentation;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.TypeDescription;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Duplicates a value that is lying on top of the stack.
 */
public enum Duplication implements StackManipulation {
    ZERO(StackSize.ZERO, Opcodes.NOP),
    SINGLE(StackSize.SINGLE, Opcodes.DUP),
    DOUBLE(StackSize.DOUBLE, Opcodes.DUP2);

    /**
     * Duplicates a value given its type.
     *
     * @param typeDescription The type to be duplicated.
     * @return A stack manipulation that duplicates the given type.
     */
    public static StackManipulation duplicate(TypeDescription typeDescription) {
        switch (typeDescription.getStackSize()) {
            case SINGLE:
                return SINGLE;
            case DOUBLE:
                return DOUBLE;
            case ZERO:
                return ZERO;
            default:
                throw new AssertionError();
        }
    }

    private final Size size;
    private final int opcode;

    private Duplication(StackSize stackSize, int opcode) {
        size = stackSize.toIncreasingSize();
        this.opcode = opcode;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Instrumentation.Context instrumentationContext) {
        methodVisitor.visitInsn(opcode);
        return size;
    }
}