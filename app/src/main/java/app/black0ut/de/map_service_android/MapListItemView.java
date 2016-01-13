package app.black0ut.de.map_service_android;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.data.Map;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */
@EViewGroup(R.layout.listview_icon_text)
public class MapListItemView extends RelativeLayout {

    @ViewById(R.id.map_preview)
    ImageView mapPreview;

    @ViewById(R.id.map_name)
    TextView mapName;

    public MapListItemView(Context context) {
        super(context);
    }

    /**
     * Bindet die Daten aus dem Objekt Map an die ImageView und die TextView
     * @param map Ein Parameter vom Typ Map.
     */
    public void bind(Map map){
        this.mapPreview.setImageResource(map.mapPreviewId);
        this.mapName.setText(map.mapName);
    }
}
