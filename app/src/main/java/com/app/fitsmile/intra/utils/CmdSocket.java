package com.app.fitsmile.intra.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;


import com.app.fitsmile.app.AppController;

import java.util.ArrayList;

public class CmdSocket
{
	private static final String TAG = "CmdSocket";
	public static final int CMD_SET_WIFI_NAME = 0x0001;
	public static final int CMD_SET_WIFI_PASSWORD = 0x0002;
	public static final int CMD_SET_WIFI_CLEAR_PASSWORD = 0x0003;
	public static final int CMD_SET_REBOOT = 0x0004;
	public static final int CMD_SET_CAMERA_RESOLUTION = 0x0008;
	public static final int CMD_GET_CAMERA_RESOLUTION = 0x0009;

    public static final int CMD_RESET_CAMERA_PARAMS = 0x0a;
    public static final int CMD_SET_CAMERA_PARAMS = 0x0b;
    public static final int CMD_GET_CAMERA_PARAMS = 0x0c;

	private Handler handler;
	private Context context;

    public static final int REMOTE_VALUE_PHOTO = 1;
    public static final int REMOTE_VALUE_VIDEO = 2;

    private boolean keyThreadRunning = false;
    private boolean searchThreadRunning = false;

	private ArrayList<String> params = new ArrayList<String>();

	public CmdSocket(Context context)
	{
		this.context = context;
	}

	public void setHandler(Handler handler) {
        this.handler = handler;
    }

	public void sendCommand(final CmdParams mCmdParams) {
		StreamSelf.EXECUTER.execute(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int ret;
				switch (mCmdParams.cmdType) {
					case CMD_SET_WIFI_NAME:
					{
						ret = NativeLibs.nativeCmdSetName(AppController.defaultIpAddr, mCmdParams.wifiName);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_WIFI_PASSWORD:
					{
						ret = NativeLibs.nativeCmdSetPassword(AppController.defaultIpAddr, mCmdParams.wifiPass);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_WIFI_CLEAR_PASSWORD:
					{
						ret = NativeLibs.nativeCmdClearPassword(AppController.defaultIpAddr);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_REBOOT:
					{
						ret = NativeLibs.nativeCmdSendReboot(AppController.defaultIpAddr);
						Log.d(TAG, "ret:" + ret);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_CAMERA_RESOLUTION:
					{
						ret = NativeLibs.nativeCmdSetResolution(AppController.defaultIpAddr, mCmdParams.width, mCmdParams.height, 30);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_GET_CAMERA_RESOLUTION:
					{
						params.clear();
						byte[] buf_tmp = NativeLibs.nativeCmdGetResolution(AppController.defaultIpAddr);
						if (buf_tmp != null && buf_tmp.length > 0) {
							int mWidth, mHeight, fps;
							int count = buf_tmp[0];
							for (int i = 0; i < count; i++) {
								mWidth = byteToInt(buf_tmp[i * 5 + 1]) + (buf_tmp[i * 5 + 2] << 8);
								mHeight = byteToInt(buf_tmp[i * 5 + 3]) + (buf_tmp[i * 5 + 4] << 8);
								fps = buf_tmp[i * 5 + 5];
								params.add(mWidth + "*" + mHeight);
								Log.e(TAG, Integer.toString(mWidth) + "*" + mHeight + " " + fps);
							}
							// update the spinner
							Message msg = new Message();
							msg.obj = params;
							msg.what = HandlerParams.UPDATE_RESOLUTION;
							handler.sendMessage(msg);
						}
					}
					break;
                    case CMD_GET_CAMERA_PARAMS: {
                        MLCameraParams params[] = NativeLibs.nativeCmdGetCameraParams(AppController.defaultIpAddr);
                        if(params != null) {
                            Log.e(TAG, "Get params:" +
                                    "  brightness:" + params[0].cur +
                                    "  contrast:" + params[1].cur +
                                    "  hue:" + params[2].cur +
                                    "  saturation:" + params[3].cur +
                                    "  sharpness:" + params[4].cur +
                                    "  gain:" + params[5].cur);
                        }
                    }
                    break;
					default:
					break;
				}
			}
		});
	}

	private static int byteToInt(byte x) {
		return x >= 0 ? (int) x : (int) (x + 256);
	}


	public void stopSocket() {
        keyThreadRunning = false;
	}

	public class CmdParams
	{
		public int cmdType;
		public String wifiName;
		public String wifiPass;
		public int width;
		public int height;
		public int video_format;
	}


    /**
     * 按键&串口透传线程demo
     */
    public void startRunKeyThread() {
        keyThreadRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int type;
                int key_value;

                while(keyThreadRunning) {
                    type = NativeLibs.nativeCmdGetKeyOrUart(AppController.defaultIpAddr);
                    if(type <= 0) {
                        SystemClock.sleep(100);
                        continue;
                    }
                    if(type == 1) {
                        Log.e(TAG, "Device support [KEY]");

                        while (keyThreadRunning) {
                            key_value = NativeLibs.nativeCmdGetRemoteKey(AppController.defaultIpAddr);
                            if (key_value == REMOTE_VALUE_PHOTO)
                                handler.sendEmptyMessage(HandlerParams.REMOTE_TAKE_PHOTO);
                            else if (key_value == REMOTE_VALUE_VIDEO)
                                handler.sendEmptyMessage(HandlerParams.REMOTE_TAKE_RECORD);

                            SystemClock.sleep(200);
                        }
                    }else if(type == 2) {

                    }
                }
            }
        }).start();
    }

    public void stopRunKeyThread() {
        keyThreadRunning = false;
    }


    public void stopRunSearchThread() {
        searchThreadRunning = false;
    }



    public static int getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return wifiInfo.getIpAddress();
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return 0;
    }


    /**
     * 讲最后的替换为从0~255
     * @param ip
     * @param last
     * @return
     */
    public static String intIP2StringIP(int ip, int last) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (last & 0xFF);
    }


}
