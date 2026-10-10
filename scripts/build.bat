@echo off
cd /d %~dp0\..
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist
if exist sources.txt del sources.txt
for /r src\main\java %%f in (*.java) do @echo "%%f">>sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 exit /b 1
del sources.txt
>MANIFEST.MF echo Main-Class: com.petcarehub.app.Main
jar cfm dist\petcarehub.jar MANIFEST.MF -C out .
del MANIFEST.MF
echo Built dist\petcarehub.jar
