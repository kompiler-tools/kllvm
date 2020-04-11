package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.llvm.typedefs.Context
import dev.supergrecko.kllvm.internal.util.iterateIntoType
import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class StructType internal constructor() : Type(),
    CompositeType {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Struct)
    }

    /**
     * Create a structure type
     *
     * This method creates a structure type inside the given [ctx]. Do not that this method cannot produce opaque struct
     * types, use [opaque] for that.
     *
     * The struct body will be the types provided in [tys].
     */
    public constructor(types: List<Type>, packed: Boolean, ctx: Context = Context.getGlobalContext()) : this() {
        val arr = ArrayList(types.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMStructTypeInContext(ctx.ref, PointerPointer(*arr), arr.size, packed.toInt())
    }

    /**
     * Create an opaque struct type
     *
     * This will create an opaque struct (a struct without a body, like C forward declaration) with the given [name].
     * You will be able to use [setBody] to assign a body to the opaque struct.
     */
    public constructor(name: String, ctx: Context = Context.getGlobalContext()) : this() {
        ref = LLVM.LLVMStructCreateNamed(ctx.ref, name)
    }

    //region Core::Types::StructureTypes
    public fun isPacked(): Boolean {
        return LLVM.LLVMIsPackedStruct(ref).toBoolean()
    }

    public fun isOpaque(): Boolean {
        return LLVM.LLVMIsOpaqueStruct(ref).toBoolean()
    }

    public fun isLiteral(): Boolean {
        return LLVM.LLVMIsLiteralStruct(ref).toBoolean()
    }

    public fun setBody(elementTypes: List<Type>, packed: Boolean) {
        require(isOpaque())

        val types = elementTypes.map { it.ref }
        val array = ArrayList(types).toTypedArray()
        val ptr = PointerPointer(*array)

        LLVM.LLVMStructSetBody(ref, ptr, array.size, packed.toInt())
    }

    public fun getElementTypeAt(index: Int): Type {
        // Refactor when moved
        require(index <= getElementCount()) { "Requested index $index is out of bounds for this struct" }

        val type = LLVM.LLVMStructGetTypeAtIndex(ref, index)

        return Type(type)
    }

    public fun getName(): String {
        require(!isLiteral()) { "Literal structs are never named" }

        val name = LLVM.LLVMGetStructName(ref)

        return name.string
    }

    public fun getElementTypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetStructElementTypes(ref, dest)

        return dest.iterateIntoType {
            Type(
                it
            )
        }
    }
    //endregion Core::Types::StructureTypes
}