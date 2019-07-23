package cn.app.robert.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.app.robert.data.frame.DataFrameOur;
import cn.app.robert.data.frame.DataFrameRobot;
import cn.app.robert.utils.ByteUtils;

public class DataPackage {


    //static NSInteger MAX_FRAME_LENGTH = 100;
    public static final byte PACKAGE_START_TAG[] = {(byte)0xaa,(byte)0xaa,(byte)0xcc};
    public static final byte PACKAGE_START_TAG_LEN = 3;
    public static final byte PACKAGE_END_TAG[] = {(byte)0x55,(byte)0x55};
    public static final byte PACKAGE_END_TAG_LEN= 2;

    //命令字
    public static final byte DataPackageTypeRobortCommand[] = {0x32};
    public static final byte DataPackageTypeOurCommand[] = {0x03};
    public static final byte DataPackageTypeStopCommand[] = {0x0c};
    public static final byte DataPackageTypeResponseCommand[] = {0x0d};

    //帧结尾
    public static final byte DataPackageTypeRobortFrameEnd[] = {0x01,0x01};
    public static final byte FRAME_END_TAG_LEN = 2;

    public static final int ResponseAtIndex = 5;


    private int frameLength;
    private int packageType;
    private List<DataFrame> dataArray = new ArrayList<>(); //帧数据
    private byte[] packageData; //完整数据

    /**
     *
     * @param type DataPackageType
     */
    public DataPackage(int type){
        this.packageType = type;
    }

    public DataFrame createDataFrame(){
        if(this.packageType == DataPackageType.DataPackageTypeRobort){
            return new DataFrameRobot();
        }else {
            return new DataFrameOur();
        }
    }

    public byte[] toBytes() {

        byte[] result= null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //包头
            baos.write(PACKAGE_START_TAG);

            //命令字及帧
            int lenght = 0;
            switch (packageType) {
                case DataPackageType.DataPackageTypeRobort: {
                    baos.write(DataPackageTypeRobortCommand);
                }
                break;
                case DataPackageType.DataPackageTypeOur: {
                    baos.write(DataPackageTypeOurCommand);
                }
                break;
                case DataPackageType.DataPackageTypeStop: {
                    baos.write(DataPackageTypeStopCommand);
                }
                break;
                case DataPackageType.DataPackageTypeResponse:{
                    baos.write(DataPackageTypeResponseCommand);
                }
                break;
            }

            //帧数
            baos.write(ByteUtils.convertToBytes(dataArray.size(),1));

            //循环数据
            for(DataFrame data : dataArray) {
                //[self appendFrameData:data];
                baos.write(data.toBytes());
                //包尾
                if(packageType== DataPackageType.DataPackageTypeRobort) {
                    baos.write(DataPackageTypeRobortFrameEnd);
                }
            }



            baos.write(PACKAGE_END_TAG);

            result = baos.toByteArray();

        }catch (IOException ioe) {

        }
        return result;
    }

    public void appendFrame(DataFrame dataframe) {
        dataArray.add(dataframe);

    }

    public void removeFrameAt(int index) {
        dataArray.remove(index);

    }

    public void updateFrame(DataFrame  dataFrame, int index) {
        DataFrame frame  = dataArray.get(index);
        DataFrame.copyDataFrame(dataFrame,frame);
    }


}
