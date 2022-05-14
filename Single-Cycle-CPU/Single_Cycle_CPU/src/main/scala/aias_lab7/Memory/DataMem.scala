package aias_lab7.Memory

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


object wide {
  val Byte = 0.U
  val Half = 1.U
  val Word = 2.U
  val UByte = 4.U
  val UHalf = 5.U
}

import wide._

class DataMem(bits:Int) extends Module {
  val io = IO(new Bundle {
    val funct3 = Input(UInt(32.W))
    val raddr = Input(UInt(bits.W))
    val rdata = Output(UInt(32.W))
    
    val wen   = Input(Bool())
    val waddr = Input(UInt(bits.W))
    val wdata = Input(UInt(32.W))
  })
  val DATA_OFFSET = 1<<bits
  
  val memory = Mem((1<<(bits)), UInt(8.W))
  loadMemoryFromFile(memory, "./src/main/resource/data.hex")
  
  val srdata = Wire(SInt(32.W))

  io.rdata := srdata.asUInt

  val wa = WireDefault(0.U(bits.W))
  val wd = WireDefault(0.U(32.W))

  wa := MuxLookup(io.funct3,0.U(bits.W),Seq(
    Byte -> io.waddr,
    Half -> (io.waddr & ~(1.U(bits.W))),
    Word -> (io.waddr & ~(3.U(bits.W))),
  ))

  wd := MuxLookup(io.funct3,0.U,Seq(
    Byte -> io.wdata(7,0),
    Half -> io.wdata(15,0),
    Word -> io.wdata,
  ))

  srdata := 0.S

  when(io.wen){
    when(io.funct3===Byte){
      memory(wa) := wd(7,0)
    }.elsewhen(io.funct3===Half){
      memory(wa) := wd(7,0)
      memory(wa+1.U(bits.W)) := wd(15,8)
    }.elsewhen(io.funct3===Word){
      memory(wa) := wd(7,0)
      memory(wa+1.U(bits.W)) := wd(15,8)
      memory(wa+2.U(bits.W)) := wd(23,16)
      memory(wa+3.U(bits.W)) := wd(31,24)
    }
  }.otherwise{
    srdata := MuxLookup(io.funct3,0.S,Seq(
      Byte -> memory(io.raddr).asSInt,
      Half -> Cat(memory((io.raddr & (~(1.U(bits.W)))) +1.U),
                  memory(io.raddr & (~(1.U(bits.W))))).asSInt,
      Word -> Cat(memory((io.raddr & ~(3.U(bits.W))) +3.U),
                  memory((io.raddr & ~(3.U(bits.W))) +2.U),
                  memory((io.raddr & ~(3.U(bits.W))) +1.U),
                  memory(io.raddr & ~(3.U(bits.W)))).asSInt,
      UByte -> Cat(0.U(24.W),memory(io.raddr)).asSInt,
      UHalf -> Cat(0.U(16.W),
                  memory((io.raddr & ~(1.U(bits.W))) +1.U),
                  memory(io.raddr & ~(1.U(bits.W)))).asSInt
    ))
  }
}