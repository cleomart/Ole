package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class LandlordEditTenantProfileActivity extends EditTenantProfileActivity {

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tenant_profile);

        Intent tenantIntent = getIntent();
        Bundle intentExtras = tenantIntent.getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(intentExtras != null) {
            uid = intentExtras.getString(TenantProfile.EXTRA_TENANT_ID);
            getTenantFromID(uid);
        } else {
            // TODO: Raise dialog exception
            tenant = null;
        }
    }

    @Override
    public void loadData() {
        super.loadData();
        room.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            updateTenantData(uid);

            Intent intent = new Intent(this, LandlordTenantProfileActivity.class);
            intent.putExtra(TenantProfile.EXTRA_TENANT_ID, tenant.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
