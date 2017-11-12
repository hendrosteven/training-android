package brainmatic.com.contohnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import brainmatic.com.adapter.ContactAdapter;
import brainmatic.com.api.APIClient;
import brainmatic.com.api.ContactInterface;
import brainmatic.com.entity.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listContact)
    ListView listContact;

    List<Contact> contacts = new ArrayList<Contact>();

    ContactInterface contactInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        contactInterface = APIClient.getClient().create(ContactInterface.class);
        registerForContextMenu(listContact);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllContact();
    }

    private void loadAllContact(){
        final ProgressDialog pd=ProgressDialog.show(this,"","Loading..",false);
        Call<List<Contact>> call = contactInterface.findAll();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                List<Contact> contacts = response.body();
                listContact.setAdapter(new ContactAdapter(MainActivity.this,contacts));
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                pd.dismiss();
                Log.e("ERROR",t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menuDelete:
                Contact contact = (Contact) listContact.getItemAtPosition(info.position);
                delete(contact.getId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void delete(final String id){
        final ProgressDialog pd=ProgressDialog.show(this,"","Loading..",false);
        Call<Boolean> call = contactInterface.delete(id);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Toast.makeText(MainActivity.this,"Contact Deleted",Toast.LENGTH_LONG).show();
                pd.dismiss();
                loadAllContact();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                pd.dismiss();
                Log.e("ERROR",t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuInsert:
                Intent intent = new Intent(this, InputActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
