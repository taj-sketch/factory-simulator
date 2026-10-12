@echo off
setlocal EnableDelayedExpansion

set "MAIN_SOURCES="

for /r src\main %%f in (*.java) do set MAIN_SOURCES=!MAIN_SOURCES! "%%f"

javac !MAIN_SOURCES!