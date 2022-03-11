# RISC-V for sudoku and fibonacci
>RISC-V is an open standard instruction set architecture (ISA) that is based on established reduced instruction set computer (RISC) principles.
## 主要內容
兩個檔案分別為
1. **數獨 sudoku**
2. **費氏數列 fibonacci**

的 RISC-V assembly code 的實現。

並使用線上網站 [Venus](https://www.kvakil.me/venus/) 進行組合語言的編寫與除錯，與利用 qemu 進行 assembly code 的模擬，本 project 只附上核心程式編碼，關於環境安裝與編譯工具則不多加著墨。

## Assembly code 的重點
程式需要進行 calling convention 動作時，需要被儲存的值為：
1. 函式的參數值
2. 函式內暫存變數的值
3. 返回位址 (return address)，也就是遞迴完成後，接續執行的程式位置
![](https://playlab.computing.ncku.edu.tw:3001/uploads/upload_7646564d47a7c6b5b6365293a764fd37.png)
- 而在 assembly code中，我們會利用 **stack pointer + memory** 進行資料的儲存，藉此確保程式需要呼叫另外一個 function 或是返回上一個  function 時資料正確。
- register 對照表
![](https://playlab.computing.ncku.edu.tw:3001/uploads/upload_677e12f403993b93e2be2c308b3819d1.png)

## Algorithm
**費氏數列 fibonacci** 與 **數獨 sudoku** 兩者任務的演算法，皆為根據相同資料夾下的C檔，可以對於.c file 與 .S file 兩相對照，此外，為了易讀性，以下附上**數獨 sudoku** assembly code 的流程圖。
![](https://i.imgur.com/hWAPW4u.png)


## RISC-V Assembly Programming
參考資料
- [RISC-V Instruction Set Specifications](https://msyksphinz-self.github.io/riscv-isadoc/html/index.html)
- [RISC-V Assembly Programmer's Manual](https://github.com/riscv-non-isa/riscv-asm-manual/blob/master/riscv-asm.md)

## 執行結果
- **費氏數列 fibonacci**
![](https://playlab.computing.ncku.edu.tw:3001/uploads/upload_7f9cd8a8b384680d82f4e02923d08171.jpg)

- **數獨 2\*2 sudoku**
![](https://playlab.computing.ncku.edu.tw:3001/uploads/upload_2a517798287cfd7b1d2fe468ee439bb0.png)

## 環境設置
關於環境設置與，可以參考 [**playlab AIAS_2022_spring**](https://playlab.computing.ncku.edu.tw/AIAS_2022_spring.html) 的課程教學與環境安裝，故本 project 不在此多著墨介紹。