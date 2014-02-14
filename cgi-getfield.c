/*
 * Copyright (c) 2013-2014 Intel Corporation, All Rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU General Public License,
 * version 2, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.

Authors:
        Wang, Jing J <jing.j.wang@intel.com>

*/

#include <stdlib.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>
#include <stdarg.h>
#include <stdio.h>

#define MAX_QUERY_SIZE 1024
static int
hex(int digit) {
    switch(digit) {

    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
        return digit - '0';

    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
        return 10 + digit - 'A';

    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
        return 10 + digit - 'a';

    default:
        return -1;
    }
}


int decode_query(const char *query, char *pkey, char *pval, int *psize) {
    char buf[256], *name, *value;
    int i, k, j, L, R, done;

    if (query == 0) {
        return -1;
    }
    name = value = 0;
    for (i = j = k = done = 0; done == 0; i++) {
        switch (query[i]) {
        case '=':
            if (name != 0) {
                break;  /* treat extraneous '=' as data */
            }
            if (name == 0 && k > 0) {
                name = buf;
                buf[k++] = 0;
                j = k;
                value = buf + k;
            }
            continue;

        case 0:
            done = 1;  /* fall through */

        case '&':
            buf[k] = 0;
            if (name == 0 && k > 0) {
                name = buf;
                value = buf + k;
            }
            if (name != 0) {
                if (strncmp(name, pkey, strlen(pkey)) == 0) {
                    *psize = k - j;
                    memcpy(pval, value, k - j);
                    return 0;     	
                }
            }
            k = 0;
            name = value = 0;
            continue;

        case '+':
            buf[k++] = ' ';
            continue;

        case '%':
            if ((L = hex(query[i + 1])) >= 0 &&
                (R = hex(query[i + 2])) >= 0)
            {
                buf[k++] = (L << 4) + R;
                i += 2;
                continue;
            }
            break;  /* treat extraneous '%' as data */
        }
        buf[k++] = query[i];
    }
    return -1;
}

int
main(int argc, char **argv) {
    char qbuf[MAX_QUERY_SIZE], val[MAX_QUERY_SIZE];
    int len = MAX_QUERY_SIZE;
    int size = 0;
    if (argc < 2)
        return -1;
    char *key = argv[1];
    if (argc > 2)
        len = atoi(argv[2]);  
    if ((len = fread(qbuf, 1, len, stdin)) > 0) {
        qbuf[len] = 0;
        if (decode_query(qbuf, key, val, &size) == 0) {
            fwrite(val, 1, size, stdout);
            return 0;
        }
    }
    return -1;
}
