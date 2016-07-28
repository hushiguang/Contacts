package gavinhua.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gavinhua.contacts.model.ContactEntity;
import gavinhua.contacts.presenter.ContactPresenter;
import gavinhua.contacts.presenter.IContactPresenter;
import gavinhua.contacts.utils.ColorUtils;
import gavinhua.contacts.view.IContactView;

public class DetailActivity extends AppCompatActivity implements IContactView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.phone)
    TextView phone;

    @Bind(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.msg)
    ImageView msg;

    ContactEntity contactEntity;

    int index;

    IContactPresenter contactPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitleEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        index = getIntent().getExtras().getInt("index");

        contactPresenter = new ContactPresenter(this);

        contactPresenter.initDataByIndex(index);

    }

    @Override
    public void initData(ContactEntity contactEntity) {
        this.contactEntity = contactEntity;
        setTitle(contactEntity.getName());

        phone.setText(contactEntity.getPhone());

        // 着色
        int color = ColorUtils.getColor(getResources(), contactEntity.getName());
        toolbar.setBackgroundColor(color);
        collapsingToolbarLayout.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorUtils.getDarkerColor(color));
            icon.getDrawable().setTint(color);
            msg.getDrawable().setTint(color);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_edit:
                Intent intent = new Intent(DetailActivity.this, EditContactActivity.class);
                intent.putExtra("index", index);
                startActivityForResult(intent, ContactsFragment.REQUEST_CODE);
                break;
            case R.id.action_delete:
                contactPresenter.deleteContact(this, index);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.msg, R.id.cardview})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.msg:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + contactEntity.getPhone()));
                break;
            case R.id.cardview:
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactEntity.getPhone()));
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == ContactsFragment.REQUEST_CODE
                    && resultCode == ContactsFragment.RESPONSE_UPDATE_CODE) {
                int i = (int) data.getExtras().get("index");
                contactEntity = contactPresenter.getIndex(i);
                phone.setText(contactEntity.getPhone());
                collapsingToolbarLayout.setTitle(contactEntity.getName());

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.dialpad_slide_out_bottom);
    }


}
