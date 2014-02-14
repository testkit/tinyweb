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

static char *
scanspaces(char *p) {
    while (*p == ' ' || *p == '\t') {
        p++;
    }
    return p;
}

static char* scan_cookie(char *p, char **items) {
    int quote = 0;
    items[0] = p = scanspaces(p);
    while (*p != '=' && *p != 0) {
        p++;
    }
    if (*p != '=' || p == items[0]) {
        return 0;
    }
    *p++ = 0;
    if (*p == '"' || *p == '\'' || *p == '`') {
        quote = *p++;
    }
    items[1] = p;
    if (quote != 0) {
        while(*p != quote && *p != 0) {
            p++;
        }
        if (*p != quote) {
            return 0;
        }
        *p++ = 0;
        if (*p == ';') {
            p++;
        }
    }
    else {
        while (*p != ';' && *p != ' ' && *p != '\t' &&
            *p != '\r' && *p != '\n' && *p != 0)
        {
            p++;
        }
        if (*p != 0) {
            *p++ = 0;
        }
    }
    return p;
}

int main(int argc, char **argv) {
    char *items[2];
    char buf[4096], *p;
    int len = -1;
    if (argc != 2)
        return -1;
    char *field = argv[1];
    if ((len = fread(buf, 1, 4096, stdin)) > 0) {
        buf[len] = 0;
        p = buf;
        while ((p = scan_cookie(p, items)) != 0) {
            if (strcmp(items[0], field) == 0) {
                printf("%s", items[1]);
                return 0;
            }
        }
    }
    return -1;
}
