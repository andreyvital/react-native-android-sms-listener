## `react-native-android-sms-listener`
An utility that allows you to add listeners for incoming SMS messages.

### Example
```JS
import SmsListener from 'react-native-android-sms-listener'

SmsListener.addListener(message => {
  console.info(message)
})
```

The contents of `message` object will be:
```JS
{
  originatingAddress: string,
  body: string
}
```

### Installation
```SH
$ npm install --save react-native-android-sms-listener
```

...and all you need to do to use this so-called utility is:
*android/settings.gradle*
```Gradle
include ':ReactNativeAndroidSmsListener'

project(':ReactNativeAndroidSmsListener').projectDir = new File(
  rootProject.projectDir,
  '../node_modules/react-native-android-sms-listener/app'
)
```

*android/app/build.gradle*
```Gradle
dependencies {
  compile project(':ReactNativeAndroidSmsListener')
  // (...)
}
```

```Java
import com.centaurwarchief.smslistener.SmsListener;

@Override
protected List<ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
    new MainReactPackage(),
    new SmsListener(this)
    // (...)
  );
}
```
