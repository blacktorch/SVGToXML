public class EOC extends Connection {
    private String outCoupled;
    private String outSubModel;
    private String subModel;

    public EOC(String type, String outCoupled, String outSubModel, String messageType, String subModel, SVGLine line, SVGText text){
        this.setType(type);
        this.outCoupled = outCoupled;
        this.outSubModel = outSubModel;
        this.setMessageType(messageType);
        this.subModel = subModel;
        this.setLine(line);
        this.setText(text);
    }

    public String getOutCoupled() {
        return outCoupled;
    }

    public void setOutCoupled(String outCoupled) {
        this.outCoupled = outCoupled;
    }

    public String getOutSubModel() {
        return outSubModel;
    }

    public void setOutSubModel(String outSubModel) {
        this.outSubModel = outSubModel;
    }


    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }
}
