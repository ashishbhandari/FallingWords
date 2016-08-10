package com.fallingwords;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by b_ashish on 10-Aug-16.
 */
public class DescriptionFragment extends Fragment {

    private static final String TAG = DescriptionFragment.class.getSimpleName();

    private String mContentDescription = null;
    private View mRoot = null;

    public interface Listener {

        void onFragmentViewCreated(DescriptionFragment fragment);

    }

    public DescriptionFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.fragment_description, container, false);
        if (mContentDescription != null) {
            mRoot.setContentDescription(mContentDescription);
        }
        return mRoot;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentViewCreated(this);
        }
    }


}
