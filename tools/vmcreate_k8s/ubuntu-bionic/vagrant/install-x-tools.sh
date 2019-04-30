#!/bin/bash

echo "---> Installing X Tools"
apt-get update
apt-get install -y --no-install-recommends ubuntu-desktop gnome-panel gnome-settings-daemon metacity nautilus gnome-terminal tightvncserver

#VNC PASSWORD
USER=vagrant
USER_HOME=/home/$USER
mkdir -p $USER_HOME/.vnc
echo password | vncpasswd -f > $USER_HOME/.vnc/passwd
chmod 600 $USER_HOME/.vnc/passwd
chown -R $(id -u $USER):$(id -g $USER) $USER_HOME/.vnc

#VNC SESSION
# mv ~/.vnc/xstartup ~/.vnc/xstartup.old; touch ~/.vnc/xstartup

cat > $USER_HOME/.vnc/xstartup <<EOF
#!/bin/sh
[ -x /etc/vnc/xstartup ] && exec /etc/vnc/xstartup
[ -r $HOME/.Xresources ] && xrdb $HOME/.Xresources
xsetroot -solid grey
x-window-manager &
gnome-session &
gnome-panel &
gnome-settings-daemon &
metacity &
nautilus &
EOF

chmod +x $USER_HOME/.vnc/xstartup
touch $USER_HOME/.Xauthority
mkdir -p $USER_HOME/.config

cat > $USER_HOME/.vnc/tightvncserver.conf <<EOF
\$geometry = "1280x1080";
\$depth = 24;
EOF
chown $(id -u $USER):$(id -g $USER) $USER_HOME/.vnc/xstartup $USER_HOME/.Xauthority $USER_HOME/.config $USER_HOME/.vnc/tightvncserver.conf



# Firefox
apt-get install -y firefox

#vncserver

