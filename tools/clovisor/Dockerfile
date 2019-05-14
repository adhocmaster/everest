#
# Copyright 2017-2019 The Everest Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.
#
#
# WARNING: a copy from clover project, please synch up!!!
#


FROM ubuntu:18.04

# the following is the Linux version for GKE for k8s 1.11.4-gke.8
ARG TARGET_KERNEL_VER="linux-headers-4.15.0-1023-gcp"

RUN set -ex; \
  echo "deb [trusted=yes] http://repo.iovisor.org/apt/bionic bionic main" > /etc/apt/sources.list.d/iovisor.list; \
  apt-get update -y; \
  DEBIAN_FRONTEND=noninteractive apt-get install -y \
    auditd \
    bcc-tools \
    $TARGET_KERNEL_VER \
    net-tools \
    iputils-ping \
    libelf1;

COPY . .
COPY bin/clovisor .
RUN chmod +x clovisor

CMD ["./clovisor"]
