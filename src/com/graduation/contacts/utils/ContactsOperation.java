
package com.graduation.contacts.utils;

import java.util.ArrayList;
import java.util.List;

import com.graduation.contacts.bean.ContactNative;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class ContactsOperation {

    private static ContactsOperation mOperation;

    private static ContentResolver mResolver;

    private ContactsOperation() {

    }

    public static ContactsOperation getInstance(ContentResolver mContentResolver) {
        if (mOperation == null) {
            mOperation = new ContactsOperation();
        }
        mResolver = mContentResolver;
        return mOperation;
    }

    public List<ContactNative> getAllContacts() {
        List<ContactNative> contactsList = new ArrayList<ContactNative>();
        String[] projection = {
                Contacts._ID, Contacts.PHOTO_ID
        };
        Cursor cursor = mResolver.query(Contacts.CONTENT_URI, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ContactNative contact = null;
                int contactsId = cursor.getInt(cursor.getColumnIndex(Contacts._ID));
                String photoId = cursor.getString(cursor.getColumnIndex(Contacts.PHOTO_ID));
                Uri uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId
                        + "/data");
                String[] projection4Num = {
                        Contacts.Data.MIMETYPE, Contacts.Data.DATA1, Contacts.Data.DATA2
                };
                Cursor phone = mResolver.query(uri, projection4Num, null, null, null);
                if (phone != null && phone.getCount() > 0) {
                    phone.moveToFirst();
                    do {
                        String type = phone.getString(phone.getColumnIndex(Contacts.Data.MIMETYPE));
                        String data = phone.getString(phone.getColumnIndex(Contacts.Data.DATA1));
                        if(data==null) {
                        	continue;
                        }
                        	
                        if (CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(type)) {
                        	if(contact==null){
                        		contact=new ContactNative();
                        		contact.setId(contactsId);
                                contact.setPhotoId(photoId);
                        	}
                            int phonetype = phone.getInt(phone.getColumnIndex(Contacts.Data.DATA2));
                            if (phonetype == CommonDataKinds.Phone.TYPE_MOBILE) {
                                contact.addMobileNumbers(data);
                            } else if (phonetype == CommonDataKinds.Phone.TYPE_HOME) {
                                contact.addHomeNumbers(data);
                            } else if (phonetype == CommonDataKinds.Phone.TYPE_WORK) {
                                contact.addWorkNumbers(data);
                            } else {
                                contact.addOtherNumbers(data);
                            }
                        } else if (CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(type)) {
                        	if(contact==null){
                        		continue;
                        	}
                            contact.setContactEmail(data);
                        } else if (CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                        	if(contact==null){
                        		contact=new ContactNative();
                        		contact.setId(contactsId);
                                contact.setPhotoId(photoId);
                        	}
                            contact.setContactName(data);
                        }
                    } while (phone.moveToNext());
                    phone.close();
                    if(contact!=null){
                    	contactsList.add(contact);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return contactsList;
    }

    public ContactNative getContactById(int contactsId) {
        ContactNative contact = new ContactNative();
        
        Cursor photoC = mResolver.query(Contacts.CONTENT_URI, new String[]{Contacts.PHOTO_ID}, 
        		Contacts._ID+"="+contactsId, null, null);
        if(photoC!=null&&photoC.getCount()>0){
        	photoC.moveToFirst();
        	contact.setPhotoId(photoC.getString(photoC.getColumnIndex(Contacts.PHOTO_ID)));
        }
        if(photoC!=null){
        	photoC.close();
        }
        Uri uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
        String[] projection4Num = {
                Contacts.Data.MIMETYPE, Contacts.Data.DATA1, Contacts.Data.DATA2
        };
        Cursor phone = mResolver.query(uri, projection4Num, null, null, null);
        if (phone != null && phone.getCount() > 0) {
            phone.moveToFirst();
            do {
                String type = phone.getString(phone.getColumnIndex(Contacts.Data.MIMETYPE));
                String data = phone.getString(phone.getColumnIndex(Contacts.Data.DATA1));
                Log.i("fwl", "data=="+data);
                contact.setId(contactsId);
                if (CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(type)) {
                    int phonetype = phone.getInt(phone.getColumnIndex(Contacts.Data.DATA2));
                    if (phonetype == CommonDataKinds.Phone.TYPE_MOBILE) {
                        contact.addMobileNumbers(data);
                    } else if (phonetype == CommonDataKinds.Phone.TYPE_HOME) {
                        contact.addHomeNumbers(data);
                    } else if (phonetype == CommonDataKinds.Phone.TYPE_WORK) {
                        contact.addWorkNumbers(data);
                    } else {
                        contact.addOtherNumbers(data);
                    }
                } else if (CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(type)) {
                    contact.setContactEmail(data);
                } else if (CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                    contact.setContactName(data);
                }
            } while (phone.moveToNext());
            phone.close();
        }

        return contact;
    }

    public ContactNative getContactByNumber(String number) {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+number);
        Cursor c = mResolver.query(uri, new String[]{Contacts._ID}, null, null, null);
        if(c==null||c.getCount()==0) return null;
        c.moveToFirst();
        int contactsId = c.getInt(c.getColumnIndex(Contacts._ID));
        c.close();
        return getContactById(contactsId);
    }
    
    public int deleteContacts(int id){
//        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//        int result = mResolver.delete(uri, ContactsContract.RawContacts._ID+"="+id, null);
//        return result;
    	
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();  
    	  
        //delete contact  
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)  
                .withSelection(ContactsContract.RawContacts.CONTACT_ID+"="+id, null)  
                .build());  
        //delete contact information such as phone number,email  
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)  
                .withSelection(Contacts._ID + "=" + id, null)  
                .build());  
          
        try {  
            ContentProviderResult result[] = mResolver.applyBatch(ContactsContract.AUTHORITY, ops); 
            if(result!=null){
            	return result.length;
            }
            Log.d("fwl", "delete contact success");  
        } catch (Exception e) {  
            Log.d("fwl", "delete contact failed");  
            Log.e("fwl", e.getMessage());  
        }  
        Log.w("fwl", "**delete end**");  
    	return 0;
    }
    
    public int deleteNumber(String number){
        Uri uri = Uri.parse("content://com.androd.contacts/raw_contacts");
        int result=mResolver.delete(uri, ContactsContract.CommonDataKinds.Phone.NUMBER+"=?", new String[]{number});
        return result;
    }
    
    public int addContacts(ContactNative contact){
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            operations.add(ContentProviderOperation.newInsert(uri)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, 
                            ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED).build());
            uri = ContactsContract.Data.CONTENT_URI;
            if(contact.getContactName()!=null){
                operations.add(ContentProviderOperation.newInsert(uri)
                		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, 
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, 
                               contact.getContactName()).build());
        }
            List<String> mobileNumbers = contact.getMobileNumbers();
            if(mobileNumbers!=null&&mobileNumbers.size()>0){
                for(String number:mobileNumbers){
                operations.add(ContentProviderOperation.newInsert(uri)
                		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
            }
            }
            List<String> homeNumbers = contact.getHomeNumbers();
            if(homeNumbers!=null&&homeNumbers.size()>0){
                for(String number:homeNumbers){
                operations.add(ContentProviderOperation.newInsert(uri)
                		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME).build());
            }
            }
            
            List<String> workNumbers = contact.getWorkNumbers();
            if(workNumbers!=null&&workNumbers.size()>0){
                for(String number:workNumbers){
            operations.add(ContentProviderOperation.newInsert(uri)
            		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());
            }
            }
            
            List<String> otherNumbers = contact.getOtherNumbers();
            if(otherNumbers!=null&&otherNumbers.size()>0){
                for(String number:otherNumbers){
            operations.add(ContentProviderOperation.newInsert(uri)
            		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM).build());
            }
            }
            
            String email = contact.getContactEmail();
            if(email!=null&&email.trim().length()>0){
                operations.add(ContentProviderOperation.newInsert(uri)
                		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                        .build());
            }
            byte[] photo = contact.getPhotoBytes();
            if(photo!=null&&photo.length>0){
                operations.add(ContentProviderOperation.newInsert(uri)
                		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
                        .build());
            }
            
            try {
                ContentProviderResult[] result =  mResolver.applyBatch(ContactsContract.AUTHORITY, operations);
                if(result!=null) 
                return result.length;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return 0;
    }
    
    public int updateContacts(ContactNative contact){
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        operations.add(ContentProviderOperation.newInsert(uri)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, 
                        ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED).build());
        
        if(contact.getContactName()!=null){
            operations.add(ContentProviderOperation.newUpdate(uri)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID,0)
                    .withValue(ContactsContract.Data.MIMETYPE, 
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, 
                           contact.getContactName()).build());
    }
        List<String> mobileNumbers = contact.getMobileNumbers();
        if(mobileNumbers!=null&&mobileNumbers.size()>0){
            for(String number:mobileNumbers){
            operations.add(ContentProviderOperation.newInsert(uri)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        }
        }
        List<String> homeNumbers = contact.getHomeNumbers();
        if(homeNumbers!=null&&homeNumbers.size()>0){
            for(String number:homeNumbers){
            operations.add(ContentProviderOperation.newInsert(uri)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME).build());
        }
        }
        
        List<String> workNumbers = contact.getWorkNumbers();
        if(workNumbers!=null&&workNumbers.size()>0){
            for(String number:workNumbers){
        operations.add(ContentProviderOperation.newInsert(uri)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());
        }
        }
        
        List<String> otherNumbers = contact.getOtherNumbers();
        if(otherNumbers!=null&&otherNumbers.size()>0){
            for(String number:otherNumbers){
        operations.add(ContentProviderOperation.newInsert(uri)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN).build());
        }
        }
        
        String email = contact.getContactEmail();
        if(email!=null&&email.trim().length()>0){
            operations.add(ContentProviderOperation.newUpdate(uri)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                    .build());
        }
        byte[] photo = contact.getPhotoBytes();
        if(photo!=null&&photo.length>0){
            operations.add(ContentProviderOperation.newUpdate(uri)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
                    .build());
        }
        
        try {
            ContentProviderResult[] results = mResolver.applyBatch(ContactsContract.AUTHORITY, operations);
            if(results!=null) return results.length;
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    
    public Bitmap getPhotoBitmap(String photoId,int size){
        if(photoId==null||"".equals(photoId.trim()))
            return null;
        String[] projection = new String[]{Contacts.Data.DATA15};
        String selection = Contacts.Data._ID+"="+photoId;
        Cursor c = mResolver.query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
        if(c==null||c.getCount()==0) return null;
        c.moveToFirst();
        byte[] photobyte = c.getBlob(c.getColumnIndex(Contacts.Data.DATA15));
        if(photobyte!=null&&photobyte.length>0){
            Options opts = new BitmapFactory.Options();
            opts.outHeight = opts.outWidth = size;
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
            Bitmap mBitmap = BitmapFactory.decodeByteArray(photobyte, 0, photobyte.length,opts);
            return mBitmap;
        }
        return null;
    }
}
