package aias_lab7.Single_Cycle.Datapath

import chisel3._
import chisel3.util._ 

import aias_lab7.Single_Cycle.Controller.opcode_map._

import aias_lab7.Single_Cycle.Controller.ALU_op._

class ALUIO extends Bundle{
  val src1    = Input(UInt(32.W))
  val src2    = Input(UInt(32.W))
  val ALUSel  = Input(UInt(6.W))
  val out  = Output(UInt(32.W))
}

class ALU extends Module{
  val io = IO(new ALUIO) 
  
  io.out := 0.U
  switch(io.ALUSel){
    is(ADD   ) {io.out := io.src1+io.src2}
    is(SLL   ) {io.out := io.src1 << io.src2(4,0)}
    is(SLT   ) {io.out := Mux(io.src1.asSInt<io.src2.asSInt,1.U,0.U)}
    is(SLTU  ) {io.out := Mux(io.src1<io.src2              ,1.U,0.U)}
    is(XOR   ) {io.out := io.src1^io.src2}
    is(SRL   ) {io.out := io.src1 >> io.src2(4,0)}
    is(OR    ) {io.out := io.src1|io.src2}
    is(AND   ) {io.out := io.src1&io.src2}
    is(SUB   ) {io.out := io.src1-io.src2}
    is(SRA   ) {io.out := (io.src1.asSInt >> io.src2(4,0)).asUInt}
    is(ANDN  ) {io.out := io.src1&(~io.src2)} 
    is(ORN   ) {io.out := io.src1|(~io.src2)} 
    is(XNOR  ) {io.out := ~(io.src1^io.src2)} 
    is(MIN   ) {io.out := Mux(io.src1.asSInt < io.src2.asSInt, io.src1, io.src2)} 
    is(MAX   ) {io.out := Mux(io.src1.asSInt > io.src2.asSInt, io.src1, io.src2)} 
    is(MINU  ) {io.out := Mux(io.src1 < io.src2, io.src1, io.src2)} 
    is(MAXU  ) {io.out := Mux(io.src1 > io.src2, io.src1, io.src2)} 
    is(BSET  ) {io.out := io.src1 |  (1.U << (io.src2(4,0)&(32.U-1.U)))} 
    is(BCLR  ) {io.out := io.src1 & ~(1.U << (io.src2(4,0)&(32.U-1.U)))} 
    is(BINV  ) {io.out := io.src1 ^  (1.U << (io.src2(4,0)&(32.U-1.U)))} 
    is(BEXT  ) {io.out := (io.src1 >> (io.src2&(32.U-1.U)))&1.U} 
    is(ROR   ) {io.out := (io.src1 >> (io.src2(4,0))) | (io.src1 << (32.U - (io.src2(4,0))))}   
    is(ROL   ) {io.out := (io.src1 << (io.src2(4,0))) | (io.src1 >> (32.U - (io.src2(4,0))))} 
    is(SH1ADD) {io.out := (io.src1 << 1.U) + io.src2} 
    is(SH2ADD) {io.out := (io.src1 << 2.U) + io.src2}
    is(SH3ADD) {io.out := (io.src1 << 3.U) + io.src2} 
    is(ZEXTH ) {
      // io.out := io.src1(15,0).UInt
      io.out := io.src1 & "b00000000000000001111111111111111".U
    } 
    // leading zeroes 1 clk
    is(CLZ   ) {
      val res = Wire(Vec(5, UInt(1.W)))
      val val16 = Wire(UInt(16.W))
      val val8 = Wire(UInt(8.W))
      val val4 = Wire(UInt(4.W))
      res(4) := (io.src1(31,16) === "b0000000000000000".U)
      val16  := Mux(res(4) === 1.U, io.src1(15,0), io.src1(31,16))
      res(3) := (val16(15,8) === "b00000000".U)
      val8   := Mux(res(3) === 1.U, val16(7,0), val16(15,8))
      res(2) := (val8(7,4) === "b0000".U)
      val4   := Mux(res(2) === 1.U, val8(3,0), val8(7,4))
      res(1) := (val4(3,2) === "b00".U)
      res(0) := Mux(res(1) === 1.U, ~val4(1), ~val4(3))
      io.out := res(4) << 4 | res(3) << 3 | res(2) << 2 | res(1) << 1 | res(0)
    }
    // trailing zeroes 1 clk?
    is(CTZ   ) {
      val res = Wire(Vec(5, UInt(1.W)))
      val val16 = Wire(UInt(16.W))
      val val8 = Wire(UInt(8.W))
      val val4 = Wire(UInt(4.W))
      res(4) := (io.src1(15,0) === "b0000000000000000".U)
      val16  := Mux(res(4) === 1.U, io.src1(31,16), io.src1(15,0))
      res(3) := (val16(7,0) === "b00000000".U)
      val8   := Mux(res(3) === 1.U, val16(15,8), val16(7,0))
      res(2) := (val8(3,0) === "b0000".U)
      val4   := Mux(res(2) === 1.U, val8(7,4), val8(3,0))
      res(1) := (val4(1,0) === "b00".U)
      res(0) := Mux(res(1) === 1.U, ~val4(3), ~val4(1))
      io.out := res(4) << 4 | res(3) << 3 | res(2) << 2 | res(1) << 1 | res(0)
    }
    // # of ones 1 clk?
    is(CPOP) {
      val res = Wire(Vec(5, UInt(32.W)))
      res(4) := (io.src1 & "b01010101010101010101010101010101".U) + ((io.src1 >> 1)  & "b01010101010101010101010101010101".U)
      res(3) := (res(4)  & "b00110011001100110011001100110011".U) + ((res(4)  >> 2)  & "b00110011001100110011001100110011".U)
      res(2) := (res(3)  & "b00001111000011110000111100001111".U) + ((res(3)  >> 4)  & "b00001111000011110000111100001111".U)
      res(1) := (res(2)  & "b00000000111111110000000011111111".U) + ((res(2)  >> 8)  & "b00000000111111110000000011111111".U)
      res(0) := (res(1)  & "b00000000000000001111111111111111".U) + ((res(1)  >> 16) & "b00000000000000001111111111111111".U)
      io.out := res(0)
    } 
    // sign extend
    is(SEXTB ) {
      // io.out := Mux(io.src1(7), io.src1(7,0).SInt, io.src1(7,0).UInt)
      io.out := Mux(io.src1(7) === 1.U, io.src1 | "b11111111111111111111111100000000".U, io.src1 & "b00000000000000000000000011111111".U)
    } 
    // sign extend
    is(SEXTH ) {
      // io.out := Mux(io.src1(15), io.src1(15,0).SInt, io.src1(15,0).UInt)
      io.out := Mux(io.src1(15) === 1.U, io.src1 | "b11111111111111110000000000000000".U, io.src1 & "b00000000000000001111111111111111".U)
    } 
    // sign extend
    is(REV8  ) { 
      io.out := ((io.src1 >> 24) | (io.src1 << 24) | ((io.src1 & "b00000000111111110000000000000000".U) >> 8) | ((io.src1 & "b00000000000000001111111100000000".U) << 8))
    } 
    // or in bytes
    is(ORCB  ) {
      // val or = VecInit(Seq.fill(4)(0.U(8.W)))
      // for(i <- 0 until 4){
      //   or(i) := Mux(io.src1(i*8+7,i*8) === 0.U , 0.U , "b11111111".U)
      // }
      // io.out := Cat(or(3),or(2),or(1),or(0))
      val or = Wire(UInt(32.W))
      or := ((io.src1 | (io.src1 >> 1) | (io.src1 >> 2) | (io.src1 >> 3) | (io.src1 >> 4) | (io.src1 >> 5) | (io.src1 >> 6) | (io.src1 >> 7)) & "b00000001000000010000000100000001".U)
      io.out := or | (or << 1) | (or << 2) | (or << 3) | (or << 4) | (or << 5) | (or << 6) | (or << 7)

    } 
  }
}

