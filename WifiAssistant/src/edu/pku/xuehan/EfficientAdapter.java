package edu.pku.xuehan;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class EfficientAdapter extends BaseAdapter {
	
	public static String[] fruit = { "Apple",
		"Banana",
		"Orange",
		"Tangerine" };
	public static String[] fruit_nickName = { "AP","BA","OR","TA" };
	
	private List<ScanResult> scanList;
    private LayoutInflater mInflater;
    public void setScanResult(List<ScanResult> scanList){
    	
    }
    public EfficientAdapter(Context context,List<ScanResult> scanList) {
    	this.scanList = scanList;
        mInflater = LayoutInflater.from(context);
    }

    /*public int getCount() {
        return fruit.length;
    }*/
    
    public int getCount() {
        return scanList.size();
        }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /*
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row, null);
            holder = new ViewHolder();
            holder.text1 = (TextView) convertView
                    .findViewById(R.id.TextView01);
            holder.text2 = (TextView) convertView
                    .findViewById(R.id.TextView02);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text1.setText(fruit_nickName[position]);
        holder.text2.setText(fruit[position]);

        return convertView;
    }
    */
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wifi_row, null);
            holder = new ViewHolder();
            holder.text1 = (TextView) convertView
                    .findViewById(R.id.SSID);
            holder.text2 = (TextView) convertView
                    .findViewById(R.id.lockStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text1.setText(scanList.get(position).SSID);
        holder.text2.setText(String.valueOf(scanList.get(position).level));

        return convertView;
    }
    static class ViewHolder {
        TextView text1;
        TextView text2;
    }
}