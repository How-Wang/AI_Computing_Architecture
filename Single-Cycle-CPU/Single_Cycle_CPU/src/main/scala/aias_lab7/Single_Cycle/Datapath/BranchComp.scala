package aias_lab7.Single_Cycle.Datapath

import chisel3._
import chisel3.util._

class BranchComp extends Module{
    val io = IO(new Bundle{
        val BrUn = Input(Bool())
        val src1 = Input(UInt(32.W))
        val src2 = Input(UInt(32.W))

        val BrEq = Output(Bool())
        val BrLT = Output(Bool())
    })

    val eq = WireDefault(false.B)
    val lt = WireDefault(false.B)

    when(io.BrUn){
      when(io.src1 < io.src2) { lt := true.B }
    }.otherwise{
      when(io.src1.asSInt < io.src2.asSInt) { lt := true.B }
    }
    
    eq := Mux(io.src1===io.src2,true.B,false.B)

    io.BrEq := eq
    io.BrLT := lt
}