# sudoku_2x2_asm.S
   
    .text                           # code section 
    .global sudoku_2x2_asm          # declare the asm function as a global function
    .type sudoku_2x2_asm, @function # define sum_asm as a function 

sudoku_2x2_asm:

prologue:
	addi	sp, sp, -4	# save ra
    sw		ra, 0(sp)

sudoku_start:
	li		a1, 0 # a1 save current sudoku index, and assign 0
	jal		solve

epilogue:
	lw		ra, 0(sp) # go back to main function
	addi	sp, sp, 4
	jr		ra


check:		### only need to return 1 or 0, and inside do not need any recursive
	andi	t0, a1, 3 # t0 for index_x (index % 4)
	srli	t1, a1, 2 # t1 for index_y (index / 4)

	slli	t3, t1, 2 # t3 = index_y * 4
	add		t3, t3, t0 # t3 is offset = index_y*4 + index_x
	add		t3, t3, a0 # t3 = base + offset
	lb		t2, 0(t3) # t2 = sudoku[index_y*4 + index_x] = check_value

	# t0 for index_x, t1 for index_y, t2 = check_value
	# save all register in sp

	addi	sp, sp, -24 
	sw		s1, 0(sp)
	sw		s2, 4(sp)
	sw		s3, 8(sp) 
	sw		s4, 12(sp)
	sw		s5, 16(sp)
	sw		s6, 20(sp) 

 
	row:
		li	t3, 4 # max
		li	t4, 0 # k
		row_loop:
			slli	t5, t1, 2 # t5 = index_y * 4
			add		t5, t5, t4 # t5 = index_y * 4 + k
			add		t5, t5, a0 # t5 = base + offset
			lb		t6, 0(t5) # t6 = sudoku[index_y * 4 + k]
			bne		t6, t2, row_continue # if sudoku[index_y * 4 + k] != check_value, ok! continue
			beq		t0, t4, row_continue # if index_x == k, ok! continue
			j		return_false # sudoku[index_y * 4 + k] == check_value && index_x != k, return false
			row_continue:
				addi	t4, t4, 1 # k = k +1
				beq		t4, t3, column # k == 4, go to next loop!
				j		row_loop

	column:
		li	t3, 4 # max
		li	t4, 0 # k
		column_loop:
			slli	t5, t4, 2 # t5 = k * 4
			add		t5, t5, t0 # t5 = k * 4 + index_x
			add		t5, t5, a0 # t5 = base + offset
			lb		t6, 0(t5) # t6 = sudoku[k * 4 + index_x]
			bne		t6, t2, column_continue # if sudoku[k * 4 + index_x] != check_value, ok! continue
			beq		t1, t4, column_continue # if index_y == k, ok! continue
			j		return_false # sudoku[index_x + k*4] == check_value && index_y != k, return false
			column_continue:
				addi	t4, t4, 1 # k = k +1
				beq		t4, t3, block # k == 4, go to next loop!
				j		column_loop

	block:
		srli t3, t1, 1 # t3 = index_y / 2
		slli t3, t3, 1 # t3 = index_y / 2 * 2 = tempRow
		srli t4, t0, 1 # t4 = index_x / 2
		slli t4, t4, 1 # t4 = index_x / 2 * 2 = tempCol

		mv	s1, t3	# s1  = k = tempRow
		addi s2, s1, 2 # s2 = k_max = tempRow + 2 
		block_loop_Row:
			mv	s3, t4	# s3 = p = tempCol
			addi s4, s3, 2 # s4 = p_max = tempCol + 2 
			block_loop_Col:
				slli s5, s1, 2 # s5 = k*4
				add	 s5, s5, s3 # s5 = k*4 + p
                add	 s5, s5, a0 # s5 = s5 + a0 = base + offset
				lb	 s6, 0(s5) # s6 = sudoku[k*4 + p]
				bne  s6, t2, block_Col_continue # sudoku[k*4 + p] != check_value, ok!, continue
				beq  s1, t1, block_Col_continue # k == index_y, ok! continue
				beq  s3, t0, block_Col_continue # p == index_x, ok! continue
				j	return_false # sudoku[k*4 + p] == check_value && k != index_y && p != index_x, return false
				block_Col_continue:
					addi  s3, s3, 1 # p = p + 1
					beq s3, s4, block_Row_continue # p == tempCol + 2, goto upper loop continue!
					j  block_loop_Col # continue same loop
			block_Row_continue:
				addi  s1, s1, 1 # k = k + 1
				beq	s1, s2, return_true # k == tempRow + 2, goto return_true!
				j	block_loop_Row # continue same loop

	return_true:
		li		a2, 1
		sw		s1, 0(sp)
		sw		s2, 4(sp)
		sw		s3, 8(sp) 
		sw		s4, 12(sp)
		sw		s5, 16(sp)
		sw		s6, 20(sp) 
		addi	sp, sp, 24 
		jr	ra

	return_false:
		li		a2, 0
		sw		s1, 0(sp)
		sw		s2, 4(sp)
		sw		s3, 8(sp) 
		sw		s4, 12(sp)
		sw		s5, 16(sp)
		sw		s6, 20(sp) 
		addi	sp, sp, 24 
		jr	ra


solve:
	addi	sp, sp, -12 
    sw		ra, 0(sp) # save ra
	sw		a1, 4(sp) # save index
	sw		s0, 8(sp) # save current n

	li		t0, 16
	bge		a1, t0, suc_end # if index is bigger and equel than 16 return suc

	add		t1, a0, a1  # base address + offset
	lb      t2, 0(t1)   # load the value
	# here we don't need to come back

	bgtz	t2, call_next_solve_in_first_check # if sudoku[index] > 0 call next_solve_in_first_check


for_loop_assignment:
	li		s0, 1	# current n
	loop:
		add		t1, a0, a1  # base address + offset
		sb		s0, 0(t1)	# save n into sudoku address		## save b??

		### here return result need to be saved!
		jal		check
		beqz	a2, check_fail 

		addi	a1, a1, 1	# add 1 to index in order to pass to solve function
		jal		solve
		addi	a1, a1, -1	# minus 1 to recover
		beqz	a2, check_fail
		j		suc_end

		check_fail:
		addi	s0, s0, 1	# add s0 by 1
		li		t2, 4		
		ble		s0, t2, loop # continue loop n less equal 4
	add		t1, a0, a1		# base + offset to 0	
	sb		x0, 0(t1)	# save n into sudoku address			## save b??
	j		fail_end		# return false

call_next_solve_in_first_check:
	addi	a1, a1, 1	# add index by 1
	jal		solve		# call solve function
	# return value save in a2 and don't need changes
	addi	a1, a1, -1	# recover index

	lw     s0,8(sp) # recover current n
    lw     a1,4(sp) # recover index
	lw     ra,0(sp) # recover ra
	addi   sp,sp,12
	jr	   ra

suc_end: # return faulst
	li	   a2, 1
	lw     s0, 8(sp) # current n
    lw     a1, 4(sp) # index
	lw     ra, 0(sp) # ra
	addi   sp, sp, 12
	jr	   ra

fail_end: # return true
	li	   a2, 0
	lw     s0, 8(sp)
    lw     a1, 4(sp)
	lw     ra, 0(sp)
	addi   sp, sp, 12
	jr	   ra

    .size sudoku_2x2_asm, .-sudoku_2x2_asm