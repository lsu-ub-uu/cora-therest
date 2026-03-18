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

## Data Model

The Cora data model is hierarchical and based on these core types (defined in [cora-data](https://github.com/lsu-ub-uu/cora-data)):

### DataGroup

A container for related data elements. DataGroups can be nested to any depth.

```json
{
  "name": "person",
  "children": [
    {"name": "firstName", "value": "John"},
    {"name": "lastName", "value": "Doe"}
  ],
  "attributes": {
    "type": "individual"
  }
}
```

### DataAtomic

A leaf element with a single value:

```json
{
  "name": "firstName",
  "value": "John",
  "repeatId": "0"
}
```

### DataRecordLink

A link to another record:

```json
{
  "name": "createdBy",
  "children": [
    {"name": "linkedRecordType", "value": "user"},
    {"name": "linkedRecordId", "value": "coraUser:111111111111111"}
  ]
}
```

### DataRecord

A complete record with data, action links, and permissions:

```json
{
  "record": {
    "data": {
      "name": "person",
      "children": [
        {
          "name": "recordInfo",
          "children": [
            {"name": "id", "value": "person:001"},
            {
              "name": "type",
              "children": [
                {"name": "linkedRecordType", "value": "recordType"},
                {"name": "linkedRecordId", "value": "person"}
              ]
            }
          ]
        },
        {
          "name": "authorisedName",
          "children": [
            {"name": "familyName", "value": "Doe"},
            {"name": "givenName", "value": "John"}
          ]
        }
      ]
    },
    "actionLinks": {
      "read": {
        "requestMethod": "GET",
        "rel": "read",
        "url": "https://example.com/rest/record/person/person:001",
        "accept": "application/vnd.cora.record+json"
      },
      "update": {
        "requestMethod": "POST",
        "rel": "update",
        "url": "https://example.com/rest/record/person/person:001",
        "contentType": "application/vnd.cora.recordgroup+json"
      }
    },
    "permissions": {
      "read": ["authorisedName", "biography"],
      "write": ["authorisedName"]
    }
  }
}
```

### Record List

A paginated list of records:

```json
{
  "dataList": {
    "fromNo": "1",
    "toNo": "10",
    "totalNo": "42",
    "containDataOfType": "person",
    "data": [
      {"record": {...}},
      {"record": {...}}
    ]
  }
}
```

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

Additional content types:
- `application/vnd.cora.record-decorated+json/xml` - Records with presentation metadata
- `application/vnd.cora.workorder+json/xml` - Validation work orders
- `application/vnd.cora.deploymentInfo+json/xml` - Deployment information

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
      {
        "name": "recordInfo",
        "children": [
          {
            "name": "dataDivider",
            "children": [
              {"name": "linkedRecordType", "value": "system"},
              {"name": "linkedRecordId", "value": "cora"}
            ]
          }
        ]
      },
      {
        "name": "authorisedName",
        "children": [
          {"name": "familyName", "value": "Doe"},
          {"name": "givenName", "value": "John"}
        ]
      }
    ]
  }'
```

### Read a Record

```bash
curl "https://example.com/rest/record/person/person:001" \
  -H "authToken: your-token" \
  -H "Accept: application/vnd.cora.record+json"
```

### List Records with Filter

```bash
curl "https://example.com/rest/record/person/" \
  -H "authToken: your-token" \
  -H "Accept: application/vnd.cora.recordList+json" \
  --data-urlencode 'filter={"name":"filter","children":[{"name":"start","value":"0"},{"name":"rows","value":"10"}]}'
```

### Update a Record

```bash
curl -X POST "https://example.com/rest/record/person/person:001" \
  -H "authToken: your-token" \
  -H "Content-Type: application/vnd.cora.recordgroup+json" \
  -H "Accept: application/vnd.cora.record+json" \
  -d '{
    "name": "person",
    "children": [
      {
        "name": "recordInfo",
        "children": [
          {"name": "id", "value": "person:001"},
          {
            "name": "type",
            "children": [
              {"name": "linkedRecordType", "value": "recordType"},
              {"name": "linkedRecordId", "value": "person"}
            ]
          }
        ]
      },
      {
        "name": "authorisedName",
        "children": [
          {"name": "familyName", "value": "Doe"},
          {"name": "givenName", "value": "Jane"}
        ]
      }
    ]
  }'
```

### Upload a Binary Resource

```bash
curl -X POST "https://example.com/rest/record/binary/binary:001/master" \
  -H "authToken: your-token" \
  -F "file=@/path/to/file.pdf"
```

### Validate a Record

```bash
curl -X POST "https://example.com/rest/record/validationOrder" \
  -H "authToken: your-token" \
  -H "Content-Type: application/vnd.cora.workorder+json" \
  -H "Accept: application/vnd.cora.record+json" \
  -d '{
    "order": {
      "name": "validationOrder",
      "children": [
        {
          "name": "recordType",
          "children": [
            {"name": "linkedRecordType", "value": "recordType"},
            {"name": "linkedRecordId", "value": "person"}
          ]
        },
        {"name": "validateLinks", "value": "true"},
        {"name": "metadataToValidate", "value": "new"}
      ]
    },
    "record": {
      "name": "person",
      "children": [...]
    }
  }'
```

### Execute a Search

```bash
curl "https://example.com/rest/record/searchResult/personSearch" \
  -H "authToken: your-token" \
  -H "Accept: application/vnd.cora.recordList+json" \
  --data-urlencode 'searchData={"name":"search","children":[{"name":"include","children":[{"name":"includePart","children":[{"name":"personSearchTerm","value":"John"}]}]}]}'
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
Error creating new record for recordType: person. Validation failed: Missing required field 'authorisedName'
```

## Available Actions

Records may include action links indicating what operations are available:

| Action | Description |
|--------|-------------|
| `read` | Read the record |
| `update` | Update the record |
| `delete` | Delete the record |
| `read_incoming_links` | Get records that link to this one |
| `index` | Index the record for search |
| `upload` | Upload a binary resource |
| `search` | Execute a search (for search records) |
| `create` | Create new records (for recordType records) |
| `list` | List all records of a type |

## Related Projects

This API is part of the Cora ecosystem:

| Project | Description |
|---------|-------------|
| [cora-data](https://github.com/lsu-ub-uu/cora-data) | Core data interfaces |
| [cora-basicdata](https://github.com/lsu-ub-uu/cora-basicdata) | Data implementations |
| [cora-spider](https://github.com/lsu-ub-uu/cora-spider) | Business logic |
| [cora-gatekeeper](https://github.com/lsu-ub-uu/cora-gatekeeper) | Authentication |
| [cora-clientdata](https://github.com/lsu-ub-uu/cora-clientdata) | Client data structures |
| [cora-javaclient](https://github.com/lsu-ub-uu/cora-javaclient) | Java client library |
| [cora-jsclient](https://github.com/lsu-ub-uu/cora-jsclient) | JavaScript client |

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
