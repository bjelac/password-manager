# Password Manager

Password Manager allow us to store our login data safely encrypted on our device without
need for internet connection or cloud service. User has full control over his data.
Data are encrypted with AES 128 bit encryption witch encryption keys are randomly 
generated and stored in Android KeyStore.

There is no advertising in this app.

## Installation

The easy way to install this app is over [Android Play Store](https://play.google.com/store/apps/details?id=com.bjelac.passwordmanager). 

You can also clone this project and build .apk with Android Studio, 
and than copy it to your device.

## Debug
To debug with Android Studio remove comment in LoggerUtils class on line 7 in com.bjelac.passwordmanager.utils package.
I commented logger out so no logs are written on device.

## Usage

On first start after installation, we need to set master password witch is 
used if fingerprint authentication is not available or if finger can not 
be recognized. 

If master password is given wrong five times at login screen, all app data will
be deleted from device!

After successful login, list with data will be shown.
We can add new login data on plus button or menage existing ones.

It is possible to change master password in settings tab, menage backups 
or delete all data from device. There is also button that shows app information.


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.


## License
[MIT](https://choosealicense.com/licenses/mit/)