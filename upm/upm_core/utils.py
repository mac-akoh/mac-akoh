# Utility functions for UPM
import subprocess
import sys

def run_command(command_parts: list):
    """
    Runs a command using subprocess and streams its output.
    Prepends 'sudo' to the command.

    Args:
        command_parts (list): A list of strings representing the command and its arguments.
                              Example: ['apt-get', 'install', '-y', 'htop']

    Returns:
        bool: True if the command executed successfully (return code 0), False otherwise.
    """
    # Prepend sudo to the command
    full_command = ['sudo'] + command_parts

    print(f"Executing: {' '.join(full_command)}", flush=True)

    try:
        # Using Popen for real-time output streaming
        process = subprocess.Popen(
            full_command,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1, # Line buffered
            universal_newlines=True
        )

        # Stream stdout
        if process.stdout:
            for line in iter(process.stdout.readline, ''):
                print(line, end='', flush=True)
            process.stdout.close()

        # Stream stderr
        if process.stderr:
            for line in iter(process.stderr.readline, ''):
                sys.stderr.write(line)
                sys.stderr.flush()
            process.stderr.close()

        process.wait() # Wait for the process to complete

        if process.returncode == 0:
            print(f"\nCommand executed successfully.", flush=True)
            return True
        else:
            print(f"\nCommand failed with return code {process.returncode}.", flush=True)
            return False

    except FileNotFoundError:
        print(f"Error: The command '{full_command[0]}' (or 'sudo') was not found. Is it in your PATH?", flush=True)
        return False
    except Exception as e:
        print(f"An error occurred while trying to execute the command: {e}", flush=True)
        return False

if __name__ == '__main__':
    print("--- Testing utils.run_command ---")
    print("Attempting to run 'ls -l /tmp' (prefixed with sudo):")
    # This command is generally safe and doesn't require actual root for listing /tmp usually,
    # but sudo will prompt for a password if the user isn't already sudo-cached.
    # In a non-interactive environment, this might behave differently.
    # For automated testing, simpler commands like 'sudo echo hello' might be better.
    run_command(['ls', '-l', '/tmp'])
    print("\nAttempting a non-existent command:")
    run_command(['nonexistentcommand123'])
    print("\nAttempting to install 'tree' (example, will require sudo password if not cached):")
    # This is a placeholder for a real package command.
    # It might fail if 'tree' is already installed or if sudo password is required interactively.
    # run_command(['apt-get', 'install', '-y', 'tree']) # Example, commented out for safety
