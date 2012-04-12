from sextante.parameters.Parameter import Parameter
from sextante.core.SextanteUtils import SextanteUtils
from sextante.core.QGisLayers import QGisLayers

class ParameterDataObject(Parameter):

    def getValueAsCommandLineParameter(self):
        if self.value == None:
            return str(None)
        else:
            if not SextanteUtils.isWindows():
                return "\"" + str(self.value) + "\""
            else:
                return "\"" + str(self.value).replace("\\", "\\\\") + "\""