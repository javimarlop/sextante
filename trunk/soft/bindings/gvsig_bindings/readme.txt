---------------------------------------------------
Instructions for installing SEXTANTE for gvSIG
---------------------------------------------------

- Locate the folder where you have installed gvSIG CE. Under Windows, it is usually something like "C:\Program Files\gvSIG_CE"
- Locate the extensions folder at [gvSIG CE folder]/bin/gvSIG/extensiones
- Under the extensions folder, create a new folder named es.unex.sextante under it
- Copy the gvSIG binding files to that folder
- Copy the SEXTANTE core files to that folder, except jts-1.12.jar

-To install the context help files, copy the content of the help folder to a new folder named help under the es.unex.sextante folder

Although this bindings have been developed to work with gvSIG CE, they can also be used with the official version of gvSIG. gvSIG 1.11 or higher (but not 2.0, only 1.x) is needed.

And additional step is required to install SEXTANTE on gvSIG 1.x:

-Delete the following files from  [gvSIG 1.x Folder]/bin/gvSIG/extensiones/com.iver.cit.gvsig/lib
     -jts-1.9_gvSIG.jar
     -jtsio-1.8.jar

Notice that, when, when running on the official gvSIG distribution, some SEXTANTE features might not be available, since they require elements of gvSIG CE not implemented in the official version of gvSIG.

Check our website for prepared packages for gvsig (.gvspkg), which make the installation easier than described above.



