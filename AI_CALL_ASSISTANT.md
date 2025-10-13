# AI Call Assistant Feature

## Overview

The AI Call Assistant is a feature that handles incoming and outgoing calls on behalf of the user. When enabled, it automatically answers incoming calls and plays a greeting message to inform callers that the user will be available shortly.

## Key Features

- **Auto-answer Incoming Calls**: Automatically accepts incoming Signal calls when enabled
- **Text-to-Speech Greeting**: Plays a customizable greeting message using Android's Text-to-Speech engine
- **Outbound Call Support**: Plays greeting when outgoing calls connect
- **Non-invasive Integration**: Built on top of existing WebRTC call infrastructure
- **No Additional Permissions**: Uses existing Signal telecom permissions
- **Disabled by Default**: Must be explicitly enabled by the user

## Architecture

The AI Call Assistant consists of the following components:

### CallAssistantManager
Located at: `app/src/main/java/org/thoughtcrime/securesms/service/webrtc/CallAssistantManager.java`

This is the main class that manages the AI assistant functionality:
- Initializes and manages Android's TextToSpeech engine
- Provides methods to enable/disable the assistant
- Handles playing greeting messages for both incoming and outgoing calls
- Manages TTS lifecycle and cleanup

### Integration Points

1. **WebRtcInteractor**: Provides access to the CallAssistantManager instance for all call processors
2. **IncomingCallActionProcessor**: Checks if AI assistant is enabled and auto-answers incoming calls
3. **CallSetupActionProcessorDelegate**: Plays greeting message when outgoing calls connect

## Usage

### Enabling the AI Assistant

```java
// Get the CallAssistantManager from WebRtcInteractor
CallAssistantManager assistant = webRtcInteractor.getCallAssistantManager();

// Enable the AI assistant
assistant.setEnabled(true);
```

### Disabling the AI Assistant

```java
CallAssistantManager assistant = webRtcInteractor.getCallAssistantManager();
assistant.setEnabled(false);
```

### Customizing the Greeting Message

The default greeting message is: "Hello, I will be right with you. Please hold."

To customize this, you can modify the `DEFAULT_GREETING` constant in `CallAssistantManager.java` or extend the class to load custom messages from user preferences.

## Technical Details

### Call Flow - Incoming Calls

1. Incoming call arrives and `IncomingCallActionProcessor.handleLocalRinging()` is called
2. CallAssistantManager checks if assistant is enabled via `shouldAutoAnswerIncomingCall()`
3. If enabled, the call is automatically accepted using `handleAcceptCall()`
4. AI greeting message is played via `playIncomingCallGreeting()`
5. Caller hears the greeting message through Text-to-Speech

### Call Flow - Outgoing Calls

1. Outgoing call connects and `CallSetupActionProcessorDelegate.handleCallConnected()` is called
2. CallAssistantManager checks if assistant is enabled via `shouldHandleOutgoingCall()`
3. If enabled, AI greeting message is played via `playOutgoingCallGreeting()`
4. Call recipient hears the greeting message through Text-to-Speech

### Text-to-Speech Configuration

The AI assistant uses Android's built-in TextToSpeech engine with the following settings:
- Default language: System default locale (falls back to English US if not supported)
- Audio stream: `AudioManager.STREAM_VOICE_CALL` for proper call audio routing
- Mode: Queue flush to ensure immediate playback

## Testing

Unit tests are located at:
`app/src/test/java/org/thoughtcrime/securesms/service/webrtc/CallAssistantManagerTest.java`

The tests verify:
- Initial state (disabled by default)
- Enable/disable functionality
- Method calls don't crash even when TTS isn't initialized
- Proper cleanup on shutdown

## Permissions

The AI Call Assistant does **not** require any additional permissions beyond what Signal already uses for calling:
- `android.permission.RECORD_AUDIO` - Already used for call functionality
- `android.permission.MODIFY_AUDIO_SETTINGS` - Already used for audio management
- `android.permission.MANAGE_OWN_CALLS` - Already used for telecom integration

## Limitations

- AI assistant is disabled by default and must be manually enabled
- Greeting message is currently hardcoded (can be extended for user customization)
- Text-to-Speech quality depends on the device's TTS engine
- TTS initialization may fail on some devices (feature will remain disabled)

## Future Enhancements

Potential future improvements:
- User preference toggle in Signal settings
- Customizable greeting messages
- Voice recording option instead of TTS
- Smart screening with caller identification
- Conditional auto-answer based on contact/group
- Integration with Signal's chat system for transcription

## Security Considerations

The AI Call Assistant:
- Does not record or store any call audio
- Does not send any data to external servers
- Uses only local device TTS engine
- Respects Signal's existing privacy and security model
- Does not modify call encryption or security protocols

## Contributing

When modifying the AI Call Assistant feature, please ensure:
1. No new permissions are added to AndroidManifest.xml
2. Existing telecom service remains unmodified
3. Changes are minimal and focused
4. Unit tests are updated/added for new functionality
5. The feature remains disabled by default
