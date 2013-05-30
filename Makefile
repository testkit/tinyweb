OBJ=tinyweb
CFLAGS=	-W -Wall -I.. -pthread

all: $(OBJ)

tinyweb: echo
	$(CC) tinyweb.c -ldl -lmongoose -L./ -o tinyweb;

echo: mongoose
	$(CC) -shared -fPIC -lmongoose -L./ echo.c -o echo.so

mongoose:
	$(CC) -shared -fPIC $(CFLAGS) -DUSE_WEBSOCKET mongoose.c -ldl -o libmongoose.so;

install: $(OBJ)
	install -d $(DESTDIR)/usr/bin/
	install -m 755 $(OBJ) $(DESTDIR)/usr/bin/
	install -d $(DESTDIR)/usr/lib
	install -m 755 libmongoose.so $(DESTDIR)/usr/lib/
	install -m 755 echo.so $(DESTDIR)/usr/lib/
	install -m 755 -d $(DESTDIR)/usr/share/$(OBJ)
	install -m 755 server.pem $(DESTDIR)/usr/share/$(OBJ)
	ln -s /usr/lib/libssl.so.1.0.0 /usr/lib/libssl.so
	ln -s /usr/lib/libcrypto.so.1.0.0 /usr/lib/libcrypto.so

clean:
	rm -rf tinyweb *.so
