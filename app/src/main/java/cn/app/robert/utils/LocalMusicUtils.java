package cn.app.robert.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.app.robert.entity.Song;


/**
 * @author daxiong
 */
public class LocalMusicUtils {

    public static List<Song> list;
    public static Song song;
    //获取专辑封面的Uri
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static List<Song> getMusics(Context context){
        list = new ArrayList<>();
        //initMusic();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor != null){
            while (cursor.moveToNext()){
                LocalMusicUtils.song = new Song();
                LocalMusicUtils.song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                LocalMusicUtils.song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                // 过滤时间短的歌曲
                if (duration == 0){
                    continue;
                }
                LocalMusicUtils.song.setDuration(formatTime(duration));
                LocalMusicUtils.song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                LocalMusicUtils.song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                LocalMusicUtils.song.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
                LocalMusicUtils.song.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                String name = LocalMusicUtils.song.getName();
                String singer = LocalMusicUtils.song.getSinger();
                if (LocalMusicUtils.song.getSize() > 1000 * 800) {
                    if (name.contains("-")) {
                        String[] str = name.split("-");
                        singer = str[0];
                        LocalMusicUtils.song.setSinger(singer);
                        name = str[1];
                        LocalMusicUtils.song.setName(name);
                    } else {
                        LocalMusicUtils.song.setName(name);
                    }
                }
                // 去掉后缀
                String replace = song.getName().replace(".mp3", "");
                song.setName(replace);
                list.add(LocalMusicUtils.song);
            }
        }
        cursor.close();
        return list;
    }

    private static void initMusic() {
        Song allFallsDown = new Song();
        allFallsDown.setName("All Falls Down");
        list.add(allFallsDown);
        Song monsters = new Song();
        monsters.setName("monsters");
        list.add(monsters);
        Song moveYourBody = new Song();
        moveYourBody.setName("move Your Body");
        list.add(moveYourBody);
        Song nevada = new Song();
        nevada.setName("nevada");
        list.add(nevada);
        Song oldTownRoad = new Song();
        oldTownRoad.setName("old Town Road");
        list.add(oldTownRoad);
        Song overTheHorizon = new Song();
        overTheHorizon.setName("over The Horizon");
        list.add(overTheHorizon);
        Song someoneYouLoved = new Song();
        someoneYouLoved.setName("someone You Loved");
        list.add(someoneYouLoved);
        Song somethingJustLikeThis = new Song();
        somethingJustLikeThis.setName("something Just Like This");
        list.add(somethingJustLikeThis);
        Song thatGirl = new Song();
        thatGirl.setName("that Girl");
        list.add(thatGirl);
        Song wayBackHome = new Song();
        wayBackHome.setName("way Back Home");
        list.add(wayBackHome);
        Song wolves = new Song();
        wolves.setName("wolves");
        list.add(wolves);
        Song youNeedToCalmDown = new Song();
        youNeedToCalmDown.setName("you Need To Calm Down");
        list.add(youNeedToCalmDown);
    }

    /**
     * 转换歌曲时间的格式
     * @param time
     * @return
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
            return tt;
        } else {
            String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
            return tt;
        }
    }

    /**
     * 根据专辑ID获取专辑封面图
     * @param album_id 专辑ID
     * @return
     */
    public static String getAlbumArt(Context context,long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Long.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        String path = null;
        if (album_art != null) {
            path = album_art;
        } else {
            //path = "drawable/music_no_icon.png";
            //bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        }
        return path;
    }

    /**
     * 计算播放时间
     * @param time
     * @return
     */
    public static String calculateTime(int time){
        int minute;
        int second;
        if(time > 60){
            minute = time / 60;
            second = time % 60;
            //分钟再0~9
            if(minute >= 0 && minute < 10){
                //判断秒
                if(second >= 0 && second < 10){
                    return "0"+minute+":"+"0"+second;
                }else {
                    return "0"+minute+":"+second;
                }
            }else {
                //分钟大于10再判断秒
                if(second >= 0 && second < 10){
                    return minute+":"+"0"+second;
                }else {
                    return minute+":"+second;
                }
            }
        }else if(time < 60){
            second = time;
            if(second >= 0 && second < 10){
                return "00:"+"0"+second;
            }else {
                return "00:"+ second;
            }
        }
        return null;
    }

    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    public static Bitmap getArtworkFromFile(Context context, long songid, long albumid){
        Bitmap bm = null;
        if(albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumid < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // 只进行大小判断
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

}
