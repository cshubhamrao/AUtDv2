from distutils.core import setup
import py2exe

setup( console=[
		{"script": "app.py"}],
	   options={
	   	"py2exe":
	   		{"includes":["BaseHTTPServer"]}},
	   	data_files=[ ("",["cacert.pem",])] )