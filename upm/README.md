# Universal Package Manager (UPM) CLI

UPM is a command-line tool that aims to provide a semi-universal interface for managing software packages on various Linux distributions. This initial version focuses on supporting package installation, removal, and system updates for Debian, Ubuntu, and Fedora.

## Features

*   Common commands for package operations: `install`, `remove`.
*   System-wide operations: `upgrade_all` (upgrades all installed packages), `update_cache` (refreshes the local package cache).
*   Automatic detection of the host distribution (Debian, Ubuntu, Fedora).
*   Handles `sudo` elevation automatically for package management tasks.

## Requirements

*   Python 3.6+
*   The native package manager for your distribution (`apt` for Debian/Ubuntu, `dnf` for Fedora).
*   `sudo` access for the user executing `upm`.

## Setup

1.  Ensure you have Python 3 installed on your system.
2.  Place the `upm` directory (containing `upm.py` and the `upm_core` subdirectory) on your system.
3.  Make the main script executable:
    ```bash
    chmod +x upm/upm.py
    ```
4.  You can run the script directly:
    ```bash
    ./upm/upm.py <action> [packages...]
    ```
    Or, consider adding the `upm` directory to your PATH or creating a symbolic link to `upm.py` in a directory that's already in your PATH (e.g., `/usr/local/bin/upm`).

## Usage

The basic command structure is:

```bash
upm <action> [package(s)...]
```

The script will automatically prepend `sudo` to the underlying package manager commands, so you may be prompted for your password.

### Supported Actions & Examples

**Supported Distributions:** Debian, Ubuntu, Fedora

**Supported Actions:**

*   `install <package_name> [package_name_2 ...]`
*   `remove <package_name> [package_name_2 ...]`
*   `update_cache`
*   `upgrade_all`

---

**1. Install a package (e.g., htop):**

*   On Debian/Ubuntu:
    ```bash
    ./upm/upm.py install htop
    ```
    *(Equivalent to: `sudo apt-get install -y htop`)*

*   On Fedora:
    ```bash
    ./upm/upm.py install htop
    ```
    *(Equivalent to: `sudo dnf install -y htop`)*

**2. Install multiple packages (e.g., neofetch and tree):**

*   On Debian/Ubuntu:
    ```bash
    ./upm/upm.py install neofetch tree
    ```
    *(Equivalent to: `sudo apt-get install -y neofetch tree`)*

*   On Fedora:
    ```bash
    ./upm/upm.py install neofetch tree
    ```
    *(Equivalent to: `sudo dnf install -y neofetch tree`)*

**3. Remove a package (e.g., htop):**

*   On Debian/Ubuntu:
    ```bash
    ./upm/upm.py remove htop
    ```
    *(Equivalent to: `sudo apt-get remove -y htop`)*

*   On Fedora:
    ```bash
    ./upm/upm.py remove htop
    ```
    *(Equivalent to: `sudo dnf remove -y htop`)*

**4. Update package cache/list:**

*   On Debian/Ubuntu:
    ```bash
    ./upm/upm.py update_cache
    ```
    *(Equivalent to: `sudo apt-get update`)*

*   On Fedora:
    ```bash
    ./upm/upm.py update_cache
    ```
    *(Equivalent to: `sudo dnf check-update`)*

**5. Upgrade all installed packages:**

*   On Debian/Ubuntu:
    ```bash
    ./upm/upm.py upgrade_all
    ```
    *(Equivalent to: `sudo apt-get upgrade -y`)*

*   On Fedora:
    ```bash
    ./upm/upm.py upgrade_all
    ```
    *(Equivalent to: `sudo dnf upgrade -y`)*

---

## Testing with Docker

You can easily test UPM on different distributions using Docker.

**1. Get a shell in a container:**

*   **Debian (Bullseye):**
    ```bash
    docker run -it --rm debian:bullseye-slim bash
    ```
*   **Ubuntu (Latest LTS - Focal Fossa as of writing):**
    ```bash
    docker run -it --rm ubuntu:focal bash
    ```
    *(You might need to run `apt-get update && apt-get install -y sudo` inside the Ubuntu container first if sudo is not present)*
*   **Fedora (Latest):**
    ```bash
    docker run -it --rm fedora:latest bash
    ```
    *(Fedora containers usually have `sudo` and `dnf` pre-installed. You might need to install a text editor like `vim` or `nano` if you want to inspect files: `dnf install -y vim`)*


**2. Copy UPM into the container:**

*   From another terminal (host machine), find your container ID:
    ```bash
    docker ps
    ```
*   Copy the `upm` directory into the running container (replace `mycontainerid` and `/path/to/host/upm`):
    ```bash
    docker cp /path/to/host/upm mycontainerid:/root/
    ```
    *(Example: `docker cp ./upm mycontainerid:/root/upm_test`)*

**3. Inside the container:**

*   Navigate to the copied directory:
    ```bash
    cd /root/upm # Or whatever you named it
    ```
*   Make the script executable and run:
    ```bash
    chmod +x upm.py
    ./upm.py install htop
    ```
    *(Note: Inside minimal Docker containers, `sudo` might not be installed or necessary if you are already root. UPM prepends `sudo`, so for testing in such containers where you are root and `sudo` isn't installed, you might need to install `sudo` first (e.g., `apt-get update && apt-get install -y sudo` on Debian/Ubuntu, `dnf install -y sudo` on Fedora) or temporarily modify `utils.py` to not prepend `sudo` if the user ID is 0 (root). For this version, UPM assumes `sudo` is available and preferred.)*

## Known Limitations

*   **Root Privileges:** The script automatically prepends `sudo` to all package manager commands. It assumes `sudo` is installed and the user has appropriate permissions.
*   **Package Name Consistency:** Package names can differ between distributions (e.g., `apache2` on Debian/Ubuntu vs. `httpd` on Fedora). UPM does not resolve these differences; you need to know the correct package name for your target distribution.
*   **Complex Scenarios:** Advanced package management features like repository management, handling broken dependencies, version pinning, or managing GPG keys are not supported in this version.
*   **Error Handling:** Error detection and reporting from the underlying package managers are basic.
*   **Idempotency:** Some commands like `update_cache` are idempotent, but `install` might behave differently if a package is already installed (usually a no-op or re-install, depending on the package manager).

## Future Development (Ideas)

*   Configuration file for custom command mappings or sudo behavior.
*   More sophisticated distribution detection.
*   Support for more distributions.
*   Interactive mode.
*   Package search functionality.
*   Option to not use `sudo` if running as root.

---
This README provides a starting point. Feel free to contribute or suggest improvements!
