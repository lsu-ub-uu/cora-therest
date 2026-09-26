# cora-therest

A REST API module for the Cora platform developed by Uppsala University Library.

## Overview

The Rest ("therest") provides RESTful web service endpoints for the Cora data management system. It handles HTTP requests and integrates with various Cora components to enable data operations through a REST API.

## Requirements

- Java (version compatible with the cora-parent pom)
- Maven

## Dependencies

This module is part of the Cora ecosystem and depends on several other Cora components:

- [cora-spider](https://github.com/lsu-ub-uu/cora-spider) - Core business logic
- [cora-json](https://github.com/lsu-ub-uu/cora-json) - JSON handling
- [cora-storage](https://github.com/lsu-ub-uu/cora-storage) - Data storage
- [cora-logger](https://github.com/lsu-ub-uu/cora-logger) - Logging
- [cora-httphandler](https://github.com/lsu-ub-uu/cora-httphandler) - HTTP handling
- [cora-gatekeeperclient](https://github.com/lsu-ub-uu/cora-gatekeeperclient) - Authentication
- [cora-solrsearch](https://github.com/lsu-ub-uu/cora-solrsearch) - Search functionality

## Building

To build the project, run:

```bash
mvn clean install
```

## License

Copyright 2015, 2018, 2019 Uppsala University Library

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE.txt) file for details.
