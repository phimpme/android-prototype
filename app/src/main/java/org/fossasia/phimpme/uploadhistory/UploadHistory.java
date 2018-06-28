package org.fossasia.phimpme.uploadhistory;


import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

/**
 * Created by pa1pal on 17/08/17.
 */

public class UploadHistory extends ThemedActivity {

    @BindView(R.id.upload_history_recycler_view)
    RecyclerView uploadHistoryRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.swipeRefreshLayout_uploadhis)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.empty_icon)
    IconicsImageView emptyIcon;

    @BindView(R.id.emptyLayout)
    RelativeLayout emptyLayout;

    @BindView(R.id.empty_text)
    TextView emptyText;

    @BindView(R.id.accounts_parent)
    RelativeLayout parentView;

    Realm realm;

    private ArrayList<UploadHistoryRealmModel> uploadResults;
    private RealmQuery<UploadHistoryRealmModel> uploadHistoryRealmModelRealmQuery;
    private UploadHistoryAdapter uploadHistoryAdapter;
    private PreferenceUtil preferenceUtil;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            UploadHistoryRealmModel uploadHistoryRealmModel = (UploadHistoryRealmModel) view.findViewById(R.id
                    .upload_time).getTag();
            view.setTransitionName(getString(R.string.transition_photo));
            Intent intent = new Intent("com.android.camera.action.REVIEW", Uri.fromFile(new File(uploadHistoryRealmModel.getPathname())));
            intent.putExtra("path", uploadHistoryRealmModel.getPathname());
            intent.putExtra("position", checkpos(uploadHistoryRealmModel.getPathname()));
            intent.putExtra("size", uploadResults.size());
            intent.putExtra("uploadhistory", true);
            ArrayList<Media> u = loaduploaddata();
            intent.putParcelableArrayListExtra("datalist", u);
            intent.setClass(getApplicationContext(), SingleMediaActivity.class);
            context.startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_history_activity);
        ButterKnife.bind(this);
        preferenceUtil = PreferenceUtil.getInstance(getContext());
        uploadHistoryAdapter = new UploadHistoryAdapter(getPrimaryColor());
        uploadHistoryAdapter.setOnClickListener(onClickListener);
        realm = Realm.getDefaultInstance();
        removedeletedphotos();
        uploadHistoryRealmModelRealmQuery = realm.where(UploadHistoryRealmModel.class);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), columnsCount());
        layoutManager.setReverseLayout(false);
        uploadHistoryRecyclerView.setLayoutManager(layoutManager);
        uploadHistoryRecyclerView.setAdapter(uploadHistoryAdapter);
        String choiceofdisply = preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string
                .last_first));
        if(choiceofdisply.equals(getString(R.string.last_first))){
            uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
        }else if(choiceofdisply.equals(getString(R.string.latest_first))){
            uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
        }
        setUpUI();
        //uploadHistoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));
    }


    private void removedeletedphotos(){
        RealmQuery<UploadHistoryRealmModel> uploadHistoryRealmModelRealmQuery = realm.where(UploadHistoryRealmModel.class);
        ArrayList<String> todel = new ArrayList<>();
        for(int i = 0; i < uploadHistoryRealmModelRealmQuery.count(); i++){
            if(!new File(uploadHistoryRealmModelRealmQuery.findAll().get(i).getPathname()).exists()){
                todel.add(uploadHistoryRealmModelRealmQuery.findAll().get(i).getPathname());
            }
        }
        for(int i = 0; i < todel.size(); i++){
            final String path = todel.get(i);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<UploadHistoryRealmModel> result = realm.where(UploadHistoryRealmModel.class).equalTo
                            ("pathname", path).findAll();
                    result.deleteAllFromRealm();
                }
            });
        }
    }

    private ArrayList<Media> loaduploaddata(){
        ArrayList<Media> data = new ArrayList<>();
        for(int i = 0; i < uploadResults.size(); i++){
            data.add(new Media(new File(uploadResults.get(i).getPathname())));
        }
        return data;
    }

    private ArrayList<UploadHistoryRealmModel> loadData(String displaychoice){
       // ArrayList<UploadHistoryRealmModel> ki = new ArrayList<>();
       // String s = preferenceUtil.getString("upload_view_choice", "Last first");
        uploadResults = new ArrayList<>();
        if(displaychoice.equals(getString(R.string.last_first))){
            for(int i = 0; i < uploadHistoryRealmModelRealmQuery.findAll().size(); i++){
                uploadResults.add(uploadHistoryRealmModelRealmQuery.findAll().get(i));
            }
        }else if(displaychoice.equals(getString(R.string.latest_first))){
            for(int i = 0; i < uploadHistoryRealmModelRealmQuery.findAll().size(); i++){
                uploadResults.add(uploadHistoryRealmModelRealmQuery.findAll().get(uploadHistoryRealmModelRealmQuery
                        .findAll().size() - i - 1));
            }
        }
        return uploadResults;
    }

    private int checkpos(String path){
        int pos = 0;
        for(int i = 0; i < uploadResults.size(); i++){
            if(path.equals(uploadResults.get(i).getPathname())){
                pos = i;
                break;
            }
        }
        return pos;
    }

    private int columnsCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 2
                : 3;
    }

    private void setUpUI() {
        emptyIcon.setColor(getIconColor());
        emptyText.setTextColor(getAccentColor());
        parentView.setBackgroundColor(getBackgroundColor());
        setupToolbar();
        swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                String choiceofdisply = preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string
                        .last_first));
                if(choiceofdisply.equals(getString(R.string.last_first))){
                    uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
                }else if(choiceofdisply.equals(getString(R.string.latest_first))){
                    uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
                }
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false
                    );
                }
            }
        });
    }

    public void setUpAdapter(@NotNull ArrayList<UploadHistoryRealmModel> accountDetails) {
        this.uploadResults = accountDetails;
        uploadHistoryAdapter.setResults(uploadResults);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_uploadhistoryactivity, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {

       if(preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string.last_first)).equals
               (getString(R.string.last_first))){
           menu.findItem(R.id.upload_history_sort).setTitle(getString(R.string.latest_first));
       }else if(preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string.last_first)).equals
               (getString(R.string.latest_first))){
           menu.findItem(R.id.upload_history_sort).setTitle(getString(R.string.last_first));
       }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.upload_history_sort:
                if(item.getTitle().equals(getString(R.string.latest_first))){
                    item.setTitle(getString(R.string.last_first));
                    new SortTask().execute(getString(R.string.latest_first));
                }else{
                    item.setTitle(getString(R.string.latest_first));
                    new SortTask().execute(getString(R.string.last_first));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUI();
        if (uploadResults.size() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            uploadHistoryRecyclerView.setVisibility(View.GONE);
        }
    }
    private void setupToolbar(){
        setSupportActionBar(toolbar);
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        toolbar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_arrow_left)
                        .color(Color.WHITE)
                        .sizeDp(19));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class SortTask extends AsyncTask<String, Void, Void> {
        Realm realm;

        @Override protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override protected Void doInBackground(String... strings) {
            realm = Realm.getDefaultInstance();
            if(strings[0].equals(getString(R.string.latest_first))){
                SharedPreferences.Editor s = preferenceUtil.getEditor();
                s.putString(getString(R.string.upload_view_choice), getString(R.string.latest_first));
                s.commit();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
                    }
                });
            }else if(strings[0].equals(getString(R.string.last_first))){
                SharedPreferences.Editor s = preferenceUtil.getEditor();
                s.putString(getString(R.string.upload_view_choice), getString(R.string.last_first));
                s.commit();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
                    }
                });
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
