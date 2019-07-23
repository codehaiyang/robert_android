package cn.app.robert.data;


public abstract  class DataFrame {

    protected byte  actionIndex; //手脚动作 1-93(77除外) | 动作
    protected byte  handActionIndex; //手部动作 100-110
    protected byte  footActionIndex; //脚部动作 200-235
    protected byte  actionCount; //动作拍数 例如前进2步，拍数为4
    protected byte  actionSpeed; //动作速度
    protected byte  lightColor; // 等的颜色  黑 0; 蓝 1; 靛蓝 2; 绿 3; 黄 4; 红 5; 品红 6; 白 7;
    protected byte  lightModle; // 灯的模式 常亮模式 0;    呼吸模式 1;   彩虹模式 2;   眨眼模式 3;
    protected byte  breathInterval; //呼吸周期时长
    protected byte  breathSleepInterval; //每次呼吸间隔

    public byte getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(byte actionIndex) {
        this.actionIndex = actionIndex;
    }

    public byte getHandActionIndex() {
        return handActionIndex;
    }

    public void setHandActionIndex(byte handActionIndex) {
        this.handActionIndex = handActionIndex;
    }

    public byte getFootActionIndex() {
        return footActionIndex;
    }

    public void setFootActionIndex(byte footActionIndex) {
        this.footActionIndex = footActionIndex;
    }

    public byte getActionCount() {
        return actionCount;
    }

    public void setActionCount(byte actionCount) {
        this.actionCount = actionCount;
    }

    public byte getActionSpeed() {
        return actionSpeed;
    }

    public void setActionSpeed(byte actionSpeed) {
        this.actionSpeed = actionSpeed;
    }

    public byte getLightColor() {
        return lightColor;
    }

    public void setLightColor(byte lightColor) {
        this.lightColor = lightColor;
    }

    public byte getLightModle() {
        return lightModle;
    }

    public void setLightModle(byte lightModle) {
        this.lightModle = lightModle;
    }

    public byte getBreathInterval() {
        return breathInterval;
    }

    public void setBreathInterval(byte breathInterval) {
        this.breathInterval = breathInterval;
    }

    public byte getBreathSleepInterval() {
        return breathSleepInterval;
    }

    public void setBreathSleepInterval(byte breathSleepInterval) {
        this.breathSleepInterval = breathSleepInterval;
    }

    public abstract byte[] toBytes();

    public static void copyDataFrame(DataFrame src, DataFrame des) {
        des.actionIndex = src.actionIndex; //手脚动作 1-93(77除外) | 动作
        des.handActionIndex = src.handActionIndex; //手部动作 100-110
        des.footActionIndex = src.footActionIndex; //脚部动作 200-235
        des.actionCount = src.actionCount; //动作拍数 例如前进2步，拍数为4
        des.actionSpeed = src.actionSpeed; //动作速度
        des.lightColor = src.lightColor; // 等的颜色  黑 0; 蓝 1; 靛蓝 2; 绿 3; 黄 4; 红 5; 品红 6; 白 7;
        des.lightModle = src.lightModle; // 灯的模式 常亮模式 0;    呼吸模式 1;   彩虹模式 2;   眨眼模式 3;
        des.breathInterval = src.breathInterval; //呼吸周期时长
        des.breathSleepInterval = src.breathSleepInterval; //每次呼吸间隔
    }

}
