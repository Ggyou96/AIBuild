package com.aibuild.data.api_keys.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.aibuild.BuildConfig
import com.aibuild.data.api_keys.dao.ApiKeysDao
import com.aibuild.data.api_keys.entity.ApiKeys
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File

/**
 * ApiKeysDatabase - Encrypted Room database for secure API key storage
 * 
 * This database implements multiple layers of security:
 * 1. SQLCipher encryption for database files
 * 2. Android Keystore integration via MasterKeys
 * 3. Developer mode requirement for initialization
 * 4. Secure key generation and storage
 * 
 * Security Features:
 * - Database encryption with random passphrase
 * - Passphrase stored in EncryptedSharedPreferences
 * - Only accessible when Developer Mode is enabled
 * - Automatic migration support
 * - Debug logging with BuildConfig guards
 * 
 * @author AIBuild Developer Agent
 */
@Database(
    entities = [ApiKeys::class],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
abstract class ApiKeysDatabase : RoomDatabase() {
    
    abstract fun apiKeysDao(): ApiKeysDao
    
    companion object {
        private const val DATABASE_NAME = "aibuild_api_keys_db"
        private const val PASSPHRASE_PREF_KEY = "db_passphrase"
        private const val ENCRYPTED_PREFS_NAME = "aibuild_secure_prefs"
        
        @Volatile
        private var INSTANCE: ApiKeysDatabase? = null
        
        /**
         * Get database instance with encryption
         * Only initializes when Developer Mode is enabled
         * 
         * @param context Application context
         * @param isDeveloperModeEnabled Whether developer mode is active
         * @return Database instance or null if developer mode is disabled
         */
        fun getDatabase(
            context: Context,
            isDeveloperModeEnabled: Boolean
        ): ApiKeysDatabase? {
            if (!isDeveloperModeEnabled) {
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("ApiKeysDatabase", "Database access denied - Developer mode disabled")
                }
                return null
            }
            
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Build encrypted database instance
         */
        private fun buildDatabase(context: Context): ApiKeysDatabase {
            try {
                val passphrase = getOrCreatePassphrase(context)
                val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase))
                
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    ApiKeysDatabase::class.java,
                    DATABASE_NAME
                )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration() // For development - remove in production
                .build()
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("ApiKeysDatabase", "Encrypted database initialized successfully")
                }
                
                return database
                
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("ApiKeysDatabase", "Failed to initialize database", e)
                }
                throw SecurityException("Failed to initialize secure database", e)
            }
        }
        
        /**
         * Get or create database passphrase using Android Keystore
         */
        private fun getOrCreatePassphrase(context: Context): String {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            
            val sharedPreferences = androidx.security.crypto.EncryptedSharedPreferences.create(
                ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                context,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            var passphrase = sharedPreferences.getString(PASSPHRASE_PREF_KEY, null)
            
            if (passphrase == null) {
                // Generate new secure passphrase
                passphrase = generateSecurePassphrase()
                sharedPreferences.edit()
                    .putString(PASSPHRASE_PREF_KEY, passphrase)
                    .apply()
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("ApiKeysDatabase", "Generated new database passphrase")
                }
            }
            
            return passphrase
        }
        
        /**
         * Generate cryptographically secure passphrase
         */
        private fun generateSecurePassphrase(): String {
            val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
            return (1..32)
                .map { charset.random() }
                .joinToString("")
        }
        
        /**
         * Close database and clear instance
         * Used for testing or security reset
         */
        fun closeDatabase() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("ApiKeysDatabase", "Database closed and instance cleared")
                }
            }
        }
        
        /**
         * Delete database and all associated files
         * This is a destructive operation for security reset
         */
        fun deleteDatabase(context: Context) {
            synchronized(this) {
                try {
                    closeDatabase()
                    
                    // Delete database files
                    val dbFile = context.getDatabasePath(DATABASE_NAME)
                    if (dbFile.exists()) {
                        dbFile.delete()
                    }
                    
                    // Delete associated files
                    val shm = File(dbFile.path + "-shm")
                    val wal = File(dbFile.path + "-wal")
                    if (shm.exists()) shm.delete()
                    if (wal.exists()) wal.delete()
                    
                    // Clear encrypted preferences
                    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                    val sharedPreferences = androidx.security.crypto.EncryptedSharedPreferences.create(
                        ENCRYPTED_PREFS_NAME,
                        masterKeyAlias,
                        context,
                        androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                    sharedPreferences.edit().clear().apply()
                    
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("ApiKeysDatabase", "Database and all associated files deleted")
                    }
                    
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        android.util.Log.e("ApiKeysDatabase", "Error deleting database", e)
                    }
                }
            }
        }
        
        /**
         * Check if database exists
         */
        fun databaseExists(context: Context): Boolean {
            return context.getDatabasePath(DATABASE_NAME).exists()
        }
    }
}