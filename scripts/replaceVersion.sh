#!/bin/bash
# script to get the current version in package.json and put it in k8s-resources

# current number version must be x.x.x
latestVersion=$(kubectl get deployment reports-core-api -o=jsonpath='{$.spec.template.metadata.labels.version}')
currentNumberVersion=$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)

# current version adds the prefix v to the current number version
currentVersion="v$currentNumberVersion"

# command sed is used to replace the token <REPORTS_API_VERSION> by current version
$(sed -r -i 's/<REPORTS_API_VERSION>/'$currentVersion'/g' k8s/k8s-resources.yml)
echo "replace version successfully..."
echo "latest version: $latestVersion"
echo "current version: $currentVersion"