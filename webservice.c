#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mongoose.h"

extern struct mg_context *ctx;      // Set by start_server()
int webservice_handler(struct mg_connection *conn) {
  const struct mg_request_info *ri = mg_get_request_info(conn);
  char *post_data, fname[256], *data;
  int post_data_len;
  #define POST_SIZE 4*1024*1024
  if (!strcmp(ri->uri, "/save_file")) {
      char fpath[256], *ptr;
      //Apply space to contain post data
      post_data = (char *)malloc(POST_SIZE);
      // User has submitted a form, show submitted data and a variable value
      post_data_len = mg_read(conn, post_data, POST_SIZE);

      data = (char *)malloc(POST_SIZE);
      // Parse post data.
      mg_get_var(post_data, post_data_len, "filename", fname, sizeof(fname));
      mg_get_var(post_data, post_data_len, "data", data, POST_SIZE);
      free(post_data);
      ptr = (char *)mg_get_option(ctx, "outdir");
      if (*ptr) {
        sprintf(fpath, "%s/%s", ptr, fname);
        FILE *fp = fopen(fpath, "w+");
        fputs(data, fp);
        fclose(fp);
      } else
        printf("data=%s\n", data);
      // Send Ok reply to the client.
      mg_printf(conn, "HTTP/1.0 200 OK\r\n");
      free(data);
      return 1;  // Mark event as processed
  } 

  return 0;  // Mark request as processed
}
