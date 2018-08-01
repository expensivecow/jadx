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
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            ListFragment.this.onListItemClick((ListView) adapterView, view, i, j);
        }
    };
    View mProgressContainer;
    private final Runnable mRequestFocus = new Runnable() {
        public void run() {
            ListFragment.this.mList.focusableViewAvailable(ListFragment.this.mList);
        }
    };
    TextView mStandardEmptyView;

    public void onListItemClick(ListView listView, View view, int i, long j) {
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = getContext();
        View frameLayout = new FrameLayout(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setId(16711682);
        linearLayout.setOrientation(1);
        linearLayout.setVisibility(8);
        int i = 17;
        linearLayout.setGravity(i);
        int i2 = -2;
        linearLayout.addView(new ProgressBar(context, null, 16842874), new LayoutParams(i2, i2));
        int i3 = -1;
        frameLayout.addView(linearLayout, new LayoutParams(i3, i3));
        linearLayout = new FrameLayout(context);
        linearLayout.setId(16711683);
        View textView = new TextView(context);
        textView.setId(16711681);
        textView.setGravity(i);
        linearLayout.addView(textView, new LayoutParams(i3, i3));
        View listView = new ListView(context);
        listView.setId(16908298);
        listView.setDrawSelectorOnTop(false);
        linearLayout.addView(listView, new LayoutParams(i3, i3));
        frameLayout.addView(linearLayout, new LayoutParams(i3, i3));
        frameLayout.setLayoutParams(new LayoutParams(i3, i3));
        return frameLayout;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ensureList();
    }

    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        View view = null;
        this.mList = view;
        this.mListShown = false;
        this.mListContainer = view;
        this.mProgressContainer = view;
        this.mEmptyView = view;
        this.mStandardEmptyView = view;
        super.onDestroyView();
    }

    public void setListAdapter(ListAdapter listAdapter) {
        boolean z = false;
        boolean z2 = true;
        boolean z3 = this.mAdapter != null ? z2 : z;
        this.mAdapter = listAdapter;
        if (this.mList != null) {
            this.mList.setAdapter(listAdapter);
            if (!this.mListShown && !z3) {
                if (getView().getWindowToken() != null) {
                    z = z2;
                }
                setListShown(z2, z);
            }
        }
    }

    public void setSelection(int i) {
        ensureList();
        this.mList.setSelection(i);
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

    public void setEmptyText(CharSequence charSequence) {
        ensureList();
        if (this.mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        this.mStandardEmptyView.setText(charSequence);
        if (this.mEmptyText == null) {
            this.mList.setEmptyView(this.mStandardEmptyView);
        }
        this.mEmptyText = charSequence;
    }

    public void setListShown(boolean z) {
        setListShown(z, true);
    }

    public void setListShownNoAnimation(boolean z) {
        setListShown(z, false);
    }

    private void setListShown(boolean z, boolean z2) {
        ensureList();
        if (this.mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        } else if (this.mListShown != z) {
            this.mListShown = z;
            int i = 0;
            int i2 = 8;
            int i3 = 17432576;
            int i4 = 17432577;
            if (z) {
                if (z2) {
                    this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), i4));
                    this.mListContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), i3));
                } else {
                    this.mProgressContainer.clearAnimation();
                    this.mListContainer.clearAnimation();
                }
                this.mProgressContainer.setVisibility(i2);
                this.mListContainer.setVisibility(i);
            } else {
                if (z2) {
                    this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), i3));
                    this.mListContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), i4));
                } else {
                    this.mProgressContainer.clearAnimation();
                    this.mListContainer.clearAnimation();
                }
                this.mProgressContainer.setVisibility(i);
                this.mListContainer.setVisibility(i2);
            }
        }
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    private void ensureList() {
        if (this.mList == null) {
            View view = getView();
            if (view == null) {
                throw new IllegalStateException("Content view not yet created");
            }
            if (view instanceof ListView) {
                this.mList = (ListView) view;
            } else {
                this.mStandardEmptyView = (TextView) view.findViewById(16711681);
                if (this.mStandardEmptyView == null) {
                    this.mEmptyView = view.findViewById(16908292);
                } else {
                    this.mStandardEmptyView.setVisibility(8);
                }
                this.mProgressContainer = view.findViewById(16711682);
                this.mListContainer = view.findViewById(16711683);
                view = view.findViewById(16908298);
                if (view instanceof ListView) {
                    this.mList = (ListView) view;
                    if (this.mEmptyView != null) {
                        this.mList.setEmptyView(this.mEmptyView);
                    } else if (this.mEmptyText != null) {
                        this.mStandardEmptyView.setText(this.mEmptyText);
                        this.mList.setEmptyView(this.mStandardEmptyView);
                    }
                } else if (view == null) {
                    throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
                } else {
                    throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
                }
            }
            this.mListShown = true;
            this.mList.setOnItemClickListener(this.mOnClickListener);
            if (this.mAdapter != null) {
                ListAdapter listAdapter = this.mAdapter;
                this.mAdapter = null;
                setListAdapter(listAdapter);
            } else if (this.mProgressContainer != null) {
                boolean z = false;
                setListShown(z, z);
            }
            this.mHandler.post(this.mRequestFocus);
        }
    }
}
