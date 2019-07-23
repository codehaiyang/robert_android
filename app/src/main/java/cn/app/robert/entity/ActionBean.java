package cn.app.robert.entity;

/**
 * 动作bean
 * @author daxiong
 */
public class ActionBean {
    /**
     * 动作序号 0-235
     */
    private int action;
    /**
     * 灯光颜色  0 黑; 1 蓝; 2 靛蓝; 3 绿; 4 黄; 5 红; 6 品红; 7 白
     */
    private int color;
    /**
     * 灯光模式 0 常亮模式; 1 呼吸模式; 2 彩虹模式; 3 眨眼模式;
     */
    private int colorMode;
    /**
     * 节怕数
     */
    private int tapCount;
    /**
     * 速度 0 - 3
     */
    private int speed;
    /**
     * 呼吸时长 ms 单位
     */
    private int slashDuration;
    /**
     * 呼吸间隔 ms 单位
     */
    private int sleepInterval;

    /**
     * 音乐时长
     */
    private int musicPosition;

    /**
     * 记录音乐结束时间
     */
    private int lastPosition;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorMode() {
        return colorMode;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    public int getTapCount() {
        return tapCount;
    }

    public void setTapCount(int tapCount) {
        this.tapCount = tapCount;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSlashDuration() {
        return slashDuration;
    }

    public void setSlashDuration(int slashDuration) {
        this.slashDuration = slashDuration;
    }

    public int getSleepInterval() {
        return sleepInterval;
    }

    public void setSleepInterval(int sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    public ActionBean(int action) {
        this.action = action;
    }

    public int getMusicPosition() {
        return musicPosition;
    }

    public void setMusicPosition(int musicPosition) {
        this.musicPosition = musicPosition;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public String toString() {
        return "ActionBean{" +
                "action=" + action +
                ", color=" + color +
                ", colorMode=" + colorMode +
                ", tapCount=" + tapCount +
                ", speed=" + speed +
                ", slashDuration=" + slashDuration +
                ", sleepInterval=" + sleepInterval +
                ", musicPosition=" + musicPosition +
                ", lastPosition=" + lastPosition +
                '}';
    }
}
