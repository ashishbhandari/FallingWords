package com.fallingwords;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;
import com.fallingwords.model.WordItem;
import com.fallingwords.util.AnimationListenerAdapter;
import com.fallingwords.util.JsonParserTask;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

/**
 * Created by b_ashish on 07-Aug-16.
 */
public class MainActivity extends Activity implements WordListFragment.Listener, DescriptionFragment.Listener {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected static final int WORD_ITEM_INIT_STATE = 0;

    protected static final int WORD_ITEM_PROGRESS_STATE = 1;

    protected static final int WORD_ITEM_RESULT_STATE = 2;

    private static final int DEFAULT_INDEX = 0;

    private static final int FALLING_WORD_DURATION = 7000;

    private boolean mGameStarted = false;

    private WordListAdapter mWordListAdapter;

    private WordListFragment wordListFragment;
    private DescriptionFragment descriptionFragment;

    private List<WordItem> mWordItems;

    private TextView mVersionDescriptionTextView;
    private TextView mFallingWordTranslation;
    private FloatingActionsMenu mFloatingIcon;

    private int mCurrentPosition = -1;

    private ObjectAnimator objectFallingAnimator;

    private JsonParserTask<WordItem> mJsonParserTask;

    private TextView scoreCard;
    private TextView gameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreCard = (TextView) findViewById(R.id.score_card_label);
        gameStatus = (TextView) findViewById(R.id.game_over_status);

    }

    private void loadWordTranslator() {
        mJsonParserTask = new JsonParserTask<>(this);
        mJsonParserTask.setJsonParserListener(new JsonParserTask.OnDataListener() {
            @Override
            public void onDecode(JsonParserTask task, List output) {
                Log.e(TAG, "result");
                mWordItems = output;
                WordGameHolder.getInstance().reset();
                if (mWordItems != null && mWordItems.size() > 0) {
                    WordSession session = WordGameHolder.getInstance().getSession();
                    session.setTotalQuestions(mWordItems.size());

                    populateWordListAdapter();
                    populateScoreCard();

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_data_available), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }).overlay();
    }

    private void populateWordListAdapter() {
        mWordListAdapter = new WordListAdapter(MainActivity.this);
        mWordListAdapter.updateItems(mWordItems);
        wordListFragment.setListAdapter(mWordListAdapter);
        wordListFragment.getListView().setRecyclerListener(mWordListAdapter);
    }

    private void populateScoreCard() {
        WordSession session = WordGameHolder.getInstance().getSession();
        if (WordGameHolder.getInstance().isGameOver()) {
            gameStatus.setText(getString(R.string.game_over));
        } else {
            gameStatus.setText(getString(R.string.game_on));
        }
        scoreCard.setText("Attempts " + session.getTotalAttempts() + "/" + session.getTotalQuestions() + "  correct : " + session.getCorrectAttempts());
    }

    @Override
    public void onFragmentViewCreated(final ListFragment fragment) {
        loadWordTranslator();
    }

    @Override
    public void onFragmentDetached(WordListFragment fragment) {

    }

    @Override
    public void onFragmentAttached(WordListFragment fragment) {
        wordListFragment = fragment;

    }

    @Override
    public void onListItemClick(int position) {

        if (!mGameStarted)
            return;

        // if any question status is goingOn, don't allow to user to click on any other list item
        if (mCurrentPosition != -1) {
            if (mWordItems.get(mCurrentPosition).isGoingOn) {
                return;
            }
        }
        mCurrentPosition = position;

        if (mWordItems.get(position).isAttempted) {
            // show resulted color either right or wrong
            // and show the result of this question on result screen

            showDescriptionScreenStatus(WORD_ITEM_RESULT_STATE);

        } else {
            //refresh word list adapter and make it active question going on

            // show animation on detail screen and after completion make it attempted true
            // and isGoingOn false and refresh list view
            mWordItems.get(position).isGoingOn = true;
            mWordListAdapter.forceUpdate();

            showDescriptionScreenStatus(WORD_ITEM_PROGRESS_STATE);
        }
    }

    @Override
    public void onFragmentViewCreated(DescriptionFragment mDescriptionFragment) {
        this.descriptionFragment = mDescriptionFragment;

        showDescriptionScreenStatus(WORD_ITEM_INIT_STATE);
    }

    private void showDescriptionScreenStatus(int state) {

        switch (state) {
            case WORD_ITEM_INIT_STATE:
                initDescriptionComponents();
                break;
            case WORD_ITEM_PROGRESS_STATE:
                showWordTranslatorGame();
                break;
            case WORD_ITEM_RESULT_STATE:
                showResultScreen();
                break;
        }

    }

    private void showResultScreen() {

        if (descriptionFragment != null) {

            View view = descriptionFragment.getView();
            view.findViewById(R.id.welcome_container).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.game_container).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.result_container).setVisibility(View.VISIBLE);

            WordItem wordItem = mWordItems.get(mCurrentPosition);

            ((TextView) view.findViewById(R.id.ques_word)).setText(wordItem.text_eng);
            ((TextView) view.findViewById(R.id.option_present)).setText("Falling word : " + wordItem.fallingTranslationWord);
            ((TextView) view.findViewById(R.id.actual_answer)).setText("\nCorrect Spanish conversion is : " + wordItem.text_spa);
            ((TextView) view.findViewById(R.id.status_answer)).setText("Your answer is " + (wordItem.isCorrect ? "Correct" : "Wrong"));

            populateScoreCard();
        }
    }


    private void initDescriptionComponents() {

        if (descriptionFragment != null) {
            View view = descriptionFragment.getView();

            view.findViewById(R.id.welcome_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.game_container).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.result_container).setVisibility(View.INVISIBLE);


            view.findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGameStarted = true;
                    activateQuestion(DEFAULT_INDEX);

                }
            });

            mVersionDescriptionTextView = (TextView) view.findViewById(R.id.word);
            mFallingWordTranslation = (TextView) view.findViewById(R.id.falling_word_trans);

            mFloatingIcon = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
            mFloatingIcon.setEnabled(false);

            view.findViewById(R.id.action_a).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerSelectedByUser(true);
                }
            });
            view.findViewById(R.id.action_b).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerSelectedByUser(false);
                }
            });
        }
    }


    private void answerSelectedByUser(boolean userOptedAnswer) {
        mWordItems.get(mCurrentPosition).userOptedAnswer = userOptedAnswer;
        //disable animation and finish this question asap
        stopFallingAnimation();
    }

    public void calculateResultStatus() {
        /**
         * Case 1: When question will be end without getting any input from user
         */
        Boolean userOptedAnswer = mWordItems.get(mCurrentPosition).userOptedAnswer;

        if (userOptedAnswer != null) {
            if (userOptedAnswer == mWordItems.get(mCurrentPosition).answerOfThisQues) {
                // user answer is correct
                // display result of this screen
                mWordItems.get(mCurrentPosition).isCorrect = true;
            } else {
                // user answer is wrong
                // display result of this screen
                mWordItems.get(mCurrentPosition).isCorrect = false;
            }
        } else {
            // read case study again and analyse how to handle this case
            mWordItems.get(mCurrentPosition).isCorrect = false;
        }

        mWordListAdapter.forceUpdate();

        WordGameHolder.getInstance().validateAttempt((mWordItems.get(mCurrentPosition).isCorrect ? true : false));
        checkForGameOver();
        // show result status on the screen with correct output and user input
        // keep floating icon menu at disable state
        showDescriptionScreenStatus(WORD_ITEM_RESULT_STATE);

    }

    private void checkForGameOver() {
        if (WordGameHolder.getInstance().getSession().getTotalAttempts() ==
                WordGameHolder.getInstance().getSession().getTotalQuestions()) {
            WordGameHolder.getInstance().setGameOver(true);
        }
    }


    /**
     * this method will help to perform onListItemClick at certain given index
     *
     * @param questionIndex
     */
    private void activateQuestion(int questionIndex) {
        if (wordListFragment != null) {
            ListView mList = wordListFragment.getListView();
            mList.performItemClick(
                    mList.getAdapter().getView(questionIndex, null, null),
                    questionIndex,
                    mList.getAdapter().getItemId(questionIndex));
        }
    }

    public void showWordTranslatorGame() {

        if (descriptionFragment != null) {
            View view = descriptionFragment.getView();

            view.findViewById(R.id.welcome_container).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.result_container).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.game_container).setVisibility(View.VISIBLE);

            mFloatingIcon.collapse();

            mVersionDescriptionTextView.setText(mWordItems.get(mCurrentPosition).text_eng);
            mFallingWordTranslation.setText(mWordItems.get(mCurrentPosition).fallingTranslationWord);
            mFallingWordTranslation.setVisibility(View.INVISIBLE);

            startFallingWordAnimation(mCurrentPosition);
        }

    }

    private void startFallingWordAnimation(final int position) {

        int yValueStart = 0;
        int yValueEnd = descriptionFragment.getView().getBottom() - mFallingWordTranslation.getHeight();

        stopFallingAnimation();

        objectFallingAnimator = ObjectAnimator.ofFloat(mFallingWordTranslation, View.TRANSLATION_Y, yValueStart, yValueEnd).setDuration(FALLING_WORD_DURATION);

        objectFallingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFloatingIcon.expand();
                mFallingWordTranslation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFloatingIcon.collapse();
                mWordItems.get(position).isGoingOn = false;
                mWordItems.get(position).isAttempted = true;

                calculateResultStatus();
                mFallingWordTranslation.setVisibility(View.INVISIBLE);
            }
        });

        Animation animationFadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.view_fade_in);
        mVersionDescriptionTextView.startAnimation(animationFadeIn);
        animationFadeIn.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                startFallingAnimation();
            }
        });
    }

    private void startFallingAnimation() {
        if (objectFallingAnimator != null) {
            objectFallingAnimator.start();
        }
    }

    private void stopFallingAnimation() {
        if (objectFallingAnimator != null) {
            objectFallingAnimator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mJsonParserTask != null && mJsonParserTask.getStatus() != AsyncTask.Status.FINISHED) {
            mJsonParserTask.cancel(true);
        }

    }
}
