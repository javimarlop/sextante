rm ../xml/en/general/*.*
cd ./en/IntroductionToSEXTANTE
latex2html -split 3 -nobottom_navigation -notop_navigation -no_navigation -noinfo -dir ../../../xml/en/general IntroductionToSEXTANTE.tex
cd ../../../xml/en/general
mv node3.html intro.html
mv node4.html toolbox.html
mv node5.html modeler.html
mv node6.html commandline.html
mv node7.html batch.html
mv node8.html history.html



