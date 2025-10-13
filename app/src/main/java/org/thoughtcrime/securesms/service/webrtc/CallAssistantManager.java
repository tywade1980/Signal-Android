package org.thoughtcrime.securesms.service.webrtc;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;

import java.util.Locale;

/**
 * Manages AI call assistant functionality for handling inbound and outbound calls
 * on behalf of the user. The assistant can automatically answer calls and provide
 * a greeting message to inform callers that the user will be available shortly.
 * 
 * <p>Key Features:</p>
 * <ul>
 *   <li>Auto-answers incoming calls when enabled</li>
 *   <li>Plays customizable greeting message using Text-to-Speech</li>
 *   <li>Handles both inbound and outbound calls</li>
 *   <li>Does not require additional permissions</li>
 *   <li>Builds upon existing Signal telecom integration</li>
 * </ul>
 * 
 * <p>Usage:</p>
 * <pre>
 * // Enable the AI assistant
 * CallAssistantManager assistant = webRtcInteractor.getCallAssistantManager();
 * assistant.setEnabled(true);
 * 
 * // The assistant will now automatically:
 * // 1. Auto-answer incoming calls
 * // 2. Play greeting message for incoming calls
 * // 3. Play greeting message when outgoing calls connect
 * </pre>
 * 
 * <p>The AI assistant is disabled by default and must be explicitly enabled.
 * It integrates with IncomingCallActionProcessor and CallSetupActionProcessorDelegate
 * to provide seamless call handling without modifying the existing telecom service.</p>
 */
public class CallAssistantManager {

  private static final String TAG = Log.tag(CallAssistantManager.class);
  private static final String DEFAULT_GREETING = "Hello, I will be right with you. Please hold.";
  
  private final Context context;
  private TextToSpeech textToSpeech;
  private volatile boolean isInitialized = false;
  private boolean isEnabled = false;

  public CallAssistantManager(@NonNull Context context) {
    this.context = context;
    initializeTextToSpeech();
  }

  /**
   * Initialize the Text-to-Speech engine for AI greeting messages
   */
  private void initializeTextToSpeech() {
    textToSpeech = new TextToSpeech(context, status -> {
      if (status == TextToSpeech.SUCCESS) {
        int result = textToSpeech.setLanguage(Locale.getDefault());
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.w(TAG, "TTS language not supported, falling back to English");
          textToSpeech.setLanguage(Locale.US);
        }
        isInitialized = true;
        Log.i(TAG, "Text-to-Speech initialized successfully");
      } else {
        Log.e(TAG, "Text-to-Speech initialization failed");
        isInitialized = false;
      }
    });
  }

  /**
   * Check if AI call assistant is enabled
   */
  public boolean isEnabled() {
    // For now, AI assistant is disabled by default
    // This can be controlled by a user preference setting
    return isEnabled && isInitialized;
  }

  /**
   * Enable or disable the AI call assistant
   */
  public void setEnabled(boolean enabled) {
    this.isEnabled = enabled;
    Log.i(TAG, "AI Call Assistant " + (enabled ? "enabled" : "disabled"));
  }

  /**
   * Check if the assistant should auto-answer an incoming call
   */
  public boolean shouldAutoAnswerIncomingCall() {
    return isEnabled();
  }

  /**
   * Check if the assistant should handle an outgoing call
   */
  public boolean shouldHandleOutgoingCall() {
    return isEnabled();
  }

  /**
   * Play the AI greeting message for an incoming call
   * This informs the caller that the user will be with them shortly
   */
  public void playIncomingCallGreeting() {
    if (!isInitialized) {
      Log.w(TAG, "Cannot play greeting - TTS not initialized");
      return;
    }

    Log.i(TAG, "Playing AI greeting for incoming call");
    String greeting = getGreetingMessage();
    
    Bundle params = new Bundle();
    params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_VOICE_CALL);
    
    textToSpeech.speak(greeting, TextToSpeech.QUEUE_FLUSH, params, "incoming_call_greeting");
  }

  /**
   * Play the AI greeting message for an outgoing call
   */
  public void playOutgoingCallGreeting() {
    if (!isInitialized) {
      Log.w(TAG, "Cannot play greeting - TTS not initialized");
      return;
    }

    Log.i(TAG, "Playing AI greeting for outgoing call");
    String greeting = getGreetingMessage();
    
    Bundle params = new Bundle();
    params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_VOICE_CALL);
    
    textToSpeech.speak(greeting, TextToSpeech.QUEUE_FLUSH, params, "outgoing_call_greeting");
  }

  /**
   * Get the greeting message to be spoken by the AI assistant
   */
  private String getGreetingMessage() {
    // This could be customized by user preferences in the future
    return DEFAULT_GREETING;
  }

  /**
   * Stop any ongoing AI greeting playback
   */
  public void stopGreeting() {
    if (textToSpeech != null && textToSpeech.isSpeaking()) {
      textToSpeech.stop();
      Log.i(TAG, "Stopped AI greeting playback");
    }
  }

  /**
   * Clean up resources when the assistant is no longer needed
   */
  public void shutdown() {
    if (textToSpeech != null) {
      textToSpeech.stop();
      textToSpeech.shutdown();
      Log.i(TAG, "Text-to-Speech engine shut down");
    }
    isInitialized = false;
  }
}
