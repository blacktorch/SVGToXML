package connections;

import graphics.SVGLine;
import graphics.SVGText;

public class IC extends Connection {
    private String from;
    private String outFrom;
    private String to;
    private String inTo;

    public IC(String type, String from, String outFrom, String to, String inTo, String messageType, SVGLine line, SVGText text){
        this.setType(type);
        this.from = from;
        this.outFrom  = outFrom;
        this.to = to;
        this.inTo = inTo;
        this.setMessageType(messageType);
        this.setLine(line);
        this.setText(text);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getOutFrom() {
        return outFrom;
    }

    public void setOutFrom(String outFrom) {
        this.outFrom = outFrom;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getInTo() {
        return inTo;
    }

    public void setInTo(String inTo) {
        this.inTo = inTo;
    }
}
