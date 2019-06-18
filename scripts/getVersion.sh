# script for return current version from package.json
echo "v$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)"
