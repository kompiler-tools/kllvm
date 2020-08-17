package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.util.map
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public sealed class Metadata : ContainsReference<LLVMMetadataRef> {
    public final override lateinit var ref: LLVMMetadataRef
        internal set

    /**
     * Represent a [MetadataNode] which has been cast to a [Value]
     */
    public class MetadataAsValue(ref: LLVMValueRef) : Value(ref)

    /**
     * Represent a [Value] which has been cast to a [MetadataNode]
     */
    public class ValueAsMetadata(ref: LLVMMetadataRef) : MetadataNode(ref)

    /**
     * Cast this Metadata node to a value
     *
     * @see LLVM.LLVMMetadataAsValue
     */
    public fun toValue(
        withContext: Context = Context.getGlobalContext()
    ): MetadataAsValue {
        val md = LLVM.LLVMMetadataAsValue(withContext.ref, ref)

        return MetadataAsValue(md)
    }

    public companion object {
        /**
         * Cast a value to a [MetadataNode]
         *
         * @see LLVM.LLVMValueAsMetadata
         */
        public fun fromValue(value: Value): ValueAsMetadata {
            val md = LLVM.LLVMValueAsMetadata(value.ref)

            return ValueAsMetadata(md)
        }

        /**
         * Cast this Metadata node to a value
         *
         * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
         * [Metadata.MetadataAsValue]. This requires a context. Set the context
         * which this should use with [withContext]
         *
         * @see LLVM.LLVMMetadataAsValue
         * @see LLVM.LLVMMetadataAsValue
         */
        public fun toValue(
            metadata: Metadata,
            withContext: Context = Context.getGlobalContext()
        ): MetadataAsValue = metadata.toValue(withContext)
    }
}

public class MetadataString : Metadata {
    public constructor(llvmRef: LLVMMetadataRef) {
        ref = llvmRef
    }

    //region Core::Metadata
    public constructor(
        data: String,
        context: Context = Context.getGlobalContext()
    ) {
        ref = LLVM.LLVMMDStringInContext2(
            context.ref,
            data,
            data.length.toLong()
        )
    }
    //endregion Core::Metadata

    /**
     * Get the string from a MDString node
     *
     * @see LLVM.LLVMGetMDString
     */
    public fun getString(
        context: Context = Context.getGlobalContext()
    ): String {
        val len = IntPointer(0)
        val ptr = LLVM.LLVMGetMDString(toValue(context).ref, len)

        len.deallocate()

        return ptr.string
    }
}

public open class MetadataNode : Metadata {
    public constructor(llvmRef: LLVMMetadataRef) {
        ref = llvmRef
    }

    //region Core::Metadata
    public constructor(
        values: List<Metadata>,
        context: Context = Context.getGlobalContext()
    ) {
        val ptr = PointerPointer(*values.map { it.ref }.toTypedArray())
        ref = LLVM.LLVMMDNodeInContext2(
            context.ref,
            ptr,
            values.size.toLong()
        )
    }

    /**
     * Get the amount of operands in a metadata node
     *
     * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
     * [Metadata.MetadataAsValue]. This requires a context. Set the context
     * which this should use with [withContext]
     *
     * @see LLVM.LLVMGetMDNodeNumOperands
     */
    public fun getOperandCount(
        withContext: Context = Context.getGlobalContext()
    ): Int {
        return LLVM.LLVMGetMDNodeNumOperands(toValue(withContext).ref)
    }

    /**
     * Get the operands from a metadata node
     *
     * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
     * [Metadata.MetadataAsValue]. This requires a context. Set the context
     * which this should use with [withContext]
     *
     * @see LLVM.LLVMGetMDNodeOperands
     */
    public fun getOperands(
        withContext: Context = Context.getGlobalContext()
    ): List<Value> {
        val count = getOperandCount().toLong()
        val ptr = PointerPointer<LLVMValueRef>(count)

        LLVM.LLVMGetMDNodeOperands(
            toValue(withContext).ref,
            ptr
        )

        return ptr.map { Value(it) }
    }
    //endregion Core::Metadata
}