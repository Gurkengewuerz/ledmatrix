#!/bin/bash
if ! [ -x "$(command -v java)" ]; then
  echo 'Error: java is not installed.' >&2
  echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list
  echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
  echo oracle-java8-installer shared/accepted-oracle-licence-v1-1 boolean true | /usr/bin/debconf-set-selections
fi

apt-get update
apt-get install -y htop oracle-java8-installer bluetooth bluez-tools blueman libbluetooth-dev maven git screen

if grep -q 'ExecStart=\/usr\/lib\/bluetooth\/bluetoothd -C' /etc/systemd/system/bluetooth.target.wants/bluetooth.service; then
    echo "Bluetooth Config is OK"
else
  echo 'Error: wrong bluetooth config.' >&2
  sed -i -- 's/ExecStart=\/usr\/lib\/bluetooth\/bluetoothd/ExecStart=\/usr\/lib\/bluetooth\/bluetoothd -C/g' /etc/systemd/system/bluetooth.target.wants/bluetooth.service
fi

if [ ! -f /etc/bluetooth/rfcomm.conf ]; then
    echo -e "rfcomm0 {\n  bind yes;\n  device xx:xx:xx:xx:xx:xx;\n  channel 1;\n  comment \"Phone\";\n}" >/etc/bluetooth/rfcomm.conf
fi

hciconfig hci0 up

cd /tmp
git clone https://github.com/Gurkengewuerz/ledmatrix.git
cd ledmatrix
mvn package
cp -f lib/*.so /var/lib/

sudo sed -i -- 's/\#dtparam=spi=on/dtparam=spi=on/g' /boot/config.txt
mkdir -p $HOME/matrix
cp -f target/LEDMatrix.jar $HOME/matrix
cd $HOME/matrix
rm $HOME/matrix/settings.json

echo "#######################################################################"
echo "# Install Success!                                                    #"
echo "# please use crontab -e -u root to                                    #"
echo "# add @reboot screen -dmS matrix java -jar $HOME/matrix/LEDMatrix.jar #"
echo "# use bluetoothctl to pair with your phone                            #"
echo "# setup your matrix config in                                         #"
echo "#                                                                     #"
echo "# after all reboot the rpi                                            #"
echo "#######################################################################"
