package com.fallingwords;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by ashish on 10/08/16.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * A dummy test that just checks that the Instrumentation was correctly injected
     */
    @Test
    public void dummyTest() {
        // Check that Instrumentation was correctly injected in setUp()
        assertThat(InstrumentationRegistry.getInstrumentation(), notNullValue());
    }

}
