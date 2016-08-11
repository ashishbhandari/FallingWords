package com.fallingwords;

import android.app.Fragment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.instanceOf;
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


    @Test
    public void populateScoreCardTest() {
        MainActivity mainActivity = mActivityRule.getActivity();
        TextView scoreCard = (TextView) mainActivity.findViewById(R.id.score_card_label);
        TextView gameStatus = (TextView) mainActivity.findViewById(R.id.game_over_status);

        WordSession session = WordGameHolder.getInstance().getSession();

        assertThat(scoreCard, notNullValue());
        assertThat(gameStatus, notNullValue());
        assertThat(session, notNullValue());
    }

    @Test
    public void checkPreconditions() {
        // Check the fragment exists
        assertNotNull(mActivityRule.getActivity().getFragmentManager().findFragmentById(R.id.description_fragment));

        assertNotNull(mActivityRule.getActivity().getFragmentManager().findFragmentByTag(MainActivity.WORD_LIST_TAG));

        WordListFragment wordListFragment = (WordListFragment) mActivityRule.getActivity()
                .getFragmentManager().findFragmentByTag(MainActivity.WORD_LIST_TAG);

        ViewMatchers.assertThat("The list fragment model should be an instance of WordItem",
                wordListFragment.getListView().getSelectedItem(), instanceOf(WordItem.class));
    }

}
