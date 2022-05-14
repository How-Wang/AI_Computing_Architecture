package aias_lab7.Single_Cycle

import chisel3._
import chisel3.util._

import aias_lab7.Memory._
import aias_lab7.Single_Cycle.Controller._
import aias_lab7.Single_Cycle.Datapath._

class Single_Cycle extends Module {
    val io = IO(new Bundle{
        //InstMem
        val pc = Output(UInt(15.W))
        val rinst = Input(UInt(32.W))
        
        //DataMem
        val raddr = Output(UInt(15.W))
        val rdata = Input(UInt(32.W))

        val wdata = Output(UInt(32.W))
        val waddr = Output(UInt(15.W))

        val MemRW  = Output(Bool())

        val funct3 = Output(UInt(3.W))

        //System
        val regs = Output(Vec(32,UInt(32.W)))
        val Hcf = Output(Bool())
    })
    
    //Module
    val ct = Module(new Controller())
    val pc = Module(new PC())
    val ig = Module(new ImmGen())
    val rf = Module(new RegFile(2))
    val alu = Module(new ALU())
    val bc = Module(new BranchComp())

    //wire
    val rd        = Wire(UInt(5.W))
    val rs1       = Wire(UInt(5.W))
    val rs2       = Wire(UInt(5.W))
    val funct3    = Wire(UInt(3.W))
    val inst_31_7 = Wire(UInt(25.W))

    rd  := io.rinst(11,7)
    rs1 := io.rinst(19,15)
    rs2 := io.rinst(24,20)
    funct3 := io.rinst(14,12)
    inst_31_7 := io.rinst(31,7)
    
    //PC
    pc.io.PCSel := ct.io.PCSel
    pc.io.alu_out := alu.io.out
    pc.io.Hcf := ct.io.Hcf
    
    //Insruction Memory
    io.pc := pc.io.pc

    //ImmGen
    ig.io.ImmSel := ct.io.ImmSel
    ig.io.inst_31_7 := inst_31_7
    
    //RegFile
    rf.io.raddr(0) := rs1
    rf.io.raddr(1) := rs2
    rf.io.waddr := rd
    rf.io.wen := ct.io.RegWEn
    
    when(ct.io.WBSel === 0.U){rf.io.wdata := io.rdata} //from DataMemory
    .elsewhen(ct.io.WBSel === 1.U){rf.io.wdata := alu.io.out} //from ALU
    .elsewhen(ct.io.WBSel === 2.U){rf.io.wdata := pc.io.pc + 4.U} //from PC (+4)
    .otherwise{rf.io.wdata := 0.U} // Default

    //ALU
    val rdata_or_zero = Wire(UInt(32.W))
    rdata_or_zero := Mux(ct.io.Lui,0.U(32.W),rf.io.rdata(0))
    alu.io.src1 := Mux(ct.io.ASel,pc.io.pc,rdata_or_zero)
    alu.io.src2 := Mux(ct.io.BSel,ig.io.imm,rf.io.rdata(1))
    alu.io.ALUSel := ct.io.ALUSel
    
    //Data Memory
    io.funct3 := funct3
    io.raddr := alu.io.out(15,0)
    io.MemRW := ct.io.MemRW
    io.waddr := alu.io.out(15,0)
    io.wdata := rf.io.rdata(1)
    
    //Branch Comparator
    bc.io.BrUn := ct.io.BrUn
    bc.io.src1 := rf.io.rdata(0)
    bc.io.src2 := rf.io.rdata(1)

    //Controller
    ct.io.Inst := io.rinst
    ct.io.BrEq := bc.io.BrEq
    ct.io.BrLT := bc.io.BrLT

    //System
    io.regs := rf.io.regs
    io.Hcf := ct.io.Hcf
}