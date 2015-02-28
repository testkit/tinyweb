#!/system/bin/sh
STOP=0
PLATFORM="x86"
while getopts 'p:kh' OPTION
do
        case $OPTION in
        p)      PLATFORM="$OPTARG"
                ;;
        k)	STOP=1
		;;
        *)      echo "Usage: launch.sh: [-p(Platform) x86|arm] [-k(Kill)] [-h(Help)]"
                exit 0
                ;;
        esac
done
shift $(($OPTIND - 1))

SYSPATH=${0%/service.sh}
if [ -f $SYSPATH/tinyweb.pid ]; then
	PID=`cat $SYSPATH/tinyweb.pid`
	[ -n "$PID" ] && kill "$PID"
	>$SYSPATH/tinyweb.pid
	sleep 2
fi

if [ $STOP -eq 1 ]; then
	exit;
fi
LIBPATH="$SYSPATH/libs/$PLATFORM"
DOCPATH="${SYSPATH%/system}/docroot"
chmod 0777 $LIBPATH/*

for x in `$LIBPATH/busybox find $DOCPATH -name '*.cgi'`
do 
{
	$LIBPATH/busybox sed -ie 's/#!\/bin\/sh/#!\/system\/bin\/sh/g' $x
	chmod 0777 $x
}
done

#for x in `$LIBPATH/busybox find $DOCPATH -name '*.cgi' | $LIBPATH/busybox grep '/cgi/'`;do `$LIBPATH/busybox dirname $x`;done | $LIBPATH/busybox sort | $LIBPATH/busybox uniq > cgi
#for src in `$LIBPATH/busybox cat cgi`;do dst=`$LIBPATH/busybox sed -e "s/\/cgi//g" $src`;cp $src/*.cgi $dst;done

echo "Launch tinyweb daemon..."
PATH=$PATH:$LIBPATH $LIBPATH/tinyweb -ssl_certificate $SYSPATH/server.pem -document_root $DOCPATH -listening_ports 8080,8081,8082,8083,8443s -pidfile $SYSPATH/tinyweb.pid
