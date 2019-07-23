package cn.app.robert.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.app.robert.R;
import cn.app.robert.adapter.SaveActionsAdapter;
import cn.app.robert.entity.Actions;

public class SaveActionsDialog extends Dialog {

    private Context mContext;
    private List<Actions> actions;

    @BindView(R.id.rv_list)
    RecyclerView mRcList;
    private OnItemClickListener listener;

    public SaveActionsDialog(@NonNull Context context, List<Actions> actions) {
        this(context,0);
        this.mContext = context;
        this.actions = actions;
    }

    public SaveActionsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_save_actions);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.RIGHT;
        getWindow().getDecorView().setPadding(0,0,0,0);
        // 给 DecorView 设置背景颜色很重要 不然导致Dialog内容显示不全 有一部分内容会充当padding
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        //设置宽高
        getWindow().setAttributes(params);
        //设置触摸dialog以外，dialog是否消失
        setCanceledOnTouchOutside(true);
        initView();
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRcList.setLayoutManager(linearLayoutManager);
        SaveActionsAdapter saveActionsAdapter = new SaveActionsAdapter(mContext,actions);
        mRcList.setAdapter(saveActionsAdapter);

        saveActionsAdapter.setOnselectActionsListener(new SaveActionsAdapter.OnselectActionsListener() {
            @Override
            public void select(Actions actions, int position) {
                if (listener != null){
                    listener.click(actions,position);
                    dismiss();
                }
            }

            @Override
            public void delete(Actions actions) {
                if (listener != null){
                    listener.del(actions);
                    saveActionsAdapter.notifyDataSetChanged();
                    int count = saveActionsAdapter.getCount();
                    if (count == 0){
                        dismiss();
                    }
                }
            }
        });
    }

    public interface OnItemClickListener{
        void click(Actions actions, int position);

        void del(Actions actions);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.listener = onItemClickListener;
    }
}
