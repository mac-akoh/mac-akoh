# Command mapping and execution logic for UPM
from .utils import run_command # Relative import

COMMAND_MAP = {
    "install": {
        "debian": "apt-get install -y",
        "ubuntu": "apt-get install -y",
        "fedora": "dnf install -y",
    },
    "remove": {
        "debian": "apt-get remove -y",
        "ubuntu": "apt-get remove -y",
        "fedora": "dnf remove -y",
    },
    "upgrade_all": {
        "debian": "apt-get upgrade -y",
        "ubuntu": "apt-get upgrade -y",
        "fedora": "dnf upgrade -y",
    },
    "update_cache": {
        "debian": "apt-get update",
        "ubuntu": "apt-get update",
        "fedora": "dnf check-update",
    }
}

def generate_and_execute_package_command(distro: str, action: str, package_names: list = None) -> bool:
    """
    Builds and executes the package manager command for the given distribution and action.

    Args:
        distro (str): The name of the distribution.
        action (str): The action to perform.
        package_names (list, optional): A list of package names.
                                        Required for "install" and "remove".

    Returns:
        bool: True if the command was constructed and execution started successfully (or simulated),
              False otherwise (e.g., unsupported action/distro, missing packages).
              Note: The actual success of the subprocess is handled by run_command.
    """
    if distro not in ["debian", "ubuntu", "fedora"]:
        print(f"Error: Unsupported distribution: {distro}")
        return False

    if action not in COMMAND_MAP:
        print(f"Error: Unsupported action: {action}")
        return False

    base_command_str = COMMAND_MAP[action].get(distro)
    if not base_command_str:
        print(f"Error: Action '{action}' not defined for distribution '{distro}'.")
        return False

    command_parts = base_command_str.split()

    if action in ["install", "remove"]:
        if not package_names:
            print(f"Error: Package name(s) required for action: {action}")
            return False
        command_parts.extend(package_names)
    elif action in ["upgrade_all", "update_cache"]:
        if package_names: # Should be empty or None
            print(f"Error: Package name(s) not applicable for action: {action}")
            return False

    # At this point, command_parts is ready for execution
    return run_command(command_parts)


if __name__ == '__main__':
    print("--- Testing generate_and_execute_package_command (will attempt real execution with sudo) ---")
    # These tests will try to run actual commands.
    # Ensure you are in an environment where this is safe (e.g., a VM or Docker container).
    # Sudo password might be required.

    # Simulate distro for testing; in upm.py, this comes from get_distro()
    current_distro = "ubuntu" # Change to "debian" or "fedora" to test others
    print(f"Simulating for distro: {current_distro}\n")

    print("1. Install 'htop' (example):")
    # generate_and_execute_package_command(current_distro, "install", ["htop"])
    print("--> (Commented out for safety during subtask execution) Install htop\n")

    print("2. Remove 'htop' (example):")
    # generate_and_execute_package_command(current_distro, "remove", ["htop"])
    print("--> (Commented out for safety during subtask execution) Remove htop\n")

    print("3. Update cache:")
    # generate_and_execute_package_command(current_distro, "update_cache")
    print("--> (Commented out for safety during subtask execution) Update Cache\n")

    print("4. Upgrade all:")
    # generate_and_execute_package_command(current_distro, "upgrade_all")
    print("--> (Commented out for safety during subtask execution) Upgrade All\n")

    print("5. Install multiple packages 'vim' and 'curl' (example):")
    # generate_and_execute_package_command(current_distro, "install", ["vim", "curl"])
    print("--> (Commented out for safety during subtask execution) Install vim curl\n")

    print("Testing error conditions:")
    generate_and_execute_package_command(current_distro, "install", []) # Missing packages
    generate_and_execute_package_command(current_distro, "upgrade_all", ["somepackage"]) # Extraneous package
    generate_and_execute_package_command("nonexistent_distro", "install", ["htop"])
