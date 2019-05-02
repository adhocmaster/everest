# Kubernetes cluster with bare metal or existing VM

This work revolves around the creation of a kubernetes-based cluster supporting cloud-native framework on which exemplar micro-serives can be deployed with ease. The current implementation of this edge cluster uses 3 existing virtualbox VM (one master - 2 slaves) and the goal is to demonstrate a mesh service based micro-service with end-to-end video file server from this cluster Kubernetes Engine.

Requirements:
- An SSH key pair on your local Linux/macOS/BSD/MacOS machine. If you haven't used SSH keys before, you can learn how to set them up by following this explanation of how to set up SSH keys on your local machine (https://www.digitalocean.com/community/tutorials/ssh-essentials-working-with-ssh-servers-clients-and-keys#generating-and-working-with-ssh-keys).

- Three servers or VM running Ubuntu 18.04 with at least 2GB RAM and 2 vCPUs each. You should be able to SSH into each server as the root user or sudo with your SSH key pair.   

- Your computer and the machines should be on the same network (or accessible).   

- Ansible 2.2 or higher on the host (your) computer. If you're running Ubuntu 18.04 as your OS, follow https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-ansible-on-ubuntu-18-04#step-1-—-installing-ansible in How to Install and Configure Ansible on Ubuntu 18.04 to install Ansible. For installation instructions on other platforms like macOS or CentOS, follow the official Ansible installation documentation (https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-ansible-on-ubuntu-18-04#step-1-—-installing-ansible).

- Internet access.   

- Familiarity with Ansible playbooks. For review, check out Configuration Management 101: https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-ansible-on-ubuntu-18-04#step-1-—-installing-ansible.

- Knowledge of how to launch a container from a Docker image. Look at "Step 5 — Running a Docker Container" in How To Install and Use Docker on Ubuntu 18.04 at https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-ansible-on-ubuntu-18-04#step-1-—-installing-ansible if you need a refresher.   


## Setting up the Kubernetes cluster

These instructions will get you a copy of the cluster up and running on environment for development and testing purposes. Note that the scripts have been tested on virtualbox vmbox based on Ubuntu bionic (18.0.x).

### Initial Steps

1. Enable ssh before OS installation for headless mode.
2. Enable passwordless SSH access from your host/computer to each of the machines/VM (needed for Ansible):
   See http://www.linuxproblem.org/art_9.html
3. Preferably, change the hostname of all the machines/VM to something like master-kube, slave1-kube, etc. for ease of distinguishability. To do this, simply edit */etc/hostname* and */etc/hosts* files in each machine/VM and reboot.

### Ansible Playbook

The rest of the steps from checking dependencies and system configurations to docker and kubernetes installation to cluster formation are automated using ansible playbook.

1. Clone the project to your local machine which has ansible and is on the same network (or can access) as the machines
`git clone https://github.com/iharijono/everest.git`

2. Navigate to the directory present inside the everest directory.
`cd tools/create_k8s/`

3. Edit the *hosts* file under kube-cluster directory. Remove all the IP addresses present in the file by default and simply add the IP address of your master machine/VM under the *master* group and the addresses of your slave machines/VM under the *slaves* group. 

4. Check if Ansible is able to reach all the RasPis by running the following ping command-
`$ansible kube-cluster -m ping`

5. Finally, run the *clusterForm* yaml playbook-
`$ansible-playbook clusterForm.yml`

Assuming no errors occurred in the previous step, you can now ssh into your master RasPi and check if the cluster is up and running-
```
kubectl get nodes
kubectl get pods --all-namespaces
```
To teardown the cluster, simply run the *clusterTear* yaml-
`$ansible-playbook clusterTear.yml`
Run *clusterForm* yaml to again build the cluster.
