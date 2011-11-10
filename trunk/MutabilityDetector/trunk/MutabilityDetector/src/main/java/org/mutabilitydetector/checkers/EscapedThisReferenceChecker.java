package org.mutabilitydetector.checkers;

import java.util.ArrayList;
import java.util.List;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.util.StackPushingOpcodes;
import org.mutabilitydetector.locations.ClassLocation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EscapedThisReferenceChecker extends AbstractMutabilityChecker {

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        super.visitMethod(access, name, desc, signature, exceptions);
        return name.equals("<init>")
                ? new ThisEscapingFromConstructorVistor(access, name, desc, signature, exceptions)
                : new EmptyVisitor();
    }

    private final class ThisEscapingFromConstructorVistor extends MethodNode {

        private final List<MethodInsnNode> methodCalls = new ArrayList<MethodInsnNode>();
        private final List<FieldInsnNode> fieldAssignmentsInConstructor = new ArrayList<FieldInsnNode>();

        private final StackPushingOpcodes stackPushingOpcodes = new StackPushingOpcodes();
        
        public ThisEscapingFromConstructorVistor(int access,
                String name,
                String desc,
                String signature,
                String[] exceptions) {
            super(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            super.visitMethodInsn(opcode, owner, name, desc);
            if (name.equals("<init>") && owner.equals("java/lang/Object")) { return; }
            methodCalls.add((MethodInsnNode) instructions.getLast());
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);

            if (opcode == Opcodes.PUTSTATIC || opcode == Opcodes.PUTFIELD) {
                fieldAssignmentsInConstructor.add((FieldInsnNode) instructions.getLast());
            }

        }

        @Override
        public void visitEnd() {
            super.visitEnd();

            checkForPassingThisReferenceAsParameter();
            checkForSettingFieldToThisReference();
        }

        private void checkForSettingFieldToThisReference() {
            if (fieldAssignmentsInConstructor.isEmpty()) { return; }

            for (FieldInsnNode fieldInstruction : fieldAssignmentsInConstructor) {
                checkFieldAssignment(fieldInstruction);
            }

        }

        private void checkFieldAssignment(FieldInsnNode assignment) {
            AbstractInsnNode previous = assignment.getPrevious();
            if (stackPushingOpcodes.includes(previous.getOpcode())) {
                checkForThisReferenceBeingPutOnStack(previous);
            }
            
        }

        private void checkForPassingThisReferenceAsParameter() {
            if (methodCalls.isEmpty()) { return; }

            for (MethodInsnNode methodInsnNode : methodCalls) {
                checkMethodCall(methodInsnNode);
            }
        }

        private void checkMethodCall(MethodInsnNode methodInsnNode) {
            AbstractInsnNode previous = methodInsnNode.getPrevious();
            Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);
            int numberOfArguments = argumentTypes.length;

            for (int i = numberOfArguments - 1; i >= 0; i--) {
                if (instructionPushesSomethingElseOnTheStack(previous)) {
                    i = i + 1;
                } 

                checkForThisReferenceBeingPutOnStack(previous);

                previous = previous.getPrevious();
            }
        }

        private boolean instructionPushesSomethingElseOnTheStack(AbstractInsnNode previous) {
            switch(previous.getOpcode()) {
            case Opcodes.DUP:
            case Opcodes.NEW:
                return true;
            default:
                return false;
            }
        }

        private void checkForThisReferenceBeingPutOnStack(AbstractInsnNode previous) {
            if (previous instanceof VarInsnNode) {
                VarInsnNode varInstruction = (VarInsnNode) previous;
                if (varInstruction.var == 0) {
                    thisReferencesEscapes();
                }
            }
        }

        private void thisReferencesEscapes() {
            addResult("The 'this' reference is passed outwith the constructor.",
                    ClassLocation.fromInternalName(ownerClass),
                    MutabilityReason.ESCAPED_THIS_REFERENCE);
        }
    }
}