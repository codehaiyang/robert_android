package cn.app.robert.utils;

public interface RobotBleListener {

    public static  final int ConnectStatusSuccess = 1;
    public static  final int ConnectStatusBreak = -1;
    public static  final int ConnectStatusFail = 0;

    public void blueStautsChange(int status);
    public void blueValueChange(byte[] value);
}
