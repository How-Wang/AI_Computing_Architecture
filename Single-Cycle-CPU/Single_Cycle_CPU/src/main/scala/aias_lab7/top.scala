package aias_lab7

import chisel3._
import chisel3.util._

import aias_lab7.Single_Cycle._
import aias_lab7.Memory._

class top extends Module {
    val io = IO(new Bundle{
        val pc = Output(UInt(15.W))
        val regs = Output(Vec(32,UInt(32.W)))
        val Hcf = Output(Bool())

        //for sure that IM and DM will be synthesized
        val inst = Output(UInt(32.W))
        val rdata = Output(UInt(32.W))
    })

    val sc = Module(new Single_Cycle())
    val im = Module(new InstMem(15))
    val dm = Module(new DataMem(15))
    
    //Single_Cycle
    sc.io.rinst := im.io.inst
    sc.io.rdata := dm.io.rdata 
    
    //Insruction Memory
    im.io.raddr := sc.io.pc
    
    //Data Memory
    dm.io.funct3 := sc.io.funct3
    dm.io.raddr := sc.io.raddr
    dm.io.wen := sc.io.MemRW
    dm.io.waddr := sc.io.waddr
    dm.io.wdata := sc.io.wdata

    //System
    io.pc := sc.io.pc
    io.regs := sc.io.regs
    io.Hcf := sc.io.Hcf
    io.inst := im.io.inst
    io.rdata := dm.io.rdata
}


import chisel3.stage.ChiselStage
object top extends App {
  (
    new chisel3.stage.ChiselStage).emitVerilog(
      new top(),
      Array("-td","generated/top")
  )
}