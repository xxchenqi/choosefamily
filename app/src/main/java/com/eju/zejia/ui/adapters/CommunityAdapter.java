package com.eju.zejia.ui.adapters;

import android.animation.AnimatorSet;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.eju.zejia.R;
import com.eju.zejia.data.models.Community;
import com.eju.zejia.databinding.ItemCommunityBinding;
import com.eju.zejia.ui.views.RoundCornersDrawable;
import com.eju.zejia.utils.ImageUtil;

/**
 * 首页匹配列表
 * <p>
 * Created by Sidney on 2016/7/25.
 */
public class CommunityAdapter extends BaseRecyclerViewAdapter<Community, CommunityAdapter.ItemViewHolder> {

    private OnItemClickListener onItemClickListener;

    public CommunityAdapter(Context context) {
        super(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCommunityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_community, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        ItemCommunityBinding binding = holder.binding;
        Community community = items.get(position);
        setText(binding.tvName, community.getName());
        setText(binding.tvRegion, community.getRegion());
        setText(binding.tvPlate, community.getPlate());
        setFormatText(binding.tvBuildAge, R.string.build_age, community.getBuildAge());
        if (TextUtils.isEmpty(community.getFeature())) {
            binding.llFeature.setVisibility(View.GONE);
        } else {
            setText(binding.tvFeature, community.getFeature());
        }
        setFormatText(binding.tvPrice, R.string.avg_price, community.getAvgPrice());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageUtil.load(context, community.getPanoUrl(), R.drawable.home_pic, binding.ivCenter);
        }
        //RoundCorners
        else {
            binding.card.setPreventCornerOverlap(false);
            Glide.with(context)
                    .load(community.getPanoUrl())
                    .asBitmap()
                    .placeholder(R.drawable.home_pic)
                    .error(R.drawable.home_pic)
                    .into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            if (!(placeholder instanceof BitmapDrawable)) {
                                binding.ivCenter.setBackground(placeholder);
                                return;
                            }
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) placeholder;
                            loadDrawable(binding, bitmapDrawable.getBitmap());
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            if (!(errorDrawable instanceof BitmapDrawable)) {
                                binding.ivCenter.setBackground(errorDrawable);
                                return;
                            }
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) errorDrawable;
                            loadDrawable(binding, bitmapDrawable.getBitmap());
                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            loadDrawable(binding, resource);
                        }
                    });
        }

        if (community.getIsFollow() == Community.IS_FOLLOW) {
            binding.ibFollow.setImageResource(R.drawable.home_collect_selected_icon);
        } else {
            binding.ibFollow.setImageResource(R.drawable.home_collect_default_icon);
        }
    }

    private void loadDrawable(ItemCommunityBinding binding, Bitmap bitmap) {
        RoundCornersDrawable round = new RoundCornersDrawable(bitmap,
                context.getResources().getDimension(R.dimen.DIMEN_12DP), 0);
        binding.card.setPreventCornerOverlap(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            binding.ivCenter.setBackground(round);
        else
            binding.ivCenter.setBackgroundDrawable(round);
        binding.ivCenter.setImageResource(0);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemCommunityBinding binding;
        private AnimatorSet animatorSet;

        private ItemViewHolder(ItemCommunityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            if (null != onItemClickListener) {
                binding.llFollow.setOnClickListener(new View.OnClickListener() {
                    private boolean checked;

                    @Override
                    public void onClick(View v) {
//                        checked = !checked;
//                        if (checked) {
//                            if (animatorSet != null && animatorSet.isRunning()) {
//                                animatorSet.cancel();
//                            }
//                            animatorSet = new AnimatorSet();
//
//                            ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(binding.ibFollow, "rotation", 0f, 360f);
//                            rotationAnim.setDuration(800);
//                            rotationAnim.setInterpolator(new AccelerateInterpolator());
//
//                            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(binding.ibFollow, "scaleX", 0.2f, 1.2f);
//                            bounceAnimX.setDuration(300);
//                            bounceAnimX.setInterpolator(new OvershootInterpolator(4));
//
//                            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(binding.ibFollow, "scaleY", 0.2f, 1.2f);
//                            bounceAnimY.setDuration(300);
//                            bounceAnimY.setInterpolator(new OvershootInterpolator(4));
//                            bounceAnimY.addListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    binding.ibFollow.setImageResource(R.drawable.home_collect_selected_icon);
//                                }
//                            });
//                            animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
//                            animatorSet.start();
//
//
//                        } else {
//                            binding.ibFollow.setImageResource(R.drawable.home_collect_default_icon);
//                        }
                        onItemClickListener.onFollowClick(binding.ibFollow, getLayoutPosition());
                    }
                });
                binding.card.setOnClickListener(v -> onItemClickListener.onImageClick(v, getLayoutPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        void onFollowClick(ImageView view, int position);

        void onImageClick(View view, int position);
    }
}
