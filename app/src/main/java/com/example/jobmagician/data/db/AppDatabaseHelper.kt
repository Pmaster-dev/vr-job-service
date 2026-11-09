package com.example.jobmagician.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InterviewSessionRecord(
    val id: String,
    val timestamp: Long,
    val formattedDate: String,
    val targetRole: String,
    val questionsAnswered: Int,
    val overallScore: Int,
    val clarityScore: Int,
    val pacingScore: Int,
    val advocacyScore: Int,
    val notes: String,
    val cryptoHash: String
)

data class SavedAccommodationRecord(
    val id: String,
    val title: String,
    val category: String,
    val targetCompany: String,
    val targetRole: String,
    val timingStage: String,
    val fullLetterText: String,
    val timestamp: Long,
    val formattedDate: String,
    val deafAuthSigned: Boolean = true,
    val verificationDigest: String
)

data class MunicipalCityFormRecord(
    val id: String,
    val cityName: String,
    val agencyName: String,
    val formUrl: String,
    val platformType: String, // "Wufoo Form Engine", "CivicPlus Portal", "Laserfiche", "Formstack", "Web Portal"
    val schemaStatus: String, // "Consistent & Normalized", "Syncing...", "Schema Verified"
    val lastPolledTimestamp: Long,
    val lastPolledDate: String,
    val consistencyHash: String,
    val canonicalFieldMapJson: String,
    val prefillUrlTemplate: String,
    val isBackgroundSyncEnabled: Boolean = true,
    val filingCount: Int = 0,
    val notes: String = ""
)

data class NormalizedMunicipalFilingRecord(
    val id: String,
    val formId: String,
    val cityName: String,
    val agencyName: String,
    val candidateDeafAuthId: String,
    val timestamp: Long,
    val formattedDate: String,
    val canonicalPayloadJson: String,
    val status: String, // "Harmonized", "Dispatched to Portal", "Verified"
    val deafAuthDigest: String,
    val syncedWithSuiteDoc: Boolean = true,
    val syncedWithSuiteEmail: Boolean = false
)

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vr4deaf_jobmagician.db"
        private const val DATABASE_VERSION = 2

        // Table: interview_sessions
        private const val TABLE_SESSIONS = "interview_sessions"
        private const val COL_S_ID = "id"
        private const val COL_S_TIMESTAMP = "timestamp"
        private const val COL_S_DATE = "formatted_date"
        private const val COL_S_ROLE = "target_role"
        private const val COL_S_QUESTIONS = "questions_answered"
        private const val COL_S_OVERALL = "overall_score"
        private const val COL_S_CLARITY = "clarity_score"
        private const val COL_S_PACING = "pacing_score"
        private const val COL_S_ADVOCACY = "advocacy_score"
        private const val COL_S_NOTES = "notes"
        private const val COL_S_HASH = "crypto_hash"

        // Table: saved_accommodations
        private const val TABLE_ACCOMMODATIONS = "saved_accommodations"
        private const val COL_A_ID = "id"
        private const val COL_A_TITLE = "title"
        private const val COL_A_CATEGORY = "category"
        private const val COL_A_COMPANY = "target_company"
        private const val COL_A_ROLE = "target_role"
        private const val COL_A_TIMING = "timing_stage"
        private const val COL_A_LETTER = "full_letter_text"
        private const val COL_A_TIMESTAMP = "timestamp"
        private const val COL_A_DATE = "formatted_date"
        private const val COL_A_SIGNED = "deafauth_signed"
        private const val COL_A_DIGEST = "verification_digest"

        // Table: municipal_city_forms (Background consistency collector)
        private const val TABLE_MUNICIPAL_FORMS = "municipal_city_forms"
        private const val COL_M_ID = "id"
        private const val COL_M_CITY = "city_name"
        private const val COL_M_AGENCY = "agency_name"
        private const val COL_M_URL = "form_url"
        private const val COL_M_PLATFORM = "platform_type"
        private const val COL_M_STATUS = "schema_status"
        private const val COL_M_TIMESTAMP = "last_polled_timestamp"
        private const val COL_M_DATE = "last_polled_date"
        private const val COL_M_HASH = "consistency_hash"
        private const val COL_M_MAP = "canonical_field_map_json"
        private const val COL_M_TEMPLATE = "prefill_url_template"
        private const val COL_M_SYNC = "is_background_sync_enabled"
        private const val COL_M_COUNT = "filing_count"
        private const val COL_M_NOTES = "notes"

        // Table: normalized_municipal_filings
        private const val TABLE_MUNICIPAL_FILINGS = "normalized_municipal_filings"
        private const val COL_F_ID = "id"
        private const val COL_F_FORM_ID = "form_id"
        private const val COL_F_CITY = "city_name"
        private const val COL_F_AGENCY = "agency_name"
        private const val COL_F_CANDIDATE = "candidate_deafauth_id"
        private const val COL_F_TIMESTAMP = "timestamp"
        private const val COL_F_DATE = "formatted_date"
        private const val COL_F_PAYLOAD = "canonical_payload_json"
        private const val COL_F_STATUS = "status"
        private const val COL_F_DIGEST = "deafauth_digest"
        private const val COL_F_DOC = "synced_with_suite_doc"
        private const val COL_F_EMAIL = "synced_with_suite_email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSessionsQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_SESSIONS (
                $COL_S_ID TEXT PRIMARY KEY,
                $COL_S_TIMESTAMP INTEGER NOT NULL,
                $COL_S_DATE TEXT NOT NULL,
                $COL_S_ROLE TEXT NOT NULL,
                $COL_S_QUESTIONS INTEGER NOT NULL,
                $COL_S_OVERALL INTEGER NOT NULL,
                $COL_S_CLARITY INTEGER NOT NULL,
                $COL_S_PACING INTEGER NOT NULL,
                $COL_S_ADVOCACY INTEGER NOT NULL,
                $COL_S_NOTES TEXT,
                $COL_S_HASH TEXT NOT NULL
            )
        """.trimIndent()

        val createAccommodationsQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACCOMMODATIONS (
                $COL_A_ID TEXT PRIMARY KEY,
                $COL_A_TITLE TEXT NOT NULL,
                $COL_A_CATEGORY TEXT NOT NULL,
                $COL_A_COMPANY TEXT NOT NULL,
                $COL_A_ROLE TEXT NOT NULL,
                $COL_A_TIMING TEXT NOT NULL,
                $COL_A_LETTER TEXT NOT NULL,
                $COL_A_TIMESTAMP INTEGER NOT NULL,
                $COL_A_DATE TEXT NOT NULL,
                $COL_A_SIGNED INTEGER NOT NULL DEFAULT 1,
                $COL_A_DIGEST TEXT NOT NULL
            )
        """.trimIndent()

        val createMunicipalFormsQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_MUNICIPAL_FORMS (
                $COL_M_ID TEXT PRIMARY KEY,
                $COL_M_CITY TEXT NOT NULL,
                $COL_M_AGENCY TEXT NOT NULL,
                $COL_M_URL TEXT NOT NULL,
                $COL_M_PLATFORM TEXT NOT NULL,
                $COL_M_STATUS TEXT NOT NULL,
                $COL_M_TIMESTAMP INTEGER NOT NULL,
                $COL_M_DATE TEXT NOT NULL,
                $COL_M_HASH TEXT NOT NULL,
                $COL_M_MAP TEXT NOT NULL,
                $COL_M_TEMPLATE TEXT NOT NULL,
                $COL_M_SYNC INTEGER NOT NULL DEFAULT 1,
                $COL_M_COUNT INTEGER NOT NULL DEFAULT 0,
                $COL_M_NOTES TEXT
            )
        """.trimIndent()

        val createFilingsQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_MUNICIPAL_FILINGS (
                $COL_F_ID TEXT PRIMARY KEY,
                $COL_F_FORM_ID TEXT NOT NULL,
                $COL_F_CITY TEXT NOT NULL,
                $COL_F_AGENCY TEXT NOT NULL,
                $COL_F_CANDIDATE TEXT NOT NULL,
                $COL_F_TIMESTAMP INTEGER NOT NULL,
                $COL_F_DATE TEXT NOT NULL,
                $COL_F_PAYLOAD TEXT NOT NULL,
                $COL_F_STATUS TEXT NOT NULL,
                $COL_F_DIGEST TEXT NOT NULL,
                $COL_F_DOC INTEGER NOT NULL DEFAULT 1,
                $COL_F_EMAIL INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createSessionsQuery)
        db.execSQL(createAccommodationsQuery)
        db.execSQL(createMunicipalFormsQuery)
        db.execSQL(createFilingsQuery)

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val createMunicipalFormsQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_MUNICIPAL_FORMS (
                $COL_M_ID TEXT PRIMARY KEY,
                $COL_M_CITY TEXT NOT NULL,
                $COL_M_AGENCY TEXT NOT NULL,
                $COL_M_URL TEXT NOT NULL,
                $COL_M_PLATFORM TEXT NOT NULL,
                $COL_M_STATUS TEXT NOT NULL,
                $COL_M_TIMESTAMP INTEGER NOT NULL,
                $COL_M_DATE TEXT NOT NULL,
                $COL_M_HASH TEXT NOT NULL,
                $COL_M_MAP TEXT NOT NULL,
                $COL_M_TEMPLATE TEXT NOT NULL,
                $COL_M_SYNC INTEGER NOT NULL DEFAULT 1,
                $COL_M_COUNT INTEGER NOT NULL DEFAULT 0,
                $COL_M_NOTES TEXT
            )
        """.trimIndent()

        val createFilingsQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_MUNICIPAL_FILINGS (
                $COL_F_ID TEXT PRIMARY KEY,
                $COL_F_FORM_ID TEXT NOT NULL,
                $COL_F_CITY TEXT NOT NULL,
                $COL_F_AGENCY TEXT NOT NULL,
                $COL_F_CANDIDATE TEXT NOT NULL,
                $COL_F_TIMESTAMP INTEGER NOT NULL,
                $COL_F_DATE TEXT NOT NULL,
                $COL_F_PAYLOAD TEXT NOT NULL,
                $COL_F_STATUS TEXT NOT NULL,
                $COL_F_DIGEST TEXT NOT NULL,
                $COL_F_DOC INTEGER NOT NULL DEFAULT 1,
                $COL_F_EMAIL INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createMunicipalFormsQuery)
        db.execSQL(createFilingsQuery)
        seedMunicipalForms(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // Seed initial sample session and accommodation record
        val initialSession = ContentValues().apply {
            put(COL_S_ID, "VRS-SES-2026-001")
            put(COL_S_TIMESTAMP, System.currentTimeMillis() - 86400000L * 2)
            put(COL_S_DATE, "Nov 1, 2026 • 2:15 PM")
            put(COL_S_ROLE, "Senior Accessibility Frontend Engineer")
            put(COL_S_QUESTIONS, 5)
            put(COL_S_OVERALL, 94)
            put(COL_S_CLARITY, 96)
            put(COL_S_PACING, 92)
            put(COL_S_ADVOCACY, 95)
            put(COL_S_NOTES, "Demonstrated crisp ASL storytelling and concise technical glossing for asynchronous code reviews.")
            put(COL_S_HASH, "sha256:4a8e99b0cdeafauth")
        }
        db.insertWithOnConflict(TABLE_SESSIONS, null, initialSession, SQLiteDatabase.CONFLICT_IGNORE)

        val initialAccommodation = ContentValues().apply {
            put(COL_A_ID, "ADA-REQ-2026-001")
            put(COL_A_TITLE, "Certified Deaf Interpreter & CART Provision")
            put(COL_A_CATEGORY, "Interview & Daily Standup Accommodation")
            put(COL_A_COMPANY, "Google AI Studio Team")
            put(COL_A_ROLE, "Android Staff Engineer")
            put(COL_A_TIMING, "Upon Interview Invitation")
            put(
                COL_A_LETTER,
                """
                Dear Hiring Committee & HR Disability Inclusion Office,
                
                I am a candidate for the Android Staff Engineer position. Under Title I of the Americans with Disabilities Act (42 U.S.C. § 12112), I am formally requesting reasonable accommodations for the upcoming interview loop:
                
                1. Dual Certified ASL Interpreter Services (RID/CDI certified) with low-latency direct video feed.
                2. Live CART (Communication Access Realtime Translation) verbatim English captioning.
                3. Technical interview diagramming tools with shared visual workspace.
                
                Vocational Rehabilitation Case: TWS-VR-2026-8841 (Counselor: Sarah Jenkins, CRC).
                Thank you for your dedication to equitable workplace access.
                """.trimIndent()
            )
            put(COL_A_TIMESTAMP, System.currentTimeMillis() - 86400000L)
            put(COL_A_DATE, "Nov 2, 2026 • 11:30 AM")
            put(COL_A_SIGNED, 1)
            put(COL_A_DIGEST, "sha256:91b2c4e88deafauth-verified")
        }
        db.insertWithOnConflict(TABLE_ACCOMMODATIONS, null, initialAccommodation, SQLiteDatabase.CONFLICT_IGNORE)

        seedMunicipalForms(db)
    }

    private fun seedMunicipalForms(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val forms = listOf(
            // 1. Fort Worth Human Relations Commission (FWHRC Wufoo form)
            ContentValues().apply {
                put(COL_M_ID, "MUNI-FTW-01")
                put(COL_M_CITY, "Fort Worth, TX")
                put(COL_M_AGENCY, "Fort Worth Human Relations Commission (FWHRC)")
                put(COL_M_URL, "https://fwhrc.wufoo.com/forms/mloqbwn15meruo/")
                put(COL_M_PLATFORM, "Wufoo Form Engine")
                put(COL_M_STATUS, "Consistent & Normalized")
                put(COL_M_TIMESTAMP, now)
                put(COL_M_DATE, "Just now (Background Polled)")
                put(COL_M_HASH, "sha256:fwhrc-wufoo-mloqbwn15meruo-v2.6")
                put(
                    COL_M_MAP,
                    """{"name":"Field1","email":"Field3","phone":"Field4","organization":"Field112","accommodation":"Field205","narrative":"Field206"}"""
                )
                put(
                    COL_M_TEMPLATE,
                    "https://fwhrc.wufoo.com/forms/mloqbwn15meruo/def/Field1={name}&Field3={email}&Field4={phone}&Field112={org}&Field206={narrative}"
                )
                put(COL_M_SYNC, 1)
                put(COL_M_COUNT, 3)
                put(COL_M_NOTES, "Unstandardized Wufoo intake form mapped to canonical schema. Supports DeafAuth VRS relay tags.")
            },
            // 2. City of Austin
            ContentValues().apply {
                put(COL_M_ID, "MUNI-ATX-02")
                put(COL_M_CITY, "Austin, TX")
                put(COL_M_AGENCY, "Austin Equal Employment / Human Rights Commission")
                put(COL_M_URL, "https://austintexas.gov/department/equal-employment-and-fair-housing")
                put(COL_M_PLATFORM, "CivicPlus Portal")
                put(COL_M_STATUS, "Consistent & Normalized")
                put(COL_M_TIMESTAMP, now - 1800000L)
                put(COL_M_DATE, "30 mins ago")
                put(COL_M_HASH, "sha256:atx-civicplus-eeo-7819")
                put(
                    COL_M_MAP,
                    """{"name":"applicant_name","email":"contact_email","phone":"vrs_phone","organization":"respondent_business","narrative":"complaint_details"}"""
                )
                put(
                    COL_M_TEMPLATE,
                    "https://austintexas.gov/department/equal-employment-and-fair-housing?name={name}&email={email}"
                )
                put(COL_M_SYNC, 1)
                put(COL_M_COUNT, 1)
                put(COL_M_NOTES, "Municipal web portal for Austin/Travis County ADA Title I compliance.")
            },
            // 3. City of Dallas
            ContentValues().apply {
                put(COL_M_ID, "MUNI-DAL-03")
                put(COL_M_CITY, "Dallas, TX")
                put(COL_M_AGENCY, "Dallas Office of Equity & Human Rights (Fair Housing)")
                put(COL_M_URL, "https://dallascityhall.com/departments/fairhousing/Pages/default.aspx")
                put(COL_M_PLATFORM, "Laserfiche Webform")
                put(COL_M_STATUS, "Consistent & Normalized")
                put(COL_M_TIMESTAMP, now - 3600000L)
                put(COL_M_DATE, "1 hour ago")
                put(COL_M_HASH, "sha256:dal-oei-laserfiche-9921")
                put(
                    COL_M_MAP,
                    """{"name":"txtFullName","email":"txtEmail","phone":"txtPhone","organization":"txtEmployer","narrative":"txtAllegation"}"""
                )
                put(
                    COL_M_TEMPLATE,
                    "https://dallascityhall.com/departments/fairhousing/Pages/default.aspx?fill_name={name}"
                )
                put(COL_M_SYNC, 1)
                put(COL_M_COUNT, 2)
                put(COL_M_NOTES, "Laserfiche digital document pipeline mapped to canonical VR4DEAF payload.")
            },
            // 4. City of Houston
            ContentValues().apply {
                put(COL_M_ID, "MUNI-HOU-04")
                put(COL_M_CITY, "Houston, TX")
                put(COL_M_AGENCY, "Mayor's Office for People with Disabilities (MCOPD)")
                put(COL_M_URL, "https://houstontx.gov/disabilities/intake")
                put(COL_M_PLATFORM, "Formstack Engine")
                put(COL_M_STATUS, "Consistent & Normalized")
                put(COL_M_TIMESTAMP, now - 5400000L)
                put(COL_M_DATE, "1.5 hours ago")
                put(COL_M_HASH, "sha256:hou-mcopd-formstack-4412")
                put(
                    COL_M_MAP,
                    """{"name":"field101","email":"field102","phone":"field103","organization":"field104","narrative":"field105"}"""
                )
                put(
                    COL_M_TEMPLATE,
                    "https://houstontx.gov/disabilities/intake?name={name}&email={email}"
                )
                put(COL_M_SYNC, 1)
                put(COL_M_COUNT, 0)
                put(COL_M_NOTES, "Direct intake routing to MCOPD disability rights specialists.")
            },
            // 5. City of San Antonio
            ContentValues().apply {
                put(COL_M_ID, "MUNI-SAT-05")
                put(COL_M_CITY, "San Antonio, TX")
                put(COL_M_AGENCY, "Disability Access Office & Human Relations")
                put(COL_M_URL, "https://sanantonio.gov/humanservices/disabilityaccess")
                put(COL_M_PLATFORM, "Municipal Web Portal")
                put(COL_M_STATUS, "Consistent & Normalized")
                put(COL_M_TIMESTAMP, now - 7200000L)
                put(COL_M_DATE, "2 hours ago")
                put(COL_M_HASH, "sha256:sat-dao-web-1102")
                put(
                    COL_M_MAP,
                    """{"name":"contactName","email":"contactEmail","phone":"vrsNumber","organization":"employerName","narrative":"accommodationSummary"}"""
                )
                put(
                    COL_M_TEMPLATE,
                    "https://sanantonio.gov/humanservices/disabilityaccess?name={name}"
                )
                put(COL_M_SYNC, 1)
                put(COL_M_COUNT, 1)
                put(COL_M_NOTES, "Bexar County & San Antonio civic disability mediation conduit.")
            }
        )

        for (form in forms) {
            db.insertWithOnConflict(TABLE_MUNICIPAL_FORMS, null, form, SQLiteDatabase.CONFLICT_REPLACE)
        }

        // Seed initial normalized filing for Fort Worth FWHRC
        val sampleFiling = ContentValues().apply {
            put(COL_F_ID, "FILING-FTW-2026-001")
            put(COL_F_FORM_ID, "MUNI-FTW-01")
            put(COL_F_CITY, "Fort Worth, TX")
            put(COL_F_AGENCY, "Fort Worth Human Relations Commission (FWHRC)")
            put(COL_F_CANDIDATE, "DA-7928-PINK (Jordan Vance)")
            put(COL_F_TIMESTAMP, now - 3600000L)
            put(COL_F_DATE, "Today, 11:15 AM")
            put(
                COL_F_PAYLOAD,
                """
                {
                  "canonical_schema": "VR4DEAF-MUNI-INTAKE-v1",
                  "city": "Fort Worth, TX",
                  "agency": "Fort Worth Human Relations Commission",
                  "original_form_url": "https://fwhrc.wufoo.com/forms/mloqbwn15meruo/",
                  "complainant": {
                    "full_name": "Jordan Vance",
                    "deaf_auth_id": "DA-7928-PINK",
                    "email": "jordan.vance@vr4deaf.org",
                    "vrs_phone": "(817) 555-0199 (ASL Relay)",
                    "vr_case_ref": "TWS-VR-2026-8841"
                  },
                  "respondent": {
                    "employer_name": "Metro Logistics & Tech Fort Worth",
                    "role_applied": "Systems Software Analyst"
                  },
                  "accommodation_requested": "Certified Deaf Interpreter (CDI) & CART services under Title I ADA / FWHRC Municipal Code",
                  "incident_details": "Employer indicated interview could not proceed due to unwillingness to schedule a qualified sign language interpreter.",
                  "consistency_verified": true
                }
                """.trimIndent()
            )
            put(COL_F_STATUS, "Harmonized & Ready to Auto-Fill")
            put(COL_F_DIGEST, "sha256:fwhrc-harmonized-7712-deafauth")
            put(COL_F_DOC, 1)
            put(COL_F_EMAIL, 1)
        }
        db.insertWithOnConflict(TABLE_MUNICIPAL_FILINGS, null, sampleFiling, SQLiteDatabase.CONFLICT_REPLACE)
    }

    suspend fun insertSession(record: InterviewSessionRecord): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_S_ID, record.id)
            put(COL_S_TIMESTAMP, record.timestamp)
            put(COL_S_DATE, record.formattedDate)
            put(COL_S_ROLE, record.targetRole)
            put(COL_S_QUESTIONS, record.questionsAnswered)
            put(COL_S_OVERALL, record.overallScore)
            put(COL_S_CLARITY, record.clarityScore)
            put(COL_S_PACING, record.pacingScore)
            put(COL_S_ADVOCACY, record.advocacyScore)
            put(COL_S_NOTES, record.notes)
            put(COL_S_HASH, record.cryptoHash)
        }
        val result = db.insertWithOnConflict(TABLE_SESSIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        result != -1L
    }

    suspend fun getAllSessions(): List<InterviewSessionRecord> = withContext(Dispatchers.IO) {
        val list = mutableListOf<InterviewSessionRecord>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_SESSIONS, null, null, null, null, null, "$COL_S_TIMESTAMP DESC")
        cursor.use {
            while (it.moveToNext()) {
                val record = InterviewSessionRecord(
                    id = it.getString(it.getColumnIndexOrThrow(COL_S_ID)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COL_S_TIMESTAMP)),
                    formattedDate = it.getString(it.getColumnIndexOrThrow(COL_S_DATE)),
                    targetRole = it.getString(it.getColumnIndexOrThrow(COL_S_ROLE)),
                    questionsAnswered = it.getInt(it.getColumnIndexOrThrow(COL_S_QUESTIONS)),
                    overallScore = it.getInt(it.getColumnIndexOrThrow(COL_S_OVERALL)),
                    clarityScore = it.getInt(it.getColumnIndexOrThrow(COL_S_CLARITY)),
                    pacingScore = it.getInt(it.getColumnIndexOrThrow(COL_S_PACING)),
                    advocacyScore = it.getInt(it.getColumnIndexOrThrow(COL_S_ADVOCACY)),
                    notes = it.getString(it.getColumnIndexOrThrow(COL_S_NOTES)) ?: "",
                    cryptoHash = it.getString(it.getColumnIndexOrThrow(COL_S_HASH))
                )
                list.add(record)
            }
        }
        list
    }

    suspend fun deleteSession(id: String): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val rows = db.delete(TABLE_SESSIONS, "$COL_S_ID = ?", arrayOf(id))
        rows > 0
    }

    suspend fun insertAccommodation(record: SavedAccommodationRecord): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_A_ID, record.id)
            put(COL_A_TITLE, record.title)
            put(COL_A_CATEGORY, record.category)
            put(COL_A_COMPANY, record.targetCompany)
            put(COL_A_ROLE, record.targetRole)
            put(COL_A_TIMING, record.timingStage)
            put(COL_A_LETTER, record.fullLetterText)
            put(COL_A_TIMESTAMP, record.timestamp)
            put(COL_A_DATE, record.formattedDate)
            put(COL_A_SIGNED, if (record.deafAuthSigned) 1 else 0)
            put(COL_A_DIGEST, record.verificationDigest)
        }
        val result = db.insertWithOnConflict(TABLE_ACCOMMODATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        result != -1L
    }

    suspend fun getAllAccommodations(): List<SavedAccommodationRecord> = withContext(Dispatchers.IO) {
        val list = mutableListOf<SavedAccommodationRecord>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_ACCOMMODATIONS, null, null, null, null, null, "$COL_A_TIMESTAMP DESC")
        cursor.use {
            while (it.moveToNext()) {
                val record = SavedAccommodationRecord(
                    id = it.getString(it.getColumnIndexOrThrow(COL_A_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_A_TITLE)),
                    category = it.getString(it.getColumnIndexOrThrow(COL_A_CATEGORY)),
                    targetCompany = it.getString(it.getColumnIndexOrThrow(COL_A_COMPANY)),
                    targetRole = it.getString(it.getColumnIndexOrThrow(COL_A_ROLE)),
                    timingStage = it.getString(it.getColumnIndexOrThrow(COL_A_TIMING)),
                    fullLetterText = it.getString(it.getColumnIndexOrThrow(COL_A_LETTER)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COL_A_TIMESTAMP)),
                    formattedDate = it.getString(it.getColumnIndexOrThrow(COL_A_DATE)),
                    deafAuthSigned = it.getInt(it.getColumnIndexOrThrow(COL_A_SIGNED)) == 1,
                    verificationDigest = it.getString(it.getColumnIndexOrThrow(COL_A_DIGEST))
                )
                list.add(record)
            }
        }
        list
    }

    suspend fun deleteAccommodation(id: String): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val rows = db.delete(TABLE_ACCOMMODATIONS, "$COL_A_ID = ?", arrayOf(id))
        rows > 0
    }

    // ----------------------------------------------------
    // Municipal Forms & Consistency Engine Queries
    // ----------------------------------------------------
    suspend fun getAllMunicipalForms(): List<MunicipalCityFormRecord> = withContext(Dispatchers.IO) {
        val list = mutableListOf<MunicipalCityFormRecord>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_MUNICIPAL_FORMS, null, null, null, null, null, "$COL_M_CITY ASC")
        cursor.use {
            while (it.moveToNext()) {
                val record = MunicipalCityFormRecord(
                    id = it.getString(it.getColumnIndexOrThrow(COL_M_ID)),
                    cityName = it.getString(it.getColumnIndexOrThrow(COL_M_CITY)),
                    agencyName = it.getString(it.getColumnIndexOrThrow(COL_M_AGENCY)),
                    formUrl = it.getString(it.getColumnIndexOrThrow(COL_M_URL)),
                    platformType = it.getString(it.getColumnIndexOrThrow(COL_M_PLATFORM)),
                    schemaStatus = it.getString(it.getColumnIndexOrThrow(COL_M_STATUS)),
                    lastPolledTimestamp = it.getLong(it.getColumnIndexOrThrow(COL_M_TIMESTAMP)),
                    lastPolledDate = it.getString(it.getColumnIndexOrThrow(COL_M_DATE)),
                    consistencyHash = it.getString(it.getColumnIndexOrThrow(COL_M_HASH)),
                    canonicalFieldMapJson = it.getString(it.getColumnIndexOrThrow(COL_M_MAP)),
                    prefillUrlTemplate = it.getString(it.getColumnIndexOrThrow(COL_M_TEMPLATE)),
                    isBackgroundSyncEnabled = it.getInt(it.getColumnIndexOrThrow(COL_M_SYNC)) == 1,
                    filingCount = it.getInt(it.getColumnIndexOrThrow(COL_M_COUNT)),
                    notes = it.getString(it.getColumnIndexOrThrow(COL_M_NOTES)) ?: ""
                )
                list.add(record)
            }
        }
        list
    }

    suspend fun insertMunicipalForm(record: MunicipalCityFormRecord): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_M_ID, record.id)
            put(COL_M_CITY, record.cityName)
            put(COL_M_AGENCY, record.agencyName)
            put(COL_M_URL, record.formUrl)
            put(COL_M_PLATFORM, record.platformType)
            put(COL_M_STATUS, record.schemaStatus)
            put(COL_M_TIMESTAMP, record.lastPolledTimestamp)
            put(COL_M_DATE, record.lastPolledDate)
            put(COL_M_HASH, record.consistencyHash)
            put(COL_M_MAP, record.canonicalFieldMapJson)
            put(COL_M_TEMPLATE, record.prefillUrlTemplate)
            put(COL_M_SYNC, if (record.isBackgroundSyncEnabled) 1 else 0)
            put(COL_M_COUNT, record.filingCount)
            put(COL_M_NOTES, record.notes)
        }
        val result = db.insertWithOnConflict(TABLE_MUNICIPAL_FORMS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        result != -1L
    }

    suspend fun updateMunicipalFormSyncStatus(formId: String, status: String, polledDate: String, hash: String): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_M_STATUS, status)
            put(COL_M_DATE, polledDate)
            put(COL_M_TIMESTAMP, System.currentTimeMillis())
            put(COL_M_HASH, hash)
        }
        val rows = db.update(TABLE_MUNICIPAL_FORMS, values, "$COL_M_ID = ?", arrayOf(formId))
        rows > 0
    }

    suspend fun deleteMunicipalForm(formId: String): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val rows = db.delete(TABLE_MUNICIPAL_FORMS, "$COL_M_ID = ?", arrayOf(formId))
        rows > 0
    }

    suspend fun getAllMunicipalFilings(): List<NormalizedMunicipalFilingRecord> = withContext(Dispatchers.IO) {
        val list = mutableListOf<NormalizedMunicipalFilingRecord>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_MUNICIPAL_FILINGS, null, null, null, null, null, "$COL_F_TIMESTAMP DESC")
        cursor.use {
            while (it.moveToNext()) {
                val record = NormalizedMunicipalFilingRecord(
                    id = it.getString(it.getColumnIndexOrThrow(COL_F_ID)),
                    formId = it.getString(it.getColumnIndexOrThrow(COL_F_FORM_ID)),
                    cityName = it.getString(it.getColumnIndexOrThrow(COL_F_CITY)),
                    agencyName = it.getString(it.getColumnIndexOrThrow(COL_F_AGENCY)),
                    candidateDeafAuthId = it.getString(it.getColumnIndexOrThrow(COL_F_CANDIDATE)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COL_F_TIMESTAMP)),
                    formattedDate = it.getString(it.getColumnIndexOrThrow(COL_F_DATE)),
                    canonicalPayloadJson = it.getString(it.getColumnIndexOrThrow(COL_F_PAYLOAD)),
                    status = it.getString(it.getColumnIndexOrThrow(COL_F_STATUS)),
                    deafAuthDigest = it.getString(it.getColumnIndexOrThrow(COL_F_DIGEST)),
                    syncedWithSuiteDoc = it.getInt(it.getColumnIndexOrThrow(COL_F_DOC)) == 1,
                    syncedWithSuiteEmail = it.getInt(it.getColumnIndexOrThrow(COL_F_EMAIL)) == 1
                )
                list.add(record)
            }
        }
        list
    }

    suspend fun insertMunicipalFiling(record: NormalizedMunicipalFilingRecord): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_F_ID, record.id)
            put(COL_F_FORM_ID, record.formId)
            put(COL_F_CITY, record.cityName)
            put(COL_F_AGENCY, record.agencyName)
            put(COL_F_CANDIDATE, record.candidateDeafAuthId)
            put(COL_F_TIMESTAMP, record.timestamp)
            put(COL_F_DATE, record.formattedDate)
            put(COL_F_PAYLOAD, record.canonicalPayloadJson)
            put(COL_F_STATUS, record.status)
            put(COL_F_DIGEST, record.deafAuthDigest)
            put(COL_F_DOC, if (record.syncedWithSuiteDoc) 1 else 0)
            put(COL_F_EMAIL, if (record.syncedWithSuiteEmail) 1 else 0)
        }
        val result = db.insertWithOnConflict(TABLE_MUNICIPAL_FILINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        result != -1L
    }

    suspend fun deleteMunicipalFiling(filingId: String): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val rows = db.delete(TABLE_MUNICIPAL_FILINGS, "$COL_F_ID = ?", arrayOf(filingId))
        rows > 0
    }
}
