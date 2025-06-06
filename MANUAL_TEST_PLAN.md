# Manual Test Plan for UPM across Distributions
# This plan outlines the steps to manually test UPM using Docker.
#---------------------------------------------------------------------
# IMPORTANT: These commands are intended to be run on a machine
# with Docker installed and will download Docker images and run containers.
#---------------------------------------------------------------------

## Part 1: Prepare UPM for copying
# First, navigate to the directory containing the 'upm' folder.
# Then, create a tarball of the upm tool:
tar czvf upm_tool.tar.gz upm/
# This upm_tool.tar.gz will be copied into the Docker containers.

#---------------------------------------------------------------------
## Part 2: Testing on Debian
#---------------------------------------------------------------------
echo '\n### Testing on Debian (bullseye-slim) ###'
# 1. Start Debian container:
#    docker run -it --rm --name upm_debian_test debian:bullseye-slim bash

# 2. Inside the Debian container, install sudo and a test utility (e.g., curl for neofetch later, or use htop):
#    apt-get update && apt-get install -y sudo curl procps

# 3. In a new terminal on your HOST machine, copy the UPM tool into the container:
#    docker cp upm_tool.tar.gz upm_debian_test:/root/

# 4. Back inside the Debian container, unpack and set up UPM:
#    cd /root
#    tar xzvf upm_tool.tar.gz
#    chmod +x upm/upm.py

# 5. Run UPM tests in Debian container:
#    echo '---> Test: update_cache on Debian <---'
#    ./upm/upm.py update_cache
#    echo '---> Test: install neofetch on Debian <---'
#    ./upm/upm.py install neofetch
#    echo '---> Verify: check if neofetch is installed (run it or check path) <---'
#    neofetch --version || which neofetch || dpkg -s neofetch
#    echo '---> Test: remove neofetch on Debian <---'
#    ./upm/upm.py remove neofetch
#    echo '---> Verify: check if neofetch is removed <---'
#    ! (neofetch --version || which neofetch || dpkg -s neofetch)
#    echo '---> Test: upgrade_all on Debian <---'
#    ./upm/upm.py upgrade_all
#    echo 'Debian tests complete. Type exit to leave container.'

#---------------------------------------------------------------------
## Part 3: Testing on Ubuntu
#---------------------------------------------------------------------
echo '\n### Testing on Ubuntu (focal) ###'
# 1. Start Ubuntu container:
#    docker run -it --rm --name upm_ubuntu_test ubuntu:focal bash

# 2. Inside the Ubuntu container, install sudo and a test utility:
#    apt-get update && apt-get install -y sudo curl procps

# 3. In a new terminal on your HOST machine, copy the UPM tool into the container:
#    docker cp upm_tool.tar.gz upm_ubuntu_test:/root/

# 4. Back inside the Ubuntu container, unpack and set up UPM:
#    cd /root
#    tar xzvf upm_tool.tar.gz
#    chmod +x upm/upm.py

# 5. Run UPM tests in Ubuntu container:
#    echo '---> Test: update_cache on Ubuntu <---'
#    ./upm/upm.py update_cache
#    echo '---> Test: install neofetch on Ubuntu <---'
#    ./upm/upm.py install neofetch
#    echo '---> Verify: check if neofetch is installed <---'
#    neofetch --version || which neofetch || dpkg -s neofetch
#    echo '---> Test: remove neofetch on Ubuntu <---'
#    ./upm/upm.py remove neofetch
#    echo '---> Verify: check if neofetch is removed <---'
#    ! (neofetch --version || which neofetch || dpkg -s neofetch)
#    echo '---> Test: upgrade_all on Ubuntu <---'
#    ./upm/upm.py upgrade_all
#    echo 'Ubuntu tests complete. Type exit to leave container.'

#---------------------------------------------------------------------
## Part 4: Testing on Fedora
#---------------------------------------------------------------------
echo '\n### Testing on Fedora (latest) ###'
# 1. Start Fedora container:
#    docker run -it --rm --name upm_fedora_test fedora:latest bash

# 2. Inside the Fedora container, install sudo (if not present by default, though usually is) and test utility:
#    dnf install -y sudo curl procps # Fedora might have sudo already. Add other tools if needed.

# 3. In a new terminal on your HOST machine, copy the UPM tool into the container:
#    docker cp upm_tool.tar.gz upm_fedora_test:/root/

# 4. Back inside the Fedora container, unpack and set up UPM:
#    cd /root
#    tar xzvf upm_tool.tar.gz
#    chmod +x upm/upm.py

# 5. Run UPM tests in Fedora container:
#    echo '---> Test: update_cache on Fedora <---'
#    ./upm/upm.py update_cache
#    echo '---> Test: install neofetch on Fedora <---'
#    ./upm/upm.py install neofetch
#    echo '---> Verify: check if neofetch is installed <---'
#    neofetch --version || which neofetch || rpm -q neofetch
#    echo '---> Test: remove neofetch on Fedora <---'
#    ./upm/upm.py remove neofetch
#    echo '---> Verify: check if neofetch is removed <---'
#    ! (neofetch --version || which neofetch || rpm -q neofetch)
#    echo '---> Test: upgrade_all on Fedora <---'
#    ./upm/upm.py upgrade_all
#    echo 'Fedora tests complete. Type exit to leave container.'

#---------------------------------------------------------------------
## Part 5: Cleanup
#---------------------------------------------------------------------
# After testing, remove the tarball:
# rm upm_tool.tar.gz
# Docker containers will be removed automatically due to '--rm' flag when exited.
# End of Manual Test Plan.
