package dev.supergrecko.kllvm.llvm.typedefs

import org.bytedeco.llvm.LLVM.LLVMComdat

public class Comdat internal constructor() {
    internal lateinit var ref: LLVMComdat

    public constructor(comdat: LLVMComdat) : this() {
        ref = comdat
    }
}