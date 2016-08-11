package com.fallingwords;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.fallingwords.model.WordItem;
import com.fallingwords.util.RoundedView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ashish Bhandari on 07-Aug-16.
 */

public class WordListAdapter implements ListAdapter, AbsListView.RecyclerListener {

    private static final String TAG = WordListAdapter.class.getSimpleName();

    private final Context mContext;

    // list of items served by this adapter
    ArrayList<WordItem> mItems = new ArrayList<>();

    // observers to notify about changes in the data
    ArrayList<DataSetObserver> mObservers = new ArrayList<>();


    private final int mIconColorDefault;
    private final int mIconColorProgressing;
    private final int mIconColorCorrect;
    private final int mIconColorWrong;

    public WordListAdapter(Context context) {
        mContext = context;
        Resources resources = context.getResources();
        mIconColorDefault = resources.getColor(R.color.word_item_icon_default);
        mIconColorProgressing = resources.getColor(R.color.word_item_icon_progressing);
        mIconColorCorrect = resources.getColor(R.color.word_item_icon_correct);
        mIconColorWrong = resources.getColor(R.color.word_item_icon_wrong);
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < mItems.size()) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.word_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (RoundedView) view.findViewById(R.id.icon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();

        }
        final WordItem item = mItems.get(position);
        if (item.isAttempted) {
            // check for right or wrong
            if (item.isCorrect) {
                holder.icon.setBackgroundColor(mIconColorCorrect);
            } else {
                holder.icon.setBackgroundColor(mIconColorWrong);
            }
        } else {
            if (item.isGoingOn) {
                holder.icon.setBackgroundColor(mIconColorProgressing);
            } else {
                holder.icon.setBackgroundColor(mIconColorDefault);
            }
        }

        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    private void notifyObservers() {
        for (DataSetObserver observer : mObservers) {
            observer.onChanged();
        }
    }

    public void forceUpdate() {
        notifyObservers();
    }

    public void updateItems(List<WordItem> items) {
        mItems.clear();
        if (items != null) {
            mItems.addAll(items);

        }
        notifyObservers();
    }

    @Override
    public void onMovedToScrapHeap(View view) {

    }

    private static class ViewHolder {
        public RoundedView icon;
        public TextView word;
    }
}
