@echo off
cd /d %~dp0\..
if exist out-test rmdir /s /q out-test
mkdir out-test
if exist sources-test.txt del sources-test.txt
for /r src\main\java %%f in (*.java) do @echo "%%f">>sources-test.txt
for /r src\test\java %%f in (*.java) do @echo "%%f">>sources-test.txt
javac -encoding UTF-8 -d out-test @sources-test.txt
if errorlevel 1 exit /b 1
del sources-test.txt
java -ea -cp out-test com.petcarehub.SmokeTest
