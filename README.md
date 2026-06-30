# JCBudgetBuddy

A Windows desktop personal finance tracker built with Qt Widgets, JSON persistence, and CMake for managing weekly and monthly expenses, invoices, and payments.

---

## Features

- **Dashboard Overview** - View weekly income, expenses, remaining balance, and monthly averages.
- **Bill Management** - Track weekly and monthly bills with custom frequencies and payment methods.
- **Invoice Tracking** - Manage invoices with payment history and balance calculations.
- **Data Persistence** - Automatic saving of user data (`%USERPROFILE%\.JCBudgetBuddy\userdata.json`).
- **Windows Installer** - Easy deployment with an NSIS installer that adds Start Menu shortcuts and uninstall support.

## Requirements

- Windows 10 or 11
- Visual Studio Build Tools 2022 or 2026 with the Desktop Development with C++ workload
- CMake 3.24 or newer
- Qt 6.x installed separately (required)
  - Qt must include the MSVC build (`msvc2022_64` is required)
  - **Do not** install Qt MinGW (not needed)

---

## Install Qt

1. Download and run the Qt Online Installer from the official Qt website.
2. Install Qt 6.x.
3. Select the following components:
   - Qt 6.x for Desktop Development
   - MSVC 2022 64‑bit kit (required)

After installation, verify that this folder exists:  
`C:\Qt\<version>\msvc2022_64\lib\cmake\Qt6`

---

## Verify your environment

Before configuring the project, verify the required tools are available:

```bash
cmake --version
```

---

## Build & Package from VS Code

```bash
build_package.bat
```

1. **Clean previous build** (recommended to avoid cache or Qt mismatch issues):
   ```bash
   rmdir /s /q build
   ```

2. **Configure** the project (generate build files and locate Qt):
   ```bash
   cmake -S . -B build -DQt6_DIR="C:/Qt/6.11.1/msvc2022_64/lib/cmake/Qt6"
   ```

3. **Build** the project (compile the application):
   ```bash
   cmake --build build --config Release
   ```

4. **Package** the application
   ```bash
   installer\package.bat build
   ```

---

## Creating an installer with NSIS

1. Open the NSIS script in the NSIS compiler (or use the command line):
   ```bash
   cd installer
   makensis JCBudgetBuddy.nsi
   ```
   This will generate `JCBudgetBuddySetup.exe` in the `installer` folder.

2. Run the installer. It will:
   - Install the application to `C:\Program Files\JCBudgetBuddy`.
   - Create a Desktop shortcut and a Start Menu folder with both application and uninstall shortcuts.
   - Register the application in Windows **Add/Remove Programs** for easy uninstallation.

---

## VS Code Configuration

The repository includes pre‑configured VS Code settings (in the `.vscode` folder) to streamline building and debugging. However, because these files contain absolute paths to the local Qt installation, **you may need to update them** if your Qt version or installation folder differs from the example.

- **`.vscode/settings.json`**:  
  Update the `cmake.configureArgs` value to point to your actual `Qt6` CMake directory.  
  Example: `"-DQt6_DIR=C:/Qt/6.11.1/msvc2022_64/lib/cmake/Qt6"`

- **`.vscode/launch.json`**:  
  Update the `PATH` environment variable to include your Qt `bin` folder, so the debugger can find the Qt runtime DLLs.  
  Example: `"value": "C:/Qt/6.11.1/msvc2022_64/bin;${env:PATH}"`

---

### IntelliSense & Build Troubleshooting

If you see red squiggles under Qt includes (e.g., `#include <QWidget>`) even though the project compiles successfully, or if CMake fails to find Qt, follow these steps to align your environment with the actual build:

1. **Verify the paths** in `.vscode/settings.json` and `.vscode/launch.json` match your local Qt installation.
2. **Select the correct MSVC kit**:  
   - Press `Ctrl+Shift+P` and run `CMake: Select a Kit`.  
   - Choose **Visual Studio Build Tools 2026 Release - amd64** (or the equivalent for your version).
3. **Configure the project**:  
   - Run `CMake: Configure` from the command palette to regenerate build files.
4. **Reset IntelliSense** to force a refresh:  
   - Run `C/C++: Reset IntelliSense Database`.

---

## License

This project is licensed under the **GNU General Public License Version 3, 29 June 2007**.

Copyright (c) 2026 Jericho Crosby (Chalwk).

See the [LICENSE](LICENSE) file for details.

---