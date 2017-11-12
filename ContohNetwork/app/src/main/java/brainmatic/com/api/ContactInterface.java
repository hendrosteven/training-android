package brainmatic.com.api;

import java.util.List;

import brainmatic.com.entity.Contact;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Hendro Steven on 01/06/2017.
 */

public interface ContactInterface {

    @GET("/contact")
    Call<List<Contact>> findAll();

    @POST("/contact")
    Call<Contact> save(@Body Contact contact);

    @DELETE("/contact/{id}")
    Call<Boolean> delete(@Path("id") String id);

    @GET("/contact/{id}")
    Call<Contact> getById(@Path("id") String id);
}
