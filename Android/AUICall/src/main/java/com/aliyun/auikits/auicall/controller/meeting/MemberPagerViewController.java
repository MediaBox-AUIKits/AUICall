package com.aliyun.auikits.auicall.controller.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.auikits.auicall.bean.UserInfo;
import com.aliyun.auikits.auicall.model.AUICallNVNModel;
import com.aliyun.auikits.auicall.widget.MeetingMemberView;
import com.aliyun.auikits.auicall.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public final class MemberPagerViewController extends BaseMeetingViewController {

    private TabLayout mIndicator;

    private ViewPager mViewPager;

    private MeetingPagerAdapter mViewPagerAdapter;

    public MemberPagerViewController(ViewGroup containerView, AUICallNVNModel model) {
        super(containerView, model);
    }

    public static final class ViewHolder {

        private MeetingPagerAdapter mAdapter;

        private View mContentView;
        private int mDataStartPos;

        private List<MeetingMemberView> mMemberViews;

        private AUICallNVNModel mModel;

        public ViewHolder(View view, int size, AUICallNVNModel model, MeetingPagerAdapter adapter) {
            this.mContentView = view;
            this.mModel = model;
            this.mAdapter = adapter;
            MeetingMemberView view1 = (MeetingMemberView) view.findViewById(R.id.member1_view);
            MeetingMemberView view2 = (MeetingMemberView) view.findViewById(R.id.member2_view);
            MeetingMemberView view3 = (MeetingMemberView) view.findViewById(R.id.member3_view);
            MeetingMemberView view4 = (MeetingMemberView) view.findViewById(R.id.member4_view);
            MeetingMemberView view5 = (MeetingMemberView) view.findViewById(R.id.member5_view);
            MeetingMemberView view6 = (MeetingMemberView) view.findViewById(R.id.member6_view);
            view1.setBizModel(this.mModel);
            view2.setBizModel(this.mModel);
            view3.setBizModel(this.mModel);
            view4.setBizModel(this.mModel);
            view5.setBizModel(this.mModel);
            view6.setBizModel(this.mModel);
            fixItemSize(view1, size);
            fixItemSize(view2, size);
            fixItemSize(view3, size);
            fixItemSize(view4, size);
            fixItemSize(view5, size);
            fixItemSize(view6, size);
            this.mMemberViews = Arrays.asList(new MeetingMemberView[]{view1, view2, view3, view4, view5, view6});
        }

        private final void fixItemSize(View view, int size) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.width = size;
            lp.height = size;
            view.setLayoutParams(lp);
        }

        public final void bindData(int pos) {
            this.mDataStartPos = pos * 6;
            updateData();
        }

        public final void updateData() {
            int currentPos = this.mDataStartPos;
            int size = this.mMemberViews.size();
            int i = 0;
            while (i < size) {
                List<UserInfo> mCacheMemberList = this.mAdapter.getMCacheMemberList();
                if (mCacheMemberList.size() > currentPos) {
                    List<UserInfo> mCacheMemberList2 = this.mAdapter.getMCacheMemberList();
                    UserInfo user = mCacheMemberList2.get(currentPos);
                    updateData(this.mMemberViews.get(i), user);
                    currentPos++;
                } else {
                    updateData(this.mMemberViews.get(i), null);
                }
                i++;
            }
        }

        public final void hidePreview() {
            int currentPos = this.mDataStartPos;
            int size = this.mMemberViews.size();
            int i = 0;
            while (i < size) {
                List<UserInfo> mCacheMemberList = this.mAdapter.getMCacheMemberList();
                if (mCacheMemberList.size() > currentPos) {
                    List<UserInfo> mCacheMemberList2 = this.mAdapter.getMCacheMemberList();
                    this.mMemberViews.get(i).hidePreview(mCacheMemberList2.get(currentPos));
                    currentPos++;
                }
                i++;
            }
        }

        private final void updateData(MeetingMemberView member, UserInfo userInfo) {
            if (userInfo != null) {
                member.setVisibility(View.VISIBLE);
                member.bindData(userInfo);
                return;
            }
            member.setVisibility(View.INVISIBLE);
        }

        public final View contentView() {
            return this.mContentView;
        }
    }

    public static final class MeetingPagerAdapter extends PagerAdapter {

        private List<UserInfo> mCacheMemberList;

        private final BaseMeetingViewController.UserPriorityComparator mComparator;

        private Context mCtx;
        private int mLastPos;

        private AUICallNVNModel mModel;

        private List<ViewHolder> mViewCache;

        public MeetingPagerAdapter(Context context, MemberPagerViewController view, AUICallNVNModel model) {
            this.mCtx = context;
            this.mComparator = new BaseMeetingViewController.UserPriorityComparator(model);
            this.mModel = model;
            this.mViewCache = new ArrayList();
        }

        public final List<UserInfo> getMCacheMemberList() {
            return this.mCacheMemberList;
        }

        @Override 
        public int getCount() {
            int size = this.mModel.getMeetingMembers().size() + 1;
            int page = size / 6;
            return page + (size % 6 > 0 ? 1 : 0);
        }

        private final void updateDataList() {
            List<UserInfo> values = new ArrayList<>(mModel.getMeetingMembers().values());
            UserInfo currentUser = this.mModel.getCurrentUser();
            values.add(currentUser);
            Collections.sort(values, this.mComparator);
            this.mCacheMemberList = values;
        }

        @Override 
        public void notifyDataSetChanged() {
            updateDataList();
            updateCurrentPageData();
            super.notifyDataSetChanged();
        }

        private final void hidePagerPreview(int index) {
            if (this.mViewCache.size() > index) {
                this.mViewCache.get(index).hidePreview();
            }
        }

        private final void updateCurrentPageData() {
            int size = this.mViewCache.size();
            int i = this.mLastPos;
            if (size > i) {
                this.mViewCache.get(i).updateData();
            }
        }

        public final void onPageSelected(int position) {
            int i = this.mLastPos;
            if (i != position) {
                hidePagerPreview(i);
                updateDataList();
                this.mLastPos = position;
                updateCurrentPageData();
            }
        }

        @Override 
        public boolean isViewFromObject( View view,  Object obj) {
            if(obj instanceof Integer){
                return view == mViewCache.get((int)obj).contentView();
            }else{
                return view == ((ViewHolder)obj).contentView();
            }
        }

        @Override 
        public Object instantiateItem( ViewGroup container, int position) {
            if (this.mCacheMemberList == null) {
                updateDataList();
            }
            while (this.mViewCache.size() <= position) {
                View view = LayoutInflater.from(this.mCtx).inflate(R.layout.meeting_call_pager_item_view, container, false);
                List<ViewHolder> list = this.mViewCache;
                int itemSize = getItemSize();
                AUICallNVNModel meetingCallModel = this.mModel;
                list.add(new ViewHolder(view, itemSize, meetingCallModel, this));
            }
            container.addView(this.mViewCache.get(position).contentView(), new ViewGroup.LayoutParams(-1, -1));
            this.mViewCache.get(position).bindData(position);
            return this.mViewCache.get(position);
        }

        private final int getItemSize() {
            int height = (this.mCtx.getResources().getDisplayMetrics().heightPixels - ((int) ((195 * this.mCtx.getResources().getDisplayMetrics().density) + 0.5d))) / 3;
            int width = (this.mCtx.getResources().getDisplayMetrics().widthPixels - ((int) ((16 * this.mCtx.getResources().getDisplayMetrics().density) + 0.5d))) / 2;
            return Math.min(height, width);
        }

        @Override 
        public void destroyItem( ViewGroup container, int position,  Object obj) {
            container.removeView(((ViewHolder) obj).contentView());
        }
    }

    @Override 
    public View inflateView( Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.meeting_pager_view, getMRootContainerView(), false);
        this.mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        this.mIndicator = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager viewPager = this.mViewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { 
            @Override 
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override 
            public void onPageSelected(int position) {
                MeetingPagerAdapter meetingPagerAdapter;
                meetingPagerAdapter = MemberPagerViewController.this.mViewPagerAdapter;
                if (meetingPagerAdapter == null) {
                    return;
                }
                meetingPagerAdapter.onPageSelected(position);
            }

            @Override 
            public void onPageScrollStateChanged(int state) {
            }
        });
        Context context = getMRootContainerView().getContext();
        this.mViewPagerAdapter = new MeetingPagerAdapter(context, this, mMeetingModel);
        viewPager.setAdapter(this.mViewPagerAdapter);
        TabLayout tabLayout = this.mIndicator;
        tabLayout.setupWithViewPager(this.mViewPager, true);
        return view;
    }

    @Override 
    public void show( Context context) {
        super.show(context);
        if (mMeetingModel.getMeetingMembers().size() > 5) {
            TabLayout tabLayout = this.mIndicator;
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            TabLayout tabLayout2 = this.mIndicator;
            tabLayout2.setVisibility(View.INVISIBLE);
        }
        MeetingPagerAdapter meetingPagerAdapter = this.mViewPagerAdapter;
        meetingPagerAdapter.notifyDataSetChanged();

//        if(meetingPagerAdapter.getCount() <= mViewPager.getCurrentItem()){ //显示页面大于已有页面索引
//            mViewPager.setCurrentItem(meetingPagerAdapter.getCount()-1); //显示最后一页
//        }
    }

    @Override 
    public void updateCurrentMic( UserInfo user) {
    }
}
