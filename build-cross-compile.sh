#!/bin/bash

# Make sure you have autoconf, automake, and libtool installed!
command -v autoconf || { echo "autoconf not installed. Aborting."; exit 1; }
command -v automake || { echo "automake not installed. Aborting."; exit 1; }
command -v libtool || { echo "libtool not installed. Aborting."; exit 1; }

mkdir cross-compile
cd cross-compile

# git clone https://github.com/ivmai/bdwgc.git
cd bdwgc
git checkout v7.6.4
git apply ../../bdwgc-cross.patch
git clone --depth=50 https://github.com/ivmai/libatomic_ops.git -b release-7_6
make -f Makefile.direct gc.a
cd ..

# git clone https://github.com/libunwind/libunwind.git
cd libunwind
git checkout v1.2.1
export NOCONFIGURE="TRUE"
sh autogen.sh
./configure CC=arm-frc-linux-gnueabi-gcc --host=arm-frc-linux-gnueabi --prefix=$PWD
make install
cd ..

# git clone https://github.com/google/re2.git
cd re2
git checkout 2018-02-01
git apply ../../re2-cross.patch
make install
cd ..

# git clone https://github.com/simondlevy/BreezySLAM.git
cd BreezySLAM
git checkout 232e4464f77204f1fcd0772c078b0eb946917a85
git apply ../../breezyslam-cross.patch
cd cpp
make breezyslam.a
cd ../..