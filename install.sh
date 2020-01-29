#!/bin/bash
echo ""
echo "====================================================="
echo ""
echo "    _____ ______  _____ _______       _   _______"
echo "   / ____|  ____|/ ____|__   __|/\   | | |__   __|"
echo "  | |  __| |__  | (___    | |  /  \  | |    | |"
echo "  | | |_ |  __|  \___ \   | | / /\ \ | |    | |"
echo "  | |__| | |____ ____) |  | |/ ____ \| |____| |"
echo "   \_____|______|_____/   |_/_/    \_\______|_|"
echo ""
echo "====================================================="
echo "  An open source repository of creative coding playground, including interactive design and generative art experiments"
echo "  https://github.com/vonqo/gestalt"
echo "  Enkh-Amar.G - Mozilla Public License Version 2.0"
echo ""
echo "  [WARNING]: If things go wrong. Screw this script and go manual!"
echo "  [detecting OS]"

##############################
## Linux
##############################
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    echo "  OS: Linux-GNU"
    ##############################
    ## Linux Install
    ##############################
    
    ##############################
    ## Linux Build
    ##############################
    echo "  [building moodbar]"
    cd modules/moodbar/
    meson --buildtype=release build/
    cd build/
    ninja
    sudo ninja install
    cd ../../
    echo "  [building zenphoton]"
    cd modules/zenphoton/hqz
    cd ../../../
    make
    echo "  [building orchestrator]"
    cd modules/orchestrator
    mvn package

##############################
# Darwin
##############################
elif [[ "$OSTYPE" == "darwin"* ]]; then
    echo "  OS: Darwin / Mac OSX"
    ##############################
    ## Darwin Install
    ##############################
    
    ##############################
    ## Darwin Build
    ##############################
    echo "  [building moodbar]"
    cd modules/moodbar-xcode/
    xcodebuild
    cd ../../
    echo "  [building zenphoton]"
    cd modules/zenphoton/hqz
    cd ../../../
    make
    echo "  [building orchestrator]"
    cd modules/orchestrator
    mvn package
    else

##############################
## Eto GG
##############################
echo "  [ERROR]: Your operating system is not supported!"
fi
