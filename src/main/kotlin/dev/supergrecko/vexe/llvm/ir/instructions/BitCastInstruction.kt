package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class BitCastInstruction internal constructor() : Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}
