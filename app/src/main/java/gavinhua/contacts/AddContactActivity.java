package gavinhua.contacts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

public class AddContactActivity extends AppCompatActivity implements IContactView {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.editor_img)
    ImageView editorImg;

    @Bind(R.id.editor_edit_name)
    EditText editorEditName;

    @Bind(R.id.editor_edit_phone)
    EditText editorEditPhone;

    String mPhoneStr, mNameStr;
    Context mContext;
    HanyuPinyinOutputFormat format;

    IContactPresenter contactPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        ButterKnife.bind(this);
        mContext = this;
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactPresenter = new ContactPresenter(this);

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
                    editorImg.setBackgroundColor(ColorUtils.getColor(getResources(), s.toString().trim()));
                }
            }
        });

        // 汉字转拼音
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_done:
                addContact();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * 添加联系人信息
     */
    private void addContact() {
        mPhoneStr = editorEditPhone.getText().toString().trim();
        mNameStr = editorEditName.getText().toString().trim();
        if (!TextUtils.isEmpty(mPhoneStr) &&
                !TextUtils.isEmpty(mNameStr)) {
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setName(mNameStr);
            contactEntity.setPhone(mPhoneStr);
            try {
                String pinyin = PinyinHelper.toHanYuPinyinString(contactEntity.getName(), format, "", true);
                contactEntity.setIndex(pinyin.substring(0, 1).toUpperCase());
                contactEntity.setPinYin(pinyin);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }

            contactPresenter.addContact(mContext,contactEntity);
            closeSoftInput(mContext);
            finish();


        } else {
            if (!TextUtils.isEmpty(mNameStr) || !TextUtils.isEmpty(mPhoneStr)) {
                Toast.makeText(AddContactActivity.this, "信息补全可好", Toast.LENGTH_SHORT).show();
            } else {
                closeSoftInput(mContext);
                finish();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        closeSoftInput(mContext);
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.dialpad_slide_out_bottom);
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

    @Override
    public void initData(ContactEntity contactEntity) {

    }
}
