from Gui import main_ui
import sys
from PyQt5.QtWidgets import QApplication, QMainWindow

app = QApplication(sys.argv)
MainWindow = QMainWindow()
ui = main_ui.Ui_MainWindow()
ui.setupUi(MainWindow)
MainWindow.show()
sys.exit(app.exec_())
