package cn.app.robert.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.app.robert.R;
import cn.app.robert.adapter.MusicAdapter;
import cn.app.robert.entity.Song;

/**
 * @author daxiong
 */
public class MusicListDialog extends Dialog {

    private static final String TAG = "MusicListDialog";

    private Context mContext;

    private List<Song> mSongList = new ArrayList<>();

    private OnItemClickListener listener;
    private MusicAdapter musicAdapter;

    public MusicListDialog(@NonNull Context context, List<Song> songList) {
        this(context,0);
        this.mContext = context;
        this.mSongList = songList;
    }

    public MusicListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_music);
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
        mRecyclerView.setLayoutManager(linearLayoutManager);

        musicAdapter = new MusicAdapter(mSongList);
        mRecyclerView.setAdapter(musicAdapter);

        musicAdapter.setOnselectMusicListener(new MusicAdapter.OnselectMusicListener() {
            @Override
            public void select(Song song) {
                if (listener != null){
                    listener.click(song);
                    dismiss();
                }
            }

            @Override
            public void del(Song song,int position) {
                if (listener != null){
                    listener.del(song,position);
                }
            }
        });
    }

    public void updateSong(Song song,int position) {
        musicAdapter.changeDate(song,position);
    }

    public interface OnItemClickListener{
        void click(Song song);
        void del(Song song, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.listener = onItemClickListener;
    }
}
