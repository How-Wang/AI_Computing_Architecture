## fibonacci.S

    .text                          # code section 
    .global fibonacci_asm          # declar the sum_asm function as a  global function
    .type fibonacci_asm, @function # define sum_asm as a function 
fibonacci_asm:

prologue:
	addi   sp,sp,-4
    sw     ra,0(sp)

fibonacci_start:
	jal    fibonacci

epilogue:
	mv     a0, a1				  # save the return value to a0
	lw     ra,0(sp)
	addi   sp,sp,4
	jr     ra

fibonacci:
	# check whether is less than 2, if yes
	# call ret_one label
	beq	   a0, x0, ret_zero
	li     t0, 1    
    beq    a0, t0, ret_one    
	
	# save caller info, and prepare space for a1 (fib return value)
    addi   sp, sp, -12        # move sp to place ra, a0, a1
    sw     ra, 0(sp)          # save ra
    sw     a0, 4(sp)          # save current n (a0)
    
    addi   a0, a0, -1		  # change current n (a0) for callee argument
    jal	   fibonacci		  # call first fib
    sw     a1, 8(sp)          # save a1 (fib return value)

    addi   a0, a0, -1		  # change current n (a0) for callee argument
    jal	   fibonacci		  # call second fib

	# recover caller info, and take last fib a1 out to t1
    lw     ra, 0(sp)          # 恢復 ra
    lw     a0, 4(sp)          # 恢復 current n (a0)
	lw     t1, 8(sp)
    addi   sp, sp, 12         # 移動 sp 
   
    add    a1, t1, a1		  # 相加出 a1
    jr     ra

ret_one:
	# load a1 1, and just return
	# because we do not move the sp in caller (no need)
	# we can go back to the caller's caller
    li     a1, 1
    jr     ra

ret_zero:
	mv	   a1, zero
	jr     ra

    # Write your assembly code funtion here.
    # Please notice the rules of calling convention.
    
    .size fibonacci_asm, .-fibonacci_asm