package app.black0ut.de.map_service_android;

import com.google.gson.JsonObject;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import app.black0ut.de.map_service_android.data.Status;

/**
 * Created by Jan-Philipp Altenhof on 29.01.2016.
 */
@Rest(rootUrl = "https://p4dme.shaula.uberspace.de", converters = { MappingJackson2HttpMessageConverter.class })
public interface RestClient {
    @Post("/status")
    @Accept(MediaType.APPLICATION_JSON)
    void addReg(JSONObject status);
}
