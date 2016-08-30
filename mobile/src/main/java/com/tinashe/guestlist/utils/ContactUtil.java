package com.tinashe.guestlist.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import java.io.IOException;

/**
 * Created by tinashe on 2016/02/12.
 */
public class ContactUtil {

    public static long getContactIDFromNumber(String contactNumber, Context context) {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = 0;
        Cursor contactLookupCursor = context.getContentResolver()
                .query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber)),
                        new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        if (contactLookupCursor != null) {
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    public static long getContactIdByEmailAddress(Context context, String emailAddress) {
        long contactId = -1;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{BaseColumns._ID, ContactsContract.CommonDataKinds.Email.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Email.DATA + " LIKE '" + emailAddress + "'", null, null);

        if (contactLookup != null && contactLookup.getCount() > 0) {
            contactLookup.moveToNext();
            contactId = contactLookup.getLong(contactLookup.getColumnIndex(BaseColumns._ID));
        }
        if (contactLookup != null) contactLookup.close();

        return contactId;
    }

    public static Bitmap openDisplayPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            if (fd != null) {
                return BitmapFactory.decodeStream(fd.createInputStream());
            } else {
                return null;
            }

        } catch (IOException e) {
            return null;
        }
    }
}
