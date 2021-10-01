# Finn 
[![Author](https://img.shields.io/static/v1?label=@author&message=Eduardo%20Santos&color=navy)](https://github.com/edufelip)
[![LinkedIn](https://img.shields.io/static/v1?label=@linkedin&message=@edu_santos&color=blue)](https://www.linkedin.com/in/eduardo-felipe-dev/)

A network of communities where people can share their interests!

## Layouts
<br>
  <p align="left">
            <img alt="splash screen"
            src="https://scontent.fgig4-1.fna.fbcdn.net/v/t1.6435-9/242819402_6096295977108687_8545961455669866025_n.jpg?_nc_cat=105&ccb=1-5&_nc_sid=0debeb&_nc_eui2=AeGVlhSF4opQsQqDLiwnOh6156Z1rMosFCnnpnWsyiwUKRaS4lzuUjgbXEYrYEqCHVYyg4BHXZ9S67wZ-vvf96SE&_nc_ohc=ikMMjjDRpJgAX8m20CC&_nc_ht=scontent.fgig4-1.fna&oh=a69f19e34976956f7ec1ba974da90349&oe=6170C174" width="20%" 
            title="splash screen">
            <img alt="auth screen"
            src="https://scontent.fgig4-1.fna.fbcdn.net/v/t1.6435-9/242595259_6096295807108704_2500277214651690641_n.jpg?_nc_cat=106&ccb=1-5&_nc_sid=0debeb&_nc_eui2=AeE_mSCex-MsFdmC1mTrD08BZrIJaxNhO8ZmsglrE2E7xl6hrAHHDryHnoE3zaORgxjG9VQfzO-5AZvdl8SbYFRW&_nc_ohc=hvPxazuNLtAAX90QRZN&_nc_ht=scontent.fgig4-1.fna&oh=77c73826834d6b7b29d7001cce251936&oe=61733A9D" width="20%" 
            title="auth screen">
            <img alt="main screen"
                 src="https://scontent.fgig4-1.fna.fbcdn.net/v/t1.6435-9/242662295_6096295783775373_9182471683835889129_n.jpg?_nc_cat=101&ccb=1-5&_nc_sid=0debeb&_nc_eui2=AeF545rf6cpFq2ijx4fQT-cmH9fwRqN5YhQf1_BGo3liFOFciGGv8t1BVwHgNrETL5XqZJvlJ-g5R7THr9YfyJIk&_nc_ohc=Vmp3YAXKvngAX9LCMTh&tn=IWUZvjl-ILXjS8Xi&_nc_ht=scontent.fgig4-1.fna&oh=31dc8f77b9c83e4738e4587734098305&oe=61717383" width="20%" title="main screen">
            <img alt="communities screen"
                 src="https://scontent.fgig4-1.fna.fbcdn.net/v/t1.6435-9/242698481_6096295797108705_379463376871528690_n.jpg?_nc_cat=105&ccb=1-5&_nc_sid=0debeb&_nc_eui2=AeGfTZhxpl4bZo7xeqYWUtG1_EJgC-cg2P_8QmAL5yDY_xp3OpRG3cpC3l7wdTS5RjS8-VCa6o1uAREVQK-orl4C&_nc_ohc=-skOZfaXseEAX9InkHD&tn=IWUZvjl-ILXjS8Xi&_nc_ht=scontent.fgig4-1.fna&oh=4646f268a353ca3aab3bb42475d39d35&oe=61711AB4" width="20%" title="communities screen">
            <img alt="community screen"
                 src="https://scontent.fgig4-1.fna.fbcdn.net/v/t1.6435-9/242671536_6096295743775377_345482850362305823_n.jpg?_nc_cat=108&ccb=1-5&_nc_sid=0debeb&_nc_eui2=AeF-TvgG59X5q6BmDNPKNOlMEV1EyN0c3SgRXUTI3RzdKIdV3DmF-GjxW1mC1yPuZe7_LrWgvjp4wmH6asQGqq_B&_nc_ohc=M0Xc0I2yf1kAX9q9i76&_nc_ht=scontent.fgig4-1.fna&oh=7d737658aca99846be54fcb1fdf65c22&oe=61719E8E" width="20%" title="community screen">

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
