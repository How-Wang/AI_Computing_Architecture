package aias_lab7.Single_Cycle.Controller

import chisel3._
import chisel3.util._
import aias_lab7.Single_Cycle.Datapath.inst_type._

object opcode_map {
    val LOAD      = "b0000011".U
    val STORE     = "b0100011".U

    val BRANCH    = "b1100011".U
    val JALR      = "b1100111".U
    val JAL       = "b1101111".U

    val OP_IMM    = "b0010011".U
    val OP        = "b0110011".U
    val AUIPC     = "b0010111".U 
    val LUI       = "b0110111".U 
    val HCF       = "b0001011".U
}

object ALU_op{ // for output and function3
  val ADD  = 0.U
  val SUB  = 1.U
  val SLL  = 2.U
  val SLT  = 3.U
  val SLTU = 4.U
  val XOR  = 5.U
  val SRL  = 6.U
  val SRA  = 7.U
  val OR   = 8.U
  val AND  = 9.U
  val ANDN = 10.U
  val ORN  = 11.U
  val XNOR = 12.U
  val MIN  = 13.U
  val MAX  = 14.U
  val MINU = 15.U
  val MAXU = 16.U
  val BSET = 17.U
  val BCLR = 18.U
  val BINV = 19.U
  val BEXT = 20.U
  val ROR  = 21.U
  val ROL  = 22.U
  val SH1ADD = 23.U
  val SH2ADD = 24.U
  val SH3ADD = 25.U
  val ZEXTH = 26.U
  val CLZ  = 27.U
  val CTZ  = 28.U
  val CPOP = 29.U
  val SEXTB = 30.U
  val SEXTH = 31.U
  val REV8 = 32.U
  val ORCB = 33.U
}

object condition{
  val EQ = "b000".U
  val NE = "b001".U
  val LT = "b100".U
  val GE = "b101".U
  val LTU = "b110".U
  val GEU = "b111".U
}

import opcode_map._,condition._,ALU_op._

class ALUSel_Decode extends Module {
  
  val io = IO(new Bundle{ 
    val Inst = Input(UInt(32.W))
    val ALU_Select = Output(UInt(6.W)) // Modified Size
  })

  val opcode = Wire(UInt(7.W))
  val funct3 = Wire(UInt(3.W))
  val funct7 = Wire(UInt(7.W))

  opcode := io.Inst(6,0)
  funct3 := io.Inst(14,12)
  funct7 := io.Inst(31,25)

  val zzo_imm = Wire(UInt(6.W)) // Modified Size
  zzo_imm := MuxLookup(io.Inst(22,20), CLZ , Seq(
              "b000".U -> CLZ,
              "b001".U -> CTZ,
              "b010".U -> CPOP,
              "b100".U -> SEXTB,
              "b101".U -> SEXTH,
            ))

  io.ALU_Select := Mux(opcode =/= OP && opcode =/= OP_IMM, ADD,
              MuxLookup( funct3, 0.U,Seq(
                "b000".U -> Mux(~opcode(5), ADD,
                              Mux(funct7(5),SUB, ADD), 
                            ),
                "b001".U -> Mux(~opcode(5),
                              MuxLookup(funct7, zzo_imm, Seq(
                                "b0000000".U -> SLL,
                                "b0010100".U -> BSET,
                                "b0100100".U -> BCLR,
                                "b0110100".U -> BINV,
                              )),
                              MuxLookup(funct7, SLL ,Seq(
                                "b0000000".U -> SLL,
                                "b0010100".U -> BSET,
                                "b0100100".U -> BCLR,
                                "b0110100".U -> BINV,
                                "b0110000".U -> ROL,
                              )),
                            ),
                "b010".U -> Mux(~opcode(5), SLT,
                              Mux(funct7 === "b0010000".U, SH1ADD, SLT),
                            ),
                "b011".U -> SLTU,
                "b100".U -> Mux(~opcode(5),XOR,
                              MuxLookup(funct7, XOR, Seq(
                                "b0000000".U -> XOR,
                                "b0100000".U -> XNOR,
                                "b0000101".U -> MIN,
                                "b0010000".U -> SH2ADD,
                                "b0000100".U -> ZEXTH,
                              )),
                            ),
                "b101".U -> Mux(~opcode(5),
                              MuxLookup(funct7, SRL, Seq(
                                "b0000000".U -> SRL,
                                "b0100000".U -> SRA,
                                "b0100100".U -> BEXT,
                                "b0110000".U -> ROR,
                                "b0110100".U -> REV8,
                                "b0010100".U -> ORCB,
                              )),
                              MuxLookup(funct7, SRL, Seq(
                                "b0000000".U -> SRL,
                                "b0100000".U -> SRA,
                                "b0000101".U -> MINU,
                                "b0100100".U -> BEXT,
                                "b0110000".U -> ROR,
                                
                              )),
                            ),
                "b110".U -> Mux(~opcode(5),OR,
                              MuxLookup(funct7, OR, Seq(
                                "b0000000".U -> OR,
                                "b0100000".U -> ORN,
                                "b0000101".U -> MAX,
                                "b0010000".U -> SH3ADD,
                              )),
                            ),
                "b111".U -> Mux(~opcode(5),AND,
                              MuxLookup(funct7, AND, Seq(
                                "b0000000".U -> AND,
                                "b0100000".U -> ANDN,
                                "b0000101".U -> MAXU,
                              )),
                            )
            ))
        )
}

class Controller extends Module {
    val io = IO(new Bundle{
        val Inst = Input(UInt(32.W))
        val BrEq = Input(Bool())
        val BrLT = Input(Bool())

        val PCSel = Output(Bool())
        val ImmSel = Output(UInt(3.W))
        val RegWEn = Output(Bool())
        val BrUn = Output(Bool())
        val BSel = Output(Bool())
        val ASel = Output(Bool())
        val ALUSel = Output(UInt(6.W))
        val MemRW = Output(Bool())
        val WBSel = Output(UInt(2.W))

        //new
        val Lui = Output(Bool())
        val Hcf = Output(Bool())
    })
    
    val opcode = Wire(UInt(7.W))
    opcode := io.Inst(6,0)

    val funct3 = Wire(UInt(3.W))
    funct3 := io.Inst(14,12)

    val funct7 = Wire(UInt(7.W))
    funct7 := io.Inst(31,25)

    val b_pc_alu = Wire(Bool())
    b_pc_alu := MuxLookup(funct3,0.U,Seq(
                    EQ  -> io.BrEq,
                    NE  -> ~io.BrEq,
                    LT  -> io.BrLT,
                    GE  -> ~io.BrLT,
                    LTU -> io.BrLT,
                    GEU -> ~io.BrLT,
                  ))

    //Control signal
    // Register WriteBack Enable : RI and LUI type
    io.RegWEn := (opcode === OP 
               || opcode === OP_IMM 
               || opcode === JALR
               || opcode === JAL
               || opcode === LOAD 
               || opcode === LUI)
    
    // rs1 from : RIS(Reg1(F)) or BJ(PC(T)) => U(None)
    io.ASel := opcode === BRANCH || opcode === JAL || opcode === AUIPC
    // rs2 from : ISUJ(imm(T)) or RB(Reg2(F)) 
    io.BSel := opcode =/= OP
    // Memory Read(F) Write(T) 
    io.MemRW := opcode === STORE
    io.ImmSel :=  MuxLookup(opcode,0.U,Seq(
                    // R-type
                    OP     -> R,
                    // I-type
                    OP_IMM -> I,
                    LOAD   -> I,
                    JALR   -> I,
                    // S-type
                    STORE  -> S,
                    // B-type
                    BRANCH -> B,
                    // J-type
                    JAL    -> J,
                    // U-type
                    LUI    -> U,
                    AUIPC  -> U,
                  ))
    val alusel_decode = Module(new ALUSel_Decode)
    alusel_decode.io.Inst := io.Inst
    io.ALUSel := alusel_decode.io.ALU_Select

    io.PCSel :=  MuxLookup(opcode,false.B,Seq(
                  JAL    -> true.B,
                  JALR   -> true.B,
                  BRANCH -> b_pc_alu,
                  ))
    io.WBSel := MuxLookup(opcode,3.U,Seq(
                    // R-type
                    OP     -> 1.U,
                    // I-type
                    LOAD   -> 0.U,
                    OP_IMM -> 1.U,
                    JALR   -> 2.U,
                    // U-type
                    LUI    -> 1.U,
                    AUIPC  -> 1.U,
                    // J-type
                    JAL    -> 2.U,
                ))
    io.Lui := opcode === LUI
    io.Hcf := opcode === HCF
    
    // BRABCH Unsigned
    io.BrUn := (funct3 === LTU || funct3 === GEU)
}