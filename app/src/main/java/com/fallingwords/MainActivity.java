package com.fallingwords;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

/**
 * Created by Ashish Bhandari on 07-Aug-16.
 */
public class MainActivity extends Activity implements WordListFragment.Listener, DescriptionFragment.Listener {

    private static final String TAG = MainActivity.class.getSimpleName();


    protected static final int WORD_ITEM_INIT_STATE = 0;

    protected static final int WORD_ITEM_PROGRESS_STATE = 1;

    protected static final int WORD_ITEM_RESULT_STATE = 2;

    private static final int DEFAULT_INDEX = 0;

    private boolean mGameStarted = false;

    private WordListAdapter mWordListAdapter;

    private WordListFragment mWordListFragment;
    private DescriptionFragment descriptionFragment;

    private List<WordItem> wordItems;

    private TextView mVersionDescriptionTextView;
    private TextView fallingWordTrans;
    private FloatingActionsMenu floatingIcon;

    private int mCurrentPosition = -1;

    private ObjectAnimator objectAnimatorWordTrans;

    private JsonParserTask<WordItem> mJsonParserTask;

    private TextView scoreCard;
    private TextView gameStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreCard = (TextView) findViewById(R.id.score_card_label);
        gameStatus = (TextView) findViewById(R.id.game_over_status);

        mJsonParserTask = new JsonParserTask<>(this);
        mJsonParserTask.setJsonParserListener(new JsonParserTask.OnDataListener() {
            @Override
            public void onDecode(JsonParserTask task, List output) {
                Log.e(TAG, "result");
                wordItems = output;

                WordSession session = WordGameHolder.getInstance().getSession();
                session.setTotalQuestions(wordItems.size());

                WordListFragment frag = new WordListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.list_container, frag);
                transaction.commit();

                mWordListAdapter = new WordListAdapter(MainActivity.this);
                mWordListAdapter.updateItems(wordItems);
                populateScoreCard();
            }
        }).overlay();

    }

    private void populateScoreCard() {
        WordSession session = WordGameHolder.getInstance().getSession();
        if (WordGameHolder.getInstance().isGameOver()) {
            gameStatus.setText("Game Over");
        }
        scoreCard.setText(session.getTotalAttempts() + "/" + session.getTotalQuestions() + " correct : " + session.getCorrectAttempts());
    }

    @Override
    public void onFragmentViewCreated(final ListFragment fragment) {
        fragment.setListAdapter(mWordListAdapter);
        fragment.getListView().setRecyclerListener(mWordListAdapter);
    }

    @Override
    public void onFragmentAttached(WordListFragment fragment) {
        mWordListFragment = fragment;
    }

    @Override
    public void onFragmentDetached(WordListFragment fragment) {
    }

    @Override
    public void onListItemClick(int position) {

        if (!mGameStarted)
            return;

        // if any question status is goingOn, don't allow to user to click on any other list item
        if (mCurrentPosition != -1) {
            if (wordItems.get(mCurrentPosition).isGoingOn) {
                return;
            }
        }
        mCurrentPosition = position;

//        DescriptionFragment descriptionFragment = (DescriptionFragment) getFragmentManager()
//                .findFragmentById(R.id.description_fragment);

        if (wordItems.get(position).isAttempted) {
            // show resulted color either right or wrong
            // and show the result of this question on result screen

            showDescriptionScreenStatus(WORD_ITEM_RESULT_STATE);

        } else {
            //refresh list adapter and make it active question going on

            // show animation on detail screen and after completion make it attempted true
            // and isGoingOn false and refresh list view
            wordItems.get(position).isGoingOn = true;
            mWordListAdapter.forceUpdate();

            showDescriptionScreenStatus(WORD_ITEM_PROGRESS_STATE);
        }

    }


    @Override
    public void onFragmentViewCreated(DescriptionFragment fragment) {
        this.descriptionFragment = fragment;

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

            WordItem wordItem = wordItems.get(mCurrentPosition);

            ((TextView) view.findViewById(R.id.ques_word)).setText(wordItem.text_eng);
            ((TextView) view.findViewById(R.id.option_present)).setText("Spanish Conversion : " + wordItem.fallingTranslationWord);
            ((TextView) view.findViewById(R.id.actual_answer)).setText("\nCorrect answer is : " + wordItem.text_spa);
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
            fallingWordTrans = (TextView) view.findViewById(R.id.falling_word_trans);

            floatingIcon = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
            floatingIcon.setEnabled(false);

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
        wordItems.get(mCurrentPosition).userOptedAnswer = userOptedAnswer;

        //disable animation and finish this question asap
        objectAnimatorWordTrans.cancel();// this method must called onAnimationEnd() callback of ObjectAnimator class
    }

    public void calculateResultStatus() {
        /**
         * Case 1: When question will be end without getting any input from user,
         * if no Input will be press by user userOptedAnswer should be null so handle it accordingly
         */
        Boolean userOptedAnswer = wordItems.get(mCurrentPosition).userOptedAnswer;

        if (userOptedAnswer != null) {
            if (userOptedAnswer == wordItems.get(mCurrentPosition).answerOfThisQues) {
                // user answer is correct
                // display result of this screen
                wordItems.get(mCurrentPosition).isCorrect = true;
            } else {
                // user answer is wrong
                // display result of this screen
                wordItems.get(mCurrentPosition).isCorrect = false;
            }
        } else {
            // read case study again and analyse how to handle this case
            wordItems.get(mCurrentPosition).isCorrect = false;
        }

        mWordListAdapter.forceUpdate();

        WordGameHolder.getInstance().validateAttempt((wordItems.get(mCurrentPosition).isCorrect ? true : false));
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
        if (mWordListFragment != null) {
            ListView mList = mWordListFragment.getListView();
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

            floatingIcon.collapse();

            mVersionDescriptionTextView.setText(wordItems.get(mCurrentPosition).text_eng);
            fallingWordTrans.setText(wordItems.get(mCurrentPosition).fallingTranslationWord);
            fallingWordTrans.setVisibility(View.INVISIBLE);

            startFallingWordAnimation(mCurrentPosition);
        }

    }

    private void startFallingWordAnimation(final int position) {
        int yValue = descriptionFragment.getView().getBottom() - fallingWordTrans.getHeight();

        objectAnimatorWordTrans = ObjectAnimator.ofFloat(fallingWordTrans, View.TRANSLATION_Y, 0, yValue).setDuration(7000);
        objectAnimatorWordTrans.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e(TAG, "animation.getAnimatedValue() : " + animation.getAnimatedValue());
            }
        });

        objectAnimatorWordTrans.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                floatingIcon.expand();
                fallingWordTrans.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                floatingIcon.collapse();
                wordItems.get(position).isGoingOn = false;
                wordItems.get(position).isAttempted = true;

                calculateResultStatus();
                fallingWordTrans.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        Animation animationFadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.view_fade_in);
        mVersionDescriptionTextView.startAnimation(animationFadeIn);
        animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e(TAG, "Animation end here");
                objectAnimatorWordTrans.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


}
