# Cora TheRest

RESTful API for managing records in the Cora system. This is part of the Uppsala University Library's Cora project.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Overview

Cora TheRest provides a comprehensive REST API for:
- **Record Management**: Create, read, update, and delete records
- **Search**: Execute saved searches with flexible filtering
- **Validation**: Validate records against metadata schemas
- **Binary Resources**: Upload and download file attachments
- **Batch Indexing**: Bulk indexing operations
- **IIIF Support**: International Image Interoperability Framework integration

## API Documentation

The complete API specification is available in OpenAPI 3.0 format:
- **OpenAPI Specification**: [openapi.yaml](openapi.yaml)

You can view and interact with the API documentation using tools like:
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [Redoc](https://redocly.github.io/redoc/)
- [Stoplight Studio](https://stoplight.io/studio/)

## Getting Started

### Authentication

Most endpoints require authentication using an auth token. The token can be provided in two ways:

1. **Header parameter** (recommended):
   ```
   authToken: your-token-here
   ```

2. **Query parameter**:
   ```
   ?authToken=your-token-here
   ```

When both are provided, the header parameter takes precedence.

To obtain an auth token, use the login service endpoints:
- **App Token Login**: `POST /login/apptoken`
- **Password Login**: `POST /login/password`

### Content Types

The API supports both JSON and XML formats using custom MIME types:

| Format | Record | Record Group | Record List |
|--------|--------|--------------|-------------|
| JSON | `application/vnd.cora.record+json` | `application/vnd.cora.recordgroup+json` | `application/vnd.cora.recordList+json` |
| XML | `application/vnd.cora.record+xml` | `application/vnd.cora.recordgroup+xml` | `application/vnd.cora.recordList+xml` |

## API Endpoints

### Deployment Information

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Get deployment metadata (version, URLs, example users) |

### Records

| Method | Path | Description |
|--------|------|-------------|
| GET | `/{type}/` | List records of a type |
| POST | `/{type}` | Create a new record |
| GET | `/{type}/{id}` | Read a specific record |
| POST | `/{type}/{id}` | Update a record |
| DELETE | `/{type}/{id}` | Delete a record |

### Search

| Method | Path | Description |
|--------|------|-------------|
| GET | `/searchResult/{searchId}` | Execute a saved search |

### Validation

| Method | Path | Description |
|--------|------|-------------|
| POST | `/validationOrder` | Validate a record without storing |

### Resources (Binary Files)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/{type}/{id}/{resourceType}` | Download a resource |
| POST | `/{type}/{id}/{resourceType}` | Upload a resource |

### Links

| Method | Path | Description |
|--------|------|-------------|
| GET | `/{type}/{id}/incomingLinks` | Get incoming record links |

### Batch Indexing

| Method | Path | Description |
|--------|------|-------------|
| POST | `/index/{type}` | Start batch indexing job |

### IIIF (Image API)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/{identifier}/{requestedUri}` | IIIF image requests |

## Examples

### Create a Record

```bash
curl -X POST "https://example.com/rest/record/person" \
  -H "authToken: your-token" \
  -H "Content-Type: application/vnd.cora.recordgroup+json" \
  -H "Accept: application/vnd.cora.record+json" \
  -d '{
    "name": "person",
    "children": [
      {"name": "firstName", "value": "John"},
      {"name": "lastName", "value": "Doe"}
    ]
  }'
```

### Read a Record

```bash
curl "https://example.com/rest/record/person/person:123" \
  -H "authToken: your-token" \
  -H "Accept: application/vnd.cora.record+json"
```

### List Records with Filter

```bash
curl "https://example.com/rest/record/person/?filter={\"start\":0,\"rows\":10}" \
  -H "authToken: your-token" \
  -H "Accept: application/vnd.cora.recordList+json"
```

### Upload a Binary Resource

```bash
curl -X POST "https://example.com/rest/record/binary/binary:123/master" \
  -H "authToken: your-token" \
  -F "file=@/path/to/file.pdf"
```

### Execute a Search

```bash
curl "https://example.com/rest/record/searchResult/personSearch?searchData={\"searchTerms\":[{\"name\":\"firstName\",\"value\":\"John\"}]}" \
  -H "authToken: your-token" \
  -H "Accept: application/vnd.cora.recordList+json"
```

## Error Responses

The API returns standard HTTP status codes:

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Resource created |
| 400 | Invalid request data |
| 401 | Missing or invalid authentication |
| 403 | Insufficient permissions |
| 404 | Resource not found |
| 405 | Method not allowed |
| 409 | Resource conflict |
| 500 | Internal server error |

Error responses include a plain text message describing the error:

```
Error creating new record for recordType: person. Validation failed: Missing required field 'firstName'
```

## Building

This project uses Maven for building:

```bash
mvn clean install
```

## Dependencies

Key dependencies include:
- **Jersey 4.0.2**: JAX-RS implementation
- **Jakarta Servlet API 6.1.0**: Servlet support
- **Cora modules**: spider, converter, logger, storage, etc.

## License

Copyright © 2015-2026 Uppsala University Library

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.txt](LICENSE.txt) file for details.

## Links

- **GitHub Repository**: [https://github.com/lsu-ub-uu/cora-therest](https://github.com/lsu-ub-uu/cora-therest)
- **Cora Project**: [https://github.com/lsu-ub-uu](https://github.com/lsu-ub-uu)
