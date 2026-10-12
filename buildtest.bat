@echo off
setlocal enabledelayedexpansion

set JUNIT_JAR=lib\junit-platform-console-standalone-6.0.3.jar
set "MAIN_SOURCES="
set "TEST_SOURCES="

for /r src\main %%f in (*.java) do set MAIN_SOURCES=!MAIN_SOURCES! "%%f"
for /r src\test %%f in (*.java) do set TEST_SOURCES=!TEST_SOURCES! "%%f"

javac --class-path src\main\java;%JUNIT_JAR% !MAIN_SOURCES! !TEST_SOURCES!