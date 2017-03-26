package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.BR;
import com.eju.zejia.R;
import com.eju.zejia.data.models.Filter;
import com.eju.zejia.data.models.FilterBean;
import com.eju.zejia.databinding.ItemFilterBinding;
import com.eju.zejia.ui.views.RecyclerItemView;
import com.eju.zejia.utils.UIUtils;

import java.util.List;

public final class FilterAdapter extends BaseRecyclerViewAdapter<Filter, RecyclerItemView.ViewHolder> {

    private static final int TYPE_DIVIDER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LAST_ITEM = 2;

    private static final int GRID_ITEM_SPACING = UIUtils.dip2px(15);
    private static final int ITEM_NUM_PER_ROW = 3;

    private static final int DIVIDER_GRAY_HEIGHT = UIUtils.dip2px(12);
    private static final int DIVIDER_WHITE_HEIGHT = UIUtils.dip2px(16);
    private static final int DIVIDER_LAST_WHITE_HEIGHT = UIUtils.dip2px(30);


    public FilterAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerItemView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Timber.d("create filter view ui");
        final RecyclerItemView view;
        final RecyclerItemView.ViewHolder vh;
        switch (viewType) {
            case TYPE_LAST_ITEM:
            case TYPE_ITEM:
                view = new FilterItemView(parent);
                break;

            default:
            case TYPE_DIVIDER:
                view = new RecyclerItemView.Divider(parent, DIVIDER_GRAY_HEIGHT, UIUtils.getColor(R.color.gray_F3F3F3));
                break;
        }

        vh = view.getViewHolder();
        vh.itemView.setTag(viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerItemView.ViewHolder holder, int position) {
        //Timber.d("bind filter view data %d", position);
        holder.onBindViewHolder(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (items.size() - 1 == position) {
            return TYPE_LAST_ITEM;
        }

        String title = items.get(position).getTitle();
        String nextTitle = items.get(position + 1).getTitle();

        if (nextTitle.isEmpty()) {
            return TYPE_LAST_ITEM;
        }

        if (title.isEmpty()) {
            return TYPE_DIVIDER;
        } else {
            return TYPE_ITEM;
        }
    }

    private final class FilterItemView extends RecyclerItemView implements View.OnClickListener {

        public FilterItemView(ViewGroup parent) {
            super(parent);
        }

        private FilterItemAdapter mAdapter = new FilterItemAdapter(context, ITEM_NUM_PER_ROW, GRID_ITEM_SPACING);
        private FilterItemAdapter.FilterItemDecoration mDecoration = mAdapter.new FilterItemDecoration();

        @Override
        protected View inflateView() {
//            Timber.d("inflate filter view");
            mBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.item_filter, mParent, false);

            RecyclerView rcv = ((ItemFilterBinding) mBinding).itemFilterGdv;
            rcv.setLayoutManager(new GridLayoutManager(context, ITEM_NUM_PER_ROW) {
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            rcv.setAdapter(mAdapter);
            rcv.addItemDecoration(mDecoration);

            return mBinding.getRoot();
        }

        @Override
        public ViewHolder getViewHolder() {
            return new ViewHolder() {
                @Override
                public void onBindViewHolder(int position) {
                    ItemFilterBinding binding = (ItemFilterBinding) mBinding;
                    Filter each = items.get(position);

                    binding.setVariable(BR.filter_item_title, each.getTitle());
                    binding.setVariable(BR.filter_item_description, each.getDescription());

                    FilterItemAdapter adapters = (FilterItemAdapter) binding.itemFilterGdv.getAdapter();//= mDataAdapter.getFilterItemAdapter(position);
                    List<FilterBean.FilterItemBean> list = each.getItems();
                    if(ITEM_NUM_PER_ROW < list.size()) {
                        binding.itemFilterCollapseRoot.setVisibility(View.VISIBLE);
                        binding.itemFilterCollapseRoot.setTag(position);
                        binding.itemFilterCollapseRoot.setOnClickListener(FilterItemView.this);

                        boolean isCollapse = each.isCollapse();
                        adapters.setCollapse(isCollapse);
                        if(isCollapse) {
                            binding.itemFilterCollapse.setRotation(0);
                        } else {
                            binding.itemFilterCollapse.setRotation(180);
                        }

                    } else {
                        binding.itemFilterCollapseRoot.setVisibility(View.GONE);
                        binding.itemFilterCollapseRoot.setOnClickListener(null);
                        binding.itemFilterCollapseRoot.setTag(null);
                    }

                    adapters.setCurrentSelect(each.getChecked());
                    adapters.updateItems(list, false);
//                    binding.itemFilterGdv.setAdapter(adapters);
                }
            };
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            items.get(position).toggleCollapse();
            ((FilterItemAdapter) ((ItemFilterBinding) mBinding).itemFilterGdv.getAdapter())
                    .toggleCollapse();

            if(items.get(position).isCollapse()) {
                ((ItemFilterBinding) mBinding).itemFilterCollapse
                        .animate()
                        .rotation(0.0f)
                        .start();
            } else {
                ((ItemFilterBinding) mBinding).itemFilterCollapse
                        .animate()
                        .rotation(180.0f)
                        .start();
            }
        }
    }

    public static final class FilterDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider = new ColorDrawable(Color.WHITE);

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int count = parent.getChildCount();
            for(int i = 0; i < count; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + lp.bottomMargin;
                int bottom = top;
                int viewType = (int) child.getTag();
                switch(viewType) {
                    case TYPE_DIVIDER:
                        break;
                    case TYPE_LAST_ITEM:
                        bottom += DIVIDER_LAST_WHITE_HEIGHT - GRID_ITEM_SPACING;
                        break;
                    case TYPE_ITEM:
                        bottom += DIVIDER_WHITE_HEIGHT - GRID_ITEM_SPACING;
                        break;
                }
                mDivider.setBounds(left, top , right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int viewType = (int) view.getTag();
            switch(viewType) {
                case TYPE_DIVIDER:
                    break;

                case TYPE_LAST_ITEM:
                    outRect.set(0, 0, 0, DIVIDER_LAST_WHITE_HEIGHT - GRID_ITEM_SPACING);
                    break;

                case TYPE_ITEM:
                    outRect.set(0, 0, 0, DIVIDER_WHITE_HEIGHT - GRID_ITEM_SPACING);
                    break;
            }
        }
    }
}
