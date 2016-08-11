package com.fallingwords.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fallingwords.R;
import com.fallingwords.model.WordItem;
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
    private String mJsonString;
    private OnDataListener mDataListener;

    public interface OnDataListener {

        void onDecode(JsonParserTask task, List output);
    }

    public JsonParserTask(Context mContext) {
        this.mContext = mContext;
    }

    public JsonParserTask setJson(String mJsonString) {
        this.mJsonString = mJsonString;
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

//        if (mJsonString == null) {
//            throw new JsonCreatorTaskException("Base json string not set");
//        }
        if (isCancelled()) {
            return null;
        }

        return (List<T>) parseWordsJsonStringToObject();
    }

    @Override
    protected void onPostExecute(List<T> objectArray) {
        if (mDataListener != null) {
            mDataListener.onDecode(this, objectArray);
        }
    }

    private List<WordItem> parseWordsJsonStringToObject() {
        List<WordItem> words = new ArrayList<>();

        Log.d(TAG, "Starting data boot words process.");
        // Load data from word raw resource.
        try {
            String bootWordsJson = JSONHandler.parseResource(mContext, R.raw.words);

            words = stringToArray(bootWordsJson, WordItem[].class);

            int position = 0;
            int min = 0;
            int max = words.size() - 1;
            Random sRandom = new Random();
            for (WordItem word : words) {
                //set wrong falling translation word in every odd number in order to make game little crazy
                if (position % 2 == 1) {
                    //generate random number in specific range
                    int randomPosition = sRandom.nextInt(max - min + 1) + min;
                    // pick randomly wrong translation word from words array
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
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }
}
