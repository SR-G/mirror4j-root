#!/bin/zsh

echo "==================================================================================="
echo "=== This script must be launched in the 'mirror4j' directory (e.g., with './src/script/build-distribution-with-modules.sh'"
echo "==================================================================================="
echo ""

DISTRIBUTION_PATH="target/distribution/"
DISTRIBUTION_ZIP="mirror4j-distribution.zip"
BASE_PATH=`pwd`
MODULES="mirror4j-plugin-sonos mirror4j-plugin-jsch mirror4j-plugin-ssh mirror4j-plugin-wake-on-lan"

# === Modules compilation
for MODULE in mirror4j `echo $MODULES`
do
  echo "Building module [$MODULE]"
  cd ../$MODULE
  mvn -Dmaven.test.skip=true clean install dependency:copy-dependencies
done

# === Prepare sub path
cd "$BASE_PATH"
[[ -d "$DISTRIBUTION_PATH" ]] && rm -rf "$DISTRIBUTION_PATH"/*
[[ ! -d "$DISTRIBUTION_PATH" ]] && mkdir -p "$DISTRIBUTION_PATH"
[[ ! -d "$DISTRIBUTION_PATH/mirror4j/lib/" ]] && mkdir -p "$DISTRIBUTION_PATH/mirror4j/lib/"
[[ ! -d "$DISTRIBUTION_PATH/mirror4j/plugins/" ]] && mkdir -p "$DISTRIBUTION_PATH/mirror4j/plugins/"
[[ -f "$DISTRIBUTION_PATH/$DISTRIBUTION_ZIP" ]] && rm -f "$DISTRIBUTION_PATH/$DISTRIBUTION_ZIP"

# === Copy files
cp ../mirror4j/target/dependency/*.jar "$DISTRIBUTION_PATH/mirror4j/lib/"
cp ../mirror4j/target/mirror4j*.jar "$DISTRIBUTION_PATH/mirror4j/lib/"

# === Copy maven dependencies
for MODULE in `echo $MODULES`
do
  echo ""
  echo "Installing plugin [$MODULE] in distribution"
  cd ../$MODULE
  mkdir -p "../mirror4j/$DISTRIBUTION_PATH/mirror4j/plugins/$MODULE/"
  ls -1 target/dependency/*.jar | while read JAR
  do
    if [[ ! -f "../mirror4j/$DISTRIBUTION_PATH/mirror4j/lib/"`basename $JAR` ]] ; then
      cp $JAR "../mirror4j/$DISTRIBUTION_PATH/mirror4j/plugins/$MODULE/"
    fi
  done
  cp target/$MODULE*.jar "../mirror4j/$DISTRIBUTION_PATH/mirror4j/plugins/$MODULE"
done

# === Prepare zip
cd "$BASE_PATH"
cp src/script/mirror4j.sh "$DISTRIBUTION_PATH/mirror4j/"
cp mirror4j.xml "$DISTRIBUTION_PATH/mirror4j/"
cd "$DISTRIBUTION_PATH"
zip -r -9 "$DISTRIBUTION_ZIP" mirror4j
unzip -l "$DISTRIBUTION_ZIP"
cd "$BASE_PATH"

echo "\nOutput is in [$DISTRIBUTION_PATH], distribution package in [$DISTRIBUTION_ZIP]"
