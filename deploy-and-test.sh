#!/bin/bash

# DNSFlip Rapid Deployment & Testing Script
# Unix/Linux/macOS version

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}DNSFlip Rapid Deployment & Testing Script${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Check if we're in the right directory
if [ ! -f "Studio/gradlew" ]; then
    echo -e "${RED}ERROR: This script must be run from the android-dnsflip root directory${NC}"
    echo -e "${YELLOW}Current directory: $(pwd)${NC}"
    echo ""
    echo -e "${YELLOW}Please navigate to the android-dnsflip directory and run this script again.${NC}"
    read -p "Press Enter to continue"
    exit 1
fi

# Navigate to Studio directory
cd Studio

echo -e "${GREEN}[1/6] Checking ADB connection...${NC}"
if ! command -v adb &> /dev/null; then
    echo -e "${RED}ERROR: ADB not found or not working${NC}"
    echo -e "${YELLOW}Please ensure Android SDK is installed and ADB is in your PATH${NC}"
    read -p "Press Enter to continue"
    exit 1
fi

# Get list of connected devices
DEVICE_ID=$(adb devices | grep "device$" | head -n1 | cut -f1)

if [ -z "$DEVICE_ID" ]; then
    echo -e "${RED}ERROR: No Android devices found${NC}"
    echo ""
    echo -e "${YELLOW}Please ensure:${NC}"
    echo -e "${YELLOW}- Virtual device is running${NC}"
    echo -e "${YELLOW}- USB debugging is enabled (for physical devices)${NC}"
    echo -e "${YELLOW}- Device is connected and authorized${NC}"
    echo ""
    
    echo -e "${CYAN}Available AVDs:${NC}"
    emulator -list-avds
    echo ""
    echo -e "${YELLOW}To start an emulator, run: emulator -avd [AVD_NAME]${NC}"
    read -p "Press Enter to continue"
    exit 1
fi

echo -e "${GREEN}Found device: $DEVICE_ID${NC}"
echo ""

echo -e "${GREEN}[2/6] Cleaning previous build...${NC}"
./gradlew clean

echo -e "${GREEN}[3/6] Building debug APK...${NC}"
./gradlew assembleDebug

echo -e "${GREEN}[4/6] Installing APK on device...${NC}"
./gradlew installDebug

echo -e "${GREEN}[5/6] Starting DNSFlip app...${NC}"
adb shell am start -n com.mjryan253.dnsflip/.MainActivity || {
    echo -e "${YELLOW}WARNING: Could not start app automatically${NC}"
    echo -e "${YELLOW}Please start the app manually from the device${NC}"
}

echo -e "${GREEN}[6/6] Deployment complete!${NC}"
echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}DEPLOYMENT COMPLETE!${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
echo -e "${WHITE}Device: $DEVICE_ID${NC}"
echo -e "${WHITE}App: DNSFlip (com.mjryan253.dnsflip)${NC}"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo -e "${WHITE}1. The app should now be running on your device${NC}"
echo -e "${WHITE}2. Test DNS switching functionality${NC}"
echo -e "${WHITE}3. Check error messages and troubleshooting features${NC}"
echo -e "${WHITE}4. Use the troubleshooting button for diagnostics${NC}"
echo ""

echo -e "${GREEN}Opening logcat for monitoring...${NC}"
echo -e "${YELLOW}Press Ctrl+C to stop logcat monitoring${NC}"
echo ""

# Start logcat with DNSFlip-specific filtering
adb logcat -s DNSManager:V ShizukuManager:V MainActivity:V ActivityManager:I System.err:W

echo ""
echo -e "${GREEN}Deployment script completed.${NC}"
read -p "Press Enter to continue"
