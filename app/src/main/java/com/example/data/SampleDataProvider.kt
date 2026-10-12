package com.example.data

import com.example.model.AccessiblePlace
import com.example.model.BarrierCategory
import com.example.model.BarrierReport
import com.example.model.BarrierSeverity
import com.example.model.BarrierStatus
import com.example.model.CommCategory
import com.example.model.CommunicationCard
import com.example.model.NoticeCategory
import com.example.model.NoticeItem
import com.example.model.PlaceType
import com.example.model.ReportStatusHistoryItem
import com.example.model.VerificationRecord
import com.example.model.VerificationStatus
import com.example.model.VerifierRole

object SampleDataProvider {

    val sampleNotices = listOf(
        NoticeItem(
            id = "notice-dmrc-01",
            title = "DMRC Metro Wheelchair Shuttle & Lift Modernization",
            category = NoticeCategory.TRANSIT,
            originalText = """
                DELHI METRO RAIL CORPORATION (DMRC)
                PUBLIC NOTICE NO. 2026/DMRC/ACC-09
                Subject: Upgradation of Elevators and Free Battery Buggy Assistance at Rajiv Chowk Station.
                All passengers requiring wheelchair assistance, crutches, or senior citizens with restricted mobility are notified that Platform 1 & 2 elevators will undergo scheduled sensor recalibration between 15th October and 20th October 2026.
                During this period, dedicated electric battery buggies with trained station attendants will operate continuously between Gate 3 and Gate 7 concourses free of charge.
                Passengers may contact the Station Control Room directly via intercom or by dialling 011-23417910 or contacting security at AFC gates.
                Wheelchair users can also request escorted interchange to Yellow / Blue Lines.
            """.trimIndent(),
            plainSummary = "Rajiv Chowk Metro station is upgrading its elevators from 15 to 20 October. During this time, free electric buggies and helper staff are available at Gate 3 and 7 to take wheelchair users and senior citizens to their platforms.",
            keyPoints = listOf(
                "Elevators will undergo maintenance from 15th to 20th October",
                "Free battery buggies with helper attendants are running between Gate 3 and Gate 7",
                "Escorts are available to help you transfer between Blue and Yellow lines",
                "Call Station Control at 011-23417910 or ask security at any entry gate"
            ),
            actionRequired = "If you use a wheelchair or need walking assistance, enter through Gate 3 or Gate 7 and ask the attendant for buggy transfer.",
            deadline = "15 Oct - 20 Oct 2026",
            emergencyContact = "011-23417910 (Station Control)",
            isVerified = true,
            dateAdded = "10 Oct 2026"
        ),
        NoticeItem(
            id = "notice-aiims-02",
            title = "AIIMS OPD Accessibility & Braille Report Guidelines",
            category = NoticeCategory.HOSPITAL,
            originalText = """
                ALL INDIA INSTITUTE OF MEDICAL SCIENCES (AIIMS)
                OFFICE OF THE MEDICAL SUPERINTENDENT - CIRCULAR 114
                Subject: Dedicated Accessibility Desk and Assistive Reading Facilities at Rajkumari Amrit Kaur OPD.
                Patients with visual impairment, hearing disability, or locomotor challenges can now access fast-track token collection at Counter 4A (Ground Floor, Main Atrium).
                Prescriptions and discharge summaries can be printed in Braille or emailed in accessible text format upon request at Counter 4A.
                Sign language interpreters are stationed in Room 12 between 9:00 AM and 2:00 PM on all weekdays.
                Wheelchairs and stretchers are available at Gate 2 entrance with no security deposit required.
            """.trimIndent(),
            plainSummary = "AIIMS New Delhi has set up Counter 4A on the ground floor for patients with disabilities. You get fast-track tokens, Braille prescription printouts, free wheelchairs at Gate 2, and sign language interpreters in Room 12.",
            keyPoints = listOf(
                "Fast-track token collection at Counter 4A on the ground floor",
                "Braille prints and accessible digital prescriptions available upon request",
                "Sign language interpreters present in Room 12 (9 AM to 2 PM, Mon-Fri)",
                "Free wheelchairs at Gate 2 with zero deposit"
            ),
            actionRequired = "Head directly to Counter 4A in the Main Atrium. Bring your disability card or photo ID for immediate fast-track assistance.",
            deadline = "Ongoing service",
            emergencyContact = "011-26588500 (OPD Helpdesk)",
            isVerified = true,
            dateAdded = "08 Oct 2026"
        ),
        NoticeItem(
            id = "notice-univ-03",
            title = "University Examination Concession & Scribe Policy",
            category = NoticeCategory.COLLEGE,
            originalText = """
                CONTROLLER OF EXAMINATIONS - NOTIFICATION 88/2026
                Subject: Compensatory Time and Scribe Allocation for Semester Exams.
                Pursuant to RPwD Act 2016 norms, students with permanent or temporary disabilities (including visual impairment, cerebral palsy, orthopaedic limitations, and specific learning disabilities like dyslexia) are granted 20 minutes compensatory time per hour of examination.
                Candidates requiring an approved scribe must submit Form Annexure-C along with valid medical certificate to Student Welfare Section by 25th October 2026.
                Accessible screen reader laptops with NVDA software will be arranged in Lab Block 3.
            """.trimIndent(),
            plainSummary = "Students with disabilities or dyslexia receive 20 minutes extra time per hour during semester exams. If you need a scribe (writer) or a computer with NVDA screen reader, submit Form Annexure-C before 25 October.",
            keyPoints = listOf(
                "20 minutes extra time per examination hour",
                "Free scribe assistance available with prior registration",
                "Screen reader computers with NVDA available in Lab Block 3",
                "Deadline to submit scribe request form: 25 October 2026"
            ),
            actionRequired = "Submit Form Annexure-C with your medical or disability certificate to the Student Welfare Section before 25 October 2026.",
            deadline = "25 October 2026",
            emergencyContact = "011-27667000 (Student Welfare)",
            isVerified = true,
            dateAdded = "05 Oct 2026"
        ),
        NoticeItem(
            id = "notice-railway-04",
            title = "Indian Railways Battery Buggy & Low-Height Berth Booking",
            category = NoticeCategory.TRANSIT,
            originalText = """
                MINISTRY OF RAILWAYS - COMMERCIAL CIRCULAR NO. 42
                Subject: Yatri Suvidha Wheelchair and Battery Operated Car booking at Category A1 Stations.
                Divyangjan passengers holding concession certificates can book lower berths under Divyang quota through PRS counters or online portal.
                To arrange free battery car pickup from station porch to coach door at New Delhi, Howrah, CSMT Mumbai, and Chennai Central, call Railway Helpline 139 at least 2 hours before train departure.
                Tactile pathways lead directly from Concourse Entry to Platforms 1 through 16.
            """.trimIndent(),
            plainSummary = "Passengers with disabilities can book lower berths under the reserved quota. You can also book a free battery cart to drive you directly from the station gate to your train coach by calling 139 two hours before your journey.",
            keyPoints = listOf(
                "Reserved lower berth quota available for passengers with disabilities",
                "Free battery car pickup from station porch directly to coach door",
                "Call 139 at least 2 hours in advance to book battery car",
                "Tactile guiding tiles installed along the entire main concourse"
            ),
            actionRequired = "Dial 139 two hours before your train leaves, or visit the Divyangjan Help Booth at Platform 1.",
            deadline = "Call 2 hours before journey",
            emergencyContact = "139 (Rail Madad Helpline)",
            isVerified = true,
            dateAdded = "01 Oct 2026"
        ),
        NoticeItem(
            id = "notice-udid-05",
            title = "UDID Disability Card Application & Renewal Form",
            category = NoticeCategory.GOVERNMENT_FORM,
            originalText = """
                DEPARTMENT OF EMPOWERMENT OF PERSONS WITH DISABILITIES
                MINISTRY OF SOCIAL JUSTICE AND EMPOWERMENT - FORM UDID-01
                INSTRUCTIONS FOR UNIQUE DISABILITY IDENTITY CARD (UDID) APPLICATION
                
                Eligibility Criteria:
                Any citizen of India with 40% or more benchmark disability certified under RPwD Act 2016.
                
                Steps to Complete Application:
                Step 1: Fill out basic demographic details (Name, Date of Birth, Father's Name, Permanent Address).
                Step 2: Upload recent passport photograph and signature or thumb impression.
                Step 3: Attach valid Proof of Identity (Aadhaar Card, Voter ID, or Passport).
                Step 4: Attach valid Proof of Address (Electricity bill, Ration card, or Domicile certificate).
                Step 5: Upload previous Disability Certificate issued by District Medical Board.
                Step 6: Submit application online or submit physical printout at District Disability Rehabilitation Centre (DDRC).
                
                Important Deadlines and Fees:
                - Application fee: Nil (Free service).
                - Card validity: 5 years for temporary disability; Lifetime for permanent disability.
                - Verification and medical board assessment appointment scheduled within 30 days.
                
                Office Address & Helpline:
                National UDID Portal Helpdesk, Room 204, Antyodaya Bhawan, CGO Complex, New Delhi 110003.
                Toll Free: 1800-180-5126 | Email: udid-support@gov.in
            """.trimIndent(),
            plainSummary = "This official form explains how to apply for the free Unique Disability Identity Card (UDID). People with 40% or higher disability can apply online or at the district centre with their photo, Aadhaar card, and medical certificate.",
            keyPoints = listOf(
                "Application is 100% free with no government charges",
                "Requires proof of identity, address, photo, and previous medical certificate",
                "Medical assessment date will be allotted within 30 days of submission",
                "Card is valid for 5 years (temporary) or lifetime (permanent)"
            ),
            actionRequired = "Gather your Aadhaar, passport photo, and doctor's certificate, then complete the 6 steps listed or visit your nearest DDRC centre.",
            deadline = "Assessment within 30 days",
            emergencyContact = "1800-180-5126 (UDID Helpdesk)",
            isVerified = true,
            dateAdded = "28 Sep 2026"
        ),
        NoticeItem(
            id = "notice-pension-06",
            title = "Divyang Pension Scheme Verification & Aadhaar Seeding Form",
            category = NoticeCategory.GOVERNMENT_FORM,
            originalText = """
                DIRECTORATE OF SOCIAL WELFARE - PUBLIC ANNOUNCEMENT
                CIRCULAR NO. DSW/PENSION/VERIF-2026/89
                
                Subject: Annual Physical / Digital Life Certificate Verification for Monthly Disability Pension.
                
                All beneficiaries receiving monthly Divyangjan financial assistance (Rs. 2,500/month) must complete annual verification before 30th November 2026.
                
                Procedure to Avoid Disruption of Pension:
                1. Submit Jeevan Pramaan digital life certificate using biometric fingerprint / iris scanner at any Common Service Centre (CSC) or Post Office.
                2. Alternatively, bedridden or high-support individuals may request doorstep postal gramin dak sevak verification by dialling 1950.
                3. Ensure bank account is active and seeded with National Payments Corporation of India (NPCI) Aadhaar mapping.
                
                Required Documents:
                - Original Pension Sanction PPO Book
                - Active Bank Passbook showing IFSC code and Account Number
                - UDID Card / Disability Certificate
                - Aadhaar Card copy
                
                Ambiguous Instruction Notice:
                Note: Paragraph 4 specifies that beneficiaries without Aadhaar linkage must submit manual Form Annexure-B at the Sub-Divisional Magistrate (SDM) office; however, opening hours for verification counters vary by district. Beneficiaries are advised to call district helpline to confirm timing before travelling.
            """.trimIndent(),
            plainSummary = "Beneficiaries of the monthly disability pension must submit their life certificate before 30 November 2026 to keep receiving payments. You can verify your fingerprint at a CSC centre, post office, or request home verification.",
            keyPoints = listOf(
                "Annual verification must be submitted before 30 November 2026",
                "Monthly assistance amount: Rs. 2,500 per month",
                "Home doorstep verification is available for bedridden individuals via postal service",
                "Make sure your bank account is linked with Aadhaar"
            ),
            actionRequired = "Visit a CSC or post office with your pension book and Aadhaar before 30 November, or dial 1950 for home visit.",
            deadline = "30 November 2026",
            emergencyContact = "1950 (Social Welfare & Postal Helpline)",
            isVerified = true,
            dateAdded = "20 Sep 2026"
        ),
        NoticeItem(
            id = "notice-scanned-ocr-07",
            title = "Scanned Form: Railway Concession Certificate (Scanned / OCR)",
            category = NoticeCategory.TRANSIT,
            originalText = """
                [SCANNED DOCUMENT - OCR PROCESSED: 200 DPI RESOLUTION]
                GOVERNMENT OF INDIA - MINISTRY OF RAILWAYS
                CONCESSION CERTIFICATE FOR PERSONS WITH ORTHOPAEDIC DISABILITY / PARAPLEGIA
                FORM NO. 19-B / REVISED 2026

                1. Name of Applicant: [Handwritten entry: Ramesh Kumar Verma]
                2. Nature of Disability: Locomotor Benchmark Disability exceeding 50%
                3. Certificate Issue Date: 12-JULY-2026 | Validity: Lifetime
                4. Medical Authority Seal: Stamped by Medical Superintendent, District Civil Hospital
                
                Official Submission Instructions:
                - Photocopy of this stamped concession certificate must be carried during journey along with Photo ID.
                - To book ticket at concession window: Present original certificate and photo proof at PRS Booking Counter.
                - Escort concession: One companion is entitled to same 75% fare concession in 2nd, Sleeper, and 3AC classes.
                - OCR Quality Note: Stamped serial number in top right header has slight ink blur (No. 4082/2026); verify against original doctor slip.
            """.trimIndent(),
            plainSummary = "This scanned official railway form gives a 75% travel discount to Ramesh Kumar Verma and one accompanying person. Show the original certificate with the hospital stamp and a photo ID at the ticket counter and while traveling.",
            keyPoints = listOf(
                "75% discount on 2nd, Sleeper, and 3rd AC train tickets",
                "One travel companion gets the exact same discount",
                "Original certificate with hospital stamp must be shown during ticket purchase and journey",
                "Doctor's stamp gives lifetime validity"
            ),
            actionRequired = "Take the original stamped certificate and your Aadhaar card to any railway booking counter.",
            deadline = "Valid for lifetime journeys",
            emergencyContact = "139 (Rail Madad Accessibility Desk)",
            isVerified = true,
            dateAdded = "15 Sep 2026"
        ),
        NoticeItem(
            id = "notice-incomplete-doc-08",
            title = "Incomplete Document: Assistive Device Subsidy (Missing Page 2)",
            category = NoticeCategory.GOVERNMENT_FORM,
            originalText = """
                MINISTRY OF SOCIAL JUSTICE - ADIP SCHEME ASSISTANCE
                CIRCULAR NO. MSJE/ADIP/2026/04 - PAGE 1 OF 2
                
                Subject: Free Distribution of Motorised Tricycles, Smart Canes, and Hearing Aids.
                
                Section 1: General Objective
                Under the ADIP scheme, eligible persons with disabilities receive modern assistive aids without cost.
                
                Section 2: Initial Application Steps
                Step 1: Obtain scheme application Form ADIP-A from District Disability Officer or ALIMCO camp.
                Step 2: Attach copy of UDID card (minimum 40% disability).
                Step 3: Attach two recent colour passport photos.
                
                [DOCUMENT INCOMPLETE NOTICE]:
                Warning: Section 3 ('Income Ceilings and Financial Eligibility Thresholds') and Section 4 ('Camp Schedule and Venue List') continue on Page 2. Page 2 was omitted from this scanned upload.
                Beneficiaries are strongly advised to seek human review or contact ALIMCO helpline before travelling, as income criteria (annual income limit for 100% subsidy) cannot be verified from Page 1 alone.
            """.trimIndent(),
            plainSummary = "This notice is Page 1 of 2 for free wheelchairs, tricycles, and hearing aids. You need a UDID card and photos. Because Page 2 is missing, the exact income limit and camp dates are not shown and need human review.",
            keyPoints = listOf(
                "Free motorised tricycles, smart canes, and hearing aids under ADIP scheme",
                "Requires UDID card with at least 40% disability",
                "Needs Form ADIP-A and two passport photographs",
                "Notice is missing Page 2: income limits and venue dates must be checked with office"
            ),
            actionRequired = "Collect your UDID card and photos, but call the ALIMCO helpline to confirm income limits from missing Page 2.",
            deadline = "Check Page 2 or call ALIMCO helpline",
            emergencyContact = "1800-180-5129 (ALIMCO National Toll-Free)",
            isVerified = false,
            dateAdded = "12 Sep 2026"
        ),
        NoticeItem(
            id = "notice-multipage-pdf-09",
            title = "Multi-Page PDF: National Accessibility Guidelines (3 Pages)",
            category = NoticeCategory.TRANSIT,
            originalText = """
                MINISTRY OF HOUSING AND URBAN AFFAIRS
                HARMONISED ACCESSIBILITY STANDARDS 2026 - GAZETTE EXTRAORDINARY
                
                --- PAGE 1: ENTRANCES, RAMPS AND CIRCULATION PATHS ---
                1.1 Step-Free Approach: All public transit hubs and municipal buildings must provide a ramp with gradient not steeper than 1:12.
                1.2 Clear Entrance Width: Automated sliding doors or swinging doors must maintain minimum clear width of 900 mm.
                1.3 Continuous Tactile Guiding: Warning studs (blister paving) must be placed 300 mm ahead of any level change or ramp start.
                
                --- PAGE 2: VERTICAL MOBILITY AND LIFTS ---
                2.1 Passenger Lifts: Car dimensions must be at least 1500 mm x 1500 mm to accommodate power wheelchairs.
                2.2 Control Buttons: Height of buttons must be between 900 mm and 1200 mm from floor level, with raised Braille numerals.
                2.3 Acoustic Feedback: Voice announcements must state floor number and door opening/closing direction in local language and English.
                
                --- PAGE 3: ACCESSIBLE RESTROOMS AND EMERGENCY EVACUATION ---
                3.1 Restroom Layout: Minimum internal clearance of 2200 mm x 2000 mm with outward swinging door.
                3.2 Grab Bars: L-shaped wall grab bars mounted 750 mm above floor level alongside water closet.
                3.3 Emergency Alarm: Red emergency pull cord installed 100 mm above floor level with audible strobe beacon outside door.
                3.4 Evacuation Chairs: Stair evacuation chairs must be stored on every floor landing for power outage emergencies.
            """.trimIndent(),
            plainSummary = "This 3-page official standard sets mandatory accessibility rules for all public buildings. Page 1 covers gentle 1:12 ramps and 900mm wide doors. Page 2 covers wheelchair-sized elevators with Braille buttons and voice announcements. Page 3 mandates accessible toilets and emergency pull cords.",
            keyPoints = listOf(
                "Page 1: 1:12 maximum ramp gradient and 900mm wide doors",
                "Page 1: Tactile blister studs 300mm before all steps and slopes",
                "Page 2: Elevators must fit power wheelchairs (1500mm x 1500mm) with Braille buttons",
                "Page 2: Voice announcements for floors in local language and English",
                "Page 3: Accessible toilets must have 750mm grab bars and floor-level red alarm cord",
                "Page 3: Evacuation chairs required on all floors for fire emergencies"
            ),
            actionRequired = "Architects and facility managers must follow all three pages of standards during audits and renovations.",
            deadline = "Mandatory compliance across all public buildings",
            emergencyContact = "011-23062300 (Urban Accessibility Cell)",
            isVerified = true,
            dateAdded = "10 Sep 2026"
        )
    )

    val samplePlaces = listOf(
        AccessiblePlace(
            id = "place-majestic-01",
            name = "Kempegowda Majestic Interchange Metro",
            type = PlaceType.METRO,
            city = "Bengaluru",
            address = "Gubbi Thotadappa Rd, Majestic, Bengaluru 560023",
            latitude = 12.9767,
            longitude = 77.5713,
            verificationStatus = VerificationStatus.VERIFIED,
            lastVerifiedDate = "15 Aug 2026",
            informationSource = "BMRCL Accessibility Audit & Joint Inspection",
            openingHours = "05:00 AM - 11:30 PM",
            entranceDetails = "Step-free ramp entry at Gate A & Gate F with automated wide fare gates (900mm width)",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = true,
            brailleSignage = true,
            audioAssistance = true,
            visualAnnouncements = true,
            accessibleParking = true,
            signLanguageAssistance = true,
            otherFacilities = listOf("Motorized wheelchair assistance", "Low-height ticket counter 3", "Tactile station map at concourse"),
            assistanceDeskPhone = "080-22945100",
            description = "Major transit hub connecting Green and Purple metro lines, KSRTC bus stand, and City Railway Station. Full step-free access across all 4 underground and elevated concourse levels.",
            accessibilityNotes = "Continuous yellow tactile paving from all bus entry gates. Dual elevators at Platform 1 & 2 with braille button panels and multilingual audible chime. Dedicated accessible restroom on Concourse Level 1.",
            nearestTransit = "Direct connection to City Railway Station (SBC)"
        ),
        AccessiblePlace(
            id = "place-aiims-02",
            name = "AIIMS Main Medical Complex & OPD",
            type = PlaceType.HOSPITAL,
            city = "Delhi",
            address = "Sri Aurobindo Marg, Ansari Nagar, New Delhi 110029",
            latitude = 28.5672,
            longitude = 77.2100,
            verificationStatus = VerificationStatus.VERIFIED,
            lastVerifiedDate = "10 Sep 2026",
            informationSource = "Ministry of Health Accessibility Cell & AIIMS Engineering Dept",
            openingHours = "24 Hours (OPD: 08:30 AM - 01:00 PM)",
            entranceDetails = "Covered gentle ramp with handrails on both sides at Gate 2 and Gate 4",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = true,
            brailleSignage = true,
            audioAssistance = true,
            visualAnnouncements = true,
            accessibleParking = true,
            signLanguageAssistance = true,
            otherFacilities = listOf("Free motorized buggies between OPD blocks", "Braille signage on all elevator panels", "UDID priority counter"),
            assistanceDeskPhone = "011-26588500",
            description = "Premier government hospital equipped with step-free ramps, wide automated sliding doors, priority counters, and assistive sign interpreters in the main outpatient building.",
            accessibilityNotes = "Wheelchair station with motorized chairs located at Gate 2. Low-height reception desks at Counter 4A. Tactile directional indicators leading to pharmacy and blood collection.",
            nearestTransit = "AIIMS Metro Station (Yellow Line) Exit 2 (Elevator equipped)"
        ),
        AccessiblePlace(
            id = "place-anna-library-03",
            name = "Anna Centenary Library",
            type = PlaceType.LIBRARY,
            city = "Chennai",
            address = "Gandhi Mandapam Rd, Surya Nagar, Kotturpuram, Chennai 600085",
            latitude = 13.0125,
            longitude = 80.2376,
            verificationStatus = VerificationStatus.VERIFIED,
            lastVerifiedDate = "01 Jul 2026",
            informationSource = "Dept. of Public Libraries Audit",
            openingHours = "09:00 AM - 08:00 PM",
            entranceDetails = "Smooth wide porch ramp with dual height handrails and tactile entry warning tiles",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = true,
            brailleSignage = true,
            audioAssistance = true,
            visualAnnouncements = true,
            accessibleParking = true,
            signLanguageAssistance = false,
            otherFacilities = listOf("Over 3,500 Braille volumes", "50 DAISY digital talking book computers", "Screen magnifier reading stations"),
            assistanceDeskPhone = "044-22201011",
            description = "State-of-the-art public library with an entire dedicated Braille and Talking Books wing on the Ground Floor.",
            accessibilityNotes = "Smooth gentle ramps at main porch. Over 3,000 Braille volumes and 50 DAISY digital audio reading workstations. Spacious accessible washrooms on all 8 reading floors.",
            nearestTransit = "Kotturpuram MRTS Station (700m) / Bus stop at gate"
        ),
        AccessiblePlace(
            id = "place-csmt-04",
            name = "Chhatrapati Shivaji Maharaj Terminus (CSMT)",
            type = PlaceType.METRO,
            city = "Mumbai",
            address = "Fort, Mumbai, Maharashtra 400001",
            latitude = 18.9400,
            longitude = 72.8353,
            verificationStatus = VerificationStatus.REPORTED_BY_VISITOR,
            lastVerifiedDate = "22 Aug 2026",
            informationSource = "Recent passenger report with uploaded entry photos",
            openingHours = "24 Hours Open",
            entranceDetails = "Level entry from suburban booking concourse; ramps installed at main entrance",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = true,
            brailleSignage = false,
            audioAssistance = true,
            visualAnnouncements = true,
            accessibleParking = false,
            signLanguageAssistance = false,
            otherFacilities = listOf("Free battery-operated transit cart", "Priority booking window at suburban hall"),
            assistanceDeskPhone = "022-22620173",
            description = "Historic railway terminus with upgraded step-free access paths, motorized passenger carts, and accessible booking counters at Platform 1.",
            accessibilityNotes = "Level boarding for suburban local trains on Platforms 1 through 7. Free wheelchair service managed by Coolie Helpdesk near suburban ticket booking hall.",
            nearestTransit = "CSMT Metro Line 3 Underground Station (Upcoming fully accessible)"
        ),
        AccessiblePlace(
            id = "place-iitb-05",
            name = "IIT Bombay Central Library & Student Hub",
            type = PlaceType.COLLEGE,
            city = "Mumbai",
            address = "Powai, Mumbai, Maharashtra 400076",
            latitude = 19.1334,
            longitude = 72.9133,
            verificationStatus = VerificationStatus.VERIFIED,
            lastVerifiedDate = "18 Sep 2026",
            informationSource = "IIT Bombay Equal Opportunity Cell (EOC)",
            openingHours = "08:00 AM - 11:00 PM (Exam weeks: 24h)",
            entranceDetails = "Automated sliding glass entrance door with ramp and tactile indicators",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = true,
            brailleSignage = true,
            audioAssistance = false,
            visualAnnouncements = true,
            accessibleParking = true,
            signLanguageAssistance = true,
            otherFacilities = listOf("JAWS screen-reader enabled terminals", "Accessible battery shuttles across campus"),
            assistanceDeskPhone = "022-25768920",
            description = "Fully barrier-free academic hub with wheelchair accessible elevators, screen reader software on student terminals, and dedicated accessible shuttle vans.",
            accessibilityNotes = "Continuous ramps linking lecture theatre halls. Accessible restrooms in all departmental wings. Dedicated Assistive Technology Cell in the Central Library.",
            nearestTransit = "Kanjurmarg Railway Station (3 km) / Auto rickshaw stand at main gate"
        ),
        AccessiblePlace(
            id = "place-hyd-collectorate-06",
            name = "Hyderabad District Collectorate & Citizen Centre",
            type = PlaceType.GOVERNMENT,
            city = "Hyderabad",
            address = "Chirag Ali Lane, Abids, Hyderabad, Telangana 500001",
            latitude = 17.3916,
            longitude = 78.4735,
            verificationStatus = VerificationStatus.REPORTED_BY_VISITOR,
            lastVerifiedDate = "05 Sep 2026",
            informationSource = "Community visitor submission during UDID camp",
            openingHours = "10:00 AM - 05:00 PM (Mon-Sat, Closed 2nd Sat)",
            entranceDetails = "Ramp at the rear entrance near parking; main front steps have steep angle",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = false,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = false,
            brailleSignage = false,
            audioAssistance = true,
            visualAnnouncements = false,
            accessibleParking = true,
            signLanguageAssistance = false,
            otherFacilities = listOf("UDID card application desk on Ground Floor", "Wheelchair available in Room 102"),
            assistanceDeskPhone = "040-23202833",
            description = "Citizen public services center for pension verification, disability identity cards (UDID), and revenue certificates with ground floor priority counters.",
            accessibilityNotes = "Ramp at the rear entrance near parking. Counter 1 is reserved exclusively for senior citizens and persons with disabilities. Wheelchair on standby in Room 102.",
            nearestTransit = "Gandhi Bhavan Metro Station (Red Line) - 800m"
        ),
        AccessiblePlace(
            id = "place-delhi-publib-07",
            name = "Delhi Public Library Central Branch",
            type = PlaceType.LIBRARY,
            city = "Delhi",
            address = "Opposite Old Delhi Railway Station, Chandni Chowk, Delhi 110006",
            latitude = 28.6582,
            longitude = 77.2289,
            verificationStatus = VerificationStatus.OUTDATED,
            lastVerifiedDate = "14 Mar 2025",
            informationSource = "Past municipal survey (Over 12 months old)",
            openingHours = "08:30 AM - 07:30 PM",
            entranceDetails = "Side ramp exists; main entrance has 4 stone steps",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = false,
            accessibleToilet = false,
            wheelchairAccessibleRoute = false,
            tactilePaving = false,
            brailleSignage = true,
            audioAssistance = false,
            visualAnnouncements = false,
            accessibleParking = false,
            signLanguageAssistance = false,
            otherFacilities = listOf("Braille reading section on Ground Floor"),
            assistanceDeskPhone = "011-23951112",
            description = "Historic municipal library with dedicated Braille books and quiet study corners.",
            accessibilityNotes = "Ground floor has Braille materials. Elevator to upper floors is currently undergoing maintenance. Visitors recommend calling ahead.",
            nearestTransit = "Chandni Chowk Metro Station (Yellow Line) - 300m",
            hasConflict = true,
            conflictDescription = "Community report indicates elevator maintenance still ongoing; under verification.",
            communityReportsCount = 1
        ),
        AccessiblePlace(
            id = "place-bengaluru-collectorate-08",
            name = "Bengaluru Urban Deputy Commissioner Office",
            type = PlaceType.GOVERNMENT,
            city = "Bengaluru",
            address = "K.G. Road, Revenue Complex, Bengaluru, Karnataka 560009",
            latitude = 12.9738,
            longitude = 77.5831,
            verificationStatus = VerificationStatus.NOT_YET_CHECKED,
            lastVerifiedDate = "Newly listed",
            informationSource = "Public directory listing; independent audit scheduled",
            openingHours = "10:00 AM - 05:30 PM (Working days)",
            entranceDetails = "Main road entrance with vehicle drop-off porch",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = false,
            tactilePaving = false,
            brailleSignage = false,
            audioAssistance = false,
            visualAnnouncements = false,
            accessibleParking = true,
            signLanguageAssistance = false,
            otherFacilities = listOf("Ground floor citizen service counters"),
            assistanceDeskPhone = "080-22211292",
            description = "District administrative headquarters providing citizen certificates and electoral services.",
            accessibilityNotes = "Ground floor ramps verified by public photos; upper floors and restroom facilities not yet formally audited.",
            nearestTransit = "Sir M. Visveshwaraya Metro Station - 450m"
        ),
        AccessiblePlace(
            id = "place-rajiv-chowk-09",
            name = "Rajiv Chowk Metro Interchange",
            type = PlaceType.METRO,
            city = "Delhi",
            address = "Block B, Connaught Place, New Delhi, Delhi 110001",
            latitude = 28.6328,
            longitude = 77.2197,
            verificationStatus = VerificationStatus.VERIFIED,
            lastVerifiedDate = "18 Sep 2026",
            informationSource = "DMRC Accessibility Audit & Joint Inspection",
            openingHours = "05:30 AM - 11:30 PM",
            entranceDetails = "Elevator access from street level at Gate 7; ramp at Gate 6 undergoing civil repair",
            stepFreeEntrance = true,
            wheelchairRamp = true,
            liftAvailable = true,
            accessibleToilet = true,
            wheelchairAccessibleRoute = true,
            tactilePaving = true,
            brailleSignage = true,
            audioAssistance = true,
            visualAnnouncements = true,
            accessibleParking = false,
            signLanguageAssistance = false,
            otherFacilities = listOf("Free battery-operated buggies for senior citizens and disabled passengers", "Wide automatic fare gates at Gate 7"),
            assistanceDeskPhone = "011-23417910",
            description = "Central underground interchange hub connecting Blue and Yellow metro lines. Full step-free elevator access at designated street gates.",
            accessibilityNotes = "Continuous tactile guiding path throughout concourse level. Dedicated wide gates for wheelchair users.",
            nearestTransit = "Direct DMRC Yellow and Blue line platform interchange",
            hasConflict = true,
            conflictDescription = "Community report: Gate 6 sidewalk ramp has loose tiles and cracked slab. Maintenance crew dispatched for resurfacing.",
            communityReportsCount = 1
        )
    )

    val sampleCommCards = listOf(
        // Quick Accessibility Phrases requested by users
        CommunicationCard(
            id = "comm-speak-slowly-00",
            category = CommCategory.DAILY_NEEDS,
            englishText = "Please speak slowly.",
            hindiText = "कृपया धीरे बोलें।",
            tamilText = "தயவுசெய்து மெதுவாக பேசுங்கள்.",
            teluguText = "దయచేసి నెమ్మదిగా మాట్లాడండి.",
            kannadaText = "ದಯವಿಟ್ಟು ನಿಧಾನವಾಗಿ ಮಾತನಾಡಿ.",
            iconName = "hearing",
            phoneticGuide = "Kripya dheere bolein"
        ),
        CommunicationCard(
            id = "comm-write-down-00",
            category = CommCategory.DAILY_NEEDS,
            englishText = "Please write that down.",
            hindiText = "कृपया इसे लिखकर दें।",
            tamilText = "தயவுசெய்து அதை எழுதிக் காட்டுங்கள்.",
            teluguText = "దయచేసి దాన్ని రాసి చూపించండి.",
            kannadaText = "ದಯವಿಟ್ಟು ಅದನ್ನು ಬರೆದು ತೋರಿಸಿ.",
            iconName = "edit",
            phoneticGuide = "Kripya likhkar dein"
        ),
        CommunicationCard(
            id = "comm-step-free-00",
            category = CommCategory.TRANSIT,
            englishText = "I need step-free access.",
            hindiText = "मुझे बिना सीढ़ियों वाला (रैंप या लिफ्ट) रास्ता चाहिए।",
            tamilText = "எனக்கு படிகள் இல்லாத பாதை தேவை.",
            teluguText = "నాకు మెట్లు లేని దారి కావాలి.",
            kannadaText = "ನನಗೆ ಮೆಟ್ಟಿಲುಗಳಿಲ್ಲದ ಮಾರ್ಗ ಬೇಕು.",
            iconName = "accessible",
            phoneticGuide = "Bina seedhiyon wala rasta chahiye"
        ),
        CommunicationCard(
            id = "comm-show-where-00",
            category = CommCategory.DIRECTIONS,
            englishText = "Could you show me where to go?",
            hindiText = "क्या आप मुझे रास्ता दिखा सकते हैं?",
            tamilText = "எங்கே செல்ல வேண்டும் என்று எனக்கு காட்ட முடியுமா?",
            teluguText = "నేను ఎక్కడికి వెళ్ళాలో చూపించగలరా?",
            kannadaText = "ನಾನು ಎಲ್ಲಿಗೆ ಹೋಗಬೇಕೆಂದು ತೋರಿಸಬಹುದೇ?",
            iconName = "navigation",
            phoneticGuide = "Rasta dikha sakte hain?"
        ),
        CommunicationCard(
            id = "comm-give-moment-00",
            category = CommCategory.DAILY_NEEDS,
            englishText = "Please give me a moment.",
            hindiText = "कृपया मुझे एक पल का समय दें।",
            tamilText = "தயவுசெய்து எனக்கு ஒரு நிமிடம் அவகாசம் கொடுங்கள்.",
            teluguText = "దయచేసి నాకు కాస్త సమయం ఇవ్వండి.",
            kannadaText = "ದಯವಿಟ್ಟು ನನಗೆ ಸ್ವಲ್ಪ ಸಮಯ ಕೊಡಿ.",
            iconName = "time",
            phoneticGuide = "Mujhe thoda samay dein"
        ),
        CommunicationCard(
            id = "comm-assistance-00",
            category = CommCategory.EMERGENCY,
            englishText = "I need assistance.",
            hindiText = "मुझे सहायता चाहिए।",
            tamilText = "எனக்கு உதவி தேவை.",
            teluguText = "నాకు సహాయం కావాలి.",
            kannadaText = "ನನಗೆ ಸಹಾಯ ಬೇಕು.",
            iconName = "alert",
            phoneticGuide = "Mujhe sahayata chahiye"
        ),
        // Daily Needs
        CommunicationCard(
            id = "comm-water-01",
            category = CommCategory.DAILY_NEEDS,
            englishText = "I need drinking water, please.",
            hindiText = "मुझे पीने का पानी चाहिए, कृपया।",
            tamilText = "எனக்கு குடிக்க தண்ணீர் வேண்டும், தயவுசெய்து.",
            teluguText = "నాకు తాగడానికి నీరు కావాలి, దయచేసి.",
            kannadaText = "ನನಗೆ ಕುಡಿಯಲು ನೀರು ಬೇಕು, ದಯವಿಟ್ಟು.",
            iconName = "water",
            phoneticGuide = "Mujhe peene ka paani chahiye"
        ),
        CommunicationCard(
            id = "comm-restroom-02",
            category = CommCategory.DAILY_NEEDS,
            englishText = "Where is the accessible restroom / toilet?",
            hindiText = "सुलभ शौचालय (टॉयलेट) कहाँ है?",
            tamilText = "கழிப்பறை எங்கே உள்ளது?",
            teluguText = "వాష్ రూమ్ ఎక్కడ ఉంది?",
            kannadaText = "ಶೌಚಾಲಯ ಎಲ್ಲಿದೆ?",
            iconName = "wc",
            phoneticGuide = "Toilet kahan hai?"
        ),
        CommunicationCard(
            id = "comm-charge-03",
            category = CommCategory.DAILY_NEEDS,
            englishText = "Can I charge my motorized wheelchair or phone here?",
            hindiText = "क्या मैं अपनी व्हीलचेयर या फोन यहाँ चार्ज कर सकता हूँ?",
            tamilText = "நான் இங்கு சார்ஜ் செய்யலாமா?",
            teluguText = "నేను ఇక్కడ ఛార్జ్ చేసుకోవచ్చా?",
            kannadaText = "ನಾನು ಇಲ್ಲಿ ಚಾರ್ಜ್ ಮಾಡಬಹುದೇ?",
            iconName = "battery",
            phoneticGuide = "Yahan charging point hai?"
        ),
        // Transit
        CommunicationCard(
            id = "comm-bus-stop-04",
            category = CommCategory.TRANSIT,
            englishText = "Please tell me when the next bus or train arrives.",
            hindiText = "कृपया बताएं अगली बस या ट्रेन कब आएगी।",
            tamilText = "அடுத்த பேருந்து எப்போது வரும் என்று சொல்லுங்கள்.",
            teluguText = "తదుపరి బస్సు ఎప్పుడు వస్తుందో చెప్పండి.",
            kannadaText = "ಮುಂದಿನ ಬಸ್ ಯಾವಾಗ ಬರುತ್ತದೆ ಹೇಳಿ.",
            iconName = "bus",
            phoneticGuide = "Agli bus kab aayegi?"
        ),
        CommunicationCard(
            id = "comm-ramp-request-05",
            category = CommCategory.TRANSIT,
            englishText = "Please lower the wheelchair ramp or open the wide gate.",
            hindiText = "कृपया व्हीलचेयर रैंप खोलें या चौड़ा गेट खोलें।",
            tamilText = "தயவுசெய்து சக்கர நாற்காலி சாய்வுதளத்தை திறக்கவும்.",
            teluguText = "దయచేసి వీల్ చైర్ ర్యాంప్ తెరవండి.",
            kannadaText = "ದಯವಿಟ್ಟು ವೀಲ್‌ಚೇರ್ ರ‍್ಯಾಂಪ್ ತೆರೆಯಿರಿ.",
            iconName = "accessible",
            phoneticGuide = "Kripya ramp khol dein"
        ),
        CommunicationCard(
            id = "comm-ticket-06",
            category = CommCategory.TRANSIT,
            englishText = "I need one ticket with disability concession.",
            hindiText = "मुझे दिव्यांगजन रियायत वाला एक टिकट चाहिए।",
            tamilText = "மாற்றுத்திறனாளி சலுகை கொண்ட ஒரு டிக்கெட் வேண்டும்.",
            teluguText = "నాకు ఒక రాయితీ టికెట్ కావాలి.",
            kannadaText = "ನನಗೆ ಒಂದು ರಿಯಾಯಿತಿ ಟಿಕೆಟ್ ಬೇಕು.",
            iconName = "ticket",
            phoneticGuide = "Ek concession ticket chahiye"
        ),
        // Health
        CommunicationCard(
            id = "comm-medicine-07",
            category = CommCategory.HEALTH,
            englishText = "I need my regular medicine. Here is my prescription.",
            hindiText = "मुझे मेरी दवा चाहिए। यह मेरा पर्चा है।",
            tamilText = "எனக்கு எனது மருந்து வேண்டும். இது எனது மருந்து சீட்டு.",
            teluguText = "నాకు నా మందులు కావాలి. ఇది నా చీటీ.",
            kannadaText = "ನನಗೆ ನನ್ನ ಔಷಧ ಬೇಕು. ಇದು ನನ್ನ ಚೀಟಿ.",
            iconName = "medical",
            phoneticGuide = "Mujhe dawa chahiye"
        ),
        CommunicationCard(
            id = "comm-pain-08",
            category = CommCategory.HEALTH,
            englishText = "I am in pain or feeling dizzy. Please help me sit down.",
            hindiText = "मुझे दर्द या चक्कर आ रहा है। कृपया मुझे बैठने में मदद करें।",
            tamilText = "எனக்கு தலைச்சுற்றலாக உள்ளது. தயவுசெய்து அமர உதவுங்கள்.",
            teluguText = "నాకు కళ్లు తిరుగుతున్నాయి. దయచేసి కూర్చోవడానికి సహాయం చేయండి.",
            kannadaText = "ನನಗೆ ತಲೆತಿರುಗುತ್ತಿದೆ. ದಯವಿಟ್ಟು ಕುಳಿತುಕೊಳ್ಳಲು ಸಹಾಯ ಮಾಡಿ.",
            iconName = "heart",
            phoneticGuide = "Mujhe baithne mein madad karein"
        ),
        CommunicationCard(
            id = "comm-write-09",
            category = CommCategory.HEALTH,
            englishText = "I am deaf or hard of hearing. Please write down what you are saying.",
            hindiText = "मैं सुन नहीं सकता/सकती। कृपया अपनी बात लिखकर बताएं।",
            tamilText = "எனக்கு காது கேளாது. தயவுசெய்து எழுதி காட்டுங்கள்.",
            teluguText = "నేను వినలేను. దయచేసి రాసి చూపించండి.",
            kannadaText = "ನನಗೆ ಕೇಳಿಸುವುದಿಲ್ಲ. ದಯವಿಟ್ಟು ಬರೆದು ತೋರಿಸಿ.",
            iconName = "hearing",
            phoneticGuide = "Kripya likhkar batayein"
        ),
        // Emergency
        CommunicationCard(
            id = "comm-emergency-10",
            category = CommCategory.EMERGENCY,
            englishText = "I need urgent help! Please call my emergency contact.",
            hindiText = "मुझे तुरंत मदद चाहिए! कृपया मेरे आपातकालीन नंबर पर कॉल करें।",
            tamilText = "எனக்கு அவசர உதவி தேவை! எனது குடும்பத்திற்கு அழைக்கவும்.",
            teluguText = "నాకు అత్యవసర సహాయం కావాలి! దయచేసి కాల్ చేయండి.",
            kannadaText = "ನನಗೆ ತುರ್ತು ಸಹಾಯ ಬೇಕು! ದಯವಿಟ್ಟು ಕರೆ ಮಾಡಿ.",
            iconName = "alert",
            phoneticGuide = "Mujhe turant madad chahiye!"
        ),
        CommunicationCard(
            id = "comm-lost-11",
            category = CommCategory.EMERGENCY,
            englishText = "I am visually impaired and lost. Please guide me to the exit.",
            hindiText = "मुझे दिखाई नहीं देता और मैं रास्ता भटक गया/गई हूँ। कृपया निकास द्वार तक ले चलें।",
            tamilText = "எனக்கு பார்வை குறைபாடு உள்ளது. தயவுசெய்து வெளியேறும் வழியைக் காட்டுங்கள்.",
            teluguText = "నాకు కనిపించదు. దయచేసి నన్ను బయటకు తీసుకెళ్లండి.",
            kannadaText = "ನನಗೆ ಕಣ್ಣು ಕಾಣಿಸುವುದಿಲ್ಲ. ದಯವಿಟ್ಟು ಹೊರಗೆ ಹೋಗುವ ದಾರಿ ತೋರಿಸಿ.",
            iconName = "navigation",
            phoneticGuide = "Exit tak le chalein"
        )
    )

    val sampleReports = listOf(
        BarrierReport(
            id = "rep-001",
            title = "Broken Wheelchair Ramp at Connaught Place Metro Gate 6",
            category = BarrierCategory.BROKEN_RAMP,
            locationName = "Rajiv Chowk Metro Station",
            city = "Delhi",
            facilityId = "place-rajiv-chowk-03",
            specificLocationDetails = "Gate 6 sidewalk ramp has broken concrete edge and loose tiles blocking wheels",
            severity = BarrierSeverity.HIGH,
            status = BarrierStatus.BEING_CHECKED,
            dateReported = "09 Oct 2026",
            timestamp = 1760000000000L,
            statusNotes = "DMRC Maintenance Unit inspected site on 10 Oct. Repair crew scheduled for resurfacing by 14 Oct.",
            resolutionEstimate = "Expected resolution: 14 Oct 2026",
            hasConflictWithOfficialAudit = true
        ),
        BarrierReport(
            id = "rep-002",
            title = "Tactile Paving Obstructed by Vendor Carts",
            category = BarrierCategory.BLOCKED_TACTILE,
            locationName = "Majestic KSRTC Bus Terminal",
            city = "Bengaluru",
            facilityId = "place-majestic-01",
            specificLocationDetails = "Platform 3 main yellow walkway completely blocked by commercial kiosks",
            severity = BarrierSeverity.MEDIUM,
            status = BarrierStatus.WAITING_FOR_REVIEW,
            dateReported = "07 Oct 2026",
            timestamp = 1759800000000L,
            statusNotes = "Notice forwarded to KSRTC Terminal Manager. Joint clearance inspection pending.",
            resolutionEstimate = "Under review with terminal authorities"
        ),
        BarrierReport(
            id = "rep-003",
            title = "Elevator Display Screen and Voice Chime Restored",
            category = BarrierCategory.ELEVATOR_OUT,
            locationName = "Anna Centenary Library Entrance",
            city = "Chennai",
            facilityId = "place-anna-library-04",
            specificLocationDetails = "Ground floor lift audio floor announcement was silent",
            severity = BarrierSeverity.LOW,
            status = BarrierStatus.RESOLVED,
            dateReported = "02 Oct 2026",
            timestamp = 1759400000000L,
            statusNotes = "Speaker module replaced and bilingual Tamil/English floor chime tested and verified.",
            resolutionEstimate = "Resolved on 04 Oct 2026"
        ),
        BarrierReport(
            id = "rep-004",
            title = "Steep Curb Without Ramp at Hospital Casualty Gate",
            category = BarrierCategory.INACCESSIBLE_ENTRANCE,
            locationName = "AIIMS New Delhi OPD & Emergency",
            city = "Delhi",
            facilityId = "place-aiims-02",
            specificLocationDetails = "Emergency ambulance bay pedestrian crossover has 6-inch sharp curb drop",
            severity = BarrierSeverity.URGENT,
            status = BarrierStatus.RECEIVED,
            dateReported = "11 Oct 2026",
            timestamp = 1760200000000L,
            statusNotes = "Intake received through visitor mobile report. Queued for regional coordinator triage.",
            resolutionEstimate = "Triage in progress"
        ),
        BarrierReport(
            id = "rep-005",
            title = "Reported Broken Braille Signage on Floor 2",
            category = BarrierCategory.MISSING_SIGN_ASSIST,
            locationName = "Delhi Public Library Central Branch",
            city = "Delhi",
            facilityId = "place-delhi-publib-07",
            specificLocationDetails = "Signage reported missing on upper floor stacks",
            severity = BarrierSeverity.LOW,
            status = BarrierStatus.UNABLE_TO_VERIFY,
            dateReported = "28 Sep 2026",
            timestamp = 1759000000000L,
            statusNotes = "On-site audit confirmed Braille plates exist in Braille Reading Section, but 2nd floor remains closed for civil renovation. Condition could not be verified as reported.",
            resolutionEstimate = "Closed: Insufficient access for physical verification"
        )
    )

    val sampleStatusHistory = listOf(
        // rep-001 audit trail
        ReportStatusHistoryItem(
            id = "hist-001-1",
            reportId = "rep-001",
            fromStatus = null,
            toStatus = BarrierStatus.RECEIVED,
            changedBy = "System Intake",
            changedRole = "Automated Gateway",
            timestamp = "09 Oct 2026, 10:15 AM",
            reviewNote = "Report logged with location coordinates at Rajiv Chowk Gate 6.",
            actionTaken = "Intake Confirmation"
        ),
        ReportStatusHistoryItem(
            id = "hist-001-2",
            reportId = "rep-001",
            fromStatus = BarrierStatus.RECEIVED,
            toStatus = BarrierStatus.WAITING_FOR_REVIEW,
            changedBy = "Kavita Rao",
            changedRole = "Certified Access Auditor",
            timestamp = "09 Oct 2026, 02:40 PM",
            reviewNote = "Triage completed. Severity marked High due to broken concrete on primary step-free access route.",
            actionTaken = "Assigned to DMRC Civil Maintenance"
        ),
        ReportStatusHistoryItem(
            id = "hist-001-3",
            reportId = "rep-001",
            fromStatus = BarrierStatus.WAITING_FOR_REVIEW,
            toStatus = BarrierStatus.BEING_CHECKED,
            changedBy = "Vikram Sen",
            changedRole = "Public Works Engineer",
            timestamp = "10 Oct 2026, 11:30 AM",
            reviewNote = "Site inspection conducted. Concrete cracked across 1.2m ramp slab. Paving crew dispatched for repair.",
            actionTaken = "Physical Inspection Conducted"
        ),

        // rep-002 audit trail
        ReportStatusHistoryItem(
            id = "hist-002-1",
            reportId = "rep-002",
            fromStatus = null,
            toStatus = BarrierStatus.RECEIVED,
            changedBy = "System Intake",
            changedRole = "Automated Gateway",
            timestamp = "07 Oct 2026, 08:00 AM",
            reviewNote = "Visitor uploaded barrier report at Majestic KSRTC Platform 3.",
            actionTaken = "Intake Confirmation"
        ),
        ReportStatusHistoryItem(
            id = "hist-002-2",
            reportId = "rep-002",
            fromStatus = BarrierStatus.RECEIVED,
            toStatus = BarrierStatus.WAITING_FOR_REVIEW,
            changedBy = "Arun K.",
            changedRole = "Certified Access Auditor",
            timestamp = "07 Oct 2026, 12:15 PM",
            reviewNote = "Notice dispatched to KSRTC Terminal Superintendent for commercial vendor clearance.",
            actionTaken = "Official Notice Sent"
        ),

        // rep-003 audit trail
        ReportStatusHistoryItem(
            id = "hist-003-1",
            reportId = "rep-003",
            fromStatus = null,
            toStatus = BarrierStatus.RECEIVED,
            changedBy = "System Intake",
            changedRole = "Automated Gateway",
            timestamp = "02 Oct 2026, 09:20 AM",
            reviewNote = "Report logged for silent elevator announcements in Ground Floor Lift A.",
            actionTaken = "Intake Confirmation"
        ),
        ReportStatusHistoryItem(
            id = "hist-003-2",
            reportId = "rep-003",
            fromStatus = BarrierStatus.RECEIVED,
            toStatus = BarrierStatus.BEING_CHECKED,
            changedBy = "Senthil Nathan",
            changedRole = "Campus Accessibility Coordinator",
            timestamp = "03 Oct 2026, 11:00 AM",
            reviewNote = "Library technicians inspected control box and ordered replacement speaker module.",
            actionTaken = "Parts Replacement Ordered"
        ),
        ReportStatusHistoryItem(
            id = "hist-003-3",
            reportId = "rep-003",
            fromStatus = BarrierStatus.BEING_CHECKED,
            toStatus = BarrierStatus.RESOLVED,
            changedBy = "Senthil Nathan",
            changedRole = "Campus Accessibility Coordinator",
            timestamp = "04 Oct 2026, 04:30 PM",
            reviewNote = "Sound module replaced. Bilingual voice announcements verified working at 75 dB.",
            actionTaken = "Resolved & Verified"
        )
    )

    val sampleVerificationRecords = listOf(
        VerificationRecord(
            id = "ver-majestic-01",
            facilityId = "place-majestic-01",
            verifierName = "Dr. Manjunath Swamy",
            verifierRole = VerifierRole.CERTIFIED_AUDITOR,
            verificationDate = "15 Sep 2026",
            auditMethod = "On-site physical inspection with digital incline gauge & tactile route audit",
            evidenceNotes = "All 4 main concourse ramps compliant with 1:12 gradient. Tactile paving unbroken from Metro concourse to Platform 1. Restroom doors have 900mm clear width.",
            verifiedFeatures = listOf(
                "Step-free entrance",
                "Wheelchair ramp",
                "Lifts with Braille buttons",
                "Accessible toilet",
                "Tactile guiding pathway",
                "Audio announcements",
                "Visual display announcements"
            ),
            statusAssigned = VerificationStatus.VERIFIED,
            isOfficialAudit = true
        ),
        VerificationRecord(
            id = "ver-aiims-02",
            facilityId = "place-aiims-02",
            verifierName = "Rajesh Sharma",
            verifierRole = VerifierRole.FACILITY_ENGINEER,
            verificationDate = "10 Sep 2026",
            auditMethod = "Hospital infrastructure audit following Accessible India Campaign Guidelines",
            evidenceNotes = "Automated sliding doors, 2.1m wide elevator cabs, continuous tactile guidance paths in Main OPD block. Emergency department drop-off has dedicated wheelchair parking bays.",
            verifiedFeatures = listOf(
                "Step-free entrance",
                "Wheelchair ramp",
                "High capacity hospital lift",
                "Accessible toilet on each floor",
                "Tactile paving",
                "Braille signage",
                "Accessible parking",
                "Sign language support desk"
            ),
            statusAssigned = VerificationStatus.VERIFIED,
            isOfficialAudit = true
        ),
        VerificationRecord(
            id = "ver-anna-04",
            facilityId = "place-anna-library-04",
            verifierName = "Meenakshi Sundaram",
            verifierRole = VerifierRole.CAMPUS_COORDINATOR,
            verificationDate = "28 Aug 2026",
            auditMethod = "Public library audit with assistive technology cell assessment",
            evidenceNotes = "Main entrance ramp has dual-level handrails. Tactile floor guides lead directly to the Braille Library. Computer terminals equipped with NVDA & JAWS.",
            verifiedFeatures = listOf(
                "Step-free ramp",
                "Accessible lift",
                "Braille library section",
                "Tactile pathways",
                "Accessible toilet"
            ),
            statusAssigned = VerificationStatus.VERIFIED,
            isOfficialAudit = true
        )
    )
}
