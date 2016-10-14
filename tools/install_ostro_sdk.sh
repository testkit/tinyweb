#!/usr/bin/env sh

wget https://download.ostroproject.org/releases/ostro-os-xt/milestone/v1.0.0/sdk/intel-corei7-64/ostroxt-x86_64-corei7-64-toolchain-1.0+snapshot.sh

chmod a+x ostroxt-x86_64-corei7-64-toolchain-1.0+snapshot.sh

./ostroxt-x86_64-corei7-64-toolchain-1.0+snapshot.sh -y >/dev/null 2>&1
