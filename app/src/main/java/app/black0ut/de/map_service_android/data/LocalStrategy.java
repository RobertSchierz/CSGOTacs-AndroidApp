package app.black0ut.de.map_service_android.data;

import java.util.ArrayList;

/**
 * Created by Jan-Philipp Altenhof on 10.02.2016.
 */
//Singleton
public class LocalStrategy {

    private static ArrayList<Integer> initListX = new ArrayList<>();
    private static ArrayList<Integer> initListY = new ArrayList<>();
    private static ArrayList<Boolean> initDragList = new ArrayList<>();

    private ArrayList<Integer> listX;
    private ArrayList<Integer> listY;
    private ArrayList<Boolean> dragList;
    private String stratName;
    private String mapName;

    private LocalStrategy(ArrayList<Integer> listX, ArrayList<Integer> listY, ArrayList<Boolean> dragList) {
        this.listX = listX;
        this.listY = listY;
        this.dragList = dragList;
        //this.stratName = stratName;
        //this.mapName = mapName;
    }

    private static LocalStrategy ourInstance = new LocalStrategy(initListX, initListY, initDragList);

    public static LocalStrategy getInstance() {
        return ourInstance;
    }

    public ArrayList<Integer> getListX() {
        return listX;
    }

    public void addListX(int x) {
        this.listX.add(x);
    }

    public ArrayList<Integer> getListY() {
        return listY;
    }

    public void addListY(int y) {
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
}
