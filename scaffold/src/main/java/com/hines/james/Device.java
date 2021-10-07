package com.hines.james;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "device_events")
public class Device {
    @JsonProperty("charging_source")
    private String chargingSource;
    @JsonProperty("processor4_temp")
    private int processor4Temp;

    @Id
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("processor2_temp")
    private int processor2Temp;
    @JsonProperty("processor1_temp")
    private int processor1Temp;
    private int charging;
    @JsonProperty("current_capacity")
    private int currentCapacity;
    @JsonProperty("inverter_state")
    private int inverterState;
    @JsonProperty("moduleL_temp")
    private int moduleLTemp;
    @JsonProperty("moduleR_temp")
    private int moduleRTemp;
    @JsonProperty("processor3_temp")
    private int processor3Temp;
    @JsonProperty("SoC_regulator")
    private float soCRegulator;

    public String getChargingSource() {
        return chargingSource;
    }

    public void setChargingSource(String chargingSource) {
        this.chargingSource = chargingSource;
    }

    public int getProcessor4Temp() {
        return processor4Temp;
    }

    public void setProcessor4Temp(int processor4Temp) {
        this.processor4Temp = processor4Temp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getProcessor2Temp() {
        return processor2Temp;
    }

    public void setProcessor2Temp(int processor2Temp) {
        this.processor2Temp = processor2Temp;
    }

    public int getProcessor1Temp() {
        return processor1Temp;
    }

    public void setProcessor1Temp(int processor1Temp) {
        this.processor1Temp = processor1Temp;
    }

    public int getCharging() {
        return charging;
    }

    public void setCharging(int charging) {
        this.charging = charging;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public int getInverterState() {
        return inverterState;
    }

    public void setInverterState(int inverterState) {
        this.inverterState = inverterState;
    }

    public int getModuleLTemp() {
        return moduleLTemp;
    }

    public void setModuleLTemp(int moduleLTemp) {
        this.moduleLTemp = moduleLTemp;
    }

    public int getModuleRTemp() {
        return moduleRTemp;
    }

    public void setModuleRTemp(int moduleRTemp) {
        this.moduleRTemp = moduleRTemp;
    }

    public int getProcessor3Temp() {
        return processor3Temp;
    }

    public void setProcessor3Temp(int processor3Temp) {
        this.processor3Temp = processor3Temp;
    }

    public float getSoCRegulator() {
        return soCRegulator;
    }

    public void setSoCRegulator(float soCRegulator) {
        this.soCRegulator = soCRegulator;
    }

    @Override
    public String toString() {
        return "DeviceEventDTO{" +
                "chargingSource='" + chargingSource + '\'' +
                ", processor4Temp=" + processor4Temp +
                ", deviceId='" + deviceId + '\'' +
                ", processor2Temp=" + processor2Temp +
                ", processor1Temp=" + processor1Temp +
                ", charging=" + charging +
                ", currentCapacity=" + currentCapacity +
                ", inverterState=" + inverterState +
                ", moduleLTemp=" + moduleLTemp +
                ", moduleRTemp=" + moduleRTemp +
                ", processor3Temp=" + processor3Temp +
                ", soCRegulator=" + soCRegulator +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return getProcessor4Temp() == device.getProcessor4Temp() && getProcessor2Temp() == device.getProcessor2Temp() && getProcessor1Temp() == device.getProcessor1Temp() && getCharging() == device.getCharging() && getCurrentCapacity() == device.getCurrentCapacity() && getInverterState() == device.getInverterState() && getModuleLTemp() == device.getModuleLTemp() && getModuleRTemp() == device.getModuleRTemp() && getProcessor3Temp() == device.getProcessor3Temp() && Float.compare(device.getSoCRegulator(), getSoCRegulator()) == 0 && getChargingSource().equals(device.getChargingSource()) && getDeviceId().equals(device.getDeviceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChargingSource(), getProcessor4Temp(), getDeviceId(), getProcessor2Temp(), getProcessor1Temp(), getCharging(), getCurrentCapacity(), getInverterState(), getModuleLTemp(), getModuleRTemp(), getProcessor3Temp(), getSoCRegulator());
    }
}
