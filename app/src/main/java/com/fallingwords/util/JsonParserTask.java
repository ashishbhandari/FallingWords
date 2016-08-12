package com.fallingwords.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.fallingwords.model.SpaWord;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * Created by b_ashish on 09-Aug-16.
 */

public class JsonParserTask<T> extends AsyncTask<Void, Void, List<T>> {

    private static final String TAG = JsonParserTask.class.getSimpleName();

    private final Context mContext;
    private int mJsonResource;
    private OnDataListener mDataListener;

    public interface OnDataListener {

        void onDecode(JsonParserTask task, List output);
    }

    public JsonParserTask(Context mContext) {
        this.mContext = mContext;
    }

    public JsonParserTask setJson(int mJsonResource) {
        this.mJsonResource = mJsonResource;
        return this;
    }

    public JsonParserTask setJsonParserListener(OnDataListener listener) {
        this.mDataListener = listener;
        return this;
    }


    public void overlay() {
        super.execute((Void[]) null);
    }

    @Override
    protected List<T> doInBackground(Void... voids) {

        if (mJsonResource <= 0) {
            throw new JsonCreatorTaskException("Word json resource not set");
        }

        if (isCancelled()) {
            return null;
        }
        /**
         * can be use switch case if another conversion available
         */
        return (List<T>) parseSpanishWordsJsonStringToObjects();
    }

    @Override
    protected void onPostExecute(List<T> objectArray) {
        if (mDataListener != null) {
            mDataListener.onDecode(this, objectArray);
        }
    }

    private List<SpaWord> parseSpanishWordsJsonStringToObjects() {
        List<SpaWord> words = new ArrayList<>();

        Log.d(TAG, "Starting data boot words process.");
        // Load data from word raw resource words.json inside raw directory
        try {
            String bootWordsJson = JSONHandler.parseResource(mContext, mJsonResource);

            words = stringToArray(bootWordsJson, SpaWord[].class);

            int position = 0;
            int min = 0;
            int max = words.size() - 1;
            
            Random sRandom = new Random();
            for (SpaWord word : words) {
                //set wrong falling translation word in every odd number in order to make game little crazy
                if (position % 2 == 1) {
                    //generate random number in specific range
                    int randomPosition = sRandom.nextInt(max - min + 1) + min;
                    // pick randomly wrong translation word from available words array
                    word.fallingTranslationWord = words.get(randomPosition).text_spa;
                    word.answerOfThisQues = false;
                } else {
                    word.fallingTranslationWord = word.text_spa;
                    word.answerOfThisQues = true;
                }
                position++;
            }

        } catch (IOException ex) {
            Log.e(TAG, "*** ERROR DURING BOOT WORD! Problem in boot words data?", ex);
        } catch (Exception e) {
            Log.e(TAG, "*** ERROR DURING BOOT WORD! Problem in boot words data?", e);
        }
        return words;
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }
}
