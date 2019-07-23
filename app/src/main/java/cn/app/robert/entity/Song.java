package cn.app.robert.entity;

/**
 * @author daxiong
 */
public class Song {

    /**
     * 歌曲名
     */
    public String name;
    /**
     * 歌手
     */
    public String singer;
    /**
     * 歌曲所占空间大小
     */
    public long size;
    /**
     * 歌曲时间长度
     */
    public String duration;
    /**
     * 歌曲地址
     */
    public String path;
    /**
     * 图片id
     */
    public long albumId;
    /**
     * 歌曲id
     */
    public long id;

    /**
     * 当前歌曲是否保存有动作
     */
    public boolean saveStaus;

    public String saveDuration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSaveStaus() {
        return saveStaus;
    }

    public void setSaveStaus(boolean saveStaus) {
        this.saveStaus = saveStaus;
    }

    public String getSaveDuration() {
        return saveDuration;
    }

    public void setSaveDuration(String saveDuration) {
        this.saveDuration = saveDuration;
    }
}
