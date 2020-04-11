package dev.supergrecko.kllvm.llvm.typedefs

import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef

public class DiagnosticInfo internal constructor() {
    internal lateinit var ref: LLVMDiagnosticInfoRef

    public constructor(info: LLVMDiagnosticInfoRef) : this() {
        ref = info
    }
}