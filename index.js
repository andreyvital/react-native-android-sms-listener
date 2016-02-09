/* @flow */
import type CancellableSubscription from './CancellableSubscription'
import type ReceivedSmsMessage from './ReceivedSmsMessage'
import { DeviceEventEmitter } from 'react-native'

const SMS_RECEIVED_EVENT = 'com.centaurwarchief.smslistener:smsReceived'

export default {
  addListener(
    listener: (message: ReceivedSmsMessage) => void
  ): CancellableSubscription {
    return DeviceEventEmitter.addListener(
      SMS_RECEIVED_EVENT,
      listener
    )
  }
}
