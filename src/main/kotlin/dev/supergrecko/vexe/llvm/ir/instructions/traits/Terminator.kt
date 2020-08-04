package dev.supergrecko.vexe.llvm.ir.instructions.traits

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.ir.BasicBlock
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface Terminator : ContainsReference<LLVMValueRef> {
    //region Core::Instructions::Terminators
    /**
     * Get the number of successors that this terminator has
     *
     * @see LLVM.LLVMGetNumSuccessors
     */
    public fun getSuccessorCount(): Int {
        return LLVM.LLVMGetNumSuccessors(ref)
    }

    /**
     * Get a successor at [index]
     *
     * @see LLVM.LLVMGetSuccessor
     */
    public fun getSuccessor(index: Int): BasicBlock {
        require(index < getSuccessorCount()) {
            "Out of bounds index. Index: $index, Count: ${getSuccessorCount()}"
        }

        val bb = LLVM.LLVMGetSuccessor(ref, index)

        return BasicBlock(bb)
    }

    /**
     * Set a successor at [index]
     *
     * @see LLVM.LLVMSetSuccessor
     */
    public fun setSuccessor(index: Int, block: BasicBlock) {
        LLVM.LLVMSetSuccessor(ref, index, block.ref)
    }
    //endregion Core::Instructions::Terminators
}
