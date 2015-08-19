#!/bin/bash
#
# To call this script, make sure make_ext4fs is somewhere in PATH
WORK_DIR=$PWD

mkdir update
cd 		update
#mkdir -p ./META-INF/com/google/android/
#cp -rf ../update_src/android_add/* ./META-INF/com/google/android/
#mkdir recovery
#cp -rf ../update_src/recovery/* ./recovery

cp -rvf  ../update_src/*         ./



mkdir -p system
#mkdir -p bootloader
cp -rvf ../user/ota/system/*  ./system/
#sudo  cp -rvf ../user/ota/bootloader/*  ./bootloader/
#sudo chgrp -R jerry  ./system/
#sudo chown -R jerry ./system/
#sudo chgrp -R jerry  ./bootloader/
#sudo chown -R jerry ./bootloader/

if [ ! -e ../user/ota/boot.img ]; then
#if [ ! -e ../user/ota/bootq.img ]; then
echo "no boot.img"
zip ../update.zip -r ./META-INF/ ./system/  ./recovery  ./daliyuxin 
else 
echo "have boot.img"
cp ../user/ota/boot.img ./

#sudo chgrp -R jerry  ./boot.img
#sudo chown -R jerry ./boot.img
if [ ! -e ../user/ota/bootloader.fex ]; then
zip ../update.zip -r  ./META-INF/ ./system/  ./recovery ./boot.img  ./daliyuxin 
else
echo "have bootloader.fex"
cp ../user/ota/bootloader.fex ./
#sudo chgrp -R jerry  ./bootloader.fex
#sudo chown -R jerry ./bootloader.fex
zip ../update.zip -r ./META-INF/ ./system/  ./recovery ./boot.img ./bootloader.fex ./daliyuxin 
fi
fi




cd ..
DATE=$(date +%Y%m%d)
OTA_NAME="ota_${DATE}.zip"
rm $OTA_NAME
java -Xmx2048m -jar signapk.jar -w testkey.x509.pem testkey.pk8 update.zip temp.zip

 
#if  [ -z $1 ]; then
#echo "no para"
#./sysupdatea13 temp.zip $OTA_NAME --update_package=/exsd/update.zip
#elif [ $1 = "d" ]; then
#echo "wipe_data"
#./sysupdate13 temp.zip $OTA_NAME --wipe_data
#elif [ $1 = "c" ] ; then
#echo "wipe_cache"
#./sysupdate13 temp.zip $OTA_NAME --wipe_cache
#else
#./sysupdate13 temp.zip $OTA_NAME --update_package=/exsd/update.zip
#fi
#
rm -rf update
rm update.zip
mv  temp.zip $OTA_NAME
