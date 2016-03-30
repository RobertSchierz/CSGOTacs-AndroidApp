package app.black0ut.de.map_service_android.listitemview;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Group;
import app.black0ut.de.map_service_android.data.Strategy;

/**
 * Created by Jan-Philipp Altenhof on 14.02.2016.
 */

/**
 * Klasse für die View der Listenelemente des Gruppendialogs.
 */
@EViewGroup(R.layout.group_dialog_listview_item)
public class GroupDialogListItemView extends LinearLayout{
    @ViewById
    TextView groupName;

    public GroupDialogListItemView(Context context) {
        super(context);
    }

    /**
     * Bindet die Daten aus dem Objekt group an die TextView.
     * @param group Ein Parameter vom Typ Group.
     */
    public void bind(String group){
        this.groupName.setText(group);
    }
}
