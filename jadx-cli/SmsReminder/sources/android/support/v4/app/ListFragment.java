package android.support.v4.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListFragment extends Fragment {
    static final int INTERNAL_EMPTY_ID = 16711681;
    static final int INTERNAL_LIST_CONTAINER_ID = 16711683;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = 16711682;
    ListAdapter mAdapter;
    CharSequence mEmptyText;
    View mEmptyView;
    private final Handler mHandler = new Handler();
    ListView mList;
    View mListContainer;
    boolean mListShown;
    private final OnItemClickListener mOnClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            ListFragment.this.onListItemClick((ListView) parent, v, position, id);
        }
    };
    View mProgressContainer;
    private final Runnable mRequestFocus = new Runnable() {
        public void run() {
            ListFragment.this.mList.focusableViewAvailable(ListFragment.this.mList);
        }
    };
    TextView mStandardEmptyView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int i = 17;
        int i2 = -2;
        int i3 = -1;
        Context context = getActivity();
        FrameLayout root = new FrameLayout(context);
        LinearLayout pframe = new LinearLayout(context);
        pframe.setId(16711682);
        pframe.setOrientation(1);
        pframe.setVisibility(8);
        pframe.setGravity(i);
        pframe.addView(new ProgressBar(context, null, 16842874), new LayoutParams(i2, i2));
        root.addView(pframe, new LayoutParams(i3, i3));
        FrameLayout lframe = new FrameLayout(context);
        lframe.setId(16711683);
        TextView tv = new TextView(getActivity());
        tv.setId(16711681);
        tv.setGravity(i);
        lframe.addView(tv, new LayoutParams(i3, i3));
        ListView lv = new ListView(getActivity());
        lv.setId(16908298);
        lv.setDrawSelectorOnTop(false);
        lframe.addView(lv, new LayoutParams(i3, i3));
        root.addView(lframe, new LayoutParams(i3, i3));
        root.setLayoutParams(new LayoutParams(i3, i3));
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureList();
    }

    public void onDestroyView() {
        View view = null;
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mList = view;
        this.mListShown = false;
        this.mListContainer = view;
        this.mProgressContainer = view;
        this.mEmptyView = view;
        this.mStandardEmptyView = view;
        super.onDestroyView();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    public void setListAdapter(ListAdapter adapter) {
        boolean z = false;
        boolean z2 = true;
        boolean hadAdapter;
        if (this.mAdapter != null) {
            hadAdapter = z2;
        } else {
            hadAdapter = z;
        }
        this.mAdapter = adapter;
        if (this.mList != null) {
            this.mList.setAdapter(adapter);
            if (!this.mListShown && !hadAdapter) {
                if (getView().getWindowToken() != null) {
                    z = z2;
                }
                setListShown(z2, z);
            }
        }
    }

    public void setSelection(int position) {
        ensureList();
        this.mList.setSelection(position);
    }

    public int getSelectedItemPosition() {
        ensureList();
        return this.mList.getSelectedItemPosition();
    }

    public long getSelectedItemId() {
        ensureList();
        return this.mList.getSelectedItemId();
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    public void setEmptyText(CharSequence text) {
        ensureList();
        if (this.mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        this.mStandardEmptyView.setText(text);
        if (this.mEmptyText == null) {
            this.mList.setEmptyView(this.mStandardEmptyView);
        }
        this.mEmptyText = text;
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    private void setListShown(boolean shown, boolean animate) {
        int i = 17432577;
        int i2 = 17432576;
        int i3 = 8;
        int i4 = 0;
        ensureList();
        if (this.mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        } else if (this.mListShown != shown) {
            this.mListShown = shown;
            if (shown) {
                if (animate) {
                    this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), i));
                    this.mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), i2));
                } else {
                    this.mProgressContainer.clearAnimation();
                    this.mListContainer.clearAnimation();
                }
                this.mProgressContainer.setVisibility(i3);
                this.mListContainer.setVisibility(i4);
                return;
            }
            if (animate) {
                this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), i2));
                this.mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), i));
            } else {
                this.mProgressContainer.clearAnimation();
                this.mListContainer.clearAnimation();
            }
            this.mProgressContainer.setVisibility(i4);
            this.mListContainer.setVisibility(i3);
        }
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    private void ensureList() {
        boolean z = false;
        if (this.mList == null) {
            View root = getView();
            if (root == null) {
                throw new IllegalStateException("Content view not yet created");
            }
            if (root instanceof ListView) {
                this.mList = (ListView) root;
            } else {
                this.mStandardEmptyView = (TextView) root.findViewById(16711681);
                if (this.mStandardEmptyView == null) {
                    this.mEmptyView = root.findViewById(16908292);
                } else {
                    this.mStandardEmptyView.setVisibility(8);
                }
                this.mProgressContainer = root.findViewById(16711682);
                this.mListContainer = root.findViewById(16711683);
                View rawListView = root.findViewById(16908298);
                if (rawListView instanceof ListView) {
                    this.mList = (ListView) rawListView;
                    if (this.mEmptyView != null) {
                        this.mList.setEmptyView(this.mEmptyView);
                    } else if (this.mEmptyText != null) {
                        this.mStandardEmptyView.setText(this.mEmptyText);
                        this.mList.setEmptyView(this.mStandardEmptyView);
                    }
                } else if (rawListView == null) {
                    throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
                } else {
                    throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
                }
            }
            this.mListShown = true;
            this.mList.setOnItemClickListener(this.mOnClickListener);
            if (this.mAdapter != null) {
                ListAdapter adapter = this.mAdapter;
                this.mAdapter = null;
                setListAdapter(adapter);
            } else if (this.mProgressContainer != null) {
                setListShown(z, z);
            }
            this.mHandler.post(this.mRequestFocus);
        }
    }
}
