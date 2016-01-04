package app.black0ut.de.map_service_android.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Map;

/**
 * Created by Jan on 03.01.16.
 */

@EFragment(R.layout.fragment_maps_detail)
public class MapsDetailFragment extends Fragment{

    @ViewById(R.id.map_image)
    ImageView mapImage;

    @AfterViews
    public void afterViews(){
        switch(Map.clickedMapName){
            case Map.ASSAULT:
                mapImage.setImageResource(R.drawable.cs_assault_radar);
                break;
            case Map.AZTEC:
                mapImage.setImageResource(R.drawable.de_aztec_radar_spectate);
                break;
            case Map.CACHE:
                mapImage.setImageResource(R.drawable.de_cache_radar_spectate);
                break;
            case Map.COBBLESTONE:
                mapImage.setImageResource(R.drawable.de_cbble_radar);
                break;
            case Map.DUST:
                mapImage.setImageResource(R.drawable.de_dust_radar_spectate);
                break;
            case Map.DUST2:
                mapImage.setImageResource(R.drawable.de_dust2_radar_spectate);
                break;
            case Map.INFERNO:
                mapImage.setImageResource(R.drawable.de_inferno_radar_spectate);
                break;
            case Map.ITALY:
                mapImage.setImageResource(R.drawable.cs_italy_radar);
                break;
            case Map.MILITIA:
                mapImage.setImageResource(R.drawable.cs_militia_radar_spectate);
                break;
            case Map.MIRAGE:
                mapImage.setImageResource(R.drawable.de_mirage_radar_spectate);
                break;
            case Map.NUKE:
                mapImage.setImageResource(R.drawable.de_nuke_radar_spectate);
                break;
            case Map.OFFICE:
                mapImage.setImageResource(R.drawable.cs_office_radar);
                break;
            case Map.OVERPASS:
                mapImage.setImageResource(R.drawable.de_overpass_radar);
                break;
            case Map.TRAIN:
                mapImage.setImageResource(R.drawable.de_train_radar_spectate);
                break;
            case Map.VERTIGO:
                mapImage.setImageResource(R.drawable.de_vertigo_radar);
                break;
            default:
                Log.d("MAP CLICK", "No image for the clicked Map.");
        }
    }
}
