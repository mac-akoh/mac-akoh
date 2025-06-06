# Main CLI script for UPM
import argparse
import sys # For sys.exit
from upm_core.detector import get_distro
from upm_core.commands import generate_and_execute_package_command, COMMAND_MAP

def main():
    parser = argparse.ArgumentParser(description="A semi-universal package manager for Debian, Ubuntu, and Fedora.")

    valid_actions = list(COMMAND_MAP.keys())

    parser.add_argument(
        "action",
        choices=valid_actions,
        help=f"The action to perform. Choose from: {', '.join(valid_actions)}"
    )

    parser.add_argument(
        "packages",
        nargs='*', # 0 or more packages
        default=[], # Default to an empty list
        help="The name(s) of the package(s) to act upon (for install/remove)."
    )

    args = parser.parse_args()

    distribution = get_distro()

    if distribution == "unknown":
        print(f"Error: Unsupported or unknown distribution. This tool currently supports Debian, Ubuntu, and Fedora.")
        return 1

    if distribution not in COMMAND_MAP.get(args.action, {}):
        print(f"Error: The action '{args.action}' is not configured for the detected distribution '{distribution}'.")
        return 1

    # Validate package arguments based on action
    if args.action in ["install", "remove"]:
        if not args.packages:
            print(f"Error: The action '{args.action}' requires at least one package name.")
            parser.print_help()
            return 1
    elif args.action in ["upgrade_all", "update_cache"]:
        if args.packages:
            print(f"Error: The action '{args.action}' does not take package names.")
            parser.print_help()
            return 1

    print(f"Distribution: {distribution}")
    print(f"Action: {args.action}")
    if args.packages:
        print(f"Package(s): {', '.join(args.packages)}")

    # Call the function that now executes the command
    success = generate_and_execute_package_command(distribution, args.action, args.packages)

    if success:
        print(f"Action '{args.action}' {('on ' + ', '.join(args.packages)) if args.packages else ''} completed.")
        return 0
    else:
        print(f"Action '{args.action}' {('on ' + ', '.join(args.packages)) if args.packages else ''} failed or was not executed.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
