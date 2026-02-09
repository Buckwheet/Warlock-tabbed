# Warlock (Tabbed Edition)

This is a modified version of the [Warlock Front End](https://github.com/sproctor/warlock3) that implements a single-window, tabbed interface for managing multiple game sessions.

## Features

-   **Single Window**: Run multiple game sessions in one application window.
-   **Tabbed Interface**: Easily switch between active sessions using tabs.
-   **Session Management**: Create new sessions and close existing ones directly from the tab bar.

## Original Project

The original Warlock project can be found here: [https://github.com/sproctor/warlock3](https://github.com/sproctor/warlock3)

For general usage and documentation of the core Warlock features, please efficient refer to the [official project page](https://warlockfe.github.io/).

## Development

### Prerequisites

-   JDK 17 or higher
-   Git

### Building

To build the project:

```bash
# Windows
./gradlew.bat build

# Linux/macOS
./gradlew build
```

### Running

To run the application from source:

```bash
# Windows
./gradlew.bat desktopApp:run

# Linux/macOS
./gradlew desktopApp:run
```

### Packaging

To create a distributable package (MSI/EXE on Windows, DEB on Linux):

```bash
# Windows
./gradlew.bat packageDistributionForCurrentOS

# Linux/macOS
./gradlew packageDistributionForCurrentOS
```

## CI/CD

This project uses GitHub Actions for automated builds.
-   **Windows Build**: Runs on `windows-latest`
-   **Linux Build**: Runs on `ubuntu-latest`
