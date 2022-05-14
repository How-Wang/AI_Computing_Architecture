## Hw 7 Single-Cycle-CPU Extension 
### 硬體架構圖：
- 選擇的base CPU架構圖
![](https://playlab.computing.ncku.edu.tw:3001/uploads/upload_ea275f06e0bbe90ab3493be3f00fdbcc.png)

### 分工方式

#### Software Design
1. 因為software裡面僅有兩種新增的指令:
- TODO work：
    1. 實作
    2. 檢查與驗證
#### Hardware Design
> 依照ISA-extensions指令，我們可以知道我們需要對OP-IMM和OP新增指令，因此我們需要修改兩個部分

1. 第一部分：`Controller/Controller.scala`: 
    - TODO work : 需要修改`ALUSel`
    - 實作 : 對於`ALUSel` 的Output，我們在 Controller.scala 中新增一個Module，ALUSel拉出去Decode
2. 第二部分：`Datapath/ALU.scala` : 
    - TODO work : 需要新增ISA-extensions的運算單元共24個
    - 實作：24個指令


## 小組最後完成CPU架構圖
![](https://playlab.computing.ncku.edu.tw:3001/uploads/upload_a9def1649f288fcc5eb37fad53664a80.png)

## 補充
### 指令意義
指令意思
```python=
#// in emulator
$ mkdir -p obj

$ g++ -o obj/emulator emulator.cpp $ translate.cpp -g -std=c++11 # compile 出 emulator

# 掃過 Hw3_inst.asm 並且 
# 1. 就是使用 Lab4 emulator.cpp 產生出相對應的結果
# 2. 再利用Lab7 相較於 Lab4 emulator.cpp 多內部呼叫的 translate_to_machine_code() 去生成m_code.hex，為了scala 可以去執行指令
# (translate_to_machine_code() 這個function 是寫在 translate.cpp 內)
$ ./obj/emulator $ example_code/Hw3_inst.asm 

# 把原本就有的 Hw3_inst.asm 與 後來生成的 m_code.hex copy到 single cycle 那裡，為了給等等的 topTest.scala 執行
$ cp example_code/Hw3_inst.asm ../Single_Cycle_CPU/src/main/resource/Hw3_inst.asm
$ cp example_code/m_code.hex ../Single_Cycle_CPU/src/main/resource/Hw3_m_code.hex

# // in single cycle
# ./test_data.sh Hw3 -> 內部的指令是    
# elif [ "$1" = "Hw3" ]
# then
#    cp ./src/main/resource/Hw3_inst.asm ./src/main/resource/inst.asm
#    cp ./src/main/resource/Hw3_m_code.hex ./src/main/resource/m_code.hex
# 就是把告訴 aias_lab7.topTest 等等要讀的 inst.asm m_code.hex 改成 Hw3 的指令版本
$ ./test_data.sh Hw3

# 執行 aias_lab7.topTest 
$ mill chiselModule.test.runMain aias_lab7.topTest
```
