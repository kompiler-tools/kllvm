package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMBinaryRef
import org.bytedeco.llvm.LLVM.LLVMObjectFileRef
import org.llvm4j.llvm4j.util.Owner

public class Binary public constructor(ptr: LLVMBinaryRef) : Owner<LLVMBinaryRef> {
    public override val ref: LLVMBinaryRef = ptr
}

public class ObjectFile public constructor(ptr: LLVMObjectFileRef) : Owner<LLVMObjectFileRef> {
    public override val ref: LLVMObjectFileRef = ptr
}

public class RelocationIterator
public class SectionIterator
public class SymbolIterator
