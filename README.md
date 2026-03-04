# UniFi Visitor Manager

UniFi Visitor Manager is a self-hosted, open-source integration layer designed to extend the capabilities of the UniFi Access ecosystem. It provides a dedicated web portal for administrators to generate temporary access credentials and automatically distributes natively formatted Apple Wallet passes and HTML emails to visitors.

By operating entirely within your local network infrastructure, it eliminates the need to expose your UniFi Console to the public internet while delivering an enterprise-grade onboarding experience for guests.

## Core Features

* **Automated Credential Distribution**: Generates and emails temporary access credentials directly to visitors, bypassing the need for users to download or install the UniFi Identity application.
* **Native Apple Wallet Integration**: Dynamically constructs cryptographically signed .pkpass files containing the visitor's secure access token, formatted with custom corporate branding.
* **Intelligent Token Extraction**: Interacts with the UniFi Access API to provision visitors, securely extracting the underlying 16-byte access token from the generated visual QR code to embed into third-party formats.
* **Built-in SMTP Relay**: Queues and dispatches localized HTML emails containing embedded QR codes and Wallet pass attachments asynchronously to prevent UI blocking and respect SMTP server rate limits.
* **Configuration UI**: Features a native, MapDB-backed setup wizard for configuring UniFi Console connections, Apple Developer certificates, and SMTP credentials without requiring manual environment variable manipulation.

## Technical Architecture

The application is built on a lightweight Java stack designed for low-resource environments (e.g., Proxmox LXC, Docker, or Raspberry Pi).

* **Web Framework**: Javalin (Synchronous UI routes with asynchronous background task offloading)
* **Templating**: FreeMarker (HTML emails and web UI)
* **Wallet Generation**: jpasskit (In-memory building and cryptographic signing of PKPass archives)
* **Mail Delivery**: SimpleJavaMail (Thread-safe SMTP delivery with inline CID image embedding)
* **Data Persistence**: MapDB (Embedded local file database for configuration states)

## Prerequisites

To utilize the full feature set of this application, the following are required:

1.  **UniFi Access Environment**: A UniFi Console (e.g., UDM Pro, Cloud Key Gen2 Plus etc.) running UniFi Access, accessible over the local network.
2.  **UniFi API Token**: A generated application password from the UniFi Access App settings.
3.  **SMTP Server**: Credentials for a mail server (e.g., Postfix, Microsoft 365, Gmail SMTP) to dispatch visitor invitations.
4.  **Apple Developer Account (Optional)**: To generate .pkpass files, a valid Pass Type ID certificate (.p12) and the Apple WWDR G4 Intermediate certificate are required.

## Installation and Execution

1. Clone the repository:
    git clone https://github.com/bjmsw/UniFi-Visitor-Manager.git

2. Build the executable JAR:
    mvn clean package

3. Run the application:
    java -jar target/unifi-visitor-manager.jar

Upon the initial launch, the application will initialize a local web server on port 8080. Navigate to http://<server-ip>:8080 in your web browser to access the First-Run Setup Wizard and input your infrastructure credentials.

There is also a Dockerfile and docker-compose.yml included for convenience.

## Security and Privacy

This application is designed with strict local-first security principles.
* **No Built-in Authentication**: The web UI does **not** include a login mechanism. It is designed to be deployed exclusively on a trusted internal network (e.g., a Proxmox LXC, Docker container on a private VLAN, behind a VPN or a ZTNA like Cloudflare Access / ZeroTrust). **Do not expose this application directly to the public internet.**
* **No Cloud Dependency**: The server does not communicate with external cloud APIs (other than the configured SMTP relay and Apple's WWDR validation).
* **Internal Routing**: Visitors do not require network access to this application or the UniFi Console. The server acts as a one-way provisioning engine, pushing the credentials out via email.
* **No Hardcoded Secrets**: All sensitive data, including API tokens and certificate passwords, are written directly to the encrypted MapDB instance and are never stored in plain text configuration files.

## License

This project is licensed under the Apache License 2.0. See the LICENSE file for details.

## Disclaimer

This application is an independent open-source project and is not affiliated with, endorsed by, or sponsored by Ubiquiti Inc. "Ubiquiti", "UniFi", and "UniFi Access" are registered trademarks of Ubiquiti Inc. Ensure your usage of the UniFi API complies with applicable terms of service.