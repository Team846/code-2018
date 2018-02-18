#!/bin/bash

# Make sure you have autoconf, automake, and libtool installed!

mkdir cross-compile
cd cross-compile

git clone https://github.com/ivmai/bdwgc.git
cd bdwgc
git checkout v7.6.4
git apply ../../bdwgc-cross.patch
git clone --depth=50 https://github.com/ivmai/libatomic_ops.git -b release-7_6
make -f Makefile.direct gc.a

cd ..
git clone https://github.com/libunwind/libunwind.git
cd libunwind
git checkout v1.2.1
export NOCONFIGURE="TRUE"
sh autogen.sh
./configure CC=arm-frc-linux-gnueabi-gcc --host=arm-frc-linux-gnueabi --prefix=$PWD
make install

cd ..
git clone https://github.com/google/re2.git
cd re2
git checkout 2018-02-01
git apply ../../re2-cross.patch
make install
cd ..

git clone https://github.com/wpilibsuite/allwpilib.git
cd allwpilib
git checkout v2018.2.2
cd ..

mkdir wpilib-cpp
cd wpilib-cpp
curl http://first.wpi.edu/FRC/roborio/release/eclipse/plugins/edu.wpi.first.wpilib.plugins.cpp_2018.2.2.jar | bsdtar -xf-
bsdtar -xf resources/cpp.zip
cd ..

mkdir wpilib-core
cd wpilib-core
curl http://first.wpi.edu/FRC/roborio/release/eclipse/plugins/edu.wpi.first.wpilib.plugins.core_2018.2.2.jar | bsdtar -xf-
bsdtar -xf resources/common.zip
cd ..

mkdir phoenix
cd phoenix
curl http://www.ctr-electronics.com//downloads/lib/CTRE_Phoenix_FRCLibs_NON-WINDOWS_v5.2.1.1.zip | bsdtar -xf-

cd ..
