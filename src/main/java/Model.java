import org.w3c.dom.Node;

import java.util.List;

public abstract class Model {
    private Node parent;
    private String type;
    private String id;
    private String cppClass;
    private List<Parameter> parameters;
    private Graphics graphics;
    private boolean hasParameters;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCppClass() {
        return cppClass;
    }

    public void setCppClass(String cppClass) {
        this.cppClass = cppClass;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public boolean isHasParameters() {
        return hasParameters;
    }

    public void setHasParameters(boolean hasParameters) {
        this.hasParameters = hasParameters;
    }

}
