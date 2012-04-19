import os
from PyQt4 import QtGui
from sextante.parameters.ParameterString import ParameterString
from sextante.outputs.OutputVector import OutputVector
from sextante.lastools.LasToolsUtils import LasToolsUtils
from sextante.lastools.LasToolsAlgorithm import LasToolsAlgorithm
from sextante.parameters.ParameterFile import ParameterFile

class las2shp(LasToolsAlgorithm):

    INPUT = "INPUT"
    OUTPUT = "OUTPUT"

    def getIcon(self):
        filepath = os.path.dirname(__file__) + "/../images/tool.png"
        return QtGui.QIcon(filepath)

    def defineCharacteristics(self):
        self.name = "las2shp"
        self.group = "Tools"
        self.addParameter(ParameterFile(las2shp.INPUT, "Input las layer", ""))
        self.addOutput(OutputVector(las2shp.OUTPUT, "Output shp layer"))
        self.addCommonParameters()

    def processAlgorithm(self, progress):
        commands = [os.path.join(LasToolsUtils.LasToolsPath(), "bin", "las2shp.exe")]
        commands.append("-i")
        commands.append(self.getParameterValue(las2shp.INPUT))
        commands.append("-o")
        commands.append(self.getOutputValue(las2shp.OUTPUT))
        self.addCommonParameterValuesToCommand(commands)


        LasToolsUtils.runLasTools(commands, progress)
