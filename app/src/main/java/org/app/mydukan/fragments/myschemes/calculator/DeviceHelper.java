package org.app.mydukan.fragments.myschemes.calculator;

import org.app.mydukan.fragments.myschemes.model.Device;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rojesharunkumar on 20/10/17.
 */

public class DeviceHelper {

    private List<Device> deviceList;
    private int size = 0;

    static DeviceHelper mInstance;

    public static DeviceHelper getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceHelper();
        }
        return mInstance;
    }


    public List<Device> getDeviceList() {
        if(deviceList== null){
            deviceList = new ArrayList<>();
        }
        return deviceList;
    }

    public boolean addDevice(Device device) {
        if (deviceList == null) {
            deviceList = new ArrayList<>();
        }
        deviceList.add(device);
        return true;
    }

    public int getListSize() {
        if (deviceList != null) {
            size = deviceList.size();
        }
        return size;
    }

    public void resetDevice() {
        deviceList = new ArrayList<>();
    }


}
