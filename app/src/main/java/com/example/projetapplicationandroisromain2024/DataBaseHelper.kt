package com.example.projetapplicationandroisromain2024

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.projetapplicationandroisromain2024.DataClasses.DataClassItems
import com.example.projetapplicationandroisromain2024.DataClasses.DataClassUsers

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "InventoryManagement.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_MAIL = "mail"

        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ITEM_ID = "id"
        private const val COLUMN_ITEM_REFERENCE= "ref"
        private const val COLUMN_ITEM_BRAND = "brand"
        private const val COLUMN_ITEM_TYPE = "name"
        private const val COLUMN_MERCHANT_LINK = "merchant_link"
        private const val COLUMN_IS_ITEM_AVAILABLE = "is_available"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createUsersTable = """CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_ROLE INTEGER NOT NULL,
                $COLUMN_MAIL TEXT NOT NULL UNIQUE
            )"""

        db?.execSQL(createUsersTable)

        val createMaterialsTable = """
            CREATE TABLE $TABLE_ITEMS (
                $COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ITEM_REFERENCE STRING NOT NULL UNIQUE,
                $COLUMN_ITEM_TYPE STRING NOT NULL,
                $COLUMN_ITEM_BRAND TEXT NOT NULL,
                $COLUMN_MERCHANT_LINK TEXT,
                $COLUMN_IS_ITEM_AVAILABLE INTEGER DEFAULT 1
                
            )
        """
        db?.execSQL(createMaterialsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }


    /////// INSERT REQUEST //////

    fun insertUser(username: String, passwordHash: String, role: Int, mail: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {

            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, passwordHash)
            put(COLUMN_ROLE, role)
            put(COLUMN_MAIL, mail)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun insertItem(ref: String, name: String, merchantLink: String?, isAvailable: Boolean, brand: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_REFERENCE, ref)
            put(COLUMN_ITEM_TYPE, name)
            put(COLUMN_MERCHANT_LINK, merchantLink)
            put(COLUMN_IS_ITEM_AVAILABLE, if (isAvailable) 1 else 0)
            put(COLUMN_ITEM_BRAND, brand)
        }
        return db.insert(TABLE_ITEMS, null, values)
    }


    //////// BOOLEAN CHECK //////

    fun isRefAlreadyAttributed(ref: String, excludeId: Int? = null): Boolean {
        val db = this.readableDatabase
        val query = if (excludeId == null) {
            "SELECT COUNT(*) FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_REFERENCE = ?"
        } else {
            "SELECT COUNT(*) FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_REFERENCE = ? AND $COLUMN_ITEM_ID != ?"
        }
        val args = if (excludeId == null) {
            arrayOf(ref)
        } else {
            arrayOf(ref, excludeId.toString())
        }

        val cursor = db.rawQuery(query, args)
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }

    fun isSuperUserCreated(): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USERS WHERE $COLUMN_ROLE = 0" // Vérification du rôle super-admin
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count > 0
    }

    fun isUserValid(email: String, passwordHash: String): Boolean {
        val db = readableDatabase
        val query = "SELECT 1 FROM $TABLE_USERS WHERE $COLUMN_MAIL = ? AND $COLUMN_PASSWORD = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(email, passwordHash))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun isEmailTaken(email: String, excludeUserId: Int = -1): Boolean {
        val db = this.readableDatabase
        val query = if (excludeUserId == -1) {
            "SELECT COUNT(*) FROM $TABLE_USERS WHERE $COLUMN_MAIL = ?"
        } else {
            "SELECT COUNT(*) FROM $TABLE_USERS WHERE $COLUMN_MAIL = ? AND $COLUMN_USER_ID != ?"
        }
        val args = if (excludeUserId == -1) arrayOf(email) else arrayOf(email, excludeUserId.toString())
        val cursor = db.rawQuery(query, args)

        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        db.close()
        return exists
    }


    //////GET REQUEST //////

    fun getUserRole(email: String): Int? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ROLE FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var role: Int? = null
        if (cursor.moveToFirst()) {
            role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
        }
        cursor.close()
        return role
    }

    fun getAllUsers(): List<DataClassUsers> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS"
        val cursor = db.rawQuery(query, null)
        val users = mutableListOf<DataClassUsers>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val role = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

            users.add(DataClassUsers(id, username, email, password, role))
        }

        cursor.close()
        return users
    }

    fun getAllItems(): List<DataClassItems> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_ITEMS"
        val cursor = db.rawQuery(query, null)
        val items = mutableListOf<DataClassItems>()

        while (cursor.moveToNext()) {
            val ref = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_REFERENCE))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TYPE))
            val link = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MERCHANT_LINK))
            val brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_BRAND))
            val isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ITEM_AVAILABLE)) == 1
            val uniqueId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID))


            items.add(DataClassItems(ref, name, link, brand, isAvailable, uniqueId))
        }

        cursor.close()
        return items
    }

    ////// UPDATE REQUEST //////

    fun updateAvailabilityItem(itemRef: String, valueAvailability: Int): Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_IS_ITEM_AVAILABLE,valueAvailability)
        }

        return db.update(TABLE_ITEMS, values, "$COLUMN_ITEM_REFERENCE = ?", arrayOf(itemRef))
    }

    fun updateItem(itemId: Int,newRef: String, newBrand: String, newLink: String, newType: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_REFERENCE,newRef)
            put(COLUMN_ITEM_TYPE, newType)
            put(COLUMN_ITEM_BRAND, newBrand)
            put(COLUMN_MERCHANT_LINK, newLink)
        }
        return db.update(TABLE_ITEMS, values, "$COLUMN_ITEM_ID = ?", arrayOf(itemId.toString()))
    }

    fun updateUser(userId: Int, newUsername: String, newEmail: String, newPasswordHash: String, newRole: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, newPasswordHash)
            put(COLUMN_USERNAME, newUsername)
            put(COLUMN_MAIL, newEmail)
            put(COLUMN_ROLE, newRole)
        }
        val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }


    ///// DELETE REQUEST ////////

    fun deleteItem(itemId: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("$TABLE_ITEMS", "$COLUMN_ITEM_ID = ?", arrayOf(itemId.toString()))
        db.close()
        return rowsDeleted > 0
    }

    fun deleteUser(id: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("$TABLE_USERS", "$COLUMN_USER_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted > 0

    }
}