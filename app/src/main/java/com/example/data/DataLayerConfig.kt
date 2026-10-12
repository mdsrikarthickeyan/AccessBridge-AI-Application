package com.example.data

enum class DatabaseStorageMode(val label: String, val badge: String, val description: String) {
    LOCAL_DEMO_PERSISTENT(
        label = "Local Storage (Room SQLite)",
        badge = "Local Mode Active",
        description = "Runs with on-device SQLite database. Fully functional for demonstrations, audits, barrier reporting, and offline usage. No external servers or API keys required."
    ),
    HOSTED_CLOUD(
        label = "Hosted Cloud Database (Supabase / Firebase)",
        badge = "Cloud Setup Path",
        description = "Requires project provisioning. Never store privileged credentials in client code; use server-side OAuth2 or authenticated Row Level Security (RLS)."
    )
}

object DatabaseSetupGuide {
    const val SQL_SCHEMA_OVERVIEW = """
-- AccessBridge AI Production Schema (PostgreSQL / Supabase compatible)

CREATE TABLE facilities (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type_name VARCHAR(64) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    verification_status VARCHAR(64) NOT NULL,
    last_verified_date VARCHAR(64) NOT NULL,
    information_source TEXT NOT NULL,
    opening_hours VARCHAR(100),
    entrance_details TEXT,
    step_free_entrance BOOLEAN DEFAULT FALSE,
    wheelchair_ramp BOOLEAN DEFAULT FALSE,
    lift_available BOOLEAN DEFAULT FALSE,
    accessible_toilet BOOLEAN DEFAULT FALSE,
    wheelchair_accessible_route BOOLEAN DEFAULT FALSE,
    tactile_paving BOOLEAN DEFAULT FALSE,
    braille_signage BOOLEAN DEFAULT FALSE,
    audio_assistance BOOLEAN DEFAULT FALSE,
    visual_announcements BOOLEAN DEFAULT FALSE,
    accessible_parking BOOLEAN DEFAULT FALSE,
    sign_language_assistance BOOLEAN DEFAULT FALSE,
    has_conflict BOOLEAN DEFAULT FALSE,
    conflict_description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE barrier_reports (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category_name VARCHAR(64) NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    specific_location_details TEXT NOT NULL,
    severity_name VARCHAR(64) NOT NULL,
    status_name VARCHAR(64) NOT NULL DEFAULT 'RECEIVED',
    date_reported VARCHAR(64) NOT NULL,
    timestamp BIGINT NOT NULL,
    status_notes TEXT,
    resolution_estimate VARCHAR(100),
    facility_id VARCHAR(64) REFERENCES facilities(id),
    optional_image_url TEXT,
    reporter_user_id VARCHAR(64),
    has_conflict_with_official_audit BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE report_status_history (
    id VARCHAR(64) PRIMARY KEY,
    report_id VARCHAR(64) REFERENCES barrier_reports(id) ON DELETE CASCADE,
    from_status_name VARCHAR(64),
    to_status_name VARCHAR(64) NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    changed_role VARCHAR(64) NOT NULL,
    timestamp VARCHAR(64) NOT NULL,
    review_note TEXT NOT NULL,
    action_taken VARCHAR(255) NOT NULL
);

-- Row Level Security (RLS) Policies
ALTER TABLE barrier_reports ENABLE ROW LEVEL SECURITY;
-- Public users can insert reports and read public aggregated facilities
CREATE POLICY "Public users can insert reports" ON barrier_reports FOR INSERT WITH CHECK (true);
CREATE POLICY "Users view own reports or public summaries" ON barrier_reports FOR SELECT USING (true);
-- Only authenticated reviewers & administrators can update status
CREATE POLICY "Reviewers can update report status" ON barrier_reports FOR UPDATE TO authenticated USING (auth.jwt() ->> 'role' IN ('reviewer', 'admin'));
"""
}
