package cn.app.robert.data.frame;

import cn.app.robert.data.DataFrame;

public class DataFrameRobot  extends DataFrame {
    @Override
    public byte[] toBytes() {
        return new byte[]{
                handActionIndex,//手部动作 100-110
                footActionIndex, //脚部动作 200-235
                //self.bothActionIndex, //手脚动作 1-93(77除外)
                actionCount, //动作拍数 例如前进2步，拍数为4
                actionSpeed, //动作速度
                lightColor,// 等的颜色  黑 0; 蓝 1; 靛蓝 2; 绿 3; 黄 4; 红 5; 品红 6; 白 7;
                lightModle, // 灯的模式 常亮模式 0;    呼吸模式 1;   彩虹模式 2;   眨眼模式 3;
                breathInterval, //呼吸周期时长
                breathSleepInterval //每次呼吸间隔
        };
    }
}
