#!/bin/bash
apt-get update \
    && apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        software-properties-common \
    && curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - \
    && add-apt-repository \
        "deb https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") \
        $(lsb_release -cs) \
        stable" \
    && apt-get update \
    && apt-get install -y docker-ce=$(apt-cache madison docker-ce | grep 17.03 | head -1 | awk '{print $3}')

USER=vagrant
usermod -a -G docker $USER

apt-get install -y ntp ntpdate
ntpdate ntp.ubuntu.com
timedatectl set-ntp on
timedatectl set-timezone America/Los_Angeles
timedatectl set-local-rtc 0
