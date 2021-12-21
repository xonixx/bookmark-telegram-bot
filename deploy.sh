#!/usr/bin/env bash

set -e

APP=bookmark_telegram_bot
APP_USER=bookmark_telegram_bot
DEPLOY_USER=admin
SERV=cmlteam3.cmlteam.com

export JAVA_HOME=/home/xonix/.sdkman/candidates/java/21.3.0.r11-grl

echo
echo "BUILD..."
echo

./mvnw clean package -DskipTests

echo
echo "DEPLOY..."
echo

tar -cvf - $APP.conf -C target/ $APP.jar | ssh $DEPLOY_USER@$SERV "sudo -u $APP_USER tar -C /home/$APP_USER -xf -"

echo
echo "RESTART..."
echo

ssh $DEPLOY_USER@$SERV "
set -e
if [[ ! -f /etc/init.d/$APP ]]
then
    echo 'Installing service $APP ...'
    sudo ln -s /home/$APP_USER/$APP.jar /etc/init.d/$APP
fi
rcs=\$(find -L /etc/rc?.d/ -samefile /etc/init.d/$APP | wc -l)
if [[ \$rcs -eq 0 ]]
then
    echo 'Scheduling for auto-start $APP ...'
    sudo update-rc.d $APP defaults 99
fi
sudo /etc/init.d/$APP restart
sleep 20
tail -n 200 /var/log/$APP.log
"
