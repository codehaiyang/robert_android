package cn.app.robert.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
 * 播放动作列表adapter
 * @author daxiong
 */
public class PlayerActionAdapter extends RecyclerView.Adapter<PlayerActionAdapter.ViewHoler>{

    private static final String TAG = "PlayerActionAdapter";

    private Context mContext;
    /**
     * 动作列表
     */
    private List<ActionBean> actions = new ArrayList<>();

    private OnPalyerActionsItemClickListener listener = null;

    private View mViewEdit;
    private PopupWindow mPopEdit;
    private boolean status = true;
    private ViewHoler viewHoler;

    public PlayerActionAdapter(Context context, ArrayList<ActionBean> actions) {
        this.mContext = context;
        this.actions = actions;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action, parent, false);
        viewHoler = new ViewHoler(view);
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
        holder.mGvAction.setOnClickListener(v -> showActionEdit(v,position));
    }


    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void addAction(ActionBean action) {
        if (actions != null){
            actions.add(action);
            notifyDataSetChanged();
        }
    }

    public void updateAction(ActionBean action,int position) {
        ActionBean actionBean = actions.get(position);
        actionBean.setAction(action.getAction());
        notifyDataSetChanged();
    }

    /**
     * 显示弹框
     * @param view
     */
    private void showActionEdit(View view,int position) {
        mViewEdit = LinearLayout.inflate(mContext, R.layout.pop_action_edit, null);
        mPopEdit = new PopupWindow(mViewEdit, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopEdit.setTouchable(true);
        mPopEdit.setOutsideTouchable(true);
        mPopEdit.getContentView().setFocusableInTouchMode(true);
        mPopEdit.setFocusable(true);

        mPopEdit.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int x0ff;
        int viewWidth = view.getWidth();
        int popWindowWidth = mViewEdit.getMeasuredWidth();
        x0ff = viewWidth  / 2 - popWindowWidth / 2;
        int yoff = mViewEdit.getMeasuredHeight() + view.getMeasuredHeight();
        mPopEdit.showAsDropDown(view,x0ff,-yoff,Gravity.BOTTOM);

        ImageButton ibDel = mViewEdit.findViewById(R.id.ib_del);
        ImageButton ibEdit = mViewEdit.findViewById(R.id.ib_edit);
        // 删除动作 删除选择及后面所有的动作
        ibDel.setOnClickListener(v -> {
            List<ActionBean> delArr = new ArrayList<>();
            int size = actions.size();
            for (int i = position ; i < size;i++){
                delArr.add(actions.get(i));
            }
            actions.removeAll(delArr);
            mPopEdit.dismiss();
            notifyDataSetChanged();
            if (listener != null){
                listener.del(delArr);
            }
        });
        // 替换动作 替换当前的动作并删除掉后面的动作
        ibEdit.setOnClickListener(v -> {
            mPopEdit.dismiss();
            List<ActionBean> delArr = new ArrayList<>();
            int size = actions.size();
            for (int i = position+1 ; i < size;i++){
                delArr.add(actions.get(i));
            }
            actions.removeAll(delArr);
            notifyDataSetChanged();
            if (listener != null){
                listener.edit(position);
            }
        });
    }

    public void setPresenterStatus(boolean presentionStatus) {
        viewHoler.mGvAction.setClickable(presentionStatus);
    }

    public interface OnPalyerActionsItemClickListener{
        void edit(int position);
        void del(List<ActionBean> actions);
    }

    public void setOnPalyerActionsItemClickListener(OnPalyerActionsItemClickListener onPalyerActionsItemClickListener){
        this.listener = onPalyerActionsItemClickListener;
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
