package aias_lab7.Single_Cycle.Datapath

import chisel3._
import chisel3.util._

class PC extends Module {
    val io = IO(new Bundle{
        val Hcf = Input(Bool())
        val PCSel = Input(Bool())
        val alu_out = Input(UInt(32.W))
        val pc = Output(UInt(15.W))
    })
    
    val pcReg = RegInit(0.U(32.W))

    when(!io.Hcf){
        pcReg := Mux(io.PCSel, io.alu_out & ~((3.U)(32.W)), pcReg + 4.U)
    }.otherwise{
        pcReg := pcReg
    }
    
    
    io.pc := pcReg
}
