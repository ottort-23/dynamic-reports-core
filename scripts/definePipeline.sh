#!/bin/bash
# script to define the pipeline type, comparing current version with latest version

# current and latest version must be x.x.x
latestVersion=$(kubectl get deployment reports-core-api -o=jsonpath='{$.spec.template.metadata.labels.version}')
currentNumber=$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)
currentVersion="v$currentNumber"

# if both versions are different the pipeline type is "build" otherwise is "update"
if [[ "$latestVersion" == "$currentVersion" ]]
    then
        echo 'update'
    else
        echo 'build'
fi

exit