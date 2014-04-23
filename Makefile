CFLAGS=	-W -Wall -I.. -pthread -g

all:
	$(CC) mongoose.c websocket.c webservice.c tinyweb.c $(CFLAGS) -DUSE_WEBSOCKET -ldl -o tinyweb;
	$(CC) cgi-getfield.c -o cgi-getfield;
	$(CC) cgi-getcookie.c -o cgi-getcookie;

clean:
	rm -rf tinyweb cgi-getcookie cgi-getfield
