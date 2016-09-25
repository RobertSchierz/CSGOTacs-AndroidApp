package app.black0ut.de.gotacs.listitemview;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.gotacs.R;
import app.black0ut.de.gotacs.data.Strategy;

/**
 * Created by Jan-Philipp Altenhof on 13.02.2016.
 */

/**
 * Klasse für die View der Listenelemente der Strategienliste.
 */
@EViewGroup(R.layout.strategies_listview_text)
public class StrategyListItemView extends RelativeLayout{
    @ViewById
    TextView stratName;

    @ViewById
    TextView stratMap;

    @ViewById
    TextView stratGroup;

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
        if (!strategy.group.equals("null") && !strategy.group.equals("")) {
            this.stratGroup.setText(String.format(getResources().getString(R.string.strat_group), strategy.group));
        }
    }
}
