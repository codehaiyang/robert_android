package cn.app.robert.data.frame;

import cn.app.robert.data.DataFrame;

public class DataFrameOur extends DataFrame {
    @Override
    public byte[] toBytes() {
        return new byte[]{
                actionIndex,//手部动作 100-110
                actionCount, //动作拍数 例如前进2步，拍数为4
                actionSpeed
        };
    }
}
