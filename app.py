from __future__ import print_function

from pydrive.auth import GoogleAuth
from pydrive.drive import GoogleDrive
import httplib2
import zipfile
import sys
import os
import time
import hashlib

gauth = GoogleAuth()
drive = GoogleDrive(gauth)
GDZipFile = drive.CreateFile()
oldHash = None

def authenticate():
    gauth.settings = {
        'client_config_backend': 'settings',
        'client_config_file' :'file',
        'save_credentials' : 'true',
        'save_credentials_backend' : 'file',
        'save_credentials_file' : 'Credentials.txt',
        'get_refresh_token' : True,
        'oauth_scope' : ['https://www.googleapis.com/auth/drive'],
        'client_config' : {
            # 'client_id':pickle.load(open('client_id', 'rb'))
            'client_id':'1069969218802-lms0do78nc6hsqkp88ltaesf9ve0dn1p.apps.googleusercontent.com',
            'auth_uri':'https://accounts.google.com/o/oauth2/auth',
            'token_uri':'https://accounts.google.com/o/oauth2/token',
            # 'client_secret':pickle.load(open('client_secret', 'rb')),
            'client_secret':'Nws7EkTv6SI5j2D8fJMNzRpp',
            'redirect_uri': 'http://localhost',
            'revoke_uri' : 'https://accounts.google.com/o/oauth2/revoke'
            }
    }

    gauth.LoadClientConfigSettings()
    # gauth.LoadCredentials()
    gauth.http = httplib2.Http(ca_certs='cacert.pem')
    gauth.LocalWebserverAuth()

def zip_folder(folder_path, output_path):
    parent_folder = os.path.dirname(folder_path)
    # Retrieve the paths of the folder contents.
    contents = os.walk(folder_path)
    try:
        zip_file = zipfile.ZipFile(output_path, 'w', zipfile.ZIP_DEFLATED)
        for root, folders, files in contents:
            # Include all subfolders, including empty ones.
            for folder_name in folders:
                absolute_path = os.path.join(root, folder_name)
                relative_path = absolute_path.replace(parent_folder + '\\','')
                # print "Adding '%s' to archive." % absolute_path
                zip_file.write(absolute_path, relative_path)
            for file_name in files:
                absolute_path = os.path.join(root, file_name)
                relative_path = absolute_path.replace(parent_folder + '\\','')
                # print "Adding '%s' to archive." % absolute_path
                zip_file.write(absolute_path, relative_path)
        print ("Archive '{0}' created successfully.".format(output_path))
    except IOError, message:
        print (message)
        sys.exit(1)
    except OSError, message:
        print (message)
        sys.exit(1)
    except zipfile.BadZipfile, message:
        print (message)
        sys.exit(1)
    finally:
        zip_file.close()


def upload(info):
    fileName = info['projectName'] + ".zip"
    zip_folder(info['dirName'] , info['projectName'] + ".zip")
    global GDZipFile
    GDZipFile['mimeType'] = 'application/zip'
    GDZipFile['title'] = info['projectName'] + time.strftime("_%x_%X") + ".zip"
    GDZipFile['description'] = info['description']
    GDZipFile.SetContentFile(info['projectName']  +".zip")

    if changed(fileName):
        print("Uploading...")
        GDZipFile.Upload()
        print("File Uploaded\n")
    else:
        return

def changed(fileName):
    zipFile = open(fileName , 'rb')
    buff = zipFile.read(128)
    sha = hashlib.sha256()
    while len(buff) > 0:
        sha.update(buff)
        buff = zipFile.read(128)
    newHash = sha.hexdigest()
    # print newHash
    # print oldHash
    global oldHash
    if newHash == str(oldHash):
        print( "No changes made, not uploading\n")
        # print newHash
        oldHash = newHash
        return False
    else:
        oldHash = newHash
        return True

        
# raw_input("File Uploaded, press any key to exit")

if __name__ == '__main__' :
    print( "Welcome to Auto Upload to Drive program!")
    print ("First we'd like to collect some information about the project")

    zip_info = {
        'projectName' : raw_input("\tWhat is the name of the project?: "),
        'dirName' : raw_input("\tWhere is the location?: ").strip("\"") ,
        'description' : raw_input("\tPlease enter something about your project, for searching in GDrive\n\t(What it does...)[Optional]: "),
        'interval' : int(raw_input("\tPlease enter Upload interval, in seconds (choose reasonably)\n\tEg:300: "))
    }
    authenticate()
    try:
        for i in range(1,100 * 60 * 35):
            print( "File #{}".format(i))
            upload(zip_info)
            print( "Waiting for next turn.")
            time.sleep(zip_info['interval'])
    except KeyboardInterrupt, messgae:
        print ("Exit requested, Exiting now")
        sys.exit(0)