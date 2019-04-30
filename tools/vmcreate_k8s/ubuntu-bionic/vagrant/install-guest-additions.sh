#!/bin/bash
GUEST_ADDITION_VERSION=5.2.10
GUEST_ADDITION_ISO=VBoxGuestAdditions_${GUEST_ADDITION_VERSION}.iso
GUEST_ADDITION_MOUNT=/media/VBoxGuestAdditions

apt-get install linux-headers-$(uname -r) build-essential dkms

wget http://download.virtualbox.org/virtualbox/${GUEST_ADDITION_VERSION}/${GUEST_ADDITION_ISO}
mkdir -p ${GUEST_ADDITION_MOUNT}
mount -o loop,ro ${GUEST_ADDITION_ISO} ${GUEST_ADDITION_MOUNT}
sh ${GUEST_ADDITION_MOUNT}/VBoxLinuxAdditions.run
rm ${GUEST_ADDITION_ISO}
umount ${GUEST_ADDITION_MOUNT}
rmdir ${GUEST_ADDITION_MOUNT}

#
# Indra
#
#apt-get update
#apt-get install -y xfce4 virtualbox-guest-dkms virtualbox-guest-utils virtualbox-guest-x11
#sed -i 's/allowed_users=.*$/allowed_users=anybody/' /etc/X11/Xwrapper.config
