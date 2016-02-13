package app.black0ut.de.map_service_android;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import app.black0ut.de.map_service_android.data.Strategy;

/**
 * Created by Jan-Philipp Altenhof on 13.02.2016.
 */
@EViewGroup(R.layout.strategies_listview_text)
public class StrategyListItemView extends RelativeLayout{
    @ViewById
    TextView stratName;

    @ViewById
    TextView stratMap;

    public StrategyListItemView(Context context) {
        super(context);
    }

    /**
     * Bindet die Daten aus dem Objekt strategy an die ImageView und die TextView.
     * @param strategy Ein Parameter vom Typ Strategy.
     */
    public void bind(Strategy strategy){
        this.stratName.setText(strategy.name);
        this.stratMap.setText(strategy.map);
    }
}
