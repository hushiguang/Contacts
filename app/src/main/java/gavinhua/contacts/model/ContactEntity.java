package gavinhua.contacts.model;

/**
 * Created by GavinHua on 2016/4/1.
 */
public class ContactEntity  {

    String mPhone;
    String mName;
    String mIndex;
    String mPinYin;
    String mLastTime;


    public String getIndex() {
        return mIndex;
    }

    public void setIndex(String mIndex) {
        this.mIndex = mIndex;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getPinYin() {
        return mPinYin;
    }

    public void setPinYin(String mPinYin) {
        this.mPinYin = mPinYin;
    }

    public String getLastTime() {
        return mLastTime;
    }

    public void setLastTime(String mLastTime) {
        this.mLastTime = mLastTime;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "mPhone='" + mPhone + '\'' +
                ", mName='" + mName + '\'' +
                ", mIndex='" + mIndex + '\'' +
                ", mPinYin='" + mPinYin + '\'' +
                ", mLastTime='" + mLastTime + '\'' +
                '}';
    }
}
