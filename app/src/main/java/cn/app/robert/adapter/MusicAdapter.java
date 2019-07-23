package cn.app.robert.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.app.robert.R;
import cn.app.robert.entity.Song;

/**
 * @author daxiong
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHoler>{

    private static final String TAG = "MusicAdapter";

    private List<Song> mSongList = new ArrayList<>();

    public MusicAdapter(List<Song> songList) {
        this.mSongList = songList;
    }

    private OnselectMusicListener listener;

    /**
     * 创建viewholder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        ViewHoler viewHoler = new ViewHoler(view);
        return viewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        Song song = mSongList.get(position);
        if(song != null) {
            String num = "";
            if (position <= 9){
                num = "0" + position;
            }else {
                num = position + "";
            }
            holder.mTvNum.setText(num);
            holder.mTvMusicName.setText(song.getName());
            holder.mTvDuration.setText(song.duration);
        }else {
            holder.mLinearLayout.setVisibility(View.GONE);
        }
        holder.mLinearLayout.setOnClickListener(v -> {
            if (listener != null){
                listener.select(song);
            }
        });
        if (song.isSaveStaus()){
            holder.rlCancle.setVisibility(View.VISIBLE);
            holder.mTvDuration.setText(song.saveDuration);
            holder.rlCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.del(song,position);
                    }
                }
            });
        }else {
            holder.rlCancle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public void changeDate(Song song, int position) {
        Song song1 = mSongList.get(position);
        song1 = song;
        notifyDataSetChanged();
    }

    public interface OnselectMusicListener{
        void select(Song song);
        void del(Song song, int position);
    }

    public void setOnselectMusicListener(OnselectMusicListener onselectMusicListener){
        this.listener = onselectMusicListener;
    }

    static class ViewHoler extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_num)
        TextView mTvNum;
        @BindView(R.id.tv_music_name)
        TextView mTvMusicName;
        @BindView(R.id.tv_duration)
        TextView mTvDuration;
        @BindView(R.id.ll_itme)
        LinearLayout mLinearLayout;
        @BindView(R.id.rl_cancle)
        RelativeLayout rlCancle;
        @BindView(R.id.ib_cancle)
        ImageButton ibCancle;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
