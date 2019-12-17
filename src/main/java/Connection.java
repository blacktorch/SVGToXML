public abstract class Connection {
   private String type;
   private SVGText text;
   private SVGLine line;
   private String messageType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SVGText getText() {
        return text;
    }

    public void setText(SVGText text) {
        this.text = text;
    }

    public SVGLine getLine() {
        return line;
    }

    public void setLine(SVGLine line) {
        this.line = line;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
