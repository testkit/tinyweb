CFLAGS=	-W -Wall -I.. -pthread -g

all: echo
#	$(CC) $(CFLAGS) -DUSE_WEBSOCKET mongoose.c tinyweb.c -ldl -o tinyweb;
	$(CC) tinyweb.c -ldl -lmongoose -L./ -o tinyweb;

echo: mongoose
	$(CC) -shared -fPIC -lmongoose -L./ echo.c -o echo.so

mongoose:
	$(CC) -shared -fPIC $(CFLAGS) -DUSE_WEBSOCKET mongoose.c -ldl -o libmongoose.so;

clean:
	rm -rf tinyweb *.so
