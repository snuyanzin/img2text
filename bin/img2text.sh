#!/bin/bash
# img2Text - Script to launch img2Text shell on Unix, Linux or Mac OS

BINPATH=$(dirname $0)
exec java -cp "$BINPATH/../target/*" com.nuyanzin.img2text.Image2Text "$@"

# End img2text.sh
