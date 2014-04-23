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

#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>
#include <errno.h>
#include <limits.h>
#include <stddef.h>
#include <stdarg.h>
#include <ctype.h>
#include <dlfcn.h>
#include "mongoose.h"

#include <sys/wait.h>
#include <unistd.h>
#include <sys/stat.h>

// Arguments:
//   flags: first byte of websocket frame, see websocket RFC,
//          http://tools.ietf.org/html/rfc6455, section 5.2
//   data, data_len: payload data. Mask, if any, is already applied.
static int websocket_data(struct mg_connection *conn, int flags,
                                  char *data, size_t data_len) {
  unsigned char reply[4];
  int offset = 2;
  int ret = 1;

//  (void) flags;

  // Truncate echoed message, to simplify output code.
  if (data_len > 65536) {
    data_len = 65536;
  }  
  printf("data_len=%d\n",data_len);
  int opcode = flags & 0x0f;
  if ((opcode == 0x08) || memcmp(data, ".close", 6)==0){
//    printf("close frame");
    reply[0] = 0x88; // close, FIN set
    reply[1] = data_len;
    // Status code
    if (data_len == 0){
      reply[2] = 3;
      reply[3] = 232;
    }else{
      reply[2] = data[0];
      reply[3] = data[1];
    }
    ret = 0;
  } else {
    reply[0] = 0x80|opcode;  // FIN set
    if (data_len <= 125){
      reply[1] = data_len;
    }
    else{
      reply[1] = 126;
      reply[2] = data_len / 256;
      reply[3] = data_len % 256;
      offset = 4;
    }
  }
  // Echo the message back to the client
  mg_write(conn, reply, offset);
  if (data_len > 0)
    mg_write(conn, data, data_len);
  // Returning zero means stoping websocket conversation.
  // Close the conversation if client has sent us "exit" string.
  return ret;
}

void websocket_ready_handler(struct mg_connection *conn){
  const char *prot = mg_get_header(conn, "Sec-WebSocket-Protocol");
  char buf[100];
  if (prot){
    char *p = NULL;
    snprintf(buf, sizeof(buf), "%s", prot);
    if ((p = strrchr(buf, ',')) != NULL) {
      *p = '\0';
    } 
    mg_printf(conn, "Sec-WebSocket-Protocol: %s\r\n", buf);
  }
  mg_printf(conn, "\r\n");
}


// Arguments:
//   flags: first byte of websocket frame, see websocket RFC,
//          http://tools.ietf.org/html/rfc6455, section 5.2
//   data, data_len: payload data. Mask, if any, is already applied.
int websocket_data_handler(struct mg_connection *conn, int flags,
                                  char *data, size_t data_len){
  const char *prot = mg_get_header(conn, "Sec-WebSocket-Protocol");
  if (prot && (strncmp(prot, "foobar", 6) == 0)){
    return 0;
  }
  const char *uri = mg_get_request_info(conn)->uri;
  if (strcmp(uri, "/") == 0 || strcmp(uri, "/echo") == 0){
      return websocket_data(conn, flags, data, data_len);
  }
  return 0; 
}
