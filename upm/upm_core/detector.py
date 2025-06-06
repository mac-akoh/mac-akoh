import os

def get_distro():
    """
    Detects the Linux distribution by reading /etc/os-release.

    Returns:
        str: The distribution ID (e.g., "ubuntu", "debian", "fedora"),
             or "unknown" if the distribution cannot be determined or
             the /etc/os-release file is not found.
    """
    try:
        with open("/etc/os-release", "r") as f:
            lines = f.readlines()

        distro_id = "unknown"
        for line in lines:
            line = line.strip()
            if line.startswith("ID="):
                # Remove "ID=" and any surrounding quotes
                distro_id = line[3:].strip('"').strip("'")
                break

        # We are only interested in debian, ubuntu, fedora
        if distro_id.lower() in ["debian", "ubuntu", "fedora"]:
            return distro_id.lower()
        else:
            # If it's something else, consider it unknown for our purposes
            return "unknown"

    except FileNotFoundError:
        return "unknown"
    except Exception as e:
        # Handle other potential errors during file processing
        print(f"Error reading /etc/os-release: {e}")
        return "unknown"

if __name__ == '__main__':
    # For manual testing if the script is run directly
    print(f"Detected distribution: {get_distro()}")
