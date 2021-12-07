package com.example.skillsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.skillsearch.R;
import com.example.skillsearch.User;

import java.util.ArrayList;

public class Signup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
     String states[], Email = "", id, Contact_Number, Aadhar_Number, Street_No, Pincode, State, City, Gender = "", Password = "", Confirm_password = "", Profession = "", Type = "", Name = "", Alternate_Contact_Number;
     String TAG = "abcdefg";
     EditText editTextEmail, editTextContact_No, editTextAadhar_No, editTextStreet, editTextPassword, editTextConfirmPassword, editTextPincode, editTextName, editTextAlternate_contact_No;
     ProgressDialog pd;
     Spinner spinner_gender, spinner_profession, spinner_state, spinner_City;
     RadioButton radioButtonseeker, radioButtonrecruiter;
     FirebaseAuth mAuth;
     FirebaseUser currentUser;
     DatabaseReference database, rec_ref, subref, seeker_ref, dup_ref;
     ArrayList<User> Userlist;

     @Override
     protected void onCreate(Bundle savedInstanceState)
     {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_signup);
	    setTitle("SignUp");

	    new checkInternetConnection(this).checkConnection();

	    mAuth = FirebaseAuth.getInstance();

	    database = FirebaseDatabase.getInstance().getReference();
	    dup_ref = database.child("Users");
	    rec_ref = database.child("Recruiter");
	    seeker_ref = database.child("Seeker");


	    init();

	    // to counter the issue where the backgroung image is not static and is compressing along with the scroll view wheneverthe keboard pops up
	    getWindow().setBackgroundDrawableResource(R.drawable.blue_stripes_bg);
	    setTitle("Sign Up");

	    //creating a string array for gender spinner
	    String[] items = new String[]{"Select" , "Male" , "Female"};

	    //array adapter to set this string array into the spinner
	    ArrayAdapter<String> adapter = new ArrayAdapter<>(this , android.R.layout.simple_spinner_dropdown_item , items);

	    //set the spinners adapter to the previously created one.
	    spinner_gender.setAdapter(adapter);
	    spinner_gender.setOnItemSelectedListener(Signup.this);
	    spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
	    {
		   @Override
		   public void onItemSelected(AdapterView<?> parent , View view , int position , long id)
		   {
			  Gender = parent.getItemAtPosition(position).toString();
		   }

		   @Override
		   public void onNothingSelected(AdapterView<?> parent)
		   {
			  Gender = "";
			  return;

		   }
	    });

	    //create a list of items for the spinner.
	    String[] items1 = new String[]{"Select" , "Electrician" , "Mason" , "Carpenter" , "Painter" , "Plumber" , "Labour"};

	    ArrayAdapter<String> profession_adapter = new ArrayAdapter<>(this , android.R.layout.simple_spinner_dropdown_item , items1);
	    //set the spinners adapter to the previously created one.
	    spinner_profession.setEnabled(false);
	    spinner_profession.setClickable(false);
	    spinner_profession.setAdapter(profession_adapter);
	    spinner_profession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
	    {
		   @Override
		   public void onItemSelected(AdapterView<?> parent , View view , int position , long id)
		   {
			  Profession = parent.getItemAtPosition(position).toString();//Toast.makeText(SignUp.this, "Selected "+parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
		   }

		   @Override
		   public void onNothingSelected(AdapterView<?> parent)
		   {
			  Profession = "";
			  return;

		   }
	    });

	    states = getResources().getStringArray(R.array.states);
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_item , states)
	    {
		   @Override
		   public boolean isEnabled(int position)
		   {
			  if(position == 0)
			  {
				 // Disable the first item from Spinner
				 // First item will be use for hint
				 return false;
			  }
			  else
			  {
				 return true;
			  }
		   }

		   @Override
		   public View getDropDownView(int position , View convertView , ViewGroup parent)
		   {
			  View view = super.getDropDownView(position , convertView , parent);
			  TextView tv = (TextView)view;
			  if(position == 0)
			  {
				 // Set the hint text color gray
				 tv.setTextColor(Color.GRAY);
			  }
			  else
			  {
				 tv.setTextColor(Color.BLACK);
			  }
			  return view;
		   }
	    };

	    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spinner_state.setAdapter(spinnerArrayAdapter);
	    spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
	    {
		   @Override
		   public void onItemSelected(AdapterView<?> parent , View view , int position , long id)
		   {
			  String s = parent.getSelectedItem().toString();
			  if(s.equals("Nairobi"))
			  {
				 Nairobi();
			  }
			  else if(s.equals("Central"))
			  {
				 Central();
			  }
			  else if(s.equals("Coast"))
			  {
				Coast();
			  }
			  else if(s.equals("Nyanza"))
			  {
				  Nyanza();
			  }
			  else if(s.equals("Eastern"))
			  {
				  Eastern();
			  }
			  else if(s.equals("RiftValley"))
			  {
				  RiftValley();
			  }
			  else if(s.equals("Western"))
			  {
				  Western();
			  }


		   }

		   @Override
		   public void onNothingSelected(AdapterView<?> parent)
		   {

		   }

	    });

     }

     public void init()
     {
	    Userlist = new ArrayList<>();
	    editTextEmail = findViewById(R.id.editTextEmail);
	    editTextPassword = findViewById(R.id.editTextPassword);
	    editTextConfirmPassword = findViewById(R.id.editTextPassword);
	    editTextAadhar_No = findViewById(R.id.editTextAadhar_No);
	    editTextStreet = findViewById(R.id.editTextStreet);
	    editTextName = findViewById(R.id.editTextName);
	    editTextPincode = findViewById(R.id.editTextPincode);
	    editTextContact_No = findViewById(R.id.editTextContact_No);
	    editTextAlternate_contact_No = findViewById(R.id.editTextAlternate_No);
	    radioButtonrecruiter = findViewById(R.id.Radio_Btn_recruiter);
	    radioButtonseeker = findViewById(R.id.Radio_btn_seeker);
	    spinner_gender = findViewById(R.id.spinner_gender);
	    spinner_state = findViewById(R.id.statespinner);
	    spinner_City = findViewById(R.id.cityspinner);
	    spinner_profession = findViewById(R.id.spinner_profession);

	    pd = new ProgressDialog(this);
     }

     public void test(View v)
     {
	    createuser();
     }

     public void onRadioButtonclicked(View view)
     {
	    switch(view.getId())
	    {
		   case R.id.Radio_btn_seeker:
			  if(radioButtonseeker.isChecked())
			  {
				 spinner_profession.setEnabled(true);
				 spinner_profession.setClickable(true);
				 //Toast.makeText(SignUp.this,"Hello",Toast.LENGTH_SHORT).show();
				 Type = "Seeker";
			  }
		   case R.id.Radio_Btn_recruiter:
			  if(radioButtonrecruiter.isChecked())
			  {
				 spinner_profession.setEnabled(false);
				 spinner_profession.setClickable(false);
				 //Toast.makeText(SignUp.this,"Hello",Toast.LENGTH_SHORT).show();
				 Profession = "";
				 Type = "Recruiter";
			  }
	    }
     }

     public void createuser()
     {

          pd.setTitle("Registering User Please Wait...");
          pd.show();
          pd.setCanceledOnTouchOutside(false);
	    Email = editTextEmail.getText().toString().trim();
	    Password = editTextPassword.getText().toString().trim();
	    Name = editTextName.getText().toString().trim();
	    Contact_Number = editTextContact_No.getText().toString().trim();
	    Aadhar_Number = editTextAadhar_No.getText().toString().trim();
	    Alternate_Contact_Number = editTextAlternate_contact_No.getText().toString().trim();
	    State = spinner_state.getSelectedItem().toString().trim();
	    Street_No = editTextStreet.getText().toString().trim();
	    City = spinner_City.getSelectedItem().toString().trim();
	    Pincode = editTextPincode.getText().toString().trim();
	    Confirm_password = editTextConfirmPassword.getText().toString().trim();


	    String m = editTextEmail.getText().toString();
	    String p = editTextPassword.getText().toString();

	    if(! validation())
	    {
		   //Toast.makeText(getApplication() , "Reached till validation" , Toast.LENGTH_LONG).show();
		   pd.dismiss();
		   return;
	    }
	    //Toast.makeText(getApplication() , "outside" , Toast.LENGTH_LONG).show();
	    //pd.setTitle("Registering Please Wait");
	    //pd.show();
	    mAuth.createUserWithEmailAndPassword(m , p).addOnSuccessListener(Signup.this , new OnSuccessListener<AuthResult>()
	    {
		   @Override
		   public void onSuccess(AuthResult authResult)
		   {

			  Toast.makeText(Signup.this , "User Registered" , Toast.LENGTH_SHORT).show();
			  pd.dismiss();
			  currentUser = FirebaseAuth.getInstance().getCurrentUser();
			  id = currentUser.getUid();
			  writedata();
		   }
	    }).addOnFailureListener(new OnFailureListener()
	    {
		   @Override
		   public void onFailure(@NonNull Exception e)
		   {
			  Toast.makeText(Signup.this , "Failed Log In" , Toast.LENGTH_SHORT).show();
			  pd.dismiss();
		   }
	    });

     }

     public void duplicate()
     {
	    Toast.makeText(getApplicationContext() , "Inside" , Toast.LENGTH_LONG).show();
	    User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
	    subref = dup_ref.child(id);
	    subref.child("Email").setValue(u.getEmail());
	    subref.child("Name").setValue(u.getName());
	    subref.child("Id").setValue(u.getId());
	    subref.child("Mobile").setValue(u.getContact_Number());
	    subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
	    subref.child("Aadhaar").setValue(u.getAadhar_Number());
	    subref.child("Address").setValue(u.getStreet_No());
	    subref.child("Pincode").setValue(u.getPincode());
	    subref.child("State").setValue(spinner_state.getSelectedItem());
	    subref.child("City").setValue(spinner_City.getSelectedItem());
	    subref.child("Gender").setValue(spinner_gender.getSelectedItem());
	    subref.child("Profession").setValue(spinner_profession.getSelectedItem());
	    subref.child("Type").setValue(u.getType());

	    //Toast.makeText(this , "User Data Write in DB Successful" , Toast.LENGTH_SHORT).show();
     }

     public void writedata()
     {
	    if(radioButtonrecruiter.isChecked())
	    {
		   duplicate();
		   Toast.makeText(this , "Recruiter " , Toast.LENGTH_SHORT).show();
		   subref = rec_ref.child(id);
		   User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
		   subref.child("Email").setValue(u.getEmail());
		   subref.child("Name").setValue(u.getName());
		   subref.child("Id").setValue(u.getId());
		   subref.child("Mobile").setValue(u.getContact_Number());
		   subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
		   subref.child("Aadhaar").setValue(u.getAadhar_Number());
		   subref.child("Address").setValue(u.getStreet_No());
		   subref.child("Pincode").setValue(u.getPincode());
		   subref.child("State").setValue(spinner_state.getSelectedItem());
		   subref.child("City").setValue(spinner_City.getSelectedItem());
		   subref.child("Gender").setValue(spinner_gender.getSelectedItem());
		   subref.child("Profession").setValue("");
		   subref.child("Type").setValue(u.getType());
		   FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Profession").setValue("");
		   startActivity(new Intent(Signup.this , RecruiterMain.class));
	    }
	    else if(radioButtonseeker.isChecked())
	    {
		   if(spinner_profession.getSelectedItem().toString().equals("Electrician"))
		   {
			  subref = seeker_ref.child("Electrician").child(id);
			  User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
			  subref.child("Email").setValue(u.getEmail());
			  subref.child("Name").setValue(u.getName());
			  subref.child("Id").setValue(u.getId());
			  subref.child("Mobile").setValue(u.getContact_Number());
			  subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
			  subref.child("Aadhaar").setValue(u.getAadhar_Number());
			  subref.child("Address").setValue(u.getStreet_No());
			  subref.child("Pincode").setValue(u.getPincode());
			  subref.child("State").setValue(spinner_state.getSelectedItem());
			  subref.child("City").setValue(spinner_City.getSelectedItem());
			  subref.child("Gender").setValue(spinner_gender.getSelectedItem());
			  subref.child("Profession").setValue(spinner_profession.getSelectedItem());
			  subref.child("Type").setValue(u.getType());
			  duplicate();
			  startActivity(new Intent(Signup.this , SeekerMain.class));
		   }
		   else if(spinner_profession.getSelectedItem().toString().equals("Mason"))
		   {
			  subref = seeker_ref.child("Mason").child(id);
			  User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
			  subref.child("Email").setValue(u.getEmail());
			  subref.child("Name").setValue(u.getName());
			  subref.child("Id").setValue(u.getId());
			  subref.child("Mobile").setValue(u.getContact_Number());
			  subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
			  subref.child("Aadhaar").setValue(u.getAadhar_Number());
			  subref.child("Address").setValue(u.getStreet_No());
			  subref.child("Pincode").setValue(u.getPincode());
			  subref.child("State").setValue(spinner_state.getSelectedItem());
			  subref.child("City").setValue(spinner_City.getSelectedItem());
			  subref.child("Gender").setValue(spinner_gender.getSelectedItem());
			  subref.child("Profession").setValue(spinner_profession.getSelectedItem());
			  subref.child("Type").setValue(u.getType());
			  duplicate();
			  startActivity(new Intent(Signup.this , SeekerMain.class));
		   }
		   else if(spinner_profession.getSelectedItem().toString().equals("Carpenter"))
		   {
			  subref = seeker_ref.child("Carpenter").child(id);
			  User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
			  subref.child("Email").setValue(u.getEmail());
			  subref.child("Name").setValue(u.getName());
			  subref.child("Id").setValue(u.getId());
			  subref.child("Mobile").setValue(u.getContact_Number());
			  subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
			  subref.child("Aadhaar").setValue(u.getAadhar_Number());
			  subref.child("Address").setValue(u.getStreet_No());
			  subref.child("Pincode").setValue(u.getPincode());
			  subref.child("State").setValue(spinner_state.getSelectedItem());
			  subref.child("City").setValue(spinner_City.getSelectedItem());
			  subref.child("Gender").setValue(spinner_gender.getSelectedItem());
			  subref.child("Profession").setValue(spinner_profession.getSelectedItem());
			  subref.child("Type").setValue(u.getType());
			  duplicate();
			  startActivity(new Intent(Signup.this , SeekerMain.class));
		   }
		   else if(spinner_profession.getSelectedItem().toString().equals("Painter"))
		   {
			  subref = seeker_ref.child("Painter").child(id);
			  User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
			  subref.child("Email").setValue(u.getEmail());
			  subref.child("Name").setValue(u.getName());
			  subref.child("Id").setValue(u.getId());
			  subref.child("Mobile").setValue(u.getContact_Number());
			  subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
			  subref.child("Aadhaar").setValue(u.getAadhar_Number());
			  subref.child("Address").setValue(u.getStreet_No());
			  subref.child("Pincode").setValue(u.getPincode());
			  subref.child("State").setValue(spinner_state.getSelectedItem());
			  subref.child("City").setValue(spinner_City.getSelectedItem());
			  subref.child("Gender").setValue(spinner_gender.getSelectedItem());
			  subref.child("Profession").setValue(spinner_profession.getSelectedItem());
			  subref.child("Type").setValue(u.getType());
			  duplicate();
			  startActivity(new Intent(Signup.this , SeekerMain.class));
		   }
		   else if(spinner_profession.getSelectedItem().toString().equals("Plumber"))
		   {
			  subref = seeker_ref.child("Plumber").child(id);
			  User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
			  subref.child("Email").setValue(u.getEmail());
			  subref.child("Name").setValue(u.getName());
			  subref.child("Id").setValue(u.getId());
			  subref.child("Mobile").setValue(u.getContact_Number());
			  subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
			  subref.child("Aadhaar").setValue(u.getAadhar_Number());
			  subref.child("Address").setValue(u.getStreet_No());
			  subref.child("Pincode").setValue(u.getPincode());
			  subref.child("State").setValue(spinner_state.getSelectedItem());
			  subref.child("City").setValue(spinner_City.getSelectedItem());
			  subref.child("Gender").setValue(spinner_gender.getSelectedItem());
			  subref.child("Profession").setValue(spinner_profession.getSelectedItem());
			  subref.child("Type").setValue(u.getType());
			  duplicate();
			  startActivity(new Intent(Signup.this , SeekerMain.class));
		   }
		   else if(spinner_profession.getSelectedItem().toString().equals("Labour"))
		   {
			  subref = seeker_ref.child("Labour").child(id);
			  User u = new User(Email , id , Contact_Number , Aadhar_Number , Street_No , Pincode , State , City , Gender , Profession , Type , Name , Alternate_Contact_Number);
			  subref.child("Email").setValue(u.getEmail());
			  subref.child("Name").setValue(u.getName());
			  subref.child("Id").setValue(u.getId());
			  subref.child("Mobile").setValue(u.getContact_Number());
			  subref.child("Alternate Mobile").setValue(u.getAlternate_Contact_Number());
			  subref.child("Aadhaar").setValue(u.getAadhar_Number());
			  subref.child("Address").setValue(u.getStreet_No());
			  subref.child("Pincode").setValue(u.getPincode());
			  subref.child("State").setValue(spinner_state.getSelectedItem());
			  subref.child("City").setValue(spinner_City.getSelectedItem());
			  subref.child("Gender").setValue(spinner_gender.getSelectedItem());
			  subref.child("Profession").setValue(spinner_profession.getSelectedItem());
			  subref.child("Type").setValue(u.getType());
			  duplicate();
			  startActivity(new Intent(Signup.this , SeekerMain.class));
		   }
	    }
     }

     private boolean validation()
     {
	    boolean valid = true;

	    if(Email.isEmpty())
	    {
		   editTextEmail.setError("Email is required");
		   editTextEmail.requestFocus();
		   valid = false;
	    }

	    if(! Patterns.EMAIL_ADDRESS.matcher(Email).matches())
	    {
		   editTextEmail.setError("Please Enter valid Email address");
		   editTextEmail.requestFocus();
		   valid = false;
	    }
	    if(Password.isEmpty())
	    {
		   editTextPassword.setError("Please Enter the password");
		   editTextPassword.requestFocus();
		   valid = false;
	    }
	    if(Password.length() < 6)
	    {
		   editTextPassword.setError("Minimum password length is 6");
		   editTextPassword.requestFocus();
		   valid = false;
	    }
	    if(Type == "SeekerProfile")
	    {
		   if(Profession.isEmpty())
		   {
			  spinner_profession.requestFocus();
			  valid = false;
		   }
	    }
	    if(Gender.isEmpty())
	    {
		   spinner_gender.requestFocus();
		   valid = false;
	    }
	    if(Pincode.isEmpty())
	    {
		   editTextPincode.setError("Enter KRAPin");
		   editTextPincode.requestFocus();
		   valid = false;
	    }
	    if(Contact_Number.isEmpty())
	    {
		   editTextContact_No.setError("Enter Mobile Number");
		   editTextContact_No.requestFocus();
		   valid = false;
	    }
	    if(Aadhar_Number.isEmpty())
	    {

		   editTextAadhar_No.setError("Enter Aadhaar");
		   editTextAadhar_No.requestFocus();
		   valid = false;

	    }
	    if(Street_No.isEmpty())
	    {
		   editTextStreet.setError("Enter the Street Name or Number ");
		   editTextStreet.requestFocus();
		   valid = false;
	    }
	    if(Name.isEmpty())
	    {
		   editTextName.setError("Enter name");
		   editTextName.requestFocus();
	    }

	    return valid;
     }

     public void Coast()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Coast , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

     public void Central()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Central , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

     public void Nairobi()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Nairobi , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

     public void Western()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.western , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

     public void Nyanza()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Nyanza , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

     public void Eastern()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Eastern , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

     public void RiftValley()
     {
	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.RiftValley , android.R.layout.simple_spinner_item);
	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner_City.setAdapter(adap2);
     }

//     public void dadra()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.dadranagarhaveli , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void daman()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.damandiu , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void delhi()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Delhi , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void goa()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Goa , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void gujarat()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.gujarat , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void haryana()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.haryana , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void himachal()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.himachal , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void jammu()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.jammu , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void jharkhand()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.jarkhand , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void karnataka()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.karnataka , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void kerala()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.kerala , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void lakshwadeep()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.lakshwadeep , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void madhyapradesh()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.madhyapradesh , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void maharashtra()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.maharashtra , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void manipur()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.manipur , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void meghalaya()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.meghalaya , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void mizoram()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.mizoram , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void nagaland()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.nagaland , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void orissa()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.orissa , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void pondicherry()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.pondicherry , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void punjab()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.Punjab , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void rajasthan()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.rajasthan , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void sikkim()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.sikkim , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void tamilnadu()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.tamilnadu , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void tripura()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.tripura , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void uttarpradesh()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.uttarpradesh , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void uttaranchal()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.uttaranchal , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }
//
//     public void westbengal()
//     {
//	    ArrayAdapter<CharSequence> adap2 = ArrayAdapter.createFromResource(this , R.array.westbengal , android.R.layout.simple_spinner_item);
//	    adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    spinner_City.setAdapter(adap2);
//     }

     @Override
     public void onClick(View view)
     {

     }

     @Override
     public void onItemSelected(AdapterView<?> adapterView , View view , int i , long l)
     {

     }

     @Override
     public void onNothingSelected(AdapterView<?> adapterView)
     {

     }

     @Override
     public void onBackPressed()
     {
	    super.onBackPressed();
     }
}
