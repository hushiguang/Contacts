package gavinhua.contacts.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gavinhua.contacts.model.ContactEntity;

/**
 * Created by HuShiGuang on 2016/3/30.
 */
public class ContactsManager {

    //当前的TAG标签
    public static final String TAG = ContactsManager.class.getSimpleName();
    //raw_contacts 数据库
    public static final String RAW_CONTACTS = "content://com.android.contacts/raw_contacts";
    //data 数据库
    public static final String CONTACTS_DATA = "content://com.android.contacts/data";
    //常用联系人 type
    public static final int USUALLY_TYPE = 10;
    //所有联系人 type
    public static final int NORMAL_TYPE = 11;


    /**
     * 获取联系人列表
     *
     * @param mContext
     * @param type     NORMAL_TYPE USUALLY_TYPE
     * @return 返回一个联系人列表集合
     */
    public static ArrayList<ContactEntity> getContacts(Context mContext, final int type) {

        // 汉字转拼音
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        ArrayList<ContactEntity> data = new ArrayList<>();
        Uri raw_uri = Uri.parse(RAW_CONTACTS);
        Uri data_uri = Uri.parse(CONTACTS_DATA);
        //拿到游标对象
        Cursor raw_cursor = mContext.getContentResolver().query(raw_uri, new String[]{"_id", "phonebook_label", "last_time_contacted"}, null, null, null);

        //判断是否为空
        if (raw_cursor == null) {
            //为空直接返回null
            return data;
        }

        //判断游标是否含有下一项
        while (raw_cursor.moveToNext()) {
            //拿到raw_cursor的id
            String id = raw_cursor.getString(0);
            //拿到raw_cursor的phonebook_label
            String index = raw_cursor.getString(1);
            String lastTime = raw_cursor.getString(2);
            //根据 raw_cursor的id 去拿到data表的游标对象
            Cursor data_cursor = mContext.getContentResolver().query(data_uri, new String[]{"mimetype", "data1"}, "raw_contact_id = ?", new String[]{id}, null);
            //实例化一个联系人集合
            ContactEntity contactEntity = new ContactEntity();
            //判断当前的data的游标对象是否为空
            if (data_cursor == null) {
                continue;
            }

            //判断phonebook_label是否为空，联系人的索引
            if (!TextUtils.isEmpty(index))
                contactEntity.setIndex(index);

            if (type == USUALLY_TYPE) {
                if (!TextUtils.isEmpty(lastTime))
                    contactEntity.setLastTime(lastTime);
            }

            //判断data是否含有下一项
            while (data_cursor.moveToNext()) {
                //拿到mimetype_id
                String dataType = data_cursor.getString(0);

                //判断当前的 类型是名称
                if (dataType.equals("vnd.android.cursor.item/name")) {
                    //添加到实体里面
                    if (!TextUtils.isEmpty(data_cursor.getString(1))) {
                        contactEntity.setName(data_cursor.getString(1));
                        try {
                            contactEntity.setPinYin(PinyinHelper.toHanYuPinyinString(contactEntity.getName(), format, "", true));
                        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                            badHanyuPinyinOutputFormatCombination.printStackTrace();
                        }
                    }
                }

                //判断当前的类型是 电话号码
                if (dataType.equals("vnd.android.cursor.item/phone_v2")) {
                    //添加到实体里面
                    if (!TextUtils.isEmpty(data_cursor.getString(1)) && !"null".equals(data_cursor.getString(1)))
                        contactEntity.setPhone(data_cursor.getString(1));
                }

            }

            switch (type) {
                case NORMAL_TYPE:
                    //判断实体三项字段都不为空的情况下 添加到集合里面
                    if (!TextUtils.isEmpty(contactEntity.getName()) &&
                            !TextUtils.isEmpty(contactEntity.getPhone()) &&
                            !TextUtils.isEmpty(contactEntity.getIndex()))
                        data.add(contactEntity);
                    break;

                case USUALLY_TYPE:
                    if (!TextUtils.isEmpty(contactEntity.getName()) &&
                            !TextUtils.isEmpty(contactEntity.getPhone()) &&
                            !TextUtils.isEmpty(contactEntity.getIndex()) &&
                            !TextUtils.isEmpty(contactEntity.getLastTime()))
                        data.add(contactEntity);
                    break;
            }


            //TODO 切记关闭不用的游标
            data_cursor.close();
        }

        //TODO 关闭游标
        raw_cursor.close();


        Collections.sort(data, new Comparator<ContactEntity>() {
            @Override
            public int compare(ContactEntity lhs, ContactEntity rhs) {
                if (type == USUALLY_TYPE)
                    return rhs.getLastTime().compareTo(lhs.getLastTime());
                else
                    return lhs.getIndex().compareTo(rhs.getIndex());
            }
        });


        Log.d(TAG, data.toString());
        return data;
    }


    /***
     * 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获得系统返回的rawContactId
     * 这时后面插入data表的数据，只有执行空值插入，才能使插入的联系人在通讯录里面可见
     *
     * @param mContext
     * @param contactEntity
     * @return
     */
    public static boolean insertContacts(Context mContext, ContactEntity contactEntity) {
        //创建一个 存储数值的 HashMap
        ContentValues values = new ContentValues();
        boolean nameInsert = false;

        /*
         * 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获得系统返回的rawContactId
         */
        Uri rawContactUri = mContext.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        //TODO 往data表里写入姓名数据
        values.clear();
        //data表里面 存放raw_conatct_id
        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        //存放 minetypes 类型 是 <name>
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE); //内容类型
        //往data表中的 data1 字段中存放 联系人的名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.DATA1, contactEntity.getName());
        //存储成功 返回一个在data表中的_id 的uri
        Uri nameUri = mContext.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        if (nameUri != null) {
            nameInsert = true;
        }

        //TODO　往data表里写入电话数据 同姓名
        values.clear();
        //写入raw_contact_id
        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        //minetypes 电话类型 phone_v2
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        //往data表中的 data1 字段中存放 phone 的名字
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactEntity.getPhone());
        //电话的类型 有多种  work home 这里是 mobile
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        //存储成功 返回一个在data表中的_id 的uri
        Uri phoneUri = mContext.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        return nameInsert && phoneUri != null;

    }


    /**
     * 更新联系人
     *
     * @param context
     * @param oldName      根据旧的名字去查询是哪条数据
     * @param contactEntity 新的contact实体
     */
    public static boolean updateContact(Context context, String oldName, ContactEntity contactEntity) {

        //通过查询data表 根据 raw_contact_id 去查询 data1 为 oldName的 游标对象
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]
                        {ContactsContract.Contacts.Data.RAW_CONTACT_ID},
                ContactsContract.Contacts.Data.DATA1 + "=?", new String[]{oldName}, null);

        //将游标移到第一个
        cursor.moveToFirst();
        //拿到 raw_contact_id
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Data.RAW_CONTACT_ID));
        //关闭游标对象
        cursor.close();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


        //更新电话号码
        //添加到ops里面
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                //根据raw_contact_id 和 minetype 和 存储的电话类型去查询
                .withSelection(
                        ContactsContract.Contacts.Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + " = ?" +
                                " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
                        new String[]{String.valueOf(id),
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)})
                //修改原来的phone为新的电话号码
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactEntity.getPhone()).build());


        // 更新姓名 同修改电话
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + " = ?",
                        new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DATA1, contactEntity.getName()).build());


        try {
            //执行修改 contact 表
            ContentProviderResult[] contentProviderResults = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (contentProviderResults[0] != null) {
                return true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return false;
    }


    /**
     * 删除联系人
     * <p/>
     * 根据名字拿到raw_contact_id 然后去删除该条目
     *
     * @param context
     * @param name
     */
    public static boolean delContact(Context context, String name) {

        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                ContactsContract.Contacts.Data.DATA1 + "=?", new String[]{name}, null);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        cursor.moveToFirst();
        long Id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
        cursor.close();
        ops.add(ContentProviderOperation.newDelete(
                ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, Id)).build());
        try {
            ContentProviderResult[] contentProviderResults = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (contentProviderResults[0] != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }
}


