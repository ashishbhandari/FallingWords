package com.fallingwords;

import android.content.Context;
import android.test.InstrumentationTestCase;

import java.util.concurrent.CountDownLatch;

/**
 * Created by b_ashish on 11-Aug-16.
 */
public class JsonParserTaskTest extends InstrumentationTestCase {

    public void testJsonParser() throws Throwable{

        final Context context = getInstrumentation().getTargetContext();
        final CountDownLatch signal = new CountDownLatch(1);
//        final NetworkTasks networkTasks = new NetworkTasks(context, new GetMediaListener(signal));
//
//        runTestOnUiThread(new Runnable() {
//            public void run() {
//                Media media = new Media();
//                media.setId("179");
//                networkTasks.getMedia(media);
//            }
//        });
//
//        signal.await(30, TimeUnit.SECONDS);

    }


}
