@echo off
cd /d %~dp0\..
if exist out rmdir /s /q out
mkdir out
if exist sources.txt del sources.txt
for /r src\main\java %%f in (*.java) do @echo "%%f">>sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 exit /b 1
del sources.txt
java -cp out com.petcarehub.app.Main
