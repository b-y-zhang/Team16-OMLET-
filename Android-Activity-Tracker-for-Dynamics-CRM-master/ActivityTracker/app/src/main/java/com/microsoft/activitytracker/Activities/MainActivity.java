package com.microsoft.activitytracker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.AlteredCharSequence;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;



import com.microsoft.activitytracker.Adapters.HistoryAdapter;
import com.microsoft.activitytracker.Classes.DatabaseContracts.*;
import com.microsoft.activitytracker.Adapters.SearchResultsAdapter;
import com.microsoft.activitytracker.Classes.ActivityTracker;
import com.microsoft.activitytracker.Classes.Constants;
import com.microsoft.activitytracker.Classes.Utils;
import com.microsoft.activitytracker.Data.ContactInformation;
import com.microsoft.activitytracker.Data.LongAnswer;
import com.microsoft.activitytracker.Data.MoreContactInfo;
import com.microsoft.activitytracker.Data.Overall;
import com.microsoft.activitytracker.Data.Prerequisites;
import com.microsoft.activitytracker.R;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;

import com.microsoft.xrm.sdk.Client.OrganizationServiceProxy;
import com.microsoft.xrm.sdk.Entity;
import com.microsoft.xrm.sdk.EntityCollection;
import com.microsoft.xrm.sdk.Query.FetchExpression;
import com.nispok.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements AuthenticationCallback<AuthenticationResult>,
        LoaderManager.LoaderCallbacks<Cursor>, MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private static final int SETUP_ID = 0;

    public String firstName;
    public String lastName;
    public String homePhone;
    public String cellPhone;
    public String email;
    public String altEmail;
    public String preferred;
    public String experiences;
    public String prevexp;
    public String littleSister;
    public String health;
    public String cultures;
    public String beliefsAboutChild;
    public String timeCommitment;
    public String changesInEdu;
    public String address;
    public String city;
    public String postalCode;
    public String dateOfBirth;
    public String age;
    public String bornInCanada;


    private static final String[] MAIL_COLUMNS = {
            HistoryEntry._ID,
            HistoryEntry.COLUMN_ACCOUNT_NAME,
            HistoryEntry.COLUMN_DATE_LAST_OPEN,
            HistoryEntry.COLUMN_FULL_NAME,
            HistoryEntry.COLUMN_JOB_TITLE,
            HistoryEntry.COLUMN_LOGICAL_NAME
    };

    public static final int COL_ID_INDEX = 0;
    public static final int COL_NAME_INDEX = 1;
    public static final int COL_DATE_INDEX = 2;
    public static final int COL_FULL_NAME_INDEX = 3;
    public static final int COL_JOB_TITLE_INDEX = 4;
    public static final int COL_LOGICAL_NAME_INDEX = 5;

    private OrganizationServiceProxy mOrgService;
    private SharedPreferences mAppPreferences;
    private HistoryAdapter mHistoryAdapter;
    private ListView mMainList;
    private SwipeRefreshLayout mSwipeRefresh;
    private AuthenticationContext mAuthContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Sets up the progress bar and the pull to refresh gesture
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.pullRefresh);
        mSwipeRefresh.setOnRefreshListener(this);

        mMainList = (ListView) findViewById(R.id.itemsList);
        mHistoryAdapter = new HistoryAdapter(this, HistoryEntry.getRecentHistory(this), 0);
        mMainList.setAdapter(mHistoryAdapter);
        mMainList.setOnItemClickListener(this);

        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSwipeRefresh.setRefreshing(true);
        Authentication();
    }

    public void to_survey (View view) {

        setContentView(R.layout.survey_begin);

    }

    @Override
    public void onRefresh() {
        getRecentActivity();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object thisItem = mMainList.getAdapter().getItem(position);
        if (thisItem.getClass() != String.class) {
            // Completely bundle up the selected entity and pass it as an extra
            // to the next activity
            Intent iItem = new Intent(MainActivity.this, ItemActivity.class);
            iItem.putExtra(Constants.CURRENT_ENTITY, ((Entity) thisItem).toEntityReference().toBundle());
            startActivity(iItem);
        }
    }

    /***
     * runs authentication to get a token. If a refresh token is stored in the application storage it will
     * try to refresh the old token, or it will navigate to the signin activity for you to log in using the
     * Azure Active Directory Library's (aadl) custom webview.
     */
    private void Authentication() {
        // Get the connectivity manager so we can check if the device has an internet connection or not
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        // If we do have an internet connection, run refresh of the auth token
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            try {
                if (mAppPreferences.contains(Constants.REFRESH_TOKEN) || !mAppPreferences.getString(Constants.REFRESH_TOKEN, "").equals("")) {
                    mAuthContext = new AuthenticationContext(
                            MainActivity.this,
                            mAppPreferences.getString(Constants.AUTHORITY, ""),
                            false);

                    mAuthContext.acquireTokenByRefreshToken(
                            mAppPreferences.getString(Constants.REFRESH_TOKEN, ""),
                            Constants.CLIENT_ID,
                            this);
                } else {
                    Intent iSetup = new Intent(MainActivity.this, SetupActivity.class);
                    startActivityForResult(iSetup, SETUP_ID);
                }

            } catch (Exception e) {
                Intent iSetup = new Intent(MainActivity.this, SetupActivity.class);
                startActivityForResult(iSetup, SETUP_ID);
            }
        }
        // If we don't create a popup to tell the user
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.network_error);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });

            dialog.show();
        }
    }


    private void populateAuthContext() {
        try {
            mAuthContext = new AuthenticationContext(
                    MainActivity.this,
                    mAppPreferences.getString(Constants.AUTHORITY, ""),
                    false);
        } catch (Exception ex) {
            ex.getCause().printStackTrace();
        }
    }

    @Override
    public void onSuccess(AuthenticationResult result) {
        ActivityTracker.setCurrentSessionToken(this, result.getAccessToken());
        mOrgService = new OrganizationServiceProxy(mAppPreferences.getString(Constants.ENDPOINT, ""),
                ActivityTracker.getRequestInterceptor());

        getRecentActivity();
    }

    @Override
    public void onError(Exception exc) {
        Toast.makeText(this, "Unable to log back in", Toast.LENGTH_SHORT).show();
        Intent iSetup = new Intent(MainActivity.this, SetupActivity.class);
        startActivityForResult(iSetup, SETUP_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETUP_ID:
                mOrgService = new OrganizationServiceProxy(mAppPreferences.getString(Constants.ENDPOINT, ""),
                        ActivityTracker.getRequestInterceptor());

                getRecentActivity();
                break;
            default:
                if (mAuthContext != null) {
                    mAuthContext.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    private void getRecentActivity() {
        mMainList.setAdapter(mHistoryAdapter);
        mSwipeRefresh.setRefreshing(false);
    }

    private void runSearchQuery(@Nullable String query) {
        if (query != null) {
            mSwipeRefresh.setRefreshing(true);

            FetchExpression fetchExpression = new FetchExpression(
                    Utils.getEscapedContactSearchTermFetch(query));
            mOrgService.RetrieveMultiple(fetchExpression, new Callback<EntityCollection>() {
                @Override
                public void success(EntityCollection entityCollection, Response response) {
                    mMainList.setAdapter(new SearchResultsAdapter(getApplicationContext(), entityCollection));
                    mSwipeRefresh.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    displayError(error.getMessage());
                }
            });
        }
    }

    private void displayError(String error) {
        Snackbar.with(this)
                .text(error)
                .textColor(this.getResources().getColor(R.color.white))
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .show(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editPrefs = mAppPreferences.edit();
                editPrefs.remove(Constants.ENDPOINT);
                editPrefs.remove(Constants.REFRESH_TOKEN);
                editPrefs.remove(Constants.USERNAME);
                editPrefs.remove(Constants.AUTHORITY);

                populateAuthContext();
                CookieSyncManager.createInstance(MainActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                CookieSyncManager.getInstance().sync();
                mAuthContext.getCache().removeAll();
                HistoryEntry.clearRecentRecords(this);

                editPrefs.apply();

                Intent iSetup = new Intent(MainActivity.this, SetupActivity.class);
                startActivityForResult(iSetup, SETUP_ID);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*

    THE FOLLOWING IS ADDED

     */

    // from Page 1 (activity_form) to Page 2 (contact_info), OR ErrorPage1 (invalid_activity_form)
    public void toContactInfo(View view) {
        CheckBox cbAge = (CheckBox) findViewById(R.id.age);
        boolean age = cbAge.isChecked();

        CheckBox cbCommitment = (CheckBox) findViewById(R.id.commitment);
        boolean commitment = cbCommitment.isChecked();

        CheckBox cbAdjectives = (CheckBox) findViewById(R.id.adjectives);
        boolean adjectives = cbAdjectives.isChecked();

        CheckBox cbLegal = (CheckBox) findViewById(R.id.legal);
        boolean legal = cbLegal.isChecked();

        //System.out.println("Age Checked? " + age);
        //System.out.println("Legal Checked? " + commitment);
        //System.out.println("Commitment Checked? " + adjectives);
        //System.out.println("Adjectives Checked? " + legal);

        Prerequisites prerequisites = new Prerequisites(age, commitment, adjectives, legal);

        // Return prerequistes if needed


        if (age && commitment && adjectives && legal) {
            setContentView(R.layout.contact_info); // move on if all fields are checked
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("You're missing a field :(");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            alertDialog.show();
        }

    }


    public ContactInformation toMoreContact(View view) {
        // ADD DATA HERE MUST PARSE DATA FROM FORM

        EditText CIfirstName = (EditText) findViewById(R.id.First_Name);
        String FirstName = CIfirstName.getText().toString();

        EditText CIlastName = (EditText) findViewById(R.id.Last_Name);
        String LastName = CIlastName.getText().toString();

        EditText CIhomePhone = (EditText) findViewById(R.id.Home_Phone);
        String HomePhone = CIhomePhone.getText().toString();

        EditText CIcellPhone = (EditText) findViewById(R.id.Cell_Phone);
        String CellPhone = CIcellPhone.getText().toString();

        EditText CIemail = (EditText) findViewById(R.id.Email);
        String Email = CIemail.getText().toString();

        EditText CIaltEmail = (EditText) findViewById(R.id.Alternate_Email);
        String altEmail = CIaltEmail.getText().toString();

        TextView CIpreferred = (TextView) findViewById(R.id.Spinner_Prompt);
        String preferred = CIpreferred.getText().toString();

        HomePhone = HomePhone.replaceAll("[^\\d.]", ""); // truncates all non-digit characters
        CellPhone = CellPhone.replaceAll("[^\\d.]", ""); // truncates all non-digit characters

        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher mEmail = p.matcher(Email);
        boolean matchFoundEmail = mEmail.matches();
        Matcher mAltEmail = p.matcher(altEmail);
        boolean matchFoundAltEmail = mAltEmail.matches();

        //System.out.println(HomePhone);
        //System.out.println(CellPhone);

        if (!FirstName.isEmpty() && !LastName.isEmpty()
                && !HomePhone.isEmpty() && matchFoundEmail && (matchFoundAltEmail || altEmail.isEmpty())) {


            this.firstName = FirstName;
            this.lastName = LastName;
            this.homePhone = HomePhone;
            this.cellPhone = CellPhone;
            this.email = Email;
            this.altEmail = altEmail;
            this.preferred = preferred;


            setContentView(R.layout.more_contact_info);


            return new ContactInformation(FirstName, LastName, HomePhone, CellPhone, Email, altEmail, preferred);


        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("Please double check your fields.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            alertDialog.show();
        }

        //System.out.println("First Name is " + FirstName); // DEBUGGING
        //System.out.println("Last Name is " + LastName); // DEBUGGING
        return new ContactInformation("", "", "", "", "", "", "");
    }

    public  void toLongAnswer(View view) {
        EditText MCIaddress = (EditText) findViewById(R.id.address);
        String address = MCIaddress.getText().toString();

        EditText MCIcity = (EditText) findViewById(R.id.city);
        String city = MCIcity.getText().toString();

        EditText MCIpostalCode = (EditText) findViewById(R.id.postalcode);
        String postalCode = MCIpostalCode.getText().toString();

        EditText MCIDOB = (EditText) findViewById(R.id.dob);
        String dateOfBirth = MCIDOB.getText().toString();

        Pattern dob = Pattern.compile("^(0[1-9]|1[0-2])\\/(0[1-9]|1\\d|2\\d|3[01])\\/(19|20)\\d{2}$");
        Matcher mDOB = dob.matcher(dateOfBirth);
        Boolean matchFoundDOB = mDOB.matches();

        EditText MCIage = (EditText) findViewById(R.id.age);
        String age = MCIage.getText().toString();

        Pattern agePattern = Pattern.compile("^\\d{1,3}$");
        Matcher ageMatcher = agePattern.matcher(age);
        Boolean matchFoundAge = ageMatcher.matches();

        Spinner MCIbornInCanada = (Spinner) findViewById(R.id.Place_Of_Birth);
        String bornInCanada = MCIbornInCanada.getSelectedItem().toString();

        Pattern p = Pattern.compile("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ] ?[0-9][ABCEGHJKLMNPRSTVWXYZ][0-9] *");
        Matcher mPostalCode = p.matcher(postalCode);
        Boolean matchFoundPostalCode = mPostalCode.matches();


        if (address.isEmpty() || city.isEmpty() || postalCode.isEmpty() ||
                dateOfBirth.isEmpty() || age.isEmpty() || bornInCanada.isEmpty()) {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("You're missing a field :(");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();



        } else if (!matchFoundAge || Integer.parseInt(age) > 150 || Integer.parseInt(age) < 19 ){

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("Please double check your age.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }

        else if(!matchFoundDOB) {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("Incorrect DOB");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();

        }


        else if (!matchFoundPostalCode) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("Incorrect Postal Code (uppercase!)");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();

        } else {

            this.dateOfBirth = dateOfBirth;
            this.address = address;
            this.city = city;
            this.postalCode = postalCode;
            this.age = age;
            this.bornInCanada = bornInCanada;


            setContentView(R.layout.long_answer);

            MoreContactInfo val = new MoreContactInfo(address, city, postalCode, dateOfBirth, age, bornInCanada);




        }
    }







    public LongAnswer references (View view) {

        EditText LAexperiences = (EditText) findViewById(R.id.experiences_a);
        String experiences = LAexperiences.getText().toString();

        CheckBox LAprevexp_yes = (CheckBox) findViewById(R.id.prevexp_yes);
        Boolean prevexp_yes_ans = LAprevexp_yes.isChecked();

        CheckBox LAprevexp_no = (CheckBox) findViewById(R.id.prevexp_no);
        Boolean prevexp_no_ans = LAprevexp_no.isChecked();

        EditText LAlittleSister_q = (EditText) findViewById(R.id.littlesister_a);
        String littleSister = LAlittleSister_q.getText().toString();

        EditText LAhealth = (EditText) findViewById(R.id.health_a);
        String health = LAhealth.getText().toString();

        EditText LAcultures = (EditText) findViewById(R.id.cultures_a);
        String cultures = LAcultures.getText().toString();

        EditText LAbeliefsAboutChild = (EditText) findViewById(R.id.beliefsaboutchild_a);
        String beliefsaboutchild = LAbeliefsAboutChild.getText().toString();

        EditText LAtimeCommitment = (EditText) findViewById(R.id.timecommitment_a);
        String timeCommitment = LAtimeCommitment.getText().toString();

        EditText LAchangesInEdu = (EditText) findViewById(R.id.changesinedu_a);
        String changesInEdu = LAchangesInEdu.getText().toString();

        String prevexp;

        if (prevexp_no_ans && prevexp_yes_ans) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Incorrect Fields");
            alertDialog.setMessage("You cannot check both yes and no!.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                } });
            alertDialog.show();
        }
        else if (experiences.isEmpty() || littleSister.isEmpty() || health.isEmpty() ||
                cultures.isEmpty() || beliefsaboutchild.isEmpty() || timeCommitment.isEmpty() ||
                changesInEdu.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Empty Field");
            alertDialog.setMessage("Please check you have entered all fields");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                } });
            alertDialog.show();

        }
        else {

            if (prevexp_no_ans) {
                prevexp = "No";
            } else {
                prevexp = "Yes";
            }

            this.experiences = experiences;
            this.prevexp = prevexp;
            this.littleSister = littleSister;
            this.health = health;
            this.cultures = cultures;
            this.beliefsAboutChild = beliefsaboutchild;
            this.timeCommitment = timeCommitment;
            this.changesInEdu = changesInEdu;






            setContentView(R.layout.submit_page);







            return new LongAnswer(experiences, prevexp, littleSister, health, cultures, beliefsaboutchild,
                    timeCommitment, changesInEdu);

        }

        return new LongAnswer("","", "","","","","","");

    }

    public void submit (View view) {


        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(homePhone);
        System.out.println(cellPhone);
        System.out.println(email);
        System.out.println(altEmail);
        System.out.println(preferred);
        System.out.println(experiences);
        System.out.println(prevexp);
        System.out.println(littleSister);
        System.out.println(health);
        System.out.println(cultures);
        System.out.println(beliefsAboutChild);
        System.out.println(timeCommitment);
        System.out.println(changesInEdu);
        System.out.println(address);
        System.out.println(city);
        System.out.println(postalCode);
        System.out.println(dateOfBirth);
        System.out.println(age);
        System.out.println(bornInCanada);

        Overall o = new Overall(firstName, lastName, homePhone, cellPhone,email,altEmail,
                preferred,experiences,prevexp,littleSister,health,cultures, beliefsAboutChild,
                timeCommitment, changesInEdu, address, city, postalCode, dateOfBirth, age, bornInCanada);

        Context c = getApplicationContext();

        CSVWriter.printToCSV(o, c);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Submitted!");
        alertDialog.setMessage("Thanks for submitting your application!");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            } });
        alertDialog.show();

    }

    public void backToStart (View view) {

        resetData();
        setContentView(R.layout.activity_main);

    }


    public void resetData () {
        firstName = "";
        lastName = "";
        homePhone = "";
        cellPhone = "";
        email = "";
        altEmail = "";
        preferred = "";
        experiences = "";
        prevexp = "";
        littleSister = "";
        health = "";
        cultures = "";
        beliefsAboutChild = "";
        timeCommitment = "";
        changesInEdu = "";
        address = "";
        city = "";
        postalCode = "";
        dateOfBirth = "";
        age = "";
        bornInCanada = "";
    }









    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        getRecentActivity();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        runSearchQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mHistoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mHistoryAdapter.swapCursor(null);
    }
}