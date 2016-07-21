from Gui import main_ui
from PyQt5.QtWidgets import QApplication, QMainWindow, QFileDialog, QMessageBox
import os
import sys

class MainWindow(QMainWindow):
    def __init__(self):
        super(MainWindow, self).__init__()

        self.ui = main_ui.Ui_MainWindow()
        self.ui.setupUi(self)


app = QApplication(sys.argv)
window = MainWindow()
window.show()
sys.exit(app.exec_())
