/*
Copyright (c) 2013 Intel Corporation.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of works must retain the original copyright notice, this list
  of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the original copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
* Neither the name of Intel Corporation nor the names of its contributors
  may be used to endorse or promote products derived from this work without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY INTEL CORPORATION "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL INTEL CORPORATION BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
