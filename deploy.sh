#!/usr/bin/env bash

set -e

APP=video_reply_telegram_bot2
APP_USER=video_reply_telegram_bot2
DEPLOY_USER=admin
SERV=cmlteam3.cmlteam.com

export JAVA_HOME=/home/xonix/.sdkman/candidates/java/current

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
    sudo update-rc.d $APP defaults 99
fi
sudo /etc/init.d/$APP restart
sleep 20
tail -n 200 /var/log/$APP.log
"
