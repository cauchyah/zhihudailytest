package com.zhihudailytest.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhihudailytest.Activity.BaseActivity;
import com.zhihudailytest.Adapter.ThemeInfoAdapter;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.Bean.ThemeInfo;
import com.zhihudailytest.Http.RetrofitManager;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.LogUtil;
import com.zhihudailytest.Utils.ReadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends BaseFragment {
    private int id;
    private String title;
    private View root;
    private ThemeInfoAdapter mAdapter;
    private List<Story> mData;
    private View headerView;
    private LinearLayout editor;
    private TextView titleTextView;
    private ImageView imageView;
    private TextView editorText;
    private Subscription loadData;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    public static ThemeFragment getInstance(int id, String title) {
        ThemeFragment fragment = new ThemeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        id = bundle.getInt("id", 0);
        title = bundle.getString("title", "");
    }
    public ThemeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_theme, container, false);
        return root;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        registerReceiver();
        setSwipeRefreshLayout(view);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
            toolbar.setTitle(title);
        } else {
            onPause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loadData!=null&&!loadData.isUnsubscribed())
            loadData.unsubscribe();
    }

    @Override
    public void onDoubleClick() {
        mLinearLayoutManager.smoothScrollToPosition(recyclerView, null, 0);
    }
    protected void loadData() {
        loadData = RetrofitManager.getInstance()
                .getThemeInfo(id)
                .subscribeOn(Schedulers.io())
                .map(new Func1<ThemeInfo, ThemeInfo>() {

                    @Override
                    public ThemeInfo call(ThemeInfo themeInfo) {
                        ReadUtil.isRead(themeInfo.getStories());
                        return themeInfo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ThemeInfo>() {
                    @Override
                    public void call(ThemeInfo themeInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        mData.clear();
                        mData.addAll(themeInfo.getStories());
                        mAdapter.notifyDataSetChanged();
                        initHeader(themeInfo);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void initHeader(ThemeInfo themeInfo) {
        Glide.with(ThemeFragment.this)
                .load(themeInfo.getBackground())
                .centerCrop()
                .skipMemoryCache(true)
                .into(imageView);
        editorText.setVisibility(View.VISIBLE);
        titleTextView.setText(themeInfo.getDescription());
        int count = editor.getChildCount();
        for (int i = 1; i < count; i++) {
            editor.removeViewAt(i);
        }
        for (ThemeInfo.Editors one : themeInfo.getEditors()) {
            ImageView image = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getContext().getResources().getDimensionPixelSize(R.dimen.image_width),
                    getContext().getResources().getDimensionPixelSize(R.dimen.image_width));
            params.setMargins(8, 0, 0, 0);
            image.setLayoutParams(params);

            editor.addView(image);
            Glide.with(ThemeFragment.this)
                    .load(one.getAvatar())
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .skipMemoryCache(true)
                    .into(image);
        }

    }


    private void initView() {
        toolbar = ((BaseActivity) getActivity()).getToolbar();
        toolbar.setTitle(title);
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.theme_header_view, null);

        titleTextView = (TextView) headerView.findViewById(R.id.title);
        editor = (LinearLayout) headerView.findViewById(R.id.editor);
        imageView = (ImageView) headerView.findViewById(R.id.imageView);
        editorText = (TextView) headerView.findViewById(R.id.editorText);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mData = new ArrayList<Story>();
        mAdapter = new ThemeInfoAdapter(getContext(), mData);
        mAdapter.setHeaderViewPager(headerView);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    void onBroadcastReceive(Context context, Intent intent) {
        // showToast("i had received");
        // LogUtil.d("i had received i had receivedi had received");
        int id = intent.getIntExtra("id", -1);
        ArrayList<Integer> ids = mAdapter.getIds();
        int index = ids.indexOf(id);
        if (index == -1) return;
        Story story = mData.get(index);
        story.setReaded(true);
        mAdapter.notifyItemChanged(index + 1);
    }
}
