package models;

import connections.Connection;
import graphics.Graphics;
import org.w3c.dom.Node;

import java.util.List;

public class CoupledModel extends Model {
    private List<Model> atomicSubModels;
    private List<Model> coupledSubModels;
    private List<Connection> connections;

    public CoupledModel(String type, List<Model> atomicSubModels, List<Model> coupledSubModels, String id, String cppClass, List<Parameter> parameters, Graphics graphics, Node parent, List<Connection> connections){
        this.setType(type);
        this.atomicSubModels = atomicSubModels;
        this.coupledSubModels = coupledSubModels;
        this.setId(id);
        this.setCppClass(cppClass);
        this.setParameters(parameters);
        this.setGraphics(graphics);
        this.setParent(parent);
        this.connections = connections;
    }

    public List<Model> getAtomicSubModels() {
        return atomicSubModels;
    }

    public void setAtomicSubModels(List<Model> atomicSubModels) {
        this.atomicSubModels = atomicSubModels;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public List<Model> getCoupledSubModels() {
        return coupledSubModels;
    }
}
