@echo off
title IntelliFind System
chcp 65001 >nul

if not exist bin mkdir bin
if not exist data mkdir data

echo [1/2] Compiling Java source files...
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    del sources.txt
    pause
    exit /b %errorlevel%
)
del sources.txt

echo [2/2] Launching IntelliFind CLI...
echo.
java -cp bin com.intellifind.Main
pause