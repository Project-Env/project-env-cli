Invoke-WebRequest https://nav.dl.sourceforge.net/project/nsis/NSIS%203/3.08/nsis-3.08-setup.exe -OutFile C:\WINDOWS\Temp\nsis-3.08-setup.exe
Invoke-Expression "& C:\WINDOWS\Temp\nsis-3.08-setup.exe \S"

Invoke-WebRequest https://nsis.sourceforge.io/mediawiki/images/7/7f/EnVar_plugin.zip -OutFile C:\WINDOWS\Temp\EnVar_plugin.zip
Expand-Archive "C:\WINDOWS\Temp\EnVar_plugin.zip" -DestinationPath "C:\Program Files (x86)\NSIS" -Force