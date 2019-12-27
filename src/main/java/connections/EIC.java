package connections;

import graphics.SVGLine;
import graphics.SVGText;

public class EIC extends Connection {
    private String inCoupled;
    private String inSubModel;
    private String subModel;

    public EIC(String type, String inCoupled, String inSubModel, String messageType, String subModel, SVGLine line, SVGText text){
        this.setType(type);
        this.inCoupled = inCoupled;
        this.inSubModel = inSubModel;
        this.setMessageType(messageType);
        this.subModel = subModel;
        this.setLine(line);
        this.setText(text);
    }

    public String getInCoupled() {
        return inCoupled;
    }

    public void setInCoupled(String inCoupled) {
        this.inCoupled = inCoupled;
    }

    public String getInSubModel() {
        return inSubModel;
    }

    public void setInSubModel(String inSubModel) {
        this.inSubModel = inSubModel;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }
}
