#!/bin/bash
# Runs the application. Must be run after compile.sh, and from the
# project's root folder so the data/ persistence folder is found correctly.
set -e
java -cp bin com.bank.Main
