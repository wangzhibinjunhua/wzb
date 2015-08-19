#!/system/bin/sh
  echo 1 > /sys/module/sec/parameters/recovery_done		#tony
if ! applypatch -c EMMC:recovery:6088704:680c0562409c700e4f5808cf52c6a0f7514fcbe9; then
  log -t recovery "Installing new recovery image"
  applypatch -b /system/etc/recovery-resource.dat EMMC:boot:5703680:e4dcf7da6a19b63ba1e23ca9e39f0f5559d09b39 EMMC:recovery 680c0562409c700e4f5808cf52c6a0f7514fcbe9 6088704 e4dcf7da6a19b63ba1e23ca9e39f0f5559d09b39:/system/recovery-from-boot.p
  if applypatch -c EMMC:recovery:6088704:680c0562409c700e4f5808cf52c6a0f7514fcbe9; then		#tony
	echo 0 > /sys/module/sec/parameters/recovery_done		#tony
        log -t recovery "Install new recovery image completed"
  else
	echo 2 > /sys/module/sec/parameters/recovery_done		#tony
        log -t recovery "Install new recovery image not completed"
  fi
else
  echo 0 > /sys/module/sec/parameters/recovery_done              #tony
  log -t recovery "Recovery image already installed"
fi
