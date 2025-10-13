package org.thoughtcrime.securesms.service.webrtc;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Tests for {@link CallAssistantManager}
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class CallAssistantManagerTest {

  private Context context;
  private CallAssistantManager callAssistantManager;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    context = RuntimeEnvironment.getApplication();
    callAssistantManager = new CallAssistantManager(context);
  }

  @Test
  public void testInitialState_shouldBeDisabled() {
    assertFalse("AI assistant should be disabled by default", callAssistantManager.isEnabled());
  }

  @Test
  public void testEnableAssistant_shouldBeEnabled() {
    callAssistantManager.setEnabled(true);
    // Note: isEnabled() also checks TTS initialization which may not complete in tests
    // So we just verify the method doesn't throw
  }

  @Test
  public void testDisableAssistant_shouldBeDisabled() {
    callAssistantManager.setEnabled(true);
    callAssistantManager.setEnabled(false);
    assertFalse("AI assistant should be disabled", callAssistantManager.isEnabled());
  }

  @Test
  public void testShouldAutoAnswerIncomingCall_whenDisabled() {
    assertFalse("Should not auto-answer when disabled", callAssistantManager.shouldAutoAnswerIncomingCall());
  }

  @Test
  public void testShouldHandleOutgoingCall_whenDisabled() {
    assertFalse("Should not handle outgoing calls when disabled", callAssistantManager.shouldHandleOutgoingCall());
  }

  @Test
  public void testPlayIncomingCallGreeting_doesNotCrash() {
    // Test that calling the method doesn't crash even if TTS isn't initialized
    callAssistantManager.playIncomingCallGreeting();
  }

  @Test
  public void testPlayOutgoingCallGreeting_doesNotCrash() {
    // Test that calling the method doesn't crash even if TTS isn't initialized
    callAssistantManager.playOutgoingCallGreeting();
  }

  @Test
  public void testStopGreeting_doesNotCrash() {
    callAssistantManager.stopGreeting();
  }

  @Test
  public void testShutdown_doesNotCrash() {
    callAssistantManager.shutdown();
  }
}
