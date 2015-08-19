#!/system/bin/sh
if ! applypatch -c EMMC:/dev/block/recovery:11010048:ef33023c3a6eda1db3327a1d4f33fa20fe337458; then
  log -t recovery "Installing new recovery image"
  applypatch -b /system/etc/recovery-resource.dat EMMC:/dev/block/boot:10491904:fed2d305cde58b74a14ce5465ee5fb9e518337b1 EMMC:/dev/block/recovery ef33023c3a6eda1db3327a1d4f33fa20fe337458 11010048 fed2d305cde58b74a14ce5465ee5fb9e518337b1:/system/recovery-from-boot.p
else
  log -t recovery "Recovery image already installed"
fi
