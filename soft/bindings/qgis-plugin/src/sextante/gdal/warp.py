from PyQt4 import QtGui
from sextante.core.GeoAlgorithm import GeoAlgorithm
from sextante.parameters.ParameterRaster import ParameterRaster
from sextante.outputs.OutputRaster import OutputRaster
import os
from sextante.gdal.GdalUtils import GdalUtils
from sextante.parameters.ParameterString import ParameterString
from sextante.parameters.ParameterSelection import ParameterSelection

class warp(GeoAlgorithm):

    INPUT = "INPUT"
    OUTPUT = "OUTPUT"
    SOURCE_SRS = "SOURCE_SRS"
    DEST_SRS = "DEST_SRS "
    METHOD = "METHOD"
    METHOD_OPTIONS = ["near", "bilinear", "cubic", "cubicspline", "lanczos"]

    def getIcon(self):
        filepath = os.path.dirname(__file__) + "/icons/warp.png"
        return QtGui.QIcon(filepath)

    def defineCharacteristics(self):
        self.name = "warp"
        self.group = "Projections"
        self.addParameter(ParameterRaster(warp.INPUT, "Input layer", False))
        self.addParameter(ParameterString(warp.SOURCE_SRS, "Source SRS (EPSG Code)", "4326"))
        self.addParameter(ParameterString(warp.DEST_SRS, "Destination SRS (EPSG Code)", "4326"))
        self.addParameter(ParameterSelection(warp.METHOD, "Resampling method", warp.METHOD_OPTIONS))
        self.addOutput(OutputRaster(warp.OUTPUT, "Output layer"))

    def processAlgorithm(self, progress):
        commands = ["gdalwarp"]
        commands.append("-s_srs")
        commands.append("EPSG:" + self.getParameterValue(warp.SOURCE_SRS))
        commands.append("-t_srs")
        commands.append("EPSG:" + self.getParameterValue(warp.DEST_SRS))
        commands.append("-r")
        commands.append(warp.METHOD_OPTIONS[self.getParameterValue(warp.METHOD)])
        commands.append("-of")
        out = self.getOutputValue(warp.OUTPUT)
        commands.append(GdalUtils.getFormatShortNameFromFilename(out))
        commands.append(self.getParameterValue(warp.INPUT))
        commands.append(out)

        GdalUtils.runGdal(commands, progress)