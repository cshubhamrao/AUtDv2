from distutils.core import setup
import py2exe


setup( console=[
		{"script": "app.py"}],
	   options={
	   	"py2exe":
	   		{
	   			"includes":["BaseHTTPServer"],
	   			'bundle_files': 1, 
	   			'compressed': True
	   		}},
	   	data_files=[ ("",["cacert.pem",])],
	   	zipfile = None
	)