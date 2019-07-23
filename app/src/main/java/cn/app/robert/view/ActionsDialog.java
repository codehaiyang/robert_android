package cn.app.robert.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.app.robert.R;
import cn.app.robert.adapter.ActionsAdapter;
import cn.app.robert.entity.ActionBean;

/**
 * 动作弹框
 * @author daxiong
 */
public class ActionsDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "ActionsDialog";

    private Context mContext;

    @BindView(R.id.rv_actions)
    RecyclerView mRvActions;
    @BindView(R.id.ib_hand)
    ImageButton mIbHand;
    @BindView(R.id.ib_foot)
    ImageButton mIbFoot;
    @BindView(R.id.ib_actions)
    ImageButton mIbActions;
    @BindView(R.id.ll_hand)
    LinearLayout mLlHand;
    @BindView(R.id.ll_foot)
    LinearLayout mLlFoot;
    @BindView(R.id.ll_actions)
    LinearLayout mLlActions;

    private ArrayList<ActionBean> handActions;
    private ArrayList<ActionBean> footActions;
    private ArrayList<ActionBean> actions;
    private ActionsAdapter actionsAdapter;
    private ActionsDialog actionsDialog;

    private int chooseFlag = 1;

    private OnActionClickListener listener = null;

    public ActionsDialog(@NonNull Context context, String page, int flag) {
        this(context,0);
        this.mContext = context;
        this.chooseFlag = flag;
        initData();
    }

    public ActionsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionsDialog = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_actions);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT;
        getWindow().getDecorView().setPadding(0,0,0,0);
        // 给 DecorView 设置背景颜色很重要 不然导致Dialog内容显示不全 有一部分内容会充当padding
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        //设置宽高
        getWindow().setAttributes(params);
        //设置触摸dialog以外，dialog是否消失
        setCanceledOnTouchOutside(true);
        initView();
    }

    /**
     * 初始化view
     */
    @SuppressLint("WrongConstant")
    private void initView() {
        //设置动作列表 初始化 recycleView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, OrientationHelper.VERTICAL, false);
        mRvActions.setLayoutManager(gridLayoutManager);
        mRvActions.addItemDecoration(new ActionsDividerDecoration(mContext));
        actionsAdapter = new ActionsAdapter(mContext,handActions);
        Log.d("chooseFlag", "initView: " + chooseFlag);

        if (chooseFlag == 1){
            mLlHand.setActivated(true);
            changeData(handActions);
        }else if (chooseFlag == 2){
            mLlFoot.setActivated(true);
            changeData(footActions);
        }else if (chooseFlag == 3){
            mLlActions.setActivated(true);
            changeData(actions);
        }


        actionsAdapter.setOnItemClickListener((actionBean) -> {
            if (listener != null){
                listener.onCheckAction(actionBean);
            }
            actionsDialog.dismiss();
        });
        mRvActions.setAdapter(actionsAdapter);
        mIbHand.setOnClickListener(this);
        mIbFoot.setOnClickListener(this);
        mIbActions.setOnClickListener(this);


    }

    private void changeData(ArrayList<ActionBean> data) {
        actionsAdapter.changeData(data);
        actionsAdapter.notifyDataSetChanged();
    }

    /**
     * 定义点击事件的接口
     */
    public interface OnActionClickListener {
        void onCheckAction(ActionBean actionBean);
        void chooseActionType(int flag);
    }

    public void setOnActionClickListener(OnActionClickListener onActionClickListener){
            this.listener = onActionClickListener;
    }

    /**
     * 初始化data
     */
    private void initData() {
        // 手的动作列表
        handActions = new ArrayList<>();
        for (int i = 100; i < 111; i++){
            handActions.add(new ActionBean(i));
        }
        // 脚的动作列表
        footActions = new ArrayList<>();
        for (int i = 200; i < 236; i++){
            if (i == 231){
                continue;
            }
            footActions.add(new ActionBean(i));
        }
        // 组合动作列表
        actions = new ArrayList<>();
        for (int i = 1; i < 94; i++){
            actions.add(new ActionBean(i));
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_hand:
                mLlHand.setActivated(true);
                mLlFoot.setActivated(false);
                mLlActions.setActivated(false);
                changeData(handActions);
                chooseFlag = 1;
                listener.chooseActionType(chooseFlag);
                break;
            case R.id.ib_foot:
                mLlHand.setActivated(false);
                mLlFoot.setActivated(true);
                mLlActions.setActivated(false);
                changeData(footActions);
                chooseFlag = 2;
                listener.chooseActionType(chooseFlag);
                break;
            case R.id.ib_actions:
                mLlHand.setActivated(false);
                mLlFoot.setActivated(false);
                mLlActions.setActivated(true);
                changeData(actions);
                chooseFlag = 3;
                listener.chooseActionType(chooseFlag);
                break;
                default:
        }
    }
}
