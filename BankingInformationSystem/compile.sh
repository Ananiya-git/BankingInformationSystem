#!/bin/bash
# Compiles all Java source files into the bin/ directory.
set -e
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
rm sources.txt
echo "Build successful. Class files are in bin/"
