package survey;

@SuppressWarnings("WeakerAccess")
public class eachAngleWithSign {
    Integer angle;
    int sign;
    public eachAngleWithSign(Integer angle,int sign) {
        this.angle = angle;
        this.sign = sign;
    }
    public void set(eachAngleWithSign eachAngleWithSign) {
        this.angle = eachAngleWithSign.angle;
        this.sign = eachAngleWithSign.sign;
    }
    public void setAngle(int newAngle) {
        this.angle = newAngle;
    }
}
