from pydrive.auth import GoogleAuth
from pydrive.drive import GoogleDrive
import httplib2
import pickle

gauth = GoogleAuth()
gauth.settings = {
    'client_config_backend': 'settings',
    'client_config_file' :'file',
    'save_credentials' : 'true',
    'save_credentials_backend' : 'file',
    'save_credentials_file' : 'Credentials.txt',
    'get_refresh_token' : True,
    'oauth_scope' : ['https://www.googleapis.com/auth/drive'],
    'client_config' : {
        'client_id':pickle.load(open('client_id', 'rb')),
        'auth_uri':'https://accounts.google.com/o/oauth2/auth',
        'token_uri':'https://accounts.google.com/o/oauth2/token',
        'client_secret':pickle.load(open('client_secret', 'rb')),
        'redirect_uri': 'http://localhost',
        'revoke_uri' : 'https://accounts.google.com/o/oauth2/revoke'
        }
}

gauth.LoadClientConfigSettings()
gauth.LoadCredentials()
gauth.LocalWebserverAuth()