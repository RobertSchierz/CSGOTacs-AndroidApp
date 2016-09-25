package app.black0ut.de.gotacs.data;

import java.util.ArrayList;

/**
 * Created by Jan-Philipp Altenhof on 10.02.2016.
 */

/**
 * Singleton Klasse, welche eine lokale Strategie repräsentiert.
 * Sie wurde als Singleton implementiert, da es in jedem Fall nur eine lokale Strategie geben kann.
 */
public class LocalStrategy {

    private static ArrayList<Double> initListX = new ArrayList<>();
    private static ArrayList<Double> initListY = new ArrayList<>();
    private static ArrayList<Boolean> initDragList = new ArrayList<>();

    private ArrayList<Double> listX;
    private ArrayList<Double> listY;
    private ArrayList<Boolean> dragList;
    private String stratName;
    private String mapName;

    private LocalStrategy(ArrayList<Double> listX, ArrayList<Double> listY, ArrayList<Boolean> dragList) {
        this.listX = listX;
        this.listY = listY;
        this.dragList = dragList;
    }

    private static LocalStrategy ourInstance = new LocalStrategy(initListX, initListY, initDragList);

    public static LocalStrategy getInstance() {
        return ourInstance;
    }

    public ArrayList<Double> getListX() {
        return listX;
    }

    public void addListX(double x) {
        this.listX.add(x);
    }

    public ArrayList<Double> getListY() {
        return listY;
    }

    public void addListY(double y) {
        this.listY.add(y);
    }

    public String getStratName() {
        return stratName;
    }

    public void setStratName(String stratName) {
        this.stratName = stratName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public ArrayList<Boolean> isDrag() {
        return dragList;
    }

    public void addDragList(boolean drag) {
        this.dragList.add(drag);
    }

    public ArrayList<Boolean> getDragList() {
        return this.dragList;
    }

    public void clearDragList(){
        this.dragList.clear();
    }

    public void clearListX(){
        this.listX.clear();
    }

    public void clearListY(){
        this.listY.clear();
    }
}
