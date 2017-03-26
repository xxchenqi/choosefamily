package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.BR;
import com.eju.zejia.R;
import com.eju.zejia.data.models.FilterBean;
import com.eju.zejia.databinding.ItemFilterEachBinding;
import com.eju.zejia.ui.views.FilterCheckBox;
import com.eju.zejia.ui.views.RecyclerItemView;

import java.util.HashSet;


public final class FilterItemAdapter
        extends BaseRecyclerViewAdapter<FilterBean.FilterItemBean, RecyclerItemView.ViewHolder>
        implements View.OnClickListener {

    private HashSet<Integer> mCurrentSelect;

    private boolean isCollapse = true;

    private int mColumns;
    private int mSpacing;


    public FilterItemAdapter(Context context, int columnCount, int spacing) {
        super(context);

        mColumns = columnCount;
        mSpacing = spacing;
    }

//        public HashSet<Integer> getCurrentSelect() {
//            return mCurrentSelect;
//        }

    public void setCurrentSelect(HashSet<Integer> currentSelect) {
        mCurrentSelect = currentSelect;
    }

    public void setCollapse(boolean collapse) {
        isCollapse = collapse;
        notifyDataSetChanged();
    }

    public void toggleCollapse() {
        isCollapse = !isCollapse;
        if(isCollapse) {
            notifyItemRangeRemoved(mColumns, items.size() - mColumns);
        } else {
            notifyItemRangeInserted(mColumns, items.size() - mColumns);
        }
    }

    @Override
    public RecyclerItemView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterItemEachView(parent).getViewHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerItemView.ViewHolder holder, int position) {
        holder.onBindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return isCollapse ? mColumns : super.getItemCount();
    }

    @Override
    public void onClick(View v) {
        final FilterBean.FilterItemBean key = ((FilterCheckBox) v).getItemBean();
        if(mCurrentSelect.contains(key.getCode())) {
            mCurrentSelect.remove(key.getCode());
        } else {
            mCurrentSelect.add(key.getCode());
        }
    }

    private final class FilterItemEachView extends RecyclerItemView {
        public FilterItemEachView(ViewGroup parent) {
            super(parent);
        }

        @Override
        protected View inflateView() {
            mBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.item_filter_each, mParent, false);
            return mBinding.getRoot();
        }

        @Override
        public ViewHolder getViewHolder() {
            return new ViewHolder() {
                @Override
                public void onBindViewHolder(int position) {
                    FilterBean.FilterItemBean bean = items.get(position);

                    ItemFilterEachBinding binding = (ItemFilterEachBinding) mBinding;
                    binding.setVariable(BR.item_filter_text, bean.getName());
//                        Timber.d("%s, %d, %s", mTitle, position, current.first);
                    binding.itemFilter.setChecked(mCurrentSelect.contains(bean.getCode()));
                    binding.itemFilter.setItemBean(bean);
                    binding.itemFilter.setOnClickListener(FilterItemAdapter.this);
                }
            };
        }
    }

    public final class FilterItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view) % mColumns;
            if(0 == position) {
                outRect.right = mSpacing * 2 / 3;
                outRect.left = 0;

            } else if(mColumns - 1 == position) {
                outRect.right = 0;
                outRect.left = mSpacing * 2 / 3;

            } else {
                outRect.right = mSpacing / 3;
                outRect.left = mSpacing / 3;
            }
            outRect.top = 0;
            outRect.bottom = mSpacing;
        }
    }
}
