package com.example.snapstory

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.snapstory.ui.onboarding.OnboardingActivity
import com.example.snapstory.ui.signin.SigninActivity
import com.example.snapstory.ui.main.MainActivity
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewAction
import androidx.test.espresso.idling.CountingIdlingResource

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignInAndLogOutTest {

    private val idlingResource = CountingIdlingResource("SignInAndLogOutTest")

    @Before
    fun setup() {
        Intents.init()
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testSignInAndLogOut() {
        // Launch OnboardingActivity
        ActivityScenario.launch(OnboardingActivity::class.java)

        // Onboarding screen
        onView(withId(R.id.btn_signin_onboarding)).perform(click())
        Intents.intended(hasComponent(SigninActivity::class.java.name))

        // Sign in screen
        onView(withId(R.id.input_email_signin)).perform(typeText("sean@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.input_password_signin)).perform(typeText("sean1234"), closeSoftKeyboard())
        onView(withId(R.id.btn_signin)).perform(click())

        // Wait for MainActivity to be displayed
        onView(isRoot()).perform(waitFor(4000))
        Intents.intended(hasComponent(MainActivity::class.java.name))

        // MainActivity screen
        onView(withId(R.id.rv_story)).perform(swipeUp())
        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.btn_logout)).perform(click())

    }

    private fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: androidx.test.espresso.UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
}
