package dev.supergrecko.kllvm.values.constants

import dev.supergrecko.kllvm.llvm.typedefs.Context
import dev.supergrecko.kllvm.types.StructType
import dev.supergrecko.kllvm.internal.util.toInt
import dev.supergrecko.kllvm.values.Constant
import dev.supergrecko.kllvm.values.Value
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantStruct internal constructor() : Value(), Constant {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    /**
     * @see [LLVM.LLVMConstStructInContext]
     */
    public constructor(
        values: List<Value>,
        packed: Boolean,
        context: Context = Context.getGlobalContext()
    ) : this() {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMConstStructInContext(
            context.ref,
            PointerPointer(*ptr),
            ptr.size,
            packed.toInt()
        )
    }

    /**
     * @see [LLVM.LLVMConstNamedStruct]
     */
    public constructor(type: StructType, values: List<Value>) : this() {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        ref =
            LLVM.LLVMConstNamedStruct(type.ref, PointerPointer(*ptr), ptr.size)
    }

    //region Core::Values::Constants::CompositeConstants
    /**
     * Get an element at specified [index] as a constant
     *
     * This is shared with [ConstantArray], [ConstantVector], [ConstantStruct]
     *
     * TODO: Move into contract
     */
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }
    //endregion Core::Values::Constants::CompositeConstants
}