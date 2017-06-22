package com.jaus.albertogiunta.justintrain_oraritreni.journeyFavourites;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.jaus.albertogiunta.justintrain_oraritreni.BuildConfig;
import com.jaus.albertogiunta.justintrain_oraritreni.MyApplication;
import com.jaus.albertogiunta.justintrain_oraritreni.R;
import com.jaus.albertogiunta.justintrain_oraritreni.aboutAndSettings.AboutActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.aboutAndSettings.AboutPageUtils;
import com.jaus.albertogiunta.justintrain_oraritreni.aboutAndSettings.LicenseUpgradeActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.aboutAndSettings.SettingsActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraritreni.db.Station;
import com.jaus.albertogiunta.justintrain_oraritreni.journeyResults.JourneyResultsActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.journeySearch.JourneySearchActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.DateTimeAdapter;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.PostProcessingEnabler;
import com.jaus.albertogiunta.justintrain_oraritreni.notification.NotificationService;
import com.jaus.albertogiunta.justintrain_oraritreni.tutorial.IntroActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.components.AnimationUtils;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.components.HideShowScrollListener;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.components.ViewsUtils;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.ENUM_SNACKBAR_ACTIONS;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.AnalyticsHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.ServerConfigsHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.ShortcutHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.sharedPreferences.MigrationHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.sharedPreferences.SettingsPreferences;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.sharedPreferences.SharedPreferencesHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

import org.joda.time.DateTime;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import trikita.log.Log;

import static butterknife.ButterKnife.apply;
import static com.android.billingclient.api.BillingClient.BillingResponse;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.components.ViewsUtils.GONE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.components.ViewsUtils.VISIBLE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_AR_FROM_POPUP;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_NO_SWIPE_BUT_CLICK;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_ON_UPDATE_CLICK;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_RA_FROM_POPUP;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_REMOVE_FROM_POPUP;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_SEARCH_JOURNEY_FROM_FAVOURITES;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_SWIPE_LEFT_TO_RIGHT;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.ACTION_SWIPE_RIGHT_TO_LEFT;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_ANALYTICS.SCREEN_FAVOURITE_JOURNEYS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_FIREBASE.FIREBASE_IS_MAINTENANCE_SET;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_FIREBASE.FIREBASE_IS_STRIKE_SET;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_FIREBASE.FIREBASE_LATEST_VERSION;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_FIREBASE.FIREBASE_MAINTENANCE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_FIREBASE.FIREBASE_STRIKE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_FIREBASE.FIREBASE_UPDATE_MESSAGE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_INTENT.I_FROM_SWIPE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_INTENT.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.CONST_SP_V0.SP_SP_FIRST_START;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.constants.ENUM_SNACKBAR_ACTIONS.NONE;

public class FavouriteJourneysActivity extends AppCompatActivity implements FavouritesContract.View,
        FlexibleAdapter.OnItemSwipeListener,
        FlexibleAdapter.OnItemClickListener,
        PurchasesUpdatedListener {

    FavouritesContract.Presenter presenter;
    AnalyticsHelper              analyticsHelper;
    BroadcastReceiver            messageReceiver;

    @BindView(R.id.btn_iap)
    Button btnIAP;

    @BindView(R.id.rv_favourite_journeys)
    RecyclerView rvFavouriteJourneys;
    @BindView(R.id.ll_add_favourite)
    LinearLayout llAddFavourite;
    FavouriteJourneysAdapter adapter;
    @BindView(R.id.rl_message)
    CardView rlMessage;
    @BindView(R.id.tv_message)
    TextView tvMessage;

    @BindView((R.id.fab_search_journey))
    FloatingActionButton btnSearch;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
            .registerTypeAdapterFactory(new PostProcessingEnabler())
            .create();

    BillingClient mBillingClient;

//    @BindView(R.id.adView)
//    AdView         adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_journeys);
        ButterKnife.bind(this);
        analyticsHelper = AnalyticsHelper.getInstance(getViewContext());
//        Ads.initializeAds(getViewContext(), adView);
        checkIntro();
//        MigrationHelper.migrateIfDue(getViewContext());
        presenter = new FavouritesPresenter(this);

        new Handler().postDelayed(() -> {
            fetchRemoteConfigs();
        }, 0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_favourite_journeys);
        }

        ShortcutHelper.updateShortcuts(getViewContext());

        adapter = new FavouriteJourneysAdapter(presenter.getRecyclerViewList(), getViewContext());
        rvFavouriteJourneys.setAdapter(adapter);
        rvFavouriteJourneys.setLayoutManager(new LinearLayoutManager(this));
        adapter.setSwipeEnabled(true);

        btnSearch.setScaleX(0);
        btnSearch.setScaleY(0);
        new Handler().postDelayed(() -> AnimationUtils.onCompare(btnSearch), 500);

        rvFavouriteJourneys.addOnScrollListener(new HideShowScrollListener() {
            @Override
            public void onHide() {
                btnSearch.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(200).setDuration(100);
                btnIAP.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(200).setDuration(100);
            }

            @Override
            public void onShow() {
                btnSearch.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0).setDuration(100);
                btnIAP.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0).setDuration(100);
            }
        });

        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showSnackbar(intent.getExtras().getString(NotificationService.NOTIFICATION_ERROR_MESSAGE), NONE, Snackbar.LENGTH_SHORT);
            }
        };

        mBillingClient = new BillingClient.Builder(getViewContext())
                .setListener(this)
                .build();

        btnIAP.setText(btnIAP.getText().toString() + new String(Character.toChars(0x21AA)));
//        btnIAP.setScaleX(0);
//        btnIAP.setScaleY(0);
//        new Handler().postDelayed(() -> AnimationUtils.onCompare(btnIAP), 500);

        btnIAP.setOnClickListener(v -> {
            Intent i = new Intent(FavouriteJourneysActivity.this, LicenseUpgradeActivity.class);
            FavouriteJourneysActivity.this.startActivity(i);
//            mBillingClient.startConnection(new BillingClientStateListener() {
//                @Override
//                public void onBillingSetupFinished(@BillingResponse int billingResponse) {
//                    if (billingResponse == BillingResponse.OK) {
//                        // The billing client is ready. You can query purchases here.
//                        List<String> skuList = new ArrayList<>();
//                        skuList.add("premium_upgrade_mp");
//                        mBillingClient.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList,
//                                result -> {
//                                    if (result.getResponseCode() == BillingResponse.OK
//                                            && result.getSkuDetailsList() != null) {
//                                        // only 1 item so far
//                                        String                    skuId        = result.getSkuDetailsList().get(0).getSku();
//                                        BillingFlowParams.Builder builder      = new BillingFlowParams.Builder().setSku(skuId).setType(BillingClient.SkuType.INAPP);
//                                        int                       responseCode = mBillingClient.launchBillingFlow(FavouriteJourneysActivity.this, builder.build());
//
//                                    }
//
//                                });
//
//                    }
//                }
//
//                @Override
//                public void onBillingServiceDisconnected() {
//                    // Try to restart the connection on the next request to the
//                    // In-app Billing service by calling the startConnection() method.
//                }
//            });


        });

        AsyncTask task = new LoadCursorTask(this).execute();
    }

    @Override
    public void onPurchasesUpdated(@BillingResponse int responseCode, List<Purchase> purchases) {
        if (responseCode == BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                Log.d("onPurchasesUpdated: ", purchase.toString());
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), (purchaseToken, resultCode) -> Log.d("onConsumeResponse: ", resultCode));
            }
        } else if (responseCode == BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }


    abstract private class BaseTask<T> extends AsyncTask<T, Void, List<Station>> {
        Context app;

        BaseTask(Context ctxt) {
            app = ctxt.getApplicationContext();
        }

        @Override
        public void onPostExecute(List<Station> result) {
        }

        List<Station> doQuery() {
            return (MyApplication.database.stationDao().getAllByNameLong());
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        private LoadCursorTask(Context ctxt) {
            super(ctxt);
        }

        @Override
        protected List<Station> doInBackground(Void... params) {
            return (doQuery());
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_join:
                FavouriteJourneysActivity.this.startActivity(new Intent(FavouriteJourneysActivity.this, LicenseUpgradeActivity.class));
                return true;
            case R.id.action_settings:
                FavouriteJourneysActivity.this.startActivity(new Intent(FavouriteJourneysActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Dai un'occhiata a quest'app pensata appositamente per i pendolari Trenitalia!\nhttps://play.google.com/store/apps/details?id=com.jaus.albertogiunta.justintrain_oraritreni");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Consiglia l'app via..."));
                return true;
            case R.id.action_review:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return true;
            case R.id.action_legend:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FavouriteJourneysActivity.this);
                View view = LayoutInflater.from(FavouriteJourneysActivity.this).inflate(R.layout.dialog_legend, null);
                alertDialog.setView(view)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
                return true;
            case R.id.action_about:
                FavouriteJourneysActivity.this.startActivity(new Intent(FavouriteJourneysActivity.this, AboutActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        adView.resume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(NotificationService.NOTIFICATION_ERROR_EVENT));
        btnSearch.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0).setDuration(0);
        presenter.setState(getIntent().getExtras());
    }

    @Override
    protected void onPause() {
//        adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public Context getViewContext() {
        return FavouriteJourneysActivity.this;
    }

    @Override
    public void showSnackbar(String message, ENUM_SNACKBAR_ACTIONS intent, int duration) {
        Log.w(message);
        Snackbar snackbar = Snackbar
                .make(this.rvFavouriteJourneys, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
        }
        snackbar.show();
    }

    @OnClick({R.id.fab_search_journey, R.id.ll_add_favourite})
    public void search() {
        analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_SEARCH_JOURNEY_FROM_FAVOURITES);
        Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneySearchActivity.class);
        FavouriteJourneysActivity.this.startActivity(myIntent);
    }

    @Override
    public void updateFavouritesList() {
        adapter.updateDataSet(presenter.getRecyclerViewList());
    }

    @Override
    public void updateDashboard(String message, ViewsUtils.COLORS titleColor, boolean isUpdateMessage) {

        apply(this.rlMessage, VISIBLE);
        this.tvMessage.setText(message);
        this.tvMessage.setTextColor(ViewsUtils.getTimeDifferenceColor(getViewContext(), titleColor));

        if (isUpdateMessage) {
            this.rlMessage.setOnClickListener(v -> {
                analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_ON_UPDATE_CLICK);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_native_url) + getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_web_url) + getPackageName())));
                }
            });
        }
    }

    @Override
    public void displayFavouriteJourneys() {
        apply(rvFavouriteJourneys, VISIBLE);
        apply(llAddFavourite, GONE);
    }

    @Override
    public void displayEntryButton() {
        apply(rvFavouriteJourneys, GONE);
        apply(llAddFavourite, VISIBLE);
    }

    @Override
    public void hideMessage() {
        apply(this.rlMessage, GONE);
    }

    private void fetchRemoteConfigs() {
        ServerConfigsHelper.removeAPIEndpoint(FavouriteJourneysActivity.this);
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 720; // 1/5 hour in seconds.
        if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            Log.d("fetchRemoteConfigs: changing cache expiration");
            cacheExpiration = 0;
        }

        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        firebaseRemoteConfig.activateFetched();
                    } else {
                        FirebaseCrash.report(new Exception("Firebase Remote Config FAILED in ADDONCOMPLETELISTENER"));
                    }
//                    if (BuildConfig.DEBUG) {
//                        updateDashboard(firebaseRemoteConfig.getString(FIREBASE_STRIKE), ViewsUtils.COLORS.GREEN, false);
//                    } else {
                    if (firebaseRemoteConfig.getBoolean(FIREBASE_IS_STRIKE_SET)) {
                        updateDashboard(firebaseRemoteConfig.getString(FIREBASE_STRIKE), ViewsUtils.COLORS.RED, false);
                    } else if (firebaseRemoteConfig.getBoolean(FIREBASE_IS_MAINTENANCE_SET)) {
                        updateDashboard(firebaseRemoteConfig.getString(FIREBASE_MAINTENANCE), ViewsUtils.COLORS.ORANGE, false);
                    } else if (firebaseRemoteConfig.getLong(FIREBASE_LATEST_VERSION) > BuildConfig.VERSION_CODE) {
                        updateDashboard(firebaseRemoteConfig.getString(FIREBASE_UPDATE_MESSAGE), ViewsUtils.COLORS.BLUE, true);
                    }
//                    }
                })
                .addOnFailureListener(this, e -> {
                    FirebaseCrash.report(new Exception("Firebase Remote Config FAILED in ADDONFAILURELISTENER"));
                    //TODO PERCHé REMOTECONFIG SUCCEDE SPESSO CHE FINISCE QUI?
                });
    }

    private Intent setISearchIntent(boolean isLeftToRightSwipe, PreferredJourney preferredJourney) {
        if (isLeftToRightSwipe) {
            analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_SWIPE_LEFT_TO_RIGHT);
        } else {
            analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_SWIPE_RIGHT_TO_LEFT);
        }
        Intent intent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
        intent.putExtras(bundleJourney(preferredJourney));
        return intent;
    }

    private void checkIntro() {
        MigrationHelper.migrateIfDue(getViewContext());
        boolean isFirstStart = SharedPreferencesHelper.getSharedPreferenceBoolean(getViewContext(), SP_SP_FIRST_START, true);
        if (isFirstStart) {
            SettingsPreferences.setDefaultSharedPreferencesOnFirstStart(getViewContext());
            Intent i = new Intent(FavouriteJourneysActivity.this, IntroActivity.class);
            startActivity(i);
        } else {
            int     savedVersion            = SettingsPreferences.getPreviouslySavedVersionCode(getViewContext());
            boolean isFirstStartAfterUpdate = savedVersion < BuildConfig.VERSION_CODE;

            if (isFirstStartAfterUpdate) {
                SettingsPreferences.setNewSettingsNotPreviouslyIncludedBefore(getViewContext());
                handler.sendEmptyMessage(1);
                SettingsPreferences.setCurrentVersionCode(getViewContext());
            }
        }
    }

    Handler handler = new Handler(message -> {
        AboutPageUtils.showChangelog(this);
        return false;
    });

    private Bundle bundleJourney(PreferredJourney journey) {
        Bundle bundle = new Bundle();
        bundle.putString(I_STATIONS, gson.toJson(journey));
        bundle.putBoolean(I_FROM_SWIPE, true);
        return bundle;
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        List<Integer> positions = new ArrayList<>(1);
        positions.add(position);
        // Build the message
        IFlexible abstractItem = adapter.getItem(position);
        // Experimenting NEW feature
        if (abstractItem.isSelectable()) {
            adapter.setRestoreSelectionOnUndo(false);
        }

        if (direction == ItemTouchHelper.LEFT) {
            FavouriteJourneysActivity.this.startActivity(setISearchIntent(true, ((FavouriteJourneysItem) presenter.getRecyclerViewList().get(position)).getPreferredJourney().swapStations()));
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_out_right);
        } else if (direction == ItemTouchHelper.RIGHT) {
            FavouriteJourneysActivity.this.startActivity(setISearchIntent(false, ((FavouriteJourneysItem) presenter.getRecyclerViewList().get(position)).getPreferredJourney()));
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    }

    @Override
    public boolean onItemClick(int position) {
        analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_NO_SWIPE_BUT_CLICK);
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (presenter.getRecyclerViewList().get(position) instanceof FavouriteJourneysItem) {
            vibe.vibrate(25);
            PreferredJourney preferredJourney = ((FavouriteJourneysItem) presenter.getRecyclerViewList().get(position)).getPreferredJourney();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(FavouriteJourneysActivity.this);
            View                view        = LayoutInflater.from(FavouriteJourneysActivity.this).inflate(R.layout.dialog_favourites_click, null);
            alertDialog.setView(view)
                    .setPositiveButton("ANNULLA", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            Dialog dialog = alertDialog.show();

            RelativeLayout rlSearchAR             = (RelativeLayout) view.findViewById(R.id.rl_search_ar);
            TextView       searchAR               = (TextView) view.findViewById(R.id.tv_search_ar);
            RelativeLayout rlSearchRA             = (RelativeLayout) view.findViewById(R.id.rl_search_ra);
            TextView       searchRA               = (TextView) view.findViewById(R.id.tv_search_ra);
            RelativeLayout rlRemoveFromFavourites = (RelativeLayout) view.findViewById(R.id.rl_remove_from_favourites);

            searchAR.setText("Da " + preferredJourney.getStation1().getNameShort() + " a " + preferredJourney.getStation2().getNameShort());
            searchRA.setText("Da " + preferredJourney.getStation2().getNameShort() + " a " + preferredJourney.getStation1().getNameShort());

            rlSearchAR.setOnClickListener(v -> {
                analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_AR_FROM_POPUP);
                Intent intent = new Intent(FavouriteJourneysActivity.this, JourneySearchActivity.class);
                intent.putExtra(I_STATIONS, gson.toJson(preferredJourney));
                startActivity(intent);
                dialog.dismiss();
            });

            rlSearchRA.setOnClickListener(v -> {
                analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_RA_FROM_POPUP);
                Intent intent = new Intent(FavouriteJourneysActivity.this, JourneySearchActivity.class);
                intent.putExtra(I_STATIONS, gson.toJson(preferredJourney.swapStations()));
                startActivity(intent);
                dialog.dismiss();
            });

            rlRemoveFromFavourites.setOnClickListener(v -> {
                analyticsHelper.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_REMOVE_FROM_POPUP);
                presenter.removeFavourite(preferredJourney.getStation1(), preferredJourney.getStation2());
                updateFavouritesList();
                btnSearch.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0).setDuration(0);
                dialog.dismiss();
            });

        } else {

        }


        return false;
    }
}
