set HOME=%USERPROFILE%
set PATH=%GRASSDIR%\msys\bin;%PATH%
set PATH=%GRASSDIR%\extrabin;%GRASSDIR%\extralib;%PATH%
set PATH=%GRASSDIR%\tcl-tk\bin;%GRASSDIR%\sqlite\bin;%GRASSDIR%\gpsbabel;%PATH%
set GRASS_HTML_BROWSER=%PROGRAMFILES%/Internet Explorer/iexplore.exe
set GRASS_PROJSHARE=%GRASSDIR%\proj
set PYTHONHOME=%GRASSDIR%\Python25
set WINGISBASE=%GRASSDIR%
set GISBASE=%GRASSDIR%
set SAVEPATH=%PATH%
set PATH=%WINGISBASE%\bin;%WINGISBASE%\lib;%PATH%
set GISRC=%HOME%\.grassrc6

g.gisenv set="MAPSET=%MAPSET%"
g.gisenv set="LOCATION=%LOCATION_NAME%"
g.gisenv set="LOCATION_NAME=%LOCATION_NAME%"
g.gisenv set="GISDBASE=%GISDBASE%"

set GRASS_VERSION=6.4.0RC5

set GRASS_PAGER=more
if "%GRASS_WISH%"=="" set GRASS_WISH=wish.exe
if "%GRASS_SH%"=="" set GRASS_SH=c:\msys\1.0\bin\sh.exe
if "%GRASS_HTML_BROWSER%"=="" set GRASS_HTML_BROWSER=%SYSTEMDRIVE%/PROGRA~1/INTERN~1/IEXPLORE.EXE
if "%GRASS_PROJSHARE%"=="" set GRASS_PROJSHARE=/c/Programs/GIS/OSGeo4W/share/proj

set PATHEXT=%PATHEXT%;.PY

g.gisenv "set=GRASS_GUI=text"

call "%GRASS_BATCH_JOB%"

"%WINGISBASE%\etc\clean_temp" > NUL:

set PATH=%SAVEPATH%
set SAVEPATH=

rem exit