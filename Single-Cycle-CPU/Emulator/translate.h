#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>
#include "emulator.h"

#ifndef TRANSLATE_H
#define TRANSLATE_H
#define OP_IMM 0b0010011
#define OP     0b0110011


char* concat(const char *, const char *);

void copy_str(char *, const char *);

void copy_path(char* , char** );

void write_data_hex(uint8_t* ,FILE* );

void translate_to_machine_code(uint8_t* ,instr* , char*);

#endif