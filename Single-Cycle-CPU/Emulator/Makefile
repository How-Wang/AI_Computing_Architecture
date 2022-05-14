all:
	mkdir -p obj
	g++ -o obj/emulator emulator.cpp translate.cpp -g -std=c++11
	./obj/emulator example_code/Hw3_inst.asm
	cp example_code/Hw3_inst.asm ../Single_Cycle_CPU/src/main/resource/Hw3_inst.asm
	cp example_code/m_code.hex ../Single_Cycle_CPU/src/main/resource/Hw3_m_code.hex