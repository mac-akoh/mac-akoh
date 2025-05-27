## 5. System Architecture

This section provides a high-level overview of the Universal Package Manager's (UPM) architecture. It details its major components, their interactions, how they fit into a layered model, and conceptual technology choices. The architecture is designed for modularity, extensibility, and robustness.

### 5.1. Layered Architecture Overview

The UPM is envisioned with a layered architecture to promote separation of concerns, maintainability, and scalability. The distinct layers are:

*   **1. Presentation Layer:**
    *   **Description:** The outermost layer responsible for all user interactions. It translates user actions into commands for the application layer and presents results and system status back to the user.
    *   **Components:** Command-Line Interface (CLI), Graphical User Interface (GUI) (Optional).

*   **2. Application Layer (Core Services & Logic):**
    *   **Description:** This layer contains the main business logic and orchestration capabilities of the UPM. It processes requests from the Presentation Layer, manages package operations, handles configurations, and interacts with data and backend services.
    *   **Components:** Core Engine (including Dependency Resolver, Transaction Manager, Package Manager Abstraction Layer), Package Source Manager, Configuration Manager, API Layer (Optional).

*   **3. Data Layer:**
    *   **Description:** Responsible for the persistent storage and management of all data required by the UPM. This includes information about available software, installed packages, system configurations, and cached files.
    *   **Components:** Metadata Database, package cache, configuration file storage.

*   **4. Execution & Backend Layer:**
    *   **Description:** This layer includes components that interact directly with the underlying operating system, specific package formats, and isolated execution environments. It handles the low-level details of package manipulation and execution.
    *   **Components:** Package Format Backends, Sandbox Environment (Optional).

### 5.2. Major Components

The UPM comprises several interconnected components:

*   **5.2.1. Command-Line Interface (CLI)**
    *   **Description:** The primary text-based interface for users to interact with the UPM. It parses user commands, forwards them to the Core Engine (or API Layer), and displays output and status messages.
    *   **Layer:** Presentation Layer.

*   **5.2.2. Graphical User Interface (GUI) (Optional)**
    *   **Description:** An optional visual interface providing a user-friendly way to perform package management tasks (discover, install, update, remove packages, manage repositories).
    *   **Layer:** Presentation Layer. Interacts with the API Layer or Core Engine.

*   **5.2.3. Core Engine**
    *   **Description:** The central brain of the UPM, orchestrating all package management operations. It processes requests, coordinates other components, and implements the primary UPM logic.
    *   **Layer:** Application Layer.
    *   **Sub-components:**
        *   **5.2.3.1. Package Manager Abstraction Layer (PMAL):** An internal interface or set of adapters that allows the Core Engine to work with diverse package formats through a common, standardized API. This layer decouples the Core Engine from the specifics of individual Package Format Backends.
        *   **5.2.3.2. Dependency Resolver:** A sophisticated module responsible for analyzing package dependencies, identifying version constraints, and resolving conflicts. It computes a valid installation or update plan that satisfies all dependencies.
        *   **5.2.3.3. Transaction Manager:** Ensures that multi-step package operations (install, update, remove) are atomic. It manages a sequence of actions, allowing for a complete rollback to the system's previous consistent state if any step fails.

*   **5.2.4. Package Source Manager**
    *   **Description:** Manages all sources of software packages, including remote repositories (official, third-party, private) and local package files/directories. Responsible for fetching package metadata and package files, handling repository authentication, and managing repository priorities and configurations.
    *   **Layer:** Application Layer. Interacts with the Metadata Database and remote/local sources.

*   **5.2.5. Metadata Database**
    *   **Description:** A persistent, queryable store for all package-related information. This includes metadata for available packages (name, version, description, dependencies, checksums, signatures, source repository) and the status of installed packages on the local system.
    *   **Layer:** Data Layer.

*   **5.2.6. Package Format Backends**
    *   **Description:** Modular components or plugins that provide the functionality to understand, unpack, install, and remove specific package formats. Each backend implements the interface defined by the PMAL. Examples include backends for UPM's native format, and potentially for .deb, .rpm, Flatpak, Snap, AppImage, or others (either natively or via conversion/wrapping).
    *   **Layer:** Execution & Backend Layer.

*   **5.2.7. Sandbox Environment (Optional, if implemented)**
    *   **Description:** Manages the creation, configuration, lifecycle, and security enforcement for sandboxed applications. It provides isolated environments with controlled access to system resources (filesystem, network, processes).
    *   **Layer:** Execution & Backend Layer.

*   **5.2.8. Configuration Manager**
    *   **Description:** Handles user-specific and system-wide configuration settings for the UPM. This includes repository definitions, default operational parameters, security policies, and UI preferences.
    *   **Layer:** Application Layer. Settings stored in the Data Layer.

*   **5.2.9. API Layer (Optional)**
    *   **Description:** An optional, well-defined programmatic interface (e.g., libraries, D-Bus, REST API) that exposes UPM functionalities to external applications, scripts, IDEs, or other system management tools.
    *   **Layer:** Application Layer.

### 5.3. Interactions Between Components

Component interactions are crucial for UPM operations. The following describes a typical flow for installing a package (`upm install <package_name>`):

1.  **User Request (Presentation Layer):** The user issues the `install <package_name>` command via the CLI (or GUI). The CLI parses the command.
2.  **Request to Core Engine (Application Layer):** The CLI forwards the parsed request to the Core Engine.
3.  **Configuration Check (Application Layer):** The Core Engine consults the Configuration Manager for relevant settings (e.g., repository list, priorities, default flags).
4.  **Metadata Lookup & Update (Application/Data Layers):**
    *   The Core Engine queries the Metadata Database for information on `<package_name>`.
    *   If the package is not found or metadata is outdated, the Core Engine instructs the Package Source Manager to fetch/update metadata from configured repositories. The Package Source Manager downloads metadata and updates the Metadata Database.
5.  **Dependency Resolution (Application Layer):**
    *   The Core Engine invokes the Dependency Resolver with the target package and its version (if specified).
    *   The Dependency Resolver fetches dependency information from the Metadata Database, considers already installed packages, and computes a transaction plan (list of packages to install/update/remove to satisfy dependencies and avoid conflicts).
6.  **User Confirmation (Presentation/Application Layers):** The Core Engine presents the transaction plan to the user via the CLI/GUI for confirmation.
7.  **Transaction Execution (Application Layer):** Upon confirmation, the Core Engine passes the plan to the Transaction Manager.
    *   **Package Download:** For each new package in the plan, the Transaction Manager directs the Package Source Manager to download the package file from its source repository (if not already in the local cache). Integrity (checksums) and authenticity (signatures) are verified.
    *   **Installation via Backends (Application/Execution Layers):** For each package to be installed/updated:
        *   The Transaction Manager, through the PMAL, selects the appropriate Package Format Backend based on the package type.
        *   If sandboxing is enabled for the package, the Sandbox Environment is invoked to prepare the isolated environment.
        *   The Package Format Backend performs the actual file extraction, runs pre/post-installation scripts, and places files into the target directory (system-wide or sandboxed).
    *   **Metadata Update (Application/Data Layers):** After successful installation of each package, the Transaction Manager updates its status in the Metadata Database.
8.  **Transaction Outcome (Application Layer):**
    *   **Commit:** If all operations in the plan succeed, the Transaction Manager commits the transaction, making changes permanent.
    *   **Rollback:** If any operation fails, the Transaction Manager initiates a rollback, instructing backends to undo the changes made during the current transaction to restore the system to its previous state.
9.  **Feedback to User (Presentation Layer):** The Core Engine reports the final status (success, failure, errors) to the CLI/GUI, which displays it to the user.

**Conceptual Interaction Diagram (Text-Based):**
```
+---------------------+      +----------------------+      +-----------------------+
| Presentation Layer  |----->|    Application Layer   |<---->|      Data Layer       |
| (CLI, GUI)          |      | (Core Engine, ConfigMgr|      | (Metadata DB, Cache)  |
+---------------------+      |  PkgSrcMgr, API Layer) |      +-----------------------+
                               +----------+-----------+
                                          |
                                          | (Via PMAL)
                                          v
                               +----------+-----------+
                               | Execution & Backend  |
                               | Layer (Pkg Backends, |
                               |  Sandbox Env)        |
                               +----------------------+
```

### 5.4. Potential Technologies (Conceptual)

The final technology stack will be decided during detailed design. This list is conceptual and illustrative:

*   **Core Engine, CLI, PMAL, Backends:**
    *   **Languages:** Rust (strong focus on performance, safety, concurrency), Go (good for CLI tools, concurrency), Python (rapid development, large ecosystem, with performance-critical parts in C++/Rust if needed), C++.
*   **GUI (Optional):**
    *   **Frameworks:** Qt (C++), GTK (C/Vala), Electron (Node.js, web technologies), Flutter (Dart), or native platform toolkits.
*   **Metadata Database:**
    *   **Databases:** SQLite (embedded, good for local single-user applications), RocksDB/LevelDB (key-value stores, good for performance), or a custom optimized file-based index.
*   **Package Source Manager:**
    *   **Networking Libraries:** `reqwest` (Rust), `libcurl` (C, with bindings), Python's `requests`.
    *   **Compression Libraries:** Standard libraries for `tar`, `gzip`, `xz`, `zstd`.
*   **Sandbox Environment (Linux):**
    *   **Technologies:** Linux Namespaces (pid, net, mnt, uts, ipc, user), Control Groups (cgroups) for resource limits, seccomp-bpf for syscall filtering, OverlayFS/bind mounts for filesystem isolation. Tools like Bubblewrap could serve as inspiration or direct components.
*   **Sandbox Environment (Other OS):**
    *   **macOS:** `sandbox-exec`, App Sandbox.
    *   **Windows:** AppContainers, Windows Sandbox mechanisms.
*   **API Layer (Optional):**
    *   **Protocols/Frameworks:** gRPC (efficient cross-language RPC), RESTful APIs (e.g., using Actix Web/Rocket for Rust, FastAPI/Flask for Python), D-Bus (Linux IPC).
*   **Configuration Management:**
    *   **Formats:** TOML, YAML, JSON, INI. Parsers available in most languages.
*   **Transaction Management:**
    *   Filesystem journaling capabilities, copy-on-write techniques, or stateful logs of operations to enable rollback.

This architectural framework provides a solid foundation for developing the UPM, ensuring that it is robust, maintainable, and capable of meeting its complex requirements. Detailed design of each component and their precise interfaces will be elaborated in subsequent design phases.
