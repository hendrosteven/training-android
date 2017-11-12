package brainmatic.com.contactdatabase.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import brainmatic.com.contactdatabase.R;
import brainmatic.com.contactdatabase.entity.ContactPerson;

/**
 * Created by Hendro Steven on 30/05/2017.
 */

public class ContactAdapter extends ArrayAdapter<ContactPerson> {

    private final Context context;
    private final List<ContactPerson> data;

    public ContactAdapter(Context context, List<ContactPerson> data) {
        super(context, R.layout.list_contact, data);
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_contact, parent, false);
        TextView nama = (TextView) rowView.findViewById(R.id.txtNama);
        TextView phone = (TextView) rowView.findViewById(R.id.txtPhone);
        TextView email = (TextView) rowView.findViewById(R.id.txtEmail);
        ImageView img = (ImageView)rowView.findViewById(R.id.imgContact);

        ContactPerson cp = data.get(position);
        nama.setText(cp.getFirstName()+" "+cp.getLastName());
        phone.setText(cp.getPhone());
        email.setText(cp.getEmail());
        if(cp.getPhoto().trim().length()>0) {
            Bitmap bm = decodeBase64(cp.getPhoto());
            img.setImageBitmap(bm);
        }
        return rowView;
    }

    private Bitmap decodeBase64(String input){
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
