<p align="center">
  <img src="composeApp/icons/clipshare.jpeg" 
       alt="ClipShare Logo" 
       width="120" 
       style="border-radius: 20%;"/>
</p>

# ClipShare

**ClipShare** is a modern application for **managing and sharing clipboard content between devices**. It supports clipboard **history** and allows sharing clipboard items of different types directly between devices, including **TEXT, FILES, IMAGE, HTML, and RTF**.

---

## Screenshots

| Mobile                                 | Desktop                                |
|----------------------------------------|----------------------------------------|
| ![Screenshot 1](assets/s1-mobile.jpeg) | ![Screenshot 1](assets/s1-desktop.png) |
| ![Screenshot 2](assets/s2-mobile.jpeg) | ![Screenshot 2](assets/s2-desktop.png) |
| ![Screenshot 3](assets/s3-mobile.jpeg) | ![Screenshot 3](assets/s3-desktop.png) |
| ![Screenshot 4](assets/s4-mobile.jpeg) | ![Screenshot 4](assets/s4-desktop.png) |
| ![Screenshot 5](assets/s5-mobile.jpeg) | Not supported on desktop               |
| ![Screenshot 6](assets/s6-mobile.jpeg) | Not supported on desktop               |

---

## Project Structure

**`composeApp/`** – Main Compose Multiplatform application entry point
**`core/`** – Core modules for app functionality

* `data/` – Data sources and repositories
* `database/` – Local database access
* `design-system/` – UI components and theming
* `di/` – Dependency injection setup
* `domain/` – Business logic
* `navigation/` – Navigation graph and routing
* `process/` – Background services (connectivity, clipboard, storage)
* `resources/` – Static resources
* `ui-models/` – UI data models

**`features/`** – Feature modules (Screens)

* `devices/` – Device discovery and management UI
* `history/` – Clipboard history UI
* `qrcode/` – QR code generation and scanning
* `scanner/` – Scanner UI for QR codes

**`shared/`** – Shared modules

* `clipboard/` – Clipboard management
* `common/` – Shared utilities and Compose helpers

    * `app/` – App-level utilities
    * `compose/` – Compose UI helpers
* `connectivity/` – Signaling, UDP, WebRTC management
* `network/` – Ktor configuration
* `storage/` – Local storage handling

---

## How it Works

1. **Device Discovery**
   ClipShare discovers nearby devices using **UDP sockets**. Each device broadcasts its presence and listens for others.

2. **Signaling Server**  
   To establish a connection for sharing clipboard items, ClipShare uses a lightweight **signaling server** to exchange **SDP information** and ICE candidates.
   The server implementation can be found here: [ClipShare Signaling Server](https://github.com/amirroid/clipshare-back)

3. **Peer-to-Peer Connection**
   After SDP exchange, a **direct P2P WebRTC connection** is established for sharing clipboard content of supported types.

4. **Background Processes**
   The `process` module handles background tasks, binding services like connectivity and clipboard monitoring to ensure smooth operation.

---

## Features

* Clipboard history management
* Sharing clipboard content between devices
* Supported types: TEXT, FILES, IMAGE, HTML, RTF
* Device discovery using UDP
* Lightweight signaling server for connection setup
* Background service management for clipboard and connectivity

---

## Contributing

We welcome contributions! Here’s how you can help:

1. **Fork the repository**
2. **Create a new branch**:

   ```bash
   git checkout -b feature/my-feature
   ```
3. **Make your changes**
4. **Commit your changes**:

   ```bash
   git commit -m "Add my feature"
   ```
5. **Push to your branch**:

   ```bash
   git push origin feature/my-feature
   ```
6. **Open a Pull Request** on the main repository

Please follow existing styles and include documentation where needed.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.