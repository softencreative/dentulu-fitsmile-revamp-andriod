package com.app.fitsmile.intra.utils;


import com.app.fitsmile.app.AppController;

public class NativeLibs
{
    public static final short FORMAT_VIDEO_MJPEG = 1;
    public static final short FORMAT_VIDEO_H264_I = 2;
    public static final short FORMAT_VIDEO_H264_P = 3;
    public static final short FORMAT_VIDEO_YUYV = 4;


    private long mNativePtr;

    static {
        System.loadLibrary("mlcamera-2.0");
    }

    public NativeLibs()
    {
        // TODO Auto-generated constructor stub
        mNativePtr = nativeCreateCamera(AppController.defaultIpAddr);
    }

    public void destroyCamera() {
        nativeDestroyCamera(mNativePtr);
        mNativePtr = 0;
    }

    public boolean startPreview() {
        return nativeStartPreview(mNativePtr);
    }

    public void stopPreview() {
        nativeStopPreview(mNativePtr);
    }

    public byte[] getOneFrameBuffer() {
        return nativeGetFrameBuffer(mNativePtr);
    }

    public int getVideoFormat(StreamSelf.VideoParams obj) {
        return nativeGetVideoFormat(mNativePtr, obj);
    }

    public boolean nativeIsDeviceConnected() {
        return nativeIsDeviceConnected(mNativePtr);
    }

    public int decodeUsbData(byte[] data, int size) {
        return nativeUsbDecodeData(mNativePtr, data, size);
    }

    /* Below for Live Stream */
    private static native long nativeCreateCamera(String ip);

    private static native void nativeDestroyCamera(long id_cam);

    private static native boolean nativeStartPreview(long id_cam);

    private static native void nativeStopPreview(long id_cam);

    private static native byte[] nativeGetFrameBuffer(long id_cam);

    public static native byte[] nativeGetPassErrorBuf();

    private static native int nativeGetVideoFormat(long id_cam, StreamSelf.VideoParams p);

    private static native boolean nativeIsDeviceConnected(long id_cam);

    public native static int nativeUsbDecodeData(long id_cam, byte[] data, int size);

    /* Below for AVI Record */
    public native static boolean nativeAVIRecStart(String absPath);

    public native static void nativeAVIRecStop();

    public native static void nativeAVIRecSetParams(int w, int h, int fps);

    public native static void nativeAVIRecSetAudioParams(int channel, int sampleRate, int bit);

    public native static void nativeAVIRecAddData(byte[] data, int size);

    public native static void nativeAVIRecAddWav(byte[] data, int size);

    public native static int nativeAVIRecGetTimestamp();

    /* Below for AVI Play */
    public native static boolean nativeAVIOpen(String absPath);

    public native static void nativeAVIClose();

    public native static double nativeAVIGetTotalTime();

    public native static int nativeAVIGetTotalFrame();

    public native static byte[] nativeAVIGetFrameAtIndex(int index);

    public native static byte[] nativeAVIGetFrameAtTime(double time);

    public native static byte[] nativeAVIGetVoiceAtTime(double time);


    /* Below for Commands */
    /**
     * ???????????????wifi??????
     * @param ip    ??????ip??????
     * @param wifiName
     * @return
     */
    public native static int nativeCmdSetName(String ip, String wifiName);

    /**
     * ???????????????wifi??????
     * @param ip    ??????ip??????
     * @param password
     * @return
     */
    public native static int nativeCmdSetPassword(String ip, String password);

    /**
     * ????????????
     * @param ip    ??????ip??????
     * @return
     */
    public native static int nativeCmdClearPassword(String ip);

    /**
     * ????????????
     * @param ip    ??????ip??????
     * @return
     */
    public native static int nativeCmdSendReboot(String ip);

    /**
     * ???????????????
     * @param ip    ??????ip??????
     * @param width
     * @param height
     * @param frameRate
     * @return
     */
    public native static int nativeCmdSetResolution(String ip, int width, int height, int frameRate);

    /**
     * ????????????????????????????????????byte[]??????????????????CmdSocket????????????
     * @param ip    ??????ip??????
     * @return
     */
    public native static byte[] nativeCmdGetResolution(String ip);

    /**
     * ?????????????????????Key??????????????????
     * @param ip    ??????ip??????
     * @return  1 key, 2 uart
     */
    public native static int nativeCmdGetKeyOrUart(String ip);

    /**
     * ??????????????????
     * @param ip    ??????ip??????
     * @return  1 ?????????2 ??????
     */
    public native static int nativeCmdGetRemoteKey(String ip);

    public native static int nativeCmdSetChannel(String ip, int channel);



    public native static int nativeCmdGetDeviceType(String ip);

    public native static int nativeCmdSetDeviceType(String ip, int type);


    public native static int nativeCmdResetCameraParams(String ip);

    public native static int nativeCmdSetCameraParams(String ip, int brightness, int contrast, int hue, int saturation, int sharpness, int gain);

    public native static MLCameraParams[] nativeCmdGetCameraParams(String ip);


}
