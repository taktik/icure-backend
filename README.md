# icure-backend
OSS version of the iCure Medical File Management software

iCureBackend is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
(at your option) any later version.

You will find a copy of GNU General Public License in the LICENSE file in this repository.

To install compile and start:

```
git clone git@github.com:taktik/icure-backend.git
git submodule init
git submodule update
./gradlew bootRun
```


Be sure you have an instance of **CouchDB** already running and you have created a user as specified in the Fauxton part of the table in: https://medispring.atlassian.net/wiki/spaces/EN/pages/167084070/Setting+up+dev+environment, otherwise booting gradle will throw an error.