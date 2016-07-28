from Gui import main_ui
from PyQt5.QtWidgets import QApplication, QMainWindow, QFileDialog, QMessageBox

import os
import sys


class MainWindow(QMainWindow):
    def __init__(self):
        super(MainWindow, self).__init__()

        self.ui = main_ui.Ui_MainWindow()
        self.ui.setupUi(self)
        self.setup_handlers()

    def setup_handlers(self):
        self.ui.btn_start.pressed.connect(self.handle_start)
        self.ui.btn_signIn.pressed.connect(self.handle_signIn)
        self.ui.btn_browse.pressed.connect(self.handle_browse)
        self.ui.btn_nb.pressed.connect(self.hold_on)
        self.ui.btn_mysql.pressed.connect(self.hold_on)

    def handle_start(self):
        self.hold_on()

    def handle_signIn(self):
        QApplication.instance().exit()

    def handle_nb(self):
        self.hold_on()

    def handle_browse(self):
        f = QFileDialog.getExistingDirectory(parent=self,
                                             caption="Choose Directory",
                                             directory=os.path.expanduser('~/Documents'),
                                             options=QFileDialog.ShowDirsOnly | QFileDialog.DontUseNativeDialog)
        self.ui.txt_folderLocation.setText(f)

    def hold_on(self):
        QMessageBox.information(self,
                                "Hold On",
                                "Wait this is just a preview!!\n"
                                "I'm still working on adding the functionality :P")
        self.ui.lcd_upload.display(self.ui.lcd_upload.intValue() + 1)


app = QApplication(sys.argv)
window = MainWindow()
window.show()
sys.exit(app.exec_())
