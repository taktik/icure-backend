#!/bin/bash

./gradlew bootRun -PjvmArgs="-Dorg.ektorp.support.AutoUpdateViewOnChange=false -Dicure.authentication.local=true" 
