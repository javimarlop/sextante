from PyQt4 import QtCore
from PyQt4.QtGui import *
import time
from sextante.core.GeoAlgorithmExecutionException import GeoAlgorithmExecutionException

class AlgorithmExecutor:

    @staticmethod
    def runalg(alg, progress):
        try:
            alg.execute(progress)
        except GeoAlgorithmExecutionException, e :
            QMessageBox.critical(self, "Error",e.msg)
        finally:
            progress.setFinished()


    @staticmethod
    def runbatch(algs, progress):
        try:
            for alg in algs:
                progress.addText(alg.getAsCommand())
                AlgorithmExecutor.runalg(alg, SilentProgress())
                progress.addText("Execution OK!")
        except GeoAlgorithmExecutionException, e :
            QMessageBox.critical(self, "Error",e.msg)
            progress.addText("Execution Failed")
        finally:
            progress.setFinished()


        #=======================================================================
        # th = RunAlgorithmThread(alg, progress)
        # th.start()
        # th.wait()
        #=======================================================================

class RunAlgorithmThread(QtCore.QThread):

    def __init__(self, alg, progress):
        self.alg = alg
        self.progress = progress
        QtCore.QThread.__init__(self)

    def run(self):
        for i in range(2):
            time.sleep(3)
            self.progress.addText(str(i))
            self.progress.setPercentage(i*50)
        #self.alg.execute(self.progress)

class SilentProgress():

    def addText(self, text):
        pass

    def setPercentage(self, i):
        pass

    def setFinished(self):
        pass