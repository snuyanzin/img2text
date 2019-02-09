@echo off
:: img2text.bat - Windows script to launch drawing shell
java -cp "%~dp0\..\target\*" com.nuyanzin.img2text.Image2Text %*

:: End img2text.bat
