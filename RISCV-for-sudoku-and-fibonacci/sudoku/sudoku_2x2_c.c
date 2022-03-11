#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

bool check(char *sudoku, int index){
    int index_x = index % 4;
    int index_y = index / 4;
    int check_value = sudoku[index_y*4 + index_x];
    int k;
    //同一列
    for(k = 0; k < 4; ++k) {
        if(sudoku[index_y*4 + k] == check_value && index_x != k) {
            return false;
        }
    }
    //同一行
    for(k = 0; k < 4; ++k) {
        if(sudoku[index_x + k*4] == check_value && index_y != k) {
            return false;
        }
    }
    //同一小格
    int tempRow = index_y / 2 * 2;
    int tempCol = index_x / 2 * 2;
    for(k = tempRow; k < tempRow + 2; ++k) {
        for(int p = tempCol; p < tempCol + 2; ++p) {
            if(sudoku[k*4 + p] == check_value && k != index_y && p != index_x) {
                return false;
            }
        }
    }
    return true;
}

bool solve(char *sudoku, int index){
    if (index >= 16 ){
        return true;                                 // 如果檢查完所有的格子，回傳 True
    }                    
    if (sudoku[index] > 0 ) {                          // set是一個儲存所有資料的array
        return solve(sudoku,index+1);                       // 如果格子中已經有值了則會往下一格判斷
    }

    for (int n=1;n<=4;++n) {
        sudoku[index] = n;
        if (check(sudoku,index) && solve(sudoku,index+1))  // check function用來檢查當前這格放入這個數值是否正確
            return true;                         // solve(index+1) function則是繼續判斷下一格的值     
    }
    sudoku[index] = 0;                                  // returns the value to 0 to mark it as empty
    return false;
}

void sudoku_2x2_c(char *test_c_data){
    solve(test_c_data,0);
}