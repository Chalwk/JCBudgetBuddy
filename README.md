# JCBudgetBuddy

A personal finance tracker built with JavaFX for managing weekly and monthly expenses, invoices, and payments.

## Features

- **Dashboard Overview** - View weekly income, expenses, remaining balance, and monthly averages
- **Bill Management** - Track weekly and monthly bills with custom frequencies and payment methods
- **Invoice Tracking** - Manage invoices with payment history and balance calculations
- **Data Persistence** - Automatic saving of user data to JSON format
- **Import/Export** - Backup and restore your financial data
- **Windows Installer** - Easy deployment with native Windows executable

## Building from Source

### Prerequisites

- Java 21 or later
- Gradle

### Build Commands

```bash
# Build the application
gradle build

# Create standalone distribution
gradle jlink

# Generate Windows installer
gradle jpackage
```

The built installer will be available in the `build/jpackage` directory.

## System Requirements

- Windows 10/11
- Java 21 Runtime (included in installer)

### [LICENSE](LICENSE)
Licensed under the [MIT License](LICENSE).