package org.metrics.visit;

import org.metrics.model.ClassInfo;
import org.metrics.model.MethodInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class CollectClassVisitor extends ClassVisitor {
    private final Consumer<ClassInfo> sink;

    private String name;
    private String superName;
    private final List<String> interfaces = new ArrayList<>();
    private boolean isInterface;
    private int fieldsCount;
    private final List<MethodInfo> methods = new ArrayList<>();

    public CollectClassVisitor(int api, Consumer<ClassInfo> sink) {
        super(api);
        this.sink = sink;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = name;
        this.superName = superName;
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        if (interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        fieldsCount++;
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodInfo mi = new MethodInfo(name, descriptor, access);
        methods.add(mi);
        MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new CollectMethodVisitor(Opcodes.ASM9, delegate, mi);
    }

    @Override
    public void visitEnd() {
        ClassInfo classInfo = new ClassInfo(name, superName, interfaces, isInterface, fieldsCount);
        classInfo.methods.addAll(methods);
        sink.accept(classInfo);
        super.visitEnd();
    }
}


