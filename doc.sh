#!/bin/sh
# doc.sh

# source path for documentation (colon separated list of directories)
SRC_PATH=./src
# packages to include in the documentation (space separated)
PACKAGES='ori.machine'

javadoc \
-d target/doc \
-sourcepath $SRC_PATH \
-subpackages $PACKAGES \
-private \
-use \
-windowtitle "Finite State Machine"
-doctitle "<b>F</b>inite <b>S</b>tate <b>M</b>achine"
-header "FSM" \
-bottom "2012" 
