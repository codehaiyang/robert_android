package cn.app.robert.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.app.robert.R;
import cn.app.robert.entity.ActionBean;
import pl.droidsonroids.gif.GifImageView;

/**
 * 动作列表adapter
 * @author daxiong
 */
public class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.ViewHoler>{

    private static final String TAG = "ActionsAdapter";
    private Context mContext;
    /**
     * 动作列表
     */
    private List<ActionBean> actions = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public ActionsAdapter(Context context, ArrayList<ActionBean> actions) {
        this.mContext = context;
        this.actions = actions;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action, parent, false);
        ViewHoler viewHoler = new ViewHoler(view);
        return viewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        ActionBean actionBean = actions.get(position);
        int action = actionBean.getAction();
        Resources resources = mContext.getResources();
        int drawable;
        drawable = resources.getIdentifier("ac_" + action, "drawable", mContext.getPackageName());

        holder.mGvAction.setImageResource(drawable);
        //给条目设置点击事件
        holder.mGvAction.setOnClickListener(v -> {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(actionBean);
            }
        });
    }

    /**
     * 设置listener事件并初始化
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 定义点击事件的接口
     */
    public interface OnItemClickListener {
        void onItemClick(ActionBean actionBean);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void changeData(ArrayList<ActionBean> actions){
        this.actions = actions;
    }

    static class ViewHoler extends RecyclerView.ViewHolder{

        @BindView(R.id.gv_action)
        GifImageView mGvAction;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
