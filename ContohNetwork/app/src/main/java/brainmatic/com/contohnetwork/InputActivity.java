package brainmatic.com.contohnetwork;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Select;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

import brainmatic.com.api.APIClient;
import brainmatic.com.api.ContactInterface;
import brainmatic.com.entity.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends AppCompatActivity implements Validator.ValidationListener,
        DatePickerDialog.OnDateSetListener {

    @BindView(R.id.txtFirstName)
    @NotEmpty
    EditText firstName;

    @BindView(R.id.txtLastName)
    @NotEmpty
    EditText lastName;

    @BindView(R.id.txtAddress)
    @NotEmpty
    EditText address;

    @BindView(R.id.txtPhone)
    @NotEmpty
    EditText phone;

    @BindView(R.id.txtEmail)
    @NotEmpty
    @Email
    EditText email;

    @BindView(R.id.spinnerSex)
    @Select
    Spinner sex;

    @BindView(R.id.txtDob)
    @NotEmpty
    EditText dob;

    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;

    Validator validator;
    boolean isValid;

    private static final int REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private Bitmap bitmap;

    ContactInterface contactInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ButterKnife.bind(this);

        contactInterface = APIClient.getClient().create(ContactInterface.class);

        validator = new Validator(this);
        validator.setValidationListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(adapter);

        if (shouldAskPermissions()) {
            askPermissions();
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.CAMERA",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @OnClick(R.id.imgPhoto)
    public void imgPhotoOnClick(){
        PopupMenu popup = new PopupMenu(InputActivity.this, imgPhoto, Gravity.CENTER);

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString().toUpperCase().trim()){
                    case "GALLERY" :
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;
                    case "CAMERA" :
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        break;
                    default:break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                bitmap = getResizedBitmap(bitmap,200,200);
                imgPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (bitmap != null) {
                bitmap.recycle();
            }
            bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = getResizedBitmap(bitmap,200,200);
            imgPhoto.setImageBitmap(bitmap);
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onValidationSucceeded() {
        isValid = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            isValid = false;
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validate() {
        if (validator != null)
            validator.validate();
        return isValid;
    }

    @OnClick(R.id.btnSave)
    public void btnSaveOnClick(){
        if(validate()){
            Contact contact = new Contact();
            contact.setFirstName(firstName.getText().toString().trim());
            contact.setLastName(lastName.getText().toString().trim());
            contact.setAddress(address.getText().toString().trim());
            contact.setPhone(phone.getText().toString().trim());
            contact.setEmail(email.getText().toString().trim());
            contact.setSex(sex.getSelectedItem().toString());
            contact.setDob(dob.getText().toString().trim());
            if(bitmap!=null) {
                contact.setPhoto(encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100));
            }else{
                contact.setPhoto("");
            }
            save(contact);
        }
    }

    private void save(final Contact contact){
        final ProgressDialog pd=ProgressDialog.show(this,"","Loading..",false);
        Call<Contact> call = contactInterface.save(contact);
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                firstName.setText("");
                lastName.setText("");
                address.setText("");
                phone.setText("");
                email.setText("");
                Toast.makeText(InputActivity.this,"Contact Saved",Toast.LENGTH_LONG).show();
                pd.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                pd.dismiss();
                Log.e("ERROR",t.getMessage());
                call.cancel();
            }
        });
    }

    @OnClick(R.id.btnDob)
    public void btnDobOnClick(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialogDate = new DatePickerDialog(
                this, this, year, month, day);

        dialogDate.getDatePicker().setMaxDate(c.getTimeInMillis());
        dialogDate.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dob.setText(year+"-"+(month+1)+"-"+dayOfMonth);
    }
}
