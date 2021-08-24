# iCure Data Stack
OSS version of the iCure Medical File Management software

iCureBackend is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
(at your option) any later version.

You will find a copy of GNU General Public License in the LICENSE file in this repository.

To install compile and start:

```
git clone git@github.com:taktik/icure-backend.git
./gradlew :bootRun
```

if you wish to build & run this project using the IntelliJ idea, you'll have to run the kaptKotlin gradle task before

### Naming convention in this project

For controllers, logic and daos:

|  Action                       |  API Name       | Method and path |
|-------------------------------|-----------------|-----------------|
| Create an object              |  createObject   | POST /rest/v?/object |
| Create objects                |  createObjects  | POST /rest/v?/object/batch |
| Modify an object              |  modifyObject   | PUT /rest/v?/object |
| Modify objects                |  modifyObjects  | PUT /rest/v?/object/batch |
| Get an object by id           |  getObject      | GET /rest/v?/object/:id |
| Get an object by other key    |  getObjectByKey | GET /rest/v?/object/byKey/:key |
| Find objects using pagination |  findObjectsBy  | GET or POST /rest/v?/object/byMainKey/:mainKey (may have extra query params or body)|
| List objects (no pagination)  |  listObjectsBy  | GET or POST /rest/v?/object/byMainKey/:mainKey (may have extra query params or body)|

