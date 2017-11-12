package brainmatic.com.contactdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import brainmatic.com.contactdatabase.adapter.ContactAdapter;
import brainmatic.com.contactdatabase.entity.ContactPerson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listContact)
    ListView listContact;

    List<ContactPerson> contacts = new ArrayList<ContactPerson>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        registerForContextMenu(listContact);
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
                ContactPerson cp = (ContactPerson)listContact.getItemAtPosition(info.position);
                cp.delete();
                Toast.makeText(this,"Contact Deleted",Toast.LENGTH_LONG).show();
                loadAllContact();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @OnItemClick(R.id.listContact)
    public void listContactOnItemClick(int position){
        ContactPerson cp = (ContactPerson)listContact.getItemAtPosition(position);
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("ID",cp.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllContact();
    }

    private void loadAllContact(){
        contacts = ContactPerson.listAll(ContactPerson.class);
        listContact.setAdapter(new ContactAdapter(this,contacts));
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
