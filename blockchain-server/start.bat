@echo off
start start-ngrok.bat
start mvn spring-boot:run
adb-wifi
echo Enter Your IP
set /p IP=
echo Enter Your PORT
set /p PORT=