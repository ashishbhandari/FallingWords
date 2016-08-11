package com.fallingwords;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.fallingwords.model.WordItem;
import com.fallingwords.util.JsonParserTask;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by b_ashish on 11-Aug-16.
 */
@RunWith(AndroidJUnit4.class)
public class JsonParserTaskTest {

    private static final String TAG = JsonParserTaskTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testJsonParserTask() throws Exception {


        final Object syncObject = new Object();

        final JsonParserTask<WordItem> mJsonParserTask = new JsonParserTask<>(mActivityRule.getActivity());
        mJsonParserTask.setJsonParserListener(new JsonParserTask.OnDataListener() {
            @Override
            public void onDecode(JsonParserTask task, List output) {

                Log.v(TAG, "onHandleResponseCalled in thread " + Thread.currentThread().getId());

                assertThat(output, notNullValue());

                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        }).overlay();

        synchronized (syncObject) {
            syncObject.wait();
        }


    }


}
