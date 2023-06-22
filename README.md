# Finn 
[![Author](https://img.shields.io/static/v1?label=@author&message=Eduardo%20Santos&color=navy)](https://github.com/edufelip)
[![LinkedIn](https://img.shields.io/static/v1?label=@linkedin&message=@edu_santos&color=blue)](https://www.linkedin.com/in/eduardo-felipe-dev/)

A social media based on communities where people can share their interests!

This project uses:
* MVVM Architecture and LiveData
* Retrofit for API calls
* RxJava
* Dagger Hilt
* Glide
* Firebase Authentication

## Layouts
<br>
  <p align="left">
            <img alt="splash screen"
            src="https://i.imgur.com/RCSzAUT.jpg" width="20%" 
            title="splash screen">
            <img alt="auth screen"
            src="https://i.imgur.com/4r6XpdU.jpg" width="20%" 
            title="auth screen">
            <img alt="main screen"
            src="https://i.imgur.com/nx57o9y.jpg" width="20%" title="main screen">
            <img alt="communities screen"
            src="https://i.imgur.com/vxqmm3l.jpg" width="20%" title="communities screen">
            <img alt="community screen"
            src="https://i.imgur.com/nx57o9y.jpg" width="20%" title="community screen">

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone https://github.com/edufelip/finn.git
```

## Configuration
### Keystores:
Create `gradle.properties` with the following info:
```gradle
BACKEND_IP="..."
FIREBASE_GOOGLE_ID="..."
```
And set these keys properly in `build.gradle` (Module):
```
android {
  defaultConfig {
    buildConfigField("String", "BACKEND_IP", BACKEND_IP)
    buildConfigField("String", "FIREBASE_GOOGLE_ID", FIREBASE_GOOGLE_ID)
  }
}
```
And then run "Rebuild Project"

## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed bundle/APK...***
3. Follow the instructions and fill in the keystore information

## Maintainers
This project is mantained by:
* [Eduardo Felipe](http://github.com/edufelip)


## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Push your branch (git push origin my-new-feature)
5. Create a new Pull Request
