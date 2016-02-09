## `react-native-android-sms-listener` [![react-native-android-sms-listener](https://badge.fury.io/js/react-native-android-sms-listener.svg)](https://badge.fury.io/js/react-native-android-sms-listener)
An utility that allows you to listen for incoming SMS messages. You'll probably use it if you're developing an application and in its sign up process you have phone number verification step which is usually done by sending a verification code via SMS to the specified number, so when the user receive the message with the code you can automatically intercept it and verify it &mdash; what an awesome UX huh?

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

`SmsListener#addListener` returns a `CancellableSubscription` so if you want to stop listening for incoming SMS messages you can simply `.remove` it:

```JS
let subscription = SmsListener.addListener(...)

subscription.remove()
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
  '../node_modules/react-native-android-sms-listener/android'
)
```

*android/app/build.gradle*
```Gradle
dependencies {
  compile project(':ReactNativeAndroidSmsListener')
  // (...)
}
```

*MainActivity.java*
```Java
import com.centaurwarchief.smslistener.SmsListener;
```

```Java
@Override
protected List<ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
    new MainReactPackage(),
    new SmsListener(this)
    // (...)
  );
}
```
