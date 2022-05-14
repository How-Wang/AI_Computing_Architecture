package aias_lab7.Memory

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import firrtl.annotations.MemoryLoadFileType

class InstMem(bits:Int) extends Module {
  val io = IO(new Bundle {
    //32kB -> bits = 15
    val raddr = Input(UInt(bits.W))
    val inst = Output(UInt(32.W))
  })
  val memory = Mem((1<<bits), UInt(8.W))
  loadMemoryFromFile(memory, "./src/main/resource/m_code.hex")
  
  val rdata = Wire(UInt(32.W))
  rdata := Cat(memory((io.raddr+3.U)),
              memory((io.raddr+2.U)),
              memory((io.raddr+1.U)),
              memory((io.raddr+0.U)))
  
  io.inst :=rdata
}