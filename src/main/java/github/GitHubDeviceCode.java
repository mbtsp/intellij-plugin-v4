package github;

import com.google.gson.annotations.SerializedName;

public class GitHubDeviceCode {

    @SerializedName("device_code")
    private String deviceCode;
    @SerializedName("user_code")
    private String userCode;
    @SerializedName("verification_uri")
    private String verificationUri;
    @SerializedName("expires_in")
    private Integer expiresIn;
    @SerializedName("interval")
    private Integer interval;

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getVerificationUri() {
        return verificationUri;
    }

    public void setVerificationUri(String verificationUri) {
        this.verificationUri = verificationUri;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
