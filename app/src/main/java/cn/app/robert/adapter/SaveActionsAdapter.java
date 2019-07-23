package cn.app.robert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.app.robert.R;
import cn.app.robert.entity.Actions;
import cn.app.robert.utils.LocalMusicUtils;

/**
 * 动作列表
 * @author daxiong
 */
public class SaveActionsAdapter extends RecyclerView.Adapter<SaveActionsAdapter.ViewHolder> {

    private static final String TAG = "SaveActionsAdapter";

    private final Context mContext;

    private final List<Actions> mActions;
    private OnselectActionsListener listener;

    public SaveActionsAdapter(Context context, List<Actions> actions) {
        this.mContext = context;
        this.mActions = actions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_actions, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actions actions = mActions.get(position);
        String num = "";
        if (position <= 9){
            num = "0" + position;
        }else {
            num = position + "";
        }
        holder.tvnum.setText(num);
        holder.tvMusicName.setText(actions.getName());
        holder.tvDuraiton.setText(LocalMusicUtils.formatTime(actions.getDuration()));
        holder.mLinearLayout.setOnClickListener(v -> {
            if (listener != null){
                listener.select(actions,position);
            }
        });
        holder.ibCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActions.remove(position);
                notifyDataSetChanged();
                if (listener != null){
                    listener.delete(actions);
                }
            }
        });
        holder.rlCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActions.remove(position);
                notifyDataSetChanged();
                if (listener != null){
                    listener.delete(actions);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActions.size();
    }

    public int getCount(){
        return mActions.size();
    }

    public interface OnselectActionsListener{
        void select(Actions actions, int position);
        void delete(Actions actions);
    }

    public void setOnselectActionsListener(OnselectActionsListener onselectActionsListener){
        this.listener = onselectActionsListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_num)
        TextView tvnum;
        @BindView(R.id.tv_music_name)
        TextView tvMusicName;
        @BindView(R.id.tv_duration)
        TextView tvDuraiton;
        @BindView(R.id.ib_cancle)
        ImageButton ibCancle;
        @BindView(R.id.ll_itme)
        LinearLayout mLinearLayout;
        @BindView(R.id.rl_cancle)
        RelativeLayout rlCancle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
