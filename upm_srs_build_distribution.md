## 7. Build and Distribution Process

This section outlines the processes involved in building software packages for the Universal Package Manager (UPM) and distributing them through UPM repositories. It covers workflows for package developers/maintainers, the conceptual design of the UPM distribution infrastructure, and key security considerations. This section primarily focuses on the UPM Native Package Format but also considers implications for handling existing formats where relevant.

### 7.1. For Package Developers/Maintainers

This subsection details the tools, workflows, and best practices for developers and maintainers to create, test, and publish UPM packages.

*   **7.1.1. Packaging Workflow**
    *   **7.1.1.1. Defining Package Metadata:**
        *   **Manifest File:** Developers will define package metadata in a human-readable manifest file, typically named `upm.toml` (TOML preferred for readability), `upm.json`, or `upm.yaml`, located at the root of their project's source code or in a dedicated packaging directory.
        *   **Content:** The manifest will include fields as specified for the UPM Native Format (see Section 6.2.2), such as package name, version, description, licenses, authors/maintainers, dependencies (runtime, build-time, test, optional), build instructions or scripts, lifecycle hooks (install/remove scripts), and sandboxing profiles/permissions.
    *   **7.1.1.2. UPM Packaging Tools & Commands (part of `upm-sdk`):**
        *   `upm init`: Initializes a new `upm.toml` manifest file in the current directory with default values and a basic structure, interactively guiding the developer.
        *   `upm build [build_profile] [--output <path>]`:
            *   Reads the `upm.toml` manifest.
            *   Resolves and fetches build-time dependencies.
            *   Executes defined build steps (source fetching, patching, compilation).
            *   Packages the build artifacts, metadata, SBOMs, and control files into the UPM native package format (`.upk`).
            *   Supports different build profiles (e.g., `debug`, `release` which can alter build flags or dependencies).
            *   The output package is typically placed in a `target` or `dist` subdirectory.
        *   `upm validate [<package_file.upk> | .]`: Checks the manifest file for syntax, completeness, and adherence to UPM standards. If a package file is provided, it validates its structure, checksums, signatures (if present), and internal consistency. Running in a project directory (`.`) validates the manifest.
        *   `upm pack [<input_dir>] [--output <path>]`: A lower-level command that assembles a pre-built application (from `input_dir`) and its corresponding manifest into a `.upk` package. Useful for projects with complex external build systems.
        *   `upm search-providers <dependency_name>`: Helps developers find available packages in configured repositories that can satisfy a named dependency.
        *   `upm lint [<package_file.upk> | .]`: Performs more extensive checks for common packaging errors, style issues, and best practice violations.
    *   **7.1.1.3. Build System Support:**
        *   **Declarative Build Integration:** The `upm.toml` manifest will allow developers to declaratively specify build commands or integrate with existing build system files.
        *   **Common Build Systems:** UPM will aim for seamless integration with common build systems:
            *   For Make, CMake, Autotools: Developers can specify standard targets (e.g., `configure`, `make`, `make install`) and environment variables/flags in `upm.toml`. `upm build` will invoke these with appropriate `DESTDIR` or prefix settings for staging.
            *   For language-specific tools (e.g., Rust's Cargo, Go's build tools, Java's Maven/Gradle, Python's Poetry/PDM/pip with `pyproject.toml`): UPM will detect project type (e.g., by `Cargo.toml`, `pom.xml`) and invoke the respective build/package commands, capturing output in a way suitable for UPM packaging.
        *   **Custom Scripts:** For complex scenarios, allow defining custom shell commands or scripts for different build phases (configure, build, install). These scripts will be executed by `upm build`.
        *   **Containerized/Isolated Builds:** `upm build --containerized [image_name]` option to perform builds in a defined, isolated container environment (e.g., using Docker, Podman, or systemd-nspawn). The container image can be specified in `upm.toml` or on the command line, ensuring a reproducible and clean build environment.
    *   **7.1.1.4. Handling of Source Code:**
        *   **Local Sources:** Default is to use sources in the current project directory.
        *   **Remote Source Fetching (in `upm.toml`):**
            ```toml
            [source]
            type = "git" # or "http", "file"
            url = "https://github.com/example/project.git"
            revision = "v1.2.3" # tag, branch, or commit hash
            # For type="http", url would be a tarball/zip URL
            # checksum_sha256 = "..." # For tarballs/zip files
            # sub_directory = "src" # Optional, if project is in a subdirectory of archive/repo
            ```
            `upm build` would fetch, verify (checksum/commit), and unpack sources before building.
        *   **Patching:** Support for applying a list of specified patch files (e.g., `patches = ["001-fix-something.patch"]`) after source fetching.
    *   **7.1.1.5. Specifying Dependencies:**
        *   Declared in `upm.toml` with support for version constraints (e.g., `libexample = ">=1.2.0, <2.0.0"`), target platforms/architectures, and scopes (e.g., `runtime`, `build`, `test`, `optional`).
            ```toml
            [dependencies]
            libwidget = "1.5.x"
            libutils = { version = "^2.1", optional = true }

            [build-dependencies]
            buildtool = "0.9.0"
            ```
        *   UPM resolves runtime dependencies and embeds this information into the package; build dependencies are resolved and made available during the build process.
    *   **7.1.1.6. Incorporating Scripts and Control Files:**
        *   Lifecycle scripts (pre/post-install, pre/post-remove, configure) can be included. To enhance security, these will ideally be written in a restricted, sandboxed scripting language (e.g., Lua with a UPM-provided API for common package operations) or as declarative actions rather than arbitrary shell scripts. If shell scripts are supported, they will be clearly marked and potentially require higher privileges or user consent.
        *   Other control files (default configurations, service definitions, sandboxing profiles) can be specified in `upm.toml` and included in the `.upk` package.

*   **7.1.2. Testing Packages**
    *   **Local Staged Installation & Test:** `upm build --test` command to build the package, install it into a temporary, isolated location (a local stage or sandbox), and then run test scripts defined in `upm.toml` against this installation.
    *   **Clean Environment Testing:** Guidelines and tooling (e.g., `upm test-in-container <package_file.upk> [test_container_image]`) for testing packages in a minimal, clean container environment to verify dependency completeness and basic functionality.
    *   **Validation Suite:** `upm validate --deep-check <package_file.upk>` may perform more extensive checks, such as ensuring all declared dependencies are satisfied by common base runtimes, checking for common packaging errors (e.g., file conflicts, incorrect permissions), and validating sandboxing profiles.
    *   **Automated Testing Hooks:** The `upm.toml` manifest can define a `[test]` section specifying commands or scripts to be run by `upm build --test`.

*   **7.1.3. Publishing Packages**
    *   **Target Repositories:** Developers can publish packages to official UPM repositories (curated, requires review), approved third-party repositories, or private organizational repositories.
    *   **Publishing Command:** `upm publish <package_file.upk> --repository <repo_alias_or_url>`
    *   **Authentication and Authorization:**
        *   Publishers must authenticate with the target repository using secure tokens (e.g., API keys, OAuth2 tokens). Direct password authentication should be discouraged.
        *   Repositories manage authorization, defining user/group permissions for publishing specific packages or under certain namespaces (e.g., `org.example.*`).
    *   **Package Signing (Mandatory for public repositories):**
        *   Packages must be cryptographically signed by the developer/maintainer using a private key (e.g., GPG, or a UPM-integrated PKI system). Public keys must be registered and verifiable.
        *   `upm sign <package_file.upk>` command, or automated signing as part of `upm build --sign` or `upm publish --sign`.
        *   Signatures allow the UPM client to verify package authenticity and integrity.

### 7.2. For the UPM Infrastructure (Conceptual)

This subsection describes the conceptual design of UPM's backend infrastructure for package storage and distribution.

*   **7.2.1. Repository Structure**
    *   **Logical Organization:** Packages organized by name, architecture, and version.
    *   **Physical Structure (Example):** `<repo_base_url>/<namespace>/<package_name>/<version>/<architecture>/<package_file.upk>` along with associated `.sig`, `.sbom.json`, and other metadata fragments.
    *   **Metadata Index:** Each repository publishes one or more compressed, signed index files (e.g., `index.json.zst`, `index.db.sqlite`) containing searchable metadata for all hosted packages. This allows efficient client-side querying.
    *   **Versioning Support:** Repositories must robustly support storing and serving multiple versions and variants of packages.
    *   **Signature Storage:** Package signatures are stored alongside packages or within a dedicated part of the metadata index.
    *   **Content-Addressable Storage (CAS) (Recommended):** For efficiency, repositories should use CAS for package payloads (or individual files within packages). This allows significant deduplication, reducing storage costs and potentially download sizes if clients also understand CAS.

*   **7.2.2. Distribution Model**
    *   **Hybrid Model Recommended:**
        *   **Centralized Official Repository:** A primary, highly available, and curated repository (or set of repositories) managed by the UPM project or a trusted foundation. Hosts core UPM software, essential libraries, and high-quality FOSS packages.
        *   **Decentralized Network:** Native support for a network of federated or independent third-party repositories. UPM clients can easily add/remove and prioritize multiple repository sources. This fosters community and vendor-specific software channels.
        *   **Private Repositories:** Tools and guidelines for organizations to easily set up secure, private UPM repositories for internal software. These should integrate with common artifact repository solutions (e.g., Artifactory, Nexus).
    *   **Peer-to-Peer (P2P) Distribution (Optional Future Exploration):**
        *   Consider supporting P2P mechanisms (e.g., BitTorrent, IPFS) for distributing popular packages to improve resilience, reduce server load, and potentially speed up downloads. This requires careful design for security, discovery, and integrity.

*   **7.2.3. Content Delivery**
    *   **CDNs:** Official and large third-party repositories should utilize Content Delivery Networks for distributing package files and metadata indexes to ensure low-latency, reliable global access.
    *   **Delta Updates:** UPM client and server infrastructure should support delta updates for both packages and metadata. This means clients only download the binary differences when updating, significantly reducing bandwidth.
    *   **Mirroring Support:** UPM clients should support fetching from official mirrors for repositories to distribute load and enhance availability. Repository operators should be able to easily set up mirrors.

*   **7.2.4. CI/CD Integration**
    *   **Scriptable Tooling:** `upm build`, `upm test-in-container`, and `upm publish` commands are designed to be fully scriptable for easy integration into CI/CD pipelines (Jenkins, GitLab CI, GitHub Actions, etc.).
    *   **Repository APIs & Webhooks:** Repositories should provide secure APIs and webhooks to:
        *   Allow CI systems to automatically upload newly built packages to staging areas or directly publish them based on branch/tag policies.
        *   Trigger downstream builds or notifications when new package versions are published.
        *   Query repository status and package information.
    *   **Secure Token Management:** CI/CD environments must use secure methods for storing and using repository authentication tokens (e.g., CI system secrets management).

### 7.3. Security in Build and Distribution

Ensuring the integrity and security of the end-to-end build and distribution process is critical.

*   **7.3.1. Build Process Integrity:**
    *   **Reproducible Builds:** The UPM project will champion and provide tools/guidelines to facilitate bit-for-bit reproducible builds for UPM packages. The native format will support embedding necessary build information.
    *   **Sandboxed/Containerized Builds:** Encouraging or defaulting to builds in isolated, ephemeral environments to protect the build host and ensure a clean, predictable build environment, minimizing risks of contamination or non-determinism.
    *   **Source Code Provenance & Verification:** When fetching source code, verify checksums or Git commit hashes specified in the manifest. Signatures on source archives should also be verifiable if provided.
    *   **Build-Time Dependency Verification:** All build-time dependencies must be fetched from trusted UPM repositories and their integrity (checksums, signatures) verified before use.

*   **7.3.2. Secure Communication Channels:**
    *   **Encrypted Transport:** All communication between UPM clients, build tools, and repositories (for metadata, package files, publishing commands) must occur over secure, encrypted channels (e.g., HTTPS with TLS 1.3 or higher).
    *   **Authentication for Publishing:** Strong, token-based authentication for developers/systems publishing packages (as described in 7.1.3).

*   **7.3.3. Package Signing and Verification:**
    *   **Developer/Maintainer Signing:** Mandatory cryptographic signing of all packages intended for public distribution.
    *   **Repository Co-signing (Optional):** Repositories may additionally sign packages they have vetted or built themselves, adding another layer of trust.
    *   **Client-Side Verification:** UPM clients must rigorously verify signatures before installation, checking against trusted public keys and ensuring the package has not been tampered with since signing.

*   **7.3.4. Supply Chain Security Enhancements:**
    *   **SBOMs as First-Class Citizens:** The UPM native format will require or strongly encourage embedding comprehensive Software Bill of Materials (SBOMs) (e.g., in CycloneDX or SPDX format). UPM tools will facilitate SBOM generation during build and allow querying of SBOM data.
    *   **Vulnerability Scanning Integration:** Repositories can integrate automated vulnerability scanning for submitted packages. UPM clients may provide commands to scan installed packages against vulnerability databases.
    *   **Secure Key Management:** Provide robust tools and guidelines for developers to manage their signing keys securely. Consider integration with hardware security tokens.

This framework aims to create a resilient, secure, and developer-friendly ecosystem for building, testing, and distributing software via UPM, fostering trust and reliability.
