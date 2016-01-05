package edu.pku.xuehan;

import java.util.List;

import android.app.AlertDialog.Builder;
//download by http://www.codefans.net
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.pku.xuehan.R.layout;

public class WifiAdmin {
	private final static String TAG = "WifiAdmin";
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult;
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	private WifiInfo currentWifiInfo;// 当前所连接的wifi

	private static WifiAdmin mWifiAdmin;
	/**
	 * 构造方法
	 */
	public WifiAdmin(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}
	
	public static void init(Context context){
		mWifiAdmin = new WifiAdmin(context);
	}
	
	public static WifiAdmin getInstance() {
		return mWifiAdmin;
	}
	
	
	/**
	 * 打开Wifi网卡
	 */
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * 关闭Wifi网卡
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 检查当前Wifi网卡状态
	 */
	public void checkNetCardState() {
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "网卡正在关闭");
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "网卡已经关闭");
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "网卡正在打开");
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "网卡已经打开");
		} else {
			Log.i(TAG, "---_---晕......没有获取到状态---_---");
		}
	}

	/**
	 * 扫描周边网络
	 */
	public void scan() {
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
		} else {
			Log.i(TAG, "当前区域没有无线网络");
		}
	}

	/**
	 * 得到扫描结果
	 */
	public String getScanResult() {
		// 每次点击扫描之前清空上一次的扫描结果
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// 开始扫描网络
		scan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				// mScanResult.
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1).append(" :").append(mScanResult.SSID)
						.append("->").append(mScanResult.BSSID).append("->").append(mScanResult.capabilities)
						.append("->").append(mScanResult.frequency).append("->").append(mScanResult.level).append("->")
						.append(mScanResult.describeContents()).append("\n\n");
			}
		}
		Log.i(TAG, mStringBuffer.toString());
		return mStringBuffer.toString();
	}

	/**
	 * 得到列表
	 */
	public List<ScanResult> getScanList() {

		scan();
		listResult = mWifiManager.getScanResults();
		return listResult;

	}

	/**
	 * 连接指定网络
	 */
	public void connect(int index, String password) {
		mWifiInfo = mWifiManager.getConnectionInfo();
		//new ConnectWifiThread().execute(listResult.get(index).SSID + "", password);
	}

	

	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * 检查当前网络状态
	 * 
	 * @return String
	 */
	public void checkNetWorkState() {
		if (mWifiInfo != null) {
			Log.i(TAG, "网络正常工作");
		} else {
			Log.i(TAG, "网络已断开");
		}
	}

	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 得到IP地址
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public int addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
		mWifiManager.enableNetwork(wcgID, true);
		return wcgID;
	}

	public List<ScanResult> getListResult() {
		// TODO Auto-generated method stub
		return listResult;
	}
	
	public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = isExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	/**
	 * 判断wifi是否存在
	 * 
	 * @param SSID
	 * @param wifiManager
	 * @return
	 */
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}	
	
	public boolean connectWithIndex(int index, String password) {
		WifiConfiguration config = WifiUtil.createWifiInfo(listResult.get(index).SSID, password, 3, mWifiManager);
		int networkId = mWifiManager.addNetwork(config);
		if (null != config) {
			boolean flag = mWifiManager.enableNetwork(networkId, true);
			if(flag){
				return true;
			} else{
				return false;
			}
		} else{
			return false;
		}
	}
	
	public boolean checkConnection(String ssid) {
		currentWifiInfo = mWifiManager.getConnectionInfo();
		String currentWifi = currentWifiInfo.getSSID();
		if (null != currentWifiInfo.getSSID() && 0 != currentWifiInfo.getIpAddress()
				&& currentWifiInfo.getSSID().replace("\"", "").equals(ssid)) {
			return true;
		} else {
			return false;
		}
			
	}
	
	/**
	 * 转换IP地址
	 * 
	 * @param i
	 * @return
	 */
	public String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}
}
