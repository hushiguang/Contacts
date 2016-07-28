package gavinhua.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import butterknife.Bind;
import butterknife.ButterKnife;
import gavinhua.contacts.model.ContactEntity;
import gavinhua.contacts.presenter.ContactPresenter;
import gavinhua.contacts.presenter.IContactPresenter;
import gavinhua.contacts.utils.ColorUtils;
import gavinhua.contacts.view.IContactView;

/**
 * Created by HuShiGuang on 2016/3/31.
 */
public class EditContactActivity extends AppCompatActivity implements IContactView {


    @Bind(R.id.editor_img)
    ImageView editorImg;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.editor_edit_name)
    EditText editorEditName;
    @Bind(R.id.editor_edit_phone)
    EditText editorEditPhone;

    String newName, newPhone;

    IContactPresenter contactPresenter;

    int index;

    ContactEntity contactEntity;
    Context mContext;
    HanyuPinyinOutputFormat format;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compact_edtors);
        ButterKnife.bind(this);
        mContext = this;
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editorEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    editorImg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    editorImg.setBackgroundColor(ColorUtils.getColor(getResources(), contactEntity.getName()));
                }
            }
        });

        index = getIntent().getExtras().getInt("index");
        contactPresenter = new ContactPresenter(this);
        contactPresenter.initDataByIndex(index);


        // 汉字转拼音
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }


    @Override
    public void initData(ContactEntity contactEntity) {
        this.contactEntity = contactEntity;
        editorEditName.setText(contactEntity.getName());
        editorEditName.setSelection(contactEntity.getName().length());
        editorEditPhone.setText(contactEntity.getPhone());
        editorImg.setBackgroundColor(ColorUtils.getColor(getResources(), contactEntity.getName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_editor_ok:
                this.updateContact();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /***
     * 添加联系人信息
     */
    private void updateContact() {
        newPhone = editorEditPhone.getText().toString().trim();
        newName = editorEditName.getText().toString().trim();
        if (!TextUtils.isEmpty(newPhone) &&
                !TextUtils.isEmpty(newName)) {
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setName(newName);
            contactEntity.setPhone(newPhone);
            try {
                String pinyin = PinyinHelper.toHanYuPinyinString(contactEntity.getName(), format, "", true);
                contactEntity.setIndex(pinyin.substring(0, 1).toUpperCase());
                contactEntity.setPinYin(pinyin);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }

            final int i = contactPresenter.updateContact(mContext, index, contactEntity);
            closeSoftInput(mContext);
            Intent intent = new Intent();
            intent.putExtra("index", i);
            setResult(ContactsFragment.RESPONSE_UPDATE_CODE, intent);
            finish();

        } else {
            if (!TextUtils.isEmpty(newPhone) || !TextUtils.isEmpty(newName)) {
                Toast.makeText(EditContactActivity.this, "信息补全可好", Toast.LENGTH_SHORT).show();
            } else {
                closeSoftInput(mContext);
                finish();
            }


        }
    }


    /**
     * 关闭键盘事件.
     *
     * @param context the context
     */
    public static void closeSoftInput(Context context) {
        try {
            if (null != context) {
                InputMethodManager inputMethodManager = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null
                        && ((Activity) context).getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(((Activity) context)
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
//			e.printStackTrace();
        }
    }
}
