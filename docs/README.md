# API Documentation Template for Cora Projects

This directory contains template documentation for the Cora data format that should be added to the API documentation webpages in the following projects:

- **systemone** - `src/main/webapp/rest/index.html`
- **alvin-cora** - `src/main/webapp/rest/index.html`  
- **diva-cora** - `src/main/webapp/rest/index.html`

## Files

- `api-data-format-template.html` - Complete HTML template with both English and Swedish content

## How to Update Each Project

### Step 1: Clone the target repository

```bash
# For systemone
git clone https://github.com/lsu-ub-uu/systemone.git
cd systemone

# For alvin-cora
git clone https://github.com/lsu-ub-uu/alvin-cora.git
cd alvin-cora

# For diva-cora
git clone https://github.com/lsu-ub-uu/diva-cora.git
cd diva-cora
```

### Step 2: Create a new branch

```bash
git checkout -b add-data-format-documentation
```

### Step 3: Edit `src/main/webapp/rest/index.html`

Open the file and make the following changes:

#### A. Add English navigation item

Find the `<ul class="english-nav">` section and add before the closing `</ul>`:

```html
<li><a href="#data-format">Data Format</a></li>
```

#### B. Add Swedish navigation item

Find the `<ul class="swedish-nav">` section and add before the closing `</ul>`:

```html
<li><a href="#dataformat">Dataformat</a></li>
```

#### C. Add English content section

Find the `<div id="english">` section and add the English `<section id="data-format">` content from the template after the description section.

#### D. Add Swedish content section

Find the `<div id="swedish">` section and add the Swedish `<section id="dataformat">` content from the template after the beskrivning section.

### Step 4: Commit and push

```bash
git add src/main/webapp/rest/index.html
git commit -m "Add data format documentation section with English and Swedish content"
git push origin add-data-format-documentation
```

### Step 5: Create a pull request

Go to the repository on GitHub and create a pull request with:

**Title:** Add data format documentation to REST API page

**Description:**
```
This PR adds a comprehensive "Data Format" section to the REST API documentation page.

## Changes
- Added new navigation items for both English and Swedish
- Added Data Format section (English) explaining:
  - Core data types (DataGroup, DataAtomic, DataRecordLink, DataResourceLink)
  - Record structure with examples
  - recordInfo structure
  - Record list structure
  - Action links table
  - Related projects links
- Added Dataformat section (Swedish) with equivalent content

## Related
- Based on data format analysis from cora-therest
- See: https://github.com/lsu-ub-uu/cora-therest/blob/master/openapi.yaml
- See: https://github.com/lsu-ub-uu/cora-data
```

## Content Overview

### English Section ("Data Format")
- Core Data Types
  - DataGroup (container for child elements)
  - DataAtomic (leaf element with value)
  - DataRecordLink (link to another record)
  - DataResourceLink (link to binary resource)
- Record Structure with complete JSON example
- recordInfo structure explanation
- Record List Structure
- Action Links table
- Related Projects links

### Swedish Section ("Dataformat")
- Same structure as English with Swedish translations
- Swedish examples (Johan Andersson instead of John Doe)
- Complete translation of all documentation text

## Data Model Source

The data format documentation is based on analysis of the following Cora projects:

| Project | Description |
|---------|-------------|
| [cora-data](https://github.com/lsu-ub-uu/cora-data) | Core data interfaces (DataRecord, DataGroup, DataAtomic, etc.) |
| [cora-basicdata](https://github.com/lsu-ub-uu/cora-basicdata) | Data implementations and JSON converters |
| [cora-clientdata](https://github.com/lsu-ub-uu/cora-clientdata) | Client-side data structures |
| [cora-therest](https://github.com/lsu-ub-uu/cora-therest) | REST API with OpenAPI specification |
