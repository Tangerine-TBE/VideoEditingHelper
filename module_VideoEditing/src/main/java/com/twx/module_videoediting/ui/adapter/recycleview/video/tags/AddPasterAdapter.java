package com.twx.module_videoediting.ui.adapter.recycleview.video.tags;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.ugckit.UGCKitImpl;
import com.tencent.qcloud.ugckit.module.effect.BaseRecyclerAdapter;
import com.tencent.qcloud.ugckit.module.effect.paster.TCPasterInfo;
import com.twx.module_base.utils.SPUtil;
import com.twx.module_videoediting.R;
import com.twx.module_videoediting.domain.PasterInfo;
import com.twx.module_videoediting.utils.Constants;


import java.util.List;

/**
 * @author wujinming QQ:1245074510
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.tags
 * @class describe
 * @time 2021/4/20 16:16:54
 * @class describe
 */
public class AddPasterAdapter  extends BaseRecyclerAdapter<AddPasterAdapter.AddPasterViewHolder> {
    public static final int TYPE_FOOTER = 0;  // 带有Footer的
    public static final int TYPE_NORMAL = 1;  // 真实数据
    private Context mContext;

    private View mFooterView;

    private List<PasterInfo> mPasterInfoList;
    private int mCurrentSelectedPos = -1;
    private int mPasterTextSize;
    private int mPasterTextColor;
    private int mCoverIcon;

    private boolean themeState= SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE);

    public AddPasterAdapter(List<PasterInfo> pasterInfoList, Context context) {
        mContext = context;
        mPasterInfoList = pasterInfoList;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setCurrentSelectedPos(int pos) {
        int tPos = mCurrentSelectedPos;
        mCurrentSelectedPos = pos;
        this.notifyItemChanged(tPos);
        this.notifyItemChanged(mCurrentSelectedPos);
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void onBindVH(@NonNull AddPasterViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            return;
        }
        PasterInfo pasterInfo = mPasterInfoList.get(position);

        int pasterPath =pasterInfo.getIcon();

        holder.ivAddPaster.setImageResource(pasterPath);


        holder.tvAddPasterText.setText(pasterInfo.getName()+"");


        holder.tvAddPasterText.setTextColor(ContextCompat.getColor(holder.tvAddPasterText.getContext(),themeState?R.color.black:R.color.white));


        if (mCoverIcon != 0) {
            if (mPasterTextSize != 0) {
                holder.tvAddPasterText.setTextSize(mPasterTextSize);
            }
            if (mPasterTextColor != 0) {
                holder.tvAddPasterText.setTextColor(mContext.getResources().getColor(mPasterTextColor));
            }
            holder.ivAddPasterTint.setImageResource(mCoverIcon);
        }
        if (mCurrentSelectedPos == position) {
            holder.ivAddPasterTint.setVisibility(View.VISIBLE);
        } else {
            holder.ivAddPasterTint.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public AddPasterViewHolder onCreateVH(@NonNull ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new AddPasterViewHolder(mFooterView);
        }
        return new AddPasterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_paster, parent, false));
    }

    @Override
    public int getItemCount() {
        if (mFooterView != null) {
            return mPasterInfoList.size() + 1;
        }
        return mPasterInfoList.size();
    }

    public void setPasterTextSize(int textSize) {
        mPasterTextSize = textSize;
    }

    public void setPasterTextColor(int textColor) {
        mPasterTextColor = textColor;
    }

    public void setCoverIconResouce(int icon) {
        mCoverIcon = icon;
    }

    public class AddPasterViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAddPaster;
        ImageView ivAddPasterTint;
        TextView tvAddPasterText;

        public AddPasterViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView == mFooterView) {
                return;
            }
            ivAddPaster = (ImageView) itemView.findViewById(R.id.add_paster_image);
            ivAddPasterTint = (ImageView) itemView.findViewById(R.id.add_paster_tint);
            tvAddPasterText = (TextView) itemView.findViewById(R.id.add_paster_tv_name);
        }
    }
}
