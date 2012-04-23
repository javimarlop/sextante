from PyQt4 import QtCore, QtGui
from PyQt4.QtCore import *
from PyQt4.QtGui import *
from sextante.script.ScriptUtils import ScriptUtils
from sextante.gui.HelpEditionDialog import HelpEditionDialog
import pickle

class EditScriptDialog(QtGui.QDialog):
    def __init__(self, alg):
        self.alg = alg
        QtGui.QDialog.__init__(self)
        self.setModal(True)
        self.setupUi()
        self.update = False
        self.help = None

    def setupUi(self):
        self.resize(600,400)
        self.setWindowTitle("Edit script")
        layout = QVBoxLayout()
        self.text = QtGui.QTextEdit()
        self.text.setObjectName("text")
        self.text.setEnabled(True)
        self.buttonBox = QtGui.QDialogButtonBox()
        self.buttonBox.setOrientation(QtCore.Qt.Horizontal)
        if self.alg != None:
            self.text.setText(self.alg.script)
            self.editHelpButton = QtGui.QPushButton()
            self.editHelpButton.setText("Edit model help")
            self.buttonBox.addButton(self.editHelpButton, QtGui.QDialogButtonBox.ActionRole)
            QObject.connect(self.editHelpButton, QtCore.SIGNAL("clicked()"), self.editHelp)
        self.saveButton = QtGui.QPushButton()
        self.saveButton.setText("Save")
        self.buttonBox.addButton(self.saveButton, QtGui.QDialogButtonBox.ActionRole)
        self.closeButton = QtGui.QPushButton()
        self.closeButton.setText("Close")
        self.buttonBox.addButton(self.closeButton, QtGui.QDialogButtonBox.ActionRole)
        QObject.connect(self.saveButton, QtCore.SIGNAL("clicked()"), self.saveAlgorithm)
        QObject.connect(self.closeButton, QtCore.SIGNAL("clicked()"), self.cancelPressed)
        layout.addWidget(self.text)
        layout.addWidget(self.buttonBox)
        self.setLayout(layout)
        QtCore.QMetaObject.connectSlotsByName(self)


    def editHelp(self):
        dlg = HelpEditionDialog(self.alg)
        dlg.exec_()
        #We store the description string in case there were not saved because there was no
        #filename defined yet
        if self.alg.descriptionFile is None and dlg.descriptions:
            self.help = dlg.descriptions


    def saveAlgorithm(self):
        if self.alg!=None:
            filename = self.alg.descriptionFile
        else:
            filename = QtGui.QFileDialog.getSaveFileName(self, "Save Script", ScriptUtils.scriptsFolder(), "Python scripts (*.py)")
        if filename:
            text = self.text.toPlainText()
            fout = open(filename, "w")
            fout.write(text)
            fout.close()
            self.update = True
            self.close()

        #if help strings were defined before saving the model for the first time, we do it here
        if self.help:
            f = open(self.alg.descriptionFile + ".help", "wb")
            pickle.dump(self.help, f)
            f.close()
            self.help = None

    def cancelPressed(self):
        self.update = False
        self.close()