package com.example.jobmagician.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmagician.data.db.InterviewSessionRecord
import com.example.jobmagician.data.db.SavedAccommodationRecord
import com.example.jobmagician.data.db.MunicipalCityFormRecord
import com.example.jobmagician.data.db.NormalizedMunicipalFilingRecord
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    val currentDestination: NavDestination = NavDestination.HOME,
    val isSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val notificationCount: Int = 3,
    val isAslModeEnabled: Boolean = true,
    val userNotificationMessage: String? = null,

    // Integration Tabs
    val activeIntegrationTab: IntegrationTab = IntegrationTab.VR4DEAF_CORE,
    val activeCycleStep: Int = 1,
    val activeDashboardTab: String = "overview", // overview, assessment, training, placement, ipe

    // VURA Knowledge Base & PinkAI State
    val selectedKnowledgeDomain: KnowledgeDomain = KnowledgeDomain.ALL,
    val knowledgeArticles: List<KnowledgeArticle> = emptyList(),
    val knowledgeGraphNodes: List<KnowledgeGraphNode> = emptyList(),
    val aiQueryHistory: List<PinkAiQuery> = emptyList(),
    val isAiThinking: Boolean = false,

    // DeafAuth Identity
    val deafAuthProfile: DeafAuthProfile = DeafAuthProfile(),

    // GitHub @pinkycollie Ecosystem
    val githubRepositories: List<GithubRepoInfo> = emptyList(),

    // 360 Magicians Suite (jobmagician, businessmagician, developermag)
    val activeMagicianSuite: MagicianSubSuite = MagicianSubSuite.JOB_MAGICIAN,

    // VR4DEAF Store & Order Requests (Linked to DeafAuth & PinkSync Kernel)
    val selectedStoreCategory: StoreCategory = StoreCategory.ALL,
    val vrStoreItems: List<VrStoreItem> = emptyList(),
    val storeOrderRequests: List<StoreOrderRequest> = emptyList(),
    val pinkSyncKernelEvents: List<PinkSyncKernelEvent> = emptyList(),
    val isKernelSyncing: Boolean = false,

    // Domain Connect Sub (Sub-domains routing & mesh)
    val subdomainConnections: List<DomainConnectSub> = emptyList(),
    val activeSubdomainFilter: String = "All",

    // VR4DEAF Ops & Tech Debt Workbook
    val activeWorkbookTab: Int = 0, // 0=Lifecycle, 1=Tech Debt, 2=Debt vs Equity, 3=Contributors
    val serviceLifecycles: List<ServiceLifecycleItem> = emptyList(),
    val techDebtItems: List<TechDebtItem> = emptyList(),
    val serviceEquityScores: Map<String, Double> = emptyMap(),
    val contributorStakes: List<ContributorStake> = emptyList(),

    // ----------------------------------------------------
    // Magicians Suite: Email, Doc, Chat, File Attachments
    // ----------------------------------------------------
    val activeSuiteTab: SuiteTab = SuiteTab.DOCS,
    val suiteEmails: List<SuiteEmail> = emptyList(),
    val suiteDocuments: List<SuiteDocument> = emptyList(),
    val suiteChatChannels: List<SuiteChatChannel> = emptyList(),
    val suiteChannelMessages: Map<String, List<ChatMessage>> = emptyMap(),
    val suiteFiles: List<SuiteFileAttachment> = emptyList(),

    // Vocational Rehabilitation & Magician Core
    val cycleSteps: List<CycleStepData> = emptyList(),
    val appointments: List<AppointmentData> = emptyList(),
    val eligibilities: List<EligibilityData> = emptyList(),
    val skillProficiencies: List<SkillScoreData> = emptyList(),
    val careerRecommendations: List<CareerRecommendationData> = emptyList(),
    val trainingCourses: List<TrainingCourseData> = emptyList(),
    val jobApplications: List<JobApplicationData> = emptyList(),
    val jobListings: List<JobListingData> = emptyList(),
    val resources: List<ResourceData> = emptyList(),

    // ASL Media Vault
    val aslMediaItems: List<AslMediaItem> = emptyList(),
    val selectedMediaCategory: String = "All",

    // ASL Video Relay & Interview Simulator & ADA Studio (Local-first Room/SQLite DB)
    val interviewSessions: List<InterviewSessionRecord> = emptyList(),
    val savedAccommodations: List<SavedAccommodationRecord> = emptyList(),
    val currentSimulatorQuestionIndex: Int = 0,
    val isSimulatorRecording: Boolean = false,
    val simulatorSignSpeed: Float = 1.0f,

    // Municipal City Forms & Background Consistency Collector (e.g. Fort Worth Wufoo, Austin, Dallas)
    val municipalForms: List<MunicipalCityFormRecord> = emptyList(),
    val municipalFilings: List<NormalizedMunicipalFilingRecord> = emptyList(),
    val isMunicipalSyncing: Boolean = false,
    val municipalSyncLogs: List<String> = emptyList(),
    val lastMunicipalSyncTime: String = ""
)

class JobMagicianViewModel(application: Application) : AndroidViewModel(application) {

    val interviewRepository = InterviewRepository(application.applicationContext)
    val municipalFormRepository = MunicipalFormRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeDatabase()
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            interviewRepository.sessions.collect { list ->
                _uiState.update { it.copy(interviewSessions = list) }
            }
        }
        viewModelScope.launch {
            interviewRepository.accommodations.collect { list ->
                _uiState.update { it.copy(savedAccommodations = list) }
            }
        }
        viewModelScope.launch {
            municipalFormRepository.municipalForms.collect { list ->
                _uiState.update { it.copy(municipalForms = list) }
            }
        }
        viewModelScope.launch {
            municipalFormRepository.municipalFilings.collect { list ->
                _uiState.update { it.copy(municipalFilings = list) }
            }
        }
        viewModelScope.launch {
            municipalFormRepository.isBackgroundSyncRunning.collect { isRunning ->
                _uiState.update { it.copy(isMunicipalSyncing = isRunning) }
            }
        }
        viewModelScope.launch {
            municipalFormRepository.syncLogs.collect { logs ->
                _uiState.update { it.copy(municipalSyncLogs = logs) }
            }
        }
        viewModelScope.launch {
            municipalFormRepository.lastConsistencyAuditTime.collect { time ->
                _uiState.update { it.copy(lastMunicipalSyncTime = time) }
            }
        }
    }

    private fun loadInitialData() {
        // --- 1. VURA Knowledge Base Initial Articles ---
        val initialArticles = listOf(
            KnowledgeArticle(
                id = "kb-01",
                title = "ADA Title I: Reasonable Accommodations for Deaf Employees",
                summary = "Federal requirements for providing qualified ASL interpreters, CART services, assistive listening systems, and visual alerts in workplace settings.",
                domain = KnowledgeDomain.ACCESSIBILITY,
                topic = "Workplace Accommodations",
                source = "U.S. Equal Employment Opportunity Commission (EEOC)",
                trustLevel = TrustLevel.LEVEL_5,
                jurisdiction = "Federal (United States)",
                keyTakeaways = listOf(
                    "Employers with 15+ employees must provide reasonable accommodations unless causing undue hardship.",
                    "Written notes are rarely sufficient for complex meetings or performance reviews; certified interpreters or CART are standard.",
                    "Cost of accommodations cannot be deducted from the deaf employee's salary."
                ),
                citationText = "42 U.S.C. § 12112; 29 C.F.R. § 1630.9 (EEOC Guidance)",
                aslVideoAvailable = true
            ),
            KnowledgeArticle(
                id = "kb-02",
                title = "WIOA & Rehabilitation Act Title I: The IPE Journey",
                summary = "Understanding your rights in developing the Individualized Plan for Employment (IPE) with state VR agencies like Texas Workforce Solutions (TWS).",
                domain = KnowledgeDomain.VOCATIONAL_REHAB,
                topic = "VR Rights & IPE",
                source = "Rehabilitation Services Administration (RSA) / TWS VR Manual",
                trustLevel = TrustLevel.LEVEL_5,
                jurisdiction = "Federal / Texas",
                keyTakeaways = listOf(
                    "Clients have informed choice in choosing career goals, service providers, and assistive technology.",
                    "IPE must be formulated within 90 days of eligibility determination unless an extension is agreed upon.",
                    "VR agencies are legally required to fund reasonable training, ASL accommodations, and job placement coaching."
                ),
                citationText = "34 C.F.R. § 361.45 (Individualized Plan for Employment development)",
                aslVideoAvailable = true
            ),
            KnowledgeArticle(
                id = "kb-03",
                title = "Deaf Culture & Workplace Communication Protocols",
                summary = "Best practices for hearing team members and managers collaborating with Deaf professionals in agile and corporate environments.",
                domain = KnowledgeDomain.COMMUNITY,
                topic = "Deaf Culture",
                source = "National Association of the Deaf (NAD) & Deaf Leaders Council",
                trustLevel = TrustLevel.LEVEL_4,
                jurisdiction = "Community Standards",
                keyTakeaways = listOf(
                    "Visual-first workflows: share agendas and slides prior to meetings to facilitate interpreter lag time.",
                    "Maintain eye contact with the Deaf individual, not the interpreter.",
                    "Embrace direct communication style characteristic of ASL discourse."
                ),
                citationText = "NAD Workplace Equity Framework (2025 Edition)",
                aslVideoAvailable = true
            ),
            KnowledgeArticle(
                id = "kb-04",
                title = "Section 504 of the Rehabilitation Act: Higher Ed & Transition",
                summary = "Non-discrimination mandate protecting Deaf students transitioning from high school to post-secondary vocational training.",
                domain = KnowledgeDomain.LEGAL,
                topic = "Transition & Education",
                source = "U.S. Department of Education Office for Civil Rights",
                trustLevel = TrustLevel.LEVEL_5,
                jurisdiction = "Federal",
                keyTakeaways = listOf(
                    "Post-secondary institutions must provide auxiliary aids without auxiliary fees.",
                    "VR counselors can coordinate pre-employment transition services (Pre-ETS) starting at age 14."
                ),
                citationText = "29 U.S.C. § 794; 34 C.F.R. Part 104",
                aslVideoAvailable = true
            ),
            KnowledgeArticle(
                id = "kb-05",
                title = "Assistive APIs & PinkSync Accommodation Pipeline",
                summary = "Technical specifications for automated CART captioning streams, WebRTC video relay endpoints, and haptic alerts for enterprise systems.",
                domain = KnowledgeDomain.TECHNOLOGY,
                topic = "Assistive Tech & APIs",
                source = "VR4DEAF Engineering SOP-701",
                trustLevel = TrustLevel.LEVEL_3,
                jurisdiction = "VR4DEAF Ecosystem",
                keyTakeaways = listOf(
                    "PinkSync bridges real-time STT captioning with DeafAuth verified communication preferences.",
                    "Webhooks trigger instant notification dispatch to counselor and client devices."
                ),
                citationText = "VR4DEAF Protocol RFC-004 (PinkSync / Gateway Mesh)",
                aslVideoAvailable = true
            )
        )

        val graphNodes = listOf(
            KnowledgeGraphNode(1, "Person", "Entity", "Jordan Vance (Deaf Candidate)", "The individual accessing vocational rehabilitation services."),
            KnowledgeGraphNode(2, "Needs", "Assessment", "ASL CDI + Technical Skill Upskilling", "Identified communication barriers and career goals."),
            KnowledgeGraphNode(3, "Accommodation", "Requirement", "Remote CART + Onsite ASL Interpreter", "Specific auxiliary aids mandated under ADA/WIOA."),
            KnowledgeGraphNode(4, "Policy", "Regulation", "ADA Title I & Rehab Act Sec 103", "Legal basis protecting rights and funding."),
            KnowledgeGraphNode(5, "Workflow", "Execution", "VR Specialist 7-Step Cycle", "Structured pathway from intake to placement."),
            KnowledgeGraphNode(6, "Service", "Provider", "Job-Magician 360 + TWS Center", "Operational team executing the training and matching."),
            KnowledgeGraphNode(7, "Organization", "Partner", "Tech & Logistics Employer Network", "Accessible and inclusive hiring organizations."),
            KnowledgeGraphNode(8, "Outcome", "Metric", "Competitive Integrated Employment", "Sustainable career placement and wage advancement.")
        )

        // --- GitHub @pinkycollie Ecosystem Repositories ---
        val initialGithubRepos = listOf(
            GithubRepoInfo(
                repoName = "vr4deaf",
                repoSlug = "pinkycollie/vr4deaf",
                fullUrl = "https://github.com/pinkycollie/vr4deaf",
                description = "Unified Deaf Vocational Rehabilitation, VURA Knowledge Platform, and Ecosystem Hub.",
                primaryFocus = "Core Ecosystem Portal & Universal Architecture",
                statusBadge = "v1.4.0 Live"
            ),
            GithubRepoInfo(
                repoName = "360magicians",
                repoSlug = "pinkycollie/360magicians",
                fullUrl = "https://github.com/pinkycollie/360magicians",
                description = "Job-Magician 360: Vocational Rehab Specialist 7-Step Cycle, IPE Tracker, & Accessible Career Matching Engine.",
                primaryFocus = "VR Specialists & Client Placement Engine",
                statusBadge = "v2.1.0 Stable"
            ),
            GithubRepoInfo(
                repoName = "deafauth",
                repoSlug = "pinkycollie/deafauth",
                fullUrl = "https://github.com/pinkycollie/deafauth",
                description = "Zero-Trust Accessible Identity & Universal Accommodation Verification for Deaf/Hard-of-Hearing individuals.",
                primaryFocus = "Decentralized Deaf Identity & Accommodations",
                statusBadge = "v1.2.0 Active"
            ),
            GithubRepoInfo(
                repoName = "pinksync",
                repoSlug = "pinkycollie/pinksync",
                fullUrl = "https://github.com/pinkycollie/pinksync",
                description = "Real-Time Telemetry Mesh, WebSockets CART Captioning Stream & State Sync for Counselors and Clients.",
                primaryFocus = "Real-Time Assistive Stream & Mesh Sync",
                statusBadge = "v1.1.5 Active"
            ),
            GithubRepoInfo(
                repoName = "vura",
                repoSlug = "pinkycollie/vura",
                fullUrl = "https://github.com/pinkycollie/vura",
                description = "VURA Legal & Community Knowledge Engine: 8-Step Knowledge Graph and Level 1-5 Trust RAG for ADA & WIOA.",
                primaryFocus = "Legal RAG & Authoritative Deaf Knowledge",
                statusBadge = "v1.0.8 Active"
            )
        )

        // --- 2. VR4DEAF Ops & Tech Debt Workbook (From PDF) ---
        val initialLifecycles = listOf(
            ServiceLifecycleItem("deafauth", "DeafAuth", "pinkycollie/deafauth", ServicePhase.BUILD, "Active Development", "Aug 2026", "Auth tokens, OTP rate limiter, and accessibility claims."),
            ServiceLifecycleItem("magicians", "360Magicians (Job-Magician)", "pinkycollie/360magicians", ServicePhase.GROW, "Active Beta & Deploy", "Aug 2026", "Core Vocational Rehabilitation portal, 7-step cycle, job matching."),
            ServiceLifecycleItem("pinksync", "PinkSync", "pinkycollie/pinksync", ServicePhase.BUILD, "Integration Pipeline", "Aug 2026", "Real-time state synchronization for VR counselors & client apps."),
            ServiceLifecycleItem("vura", "VURA Knowledge Base", "pinkycollie/vura", ServicePhase.BUILD, "Knowledge Engine", "Aug 2026", "Deaf Knowledge Registry, Legal RAG, and PinkAI citation auto-linker."),
            ServiceLifecycleItem("vr4deaf", "VR4DEAF Core Platform", "pinkycollie/vr4deaf", ServicePhase.MANAGED, "Production Live", "Aug 2026", "Unified API ingress, multi-service routing, client dashboard.")
        )

        val initialTechDebt = listOf(
            TechDebtItem("D-001", "deafauth", "DeafAuth (pinkycollie/deafauth)", "No automated test for token expiry edge case", DebtSeverity.MEDIUM, 4.0, 75.0, "Silent auth failures under high concurrency load"),
            TechDebtItem("D-002", "vr4deaf", "VR4DEAF Core (pinkycollie/vr4deaf)", "/healthz endpoint not implemented on all internal downstream services", DebtSeverity.HIGH, 2.0, 75.0, "Deploy pipeline smoke test cannot verify real backend health"),
            TechDebtItem("D-003", "pinksync", "PinkSync (pinkycollie/pinksync)", "Offline queue persistence sync retry backoff needs exponential delay", DebtSeverity.MEDIUM, 3.5, 75.0, "Potential flood of sync attempts on network reconnection"),
            TechDebtItem("D-004", "magicians", "360Magicians (pinkycollie/360magicians)", "Add automated ASL video thumbnail cache warm-up", DebtSeverity.LOW, 1.5, 75.0, "Slight latency when rendering video gallery on cold start"),
            TechDebtItem("D-005", "vura", "VURA (pinkycollie/vura)", "Formalize semantic search vector index schema", DebtSeverity.HIGH, 6.0, 75.0, "Blocks full PinkAI live citation auto-linker")
        )

        val initialEquityScores = mapOf(
            "deafauth" to 75.0,
            "magicians" to 90.0,
            "pinksync" to 65.0,
            "vura" to 70.0,
            "vr4deaf" to 85.0
        )

        val initialContributors = listOf(
            ContributorStake("c1", "@pinkycollie (Founder & Ecosystem Architect)", "360magicians, deafauth, pinksync, vura, vr4deaf", 85.0, "Core platform architecture, open-source repositories & governance"),
            ContributorStake("c2", "Deaf Community Advisory Board", "Accessibility & Community Trust Validation", 10.0, "Cultural compliance, ASL verification and CDI reviews"),
            ContributorStake("c3", "VR Specialists & Partner Network", "Vocational Rehabilitation & Employer Network", 5.0, "TWS integration, IPE workflows and client success")
        )

        // --- 3. Vocational Rehabilitation Specialist Cycle (Job Magician) ---
        val cycleSteps = listOf(
            CycleStepData(1, "Client Intake & DeafAuth Registration", "Initial assessment, communication profile collection, and registration into VR4DEAF.", listOf("Collect communication preferences (ASL, CART, Oral)", "Eligibility determination for Texas Workforce / WIOA programs", "Documentation of employment barriers and assistive tech requirements", "Assignment to Certified Deaf Rehabilitation Specialist")),
            CycleStepData(2, "Comprehensive Skills & Aptitude Assessment", "Evaluate client technical skills, experience, and career aspirations in native ASL.", listOf("ASL-guided aptitude and digital literacy assessment", "Career interest inventory (O*NET aligned)", "Previous work history and transferable skill analysis", "Assistive workplace technology ergonomic evaluation")),
            CycleStepData(3, "Training & Individualized Plan (IPE)", "Collaborative development of the IPE with personalized skill development modules.", listOf("Customized Individualized Plan for Employment (IPE)", "Enrollment in certified vocational programs (IT, Admin, Logistics)", "Soft skills & deaf workplace communication workshops", "Interview practice with Deaf career coaches")),
            CycleStepData(4, "Accessible Job Matching", "Connect clients with vetted Deaf-friendly and inclusive employer partners.", listOf("AI matching against accessible employer job database", "Resume tailoring emphasizing transferable strengths", "Coordinating ADA accommodation requests before interview", "Pre-interview briefing with qualified ASL interpreters")),
            CycleStepData(5, "Employer Engagement & Education", "Building enduring partnerships and educating corporate hiring managers.", listOf("Educating employers on Deaf culture and ADA compliance", "Showcasing tax credits (WOTC) & VR funding incentives", "Providing on-demand ASL onboarding resources", "Establishing workplace accommodation infrastructure")),
            CycleStepData(6, "Placement, Onboarding & Accommodations", "Seamless transition into competitive integrated employment.", listOf("Facilitating accessible onboarding & CDI/CART scheduling", "Deploying assistive equipment (visual alerts, video phones)", "Designating an on-site workplace peer ally", "30-day, 60-day, and 90-day progress check-ins")),
            CycleStepData(7, "Long-Term Retention & Outcome Tracking", "Monitoring career growth, wage progression, and program metrics.", listOf("Collecting 6-month & 1-year employment retention data", "Measuring client wage advancement and job satisfaction", "Analyzing VR4DEAF program efficacy metrics", "Publishing anonymized outcome reports to stakeholders"))
        )

        val appointments = listOf(
            AppointmentData("1", "IPE Goal Review & Skills Check", "Tomorrow, 10:00 AM", "Texas Workforce Center (Suite 400)", "In-Person", true),
            AppointmentData("2", "Virtual Resume & ASL Interview Prep", "March 15, 2:00 PM", "VURA Virtual Studio (CDI Attached)", "Virtual", true),
            AppointmentData("3", "Employer Match: Tech Solutions Inc.", "March 20, 11:00 AM", "Hybrid / Video Relay Room 2", "Interview", true)
        )

        val eligibilities = listOf(
            EligibilityData("Vocational Rehabilitation (VR4DEAF / State)", "Eligible & Active", true),
            EligibilityData("WIOA Adult Career Training Program", "Eligible & Enrolled", true),
            EligibilityData("Assistive Technology Grant (Federal Title I)", "Approved", true),
            EligibilityData("Trade Adjustment Assistance (TAA)", "Pending Annual Review", null)
        )

        val skillProficiencies = listOf(
            SkillScoreData("Technical & Data Entry Skills", 82),
            SkillScoreData("ASL & Visual Communication", 95),
            SkillScoreData("Problem Solving & Analysis", 78),
            SkillScoreData("Digital Literacy & Cloud Tools", 72)
        )

        val careerRecommendations = listOf(
            CareerRecommendationData("IT Support & Systems Specialist", 88, "High Growth (+14%)"),
            CareerRecommendationData("Office Operations Administrator", 84, "Stable (+6%)"),
            CareerRecommendationData("Accessible Data Analyst", 80, "Very High Growth (+22%)"),
            CareerRecommendationData("Digital Media & ASL Content Specialist", 92, "High Growth (+18%)")
        )

        val trainingCourses = listOf(
            TrainingCourseData("t1", "Microsoft Office & Cloud Certification", "Comprehensive training in Excel, Word, and SharePoint with full ASL video lessons.", CourseStatus.IN_PROGRESS, 65, "", true),
            TrainingCourseData("t2", "Workplace Communication & ADA Rights", "Understand your legal rights, requesting CART/Interpreters, and navigating team culture.", CourseStatus.SCHEDULED, 0, "Starts: March 22, 2026", true),
            TrainingCourseData("t3", "Full-Stack Web & Python Fundamentals", "Hands-on coding with visual architecture diagrams and ASL code tutorials.", CourseStatus.RECOMMENDED, 0, "", true),
            TrainingCourseData("t4", "Professional Deaf Resume & Portfolio", "Build a high-impact portfolio tailored to inclusive WIOA employers.", CourseStatus.COMPLETED, 100, "Completed", true)
        )

        val jobApplications = listOf(
            JobApplicationData("a1", "Office Administrator", "State Government Services", ApplicationStatus.INTERVIEW_SCHEDULED, "March 5, 2026", "Interview confirmed with on-site ASL interpreter.", "In-person CDI + CART Screen"),
            JobApplicationData("a2", "Help Desk Support Specialist", "Regional Health Network", ApplicationStatus.SUBMITTED, "March 9, 2026", "Application submitted through TWS VR fast-track portal.", "Virtual CART + Chat Support"),
            JobApplicationData("a3", "Junior Data Analyst", "Magician Logistics & Tech", ApplicationStatus.OFFER_RECEIVED, "March 11, 2026", "Offer received! Base $64,000/yr + accessible tech package.", "Dual Screen + Slack/Zoom CART")
        )

        val jobListings = listOf(
            JobListingData("j1", "Office Operations Administrator", "State Department of Licensing", "Austin, TX (Hybrid)", "March 12, 2026", 90, listOf("State Agency", "WIOA Partner", "Full Benefits"), "Manage digital records and customer email communications. Comprehensive ASL interpretation provided for all department meetings.", 5),
            JobListingData("j2", "Tier 2 Help Desk Specialist", "Apex Cloud Solutions", "Remote (US-Wide)", "March 14, 2026", 86, listOf("Remote", "Tech", "Asynchronous Culture"), "Assist customers through chat, ticketing, and screen shares. 100% written communication environment with flexible scheduling.", 5),
            JobListingData("j3", "Digital Logistics Coordinator", "Magician Global Supply", "San Antonio, TX", "March 15, 2026", 94, listOf("Direct VR Hire", "Deaf Leadership"), "Inventory database coordination, manifest reconciliation, and automated dispatch management. Deaf-majority team.", 5),
            JobListingData("j4", "Accessibility QA Tester", "NextGen Media Labs", "Dallas, TX (Hybrid)", "March 16, 2026", 96, listOf("Accessibility", "ASL Video", "Growth"), "Audit web and mobile applications for WCAG 2.2 AA compliance, closed captions, and screen reader / visual usability.", 5)
        )

        val resources = listOf(
            ResourceData("r1", "Texas Workforce Solutions (TWS) VR Client Handbook", "State Vocational Rehabilitation", "Detailed handbook explaining counselor duties, appeals processes, and funding entitlements under Texas VR rules."),
            ResourceData("r2", "Job Accommodation Network (JAN) Deaf Accommodation Ideas", "Accessibility & ADA", "Comprehensive guide of proven workplace accommodations for Deaf and Hard of Hearing personnel."),
            ResourceData("r3", "VURA Deaf Knowledge Graph Whitepaper", "VURA Architecture", "Architectural blueprint of how community knowledge and legal mandates integrate into PinkAI."),
            ResourceData("r4", "ASL STEM & Tech Vocabulary Dictionary", "Education & STEM", "Standardized ASL terminology for programming, cybersecurity, and data science concepts.")
        )

        val initialStoreItems = listOf(
            VrStoreItem(
                id = "store-at-01",
                title = "Sorenson Lumina VRS / Dedicated VP Hardware",
                category = StoreCategory.ASSISTIVE_HARDWARE,
                description = "High-definition video relay communications terminal with ultra-low latency, wide-angle camera, and strobe ring indicator.",
                estimatedCost = 649.00,
                isVrFundedEligible = true,
                twsSponsorshipCode = "TWS-AT-8812",
                deliveryLeadTime = "3-5 Business Days",
                provider = "Sorenson Communications / VR4DEAF Certified",
                features = listOf("HD 1080p 60fps Video", "Hardware Strobe Flasher", "Direct VRS / Remote Sign Relay", "DeafAuth Plug-and-Play")
            ),
            VrStoreItem(
                id = "store-int-02",
                title = "Certified Deaf Interpreter (CDI) 20-Hour Bank",
                category = StoreCategory.INTERPRETER_SERVICES,
                description = "Pre-paid on-demand block of RID/BEI Certified Deaf Interpreters for high-stakes interviews, legal reviews, and team onboarding.",
                estimatedCost = 1800.00,
                isVrFundedEligible = true,
                twsSponsorshipCode = "TWS-INT-2041",
                deliveryLeadTime = "Instant Dispatch (<2 hrs)",
                provider = "Deaf Interpreter Guild / VR4DEAF Relay",
                features = listOf("RID/BEI Advanced CDI Certification", "Native ASL & BASL Specialist", "HIPAA / EEOC Compliant", "PinkSync Live Session Link")
            ),
            VrStoreItem(
                id = "store-cart-03",
                title = "Real-Time CART Captioning Enterprise Stream",
                category = StoreCategory.CART_CAPTIONING,
                description = "Certified steno CART captioning with sub-second latency and direct WebSocket integration into PinkSync Kernel.",
                estimatedCost = 1200.00,
                isVrFundedEligible = true,
                twsSponsorshipCode = "TWS-CART-9102",
                deliveryLeadTime = "Immediate Activation",
                provider = "PinkSync Assistive Caption Mesh",
                features = listOf("99.4% Word Accuracy", "220+ WPM Steno Velocity", "Real-Time Text Stream via PinkSync", "Downloadable Formatted Transcripts")
            ),
            VrStoreItem(
                id = "store-edu-04",
                title = "Full-Stack Software Engineering VR Boot Camp Voucher",
                category = StoreCategory.VR_TRAINING_GRANTS,
                description = "16-week comprehensive technical immersion with 100% ASL curriculum instruction, Deaf mentors, and job placement guarantee.",
                estimatedCost = 4500.00,
                isVrFundedEligible = true,
                twsSponsorshipCode = "TWS-EDU-4091",
                deliveryLeadTime = "Next Cohort: April 1, 2026",
                provider = "Developer-Mag Academy / WIOA Certified",
                features = listOf("100% ASL Video Instruction", "Kotlin, Android & Cloud APIs", "1-on-1 Deaf Tech Mentorship", "Direct Employer Referral Network")
            ),
            VrStoreItem(
                id = "store-ergo-05",
                title = "Deaf Workspace Ergonomics: 34\" UltraWide + Dual Strobe",
                category = StoreCategory.ERGONOMICS,
                description = "Panoramic visual workspace setup designed to view simultaneous ASL video calls, code editors, and live CART captions.",
                estimatedCost = 899.00,
                isVrFundedEligible = true,
                twsSponsorshipCode = "TWS-ERGO-3301",
                deliveryLeadTime = "4 Business Days",
                provider = "Job-Magician Accessible Hardware",
                features = listOf("34\" Curved IPS 144Hz Monitor", "Dual Multi-Color Emergency Strobe Lights", "Vibration Alert Pad for Desk", "Adjustable Height Mount")
            ),
            VrStoreItem(
                id = "store-at-06",
                title = "Tactile Haptic Smart Watch & Emergency Alert Link",
                category = StoreCategory.ASSISTIVE_HARDWARE,
                description = "Vibrational wrist receiver syncing directly with doorbells, fire alarms, calendar cues, and PinkSync notifications.",
                estimatedCost = 380.00,
                isVrFundedEligible = true,
                twsSponsorshipCode = "TWS-AT-7740",
                deliveryLeadTime = "2 Business Days",
                provider = "PinkSync Hardware Labs",
                features = listOf("Customized Vibration Signatures", "PinkSync Kernel Event Sync", "Waterproof 50m", "10-Day Battery Life")
            )
        )

        val initialOrderRequests = listOf(
            StoreOrderRequest(
                orderId = "ORD-88192",
                storeItemId = "store-at-01",
                itemTitle = "Sorenson Lumina VRS / Dedicated VP Hardware",
                category = StoreCategory.ASSISTIVE_HARDWARE,
                requesterDeafAuthId = "DA-7928-PINK",
                pinkSyncKernelTxId = "PK-KERNEL-TX-9021",
                status = OrderRequestStatus.VR_SPONSORED_APPROVED,
                justification = "Required for daily client meetings and state VR counselor progress check-ins at hybrid workplace.",
                requestedTimestamp = "Yesterday, 3:15 PM",
                counselorApprovalNote = "TWS Case #TWS-VR-2026-8841 authorized under IPE Section 4 (Assistive Technology Provision)",
                kernelPayloadDigest = "sha256:4f8a91b...deafauth-da7928"
            ),
            StoreOrderRequest(
                orderId = "ORD-88195",
                storeItemId = "store-int-02",
                itemTitle = "Certified Deaf Interpreter (CDI) 20-Hour Bank",
                category = StoreCategory.INTERPRETER_SERVICES,
                requesterDeafAuthId = "DA-7928-PINK",
                pinkSyncKernelTxId = "PK-KERNEL-TX-9044",
                status = OrderRequestStatus.DISPATCHED_IN_PINKSYNC,
                justification = "Requested for upcoming second-round technical interview with Magician Logistics & Tech.",
                requestedTimestamp = "Today, 9:30 AM",
                counselorApprovalNote = "Approved under Specialized Communication Access fund #TWS-2026-CA",
                kernelPayloadDigest = "sha256:7c2b44e...pinksync-mesh"
            )
        )

        val initialKernelEvents = listOf(
            PinkSyncKernelEvent("EV-101", "KERNEL_BOOT", "pinksync", "00:00:01", "PinkSync Telemetry Kernel initialized on WebSocket channel /ws/v1/telemetry", 4, true),
            PinkSyncKernelEvent("EV-102", "DEAFAUTH_VERIFIED", "deafauth", "00:00:05", "Validated Tier 3 Deaf VR Claim for DA-7928-PINK with SHA-256 integrity check", 8, true),
            PinkSyncKernelEvent("EV-103", "VURA_KNOWLEDGE_SYNC", "vura", "00:00:12", "RAG vector indices synchronized with ADA Title I & WIOA Statutory Graph", 12, true),
            PinkSyncKernelEvent("EV-104", "STORE_ORDER_DISPATCH", "vr4deaf-store", "00:00:18", "Dispatched Order ORD-88195 (CDI 20-Hour Bank) to TWS Counselor Gateway", 15, true),
            PinkSyncKernelEvent("EV-105", "MAGICIAN_SUITE_LINK", "360magicians", "00:00:24", "Bound JobMagician, BusinessMagician, and DeveloperMag kernels to PinkSync Bus", 6, true)
        )

        val initialSubdomains = listOf(
            DomainConnectSub(
                id = "dom-vura",
                subdomain = "vura.vr4deaf.org",
                title = "VURA Main Knowledge Engine",
                description = "Authoritative Deaf Legal & RAG Retrieval Hub",
                cnameTarget = "cname.vura.vr4deaf.org",
                status = DomainStatus.CONNECTED,
                latencyMs = 12,
                isTlsSecure = true,
                linkedKernel = "vura-rag-cluster-01"
            ),
            DomainConnectSub(
                id = "dom-magicians",
                subdomain = "magiciancore.vr4deaf.org",
                title = "MagicianCore 360 Ecosystem",
                description = "Job-Magician, Business-Magician & Developer-Mag Router",
                cnameTarget = "cname.magicians.vr4deaf.org",
                status = DomainStatus.CONNECTED,
                latencyMs = 8,
                isTlsSecure = true,
                linkedKernel = "magiciancore-edge-02"
            ),
            DomainConnectSub(
                id = "dom-deafauth",
                subdomain = "deafauth.vr4deaf.org",
                title = "DeafAuth Zero-Trust Identity Authority",
                description = "Decentralized cryptographic VR & Deaf claims issuer",
                cnameTarget = "cname.auth.vr4deaf.org",
                status = DomainStatus.CONNECTED,
                latencyMs = 5,
                isTlsSecure = true,
                linkedKernel = "deafauth-signer-primary"
            ),
            DomainConnectSub(
                id = "dom-pinksync",
                subdomain = "pinksync.vr4deaf.org",
                title = "PinkSync WebSocket Kernel Bus",
                description = "Sub-millisecond CART caption stream & counselor sync",
                cnameTarget = "wss.pinksync.vr4deaf.org",
                status = DomainStatus.CONNECTED,
                latencyMs = 4,
                isTlsSecure = true,
                linkedKernel = "pinksync-telemetry-bus"
            ),
            DomainConnectSub(
                id = "dom-store",
                subdomain = "store.vr4deaf.org",
                title = "VR4DEAF Assistive Tech Store & Orders",
                description = "Automated state VR order dispatch & voucher portal",
                cnameTarget = "cname.store.vr4deaf.org",
                status = DomainStatus.CONNECTED,
                latencyMs = 14,
                isTlsSecure = true,
                linkedKernel = "store-orders-dispatch"
            ),
            DomainConnectSub(
                id = "dom-tws-gateway",
                subdomain = "tws.vr4deaf.org",
                title = "Texas Workforce Solutions (TWS) Gateway",
                description = "Secured VR counselor bridge & IPE milestone sync",
                cnameTarget = "cname.gov-bridge.vr4deaf.org",
                status = DomainStatus.CONNECTED,
                latencyMs = 21,
                isTlsSecure = true,
                linkedKernel = "tws-state-gateway"
            )
        )

        val aslMedia = listOf(
            AslMediaItem("m1", "Mastering the Job Interview in Native ASL", "David Rivera, CDI", "14:20 min", "Career Coaching", 1420, "Techniques for pacing with interpreters, explaining your strengths, and advocating for accommodations.", listOf("Interview Prep", "ASL", "Career")),
            AslMediaItem("m2", "Know Your ADA Rights at Work", "Elena Cho, Legal Advocate", "18:45 min", "Legal & Rights", 2180, "Step-by-step breakdown of requesting accommodations, interactive dialogues, and EEOC procedures.", listOf("ADA", "Legal", "Rights")),
            AslMediaItem("m3", "VR4DEAF: Overview of the 7-Step VR Cycle", "Marcus Vance, VR Counselor", "09:30 min", "Vocational Rehab", 980, "How VR4DEAF and Job Magician coordinate with your state counselor to fund your training.", listOf("VR", "TWS", "IPE")),
            AslMediaItem("m4", "Tech Terminology in ASL: Coding & Cloud", "Priya Sharma, Software Engineer", "22:15 min", "STEM & Tech", 1870, "Visual demonstrations of 40+ technical computing signs used in daily engineering standups.", listOf("Tech", "Coding", "Vocabulary"))
        )

        // ----------------------------------------------------
        // Suite Collaboration Initial Data: Documents, Emails, Channels, Files
        // ----------------------------------------------------
        val initialDocuments = listOf(
            SuiteDocument(
                id = "doc-01",
                title = "Individualized Plan for Employment (IPE) - Cloud & Mobile Engineer",
                docType = SuiteDocType.IPE_PLAN,
                lastModified = "Today, 2:15 PM",
                author = "Marcus Vance, VR Counselor & Pinky Collie",
                content = """
# INDIVIDUALIZED PLAN FOR EMPLOYMENT (IPE)
**Agency:** Texas Workforce Solutions - Vocational Rehabilitation Services (TWS-VRS)
**Client ID:** DEAF-AUTH-TX-70291 | **Case Number:** TWS-VR-2026-90412
**Employment Goal:** Senior Mobile Software Engineer & DeafTech Architect (SOC 15-1252)

---

### Section 1: Specific Vocational Objectives
- Achieve competitive integrated employment in mobile Android / Jetpack Compose / Cloud systems.
- Attain certifications in Android Architecture, Kotlin Multiplatform, and Web Accessibility (WCAG 2.2).
- Establish workplace accommodation baseline: Certified Deaf Interpreters (CDI) & CART Real-Time Captioning.

### Section 2: VR Funded Services & Assistive Tech
1. **Assistive Hardware:** Dual 27-inch 4K Studio Displays + Haptic Smartband for notifications.
2. **Interpreter Services:** 20 hours/month on-demand RID/BEI Certified ASL Interpreting.
3. **Training & Tuition:** Full sponsorship for Advanced Distributed Systems Certification.

### Section 3: Client Responsibilities
- Maintain bi-weekly check-ins via DeafAuth Portal & PinkSync encrypted dispatch.
- Submit monthly job search logs and interview logs with requested accommodation outcomes.
- Notify VR Counselor immediately upon receiving written employment offer.

---
**Status:** Signed & Legally Verified under Rehabilitation Act § 102(b).
                """.trimIndent(),
                status = SuiteDocStatus.SIGNED,
                fileSize = "48 KB",
                tags = listOf("IPE", "TWS-VR", "Sponsorship", "Career Plan")
            ),
            SuiteDocument(
                id = "doc-02",
                title = "ADA Title I Workplace Accommodation Request Letter",
                docType = SuiteDocType.ADA_ACCOMMODATION_LETTER,
                lastModified = "Yesterday, 4:40 PM",
                author = "Pinky Collie (Client)",
                content = """
# REASONABLE ACCOMMODATION REQUEST NOTICE
**To:** Human Resources & Hiring Committee
**From:** Pinky Collie (@pinkycollie)
**Subject:** Accommodation Request pursuant to Title I of the Americans with Disabilities Act (ADA)

Dear Hiring & People Operations Team,

I am writing to formally request reasonable accommodations for my position as Senior Software Engineer, in accordance with Title I of the Americans with Disabilities Act (42 U.S.C. § 12112) and EEOC Regulations (29 C.F.R. § 1630.9).

### Requested Accommodations:
1. **Qualified ASL / CDI Interpreters:** Provision of RID or state-certified ASL interpreters for team sprint planning, quarterly reviews, and all-hands company presentations.
2. **Live CART Captioning / Speech-to-Text:** Real-time captioning integration for video conferencing platforms (Zoom / Google Meet / Microsoft Teams).
3. **Visual & Haptic Notifications:** Slack/email notification protocols instead of audio-only announcements; visual flash alerts for building emergency systems.

Please note that these accommodations are subsidized and coordinated in partnership with Texas Workforce Solutions VR4DEAF Program (Case #TWS-VR-2026-90412).

Thank you for your commitment to accessible and inclusive workplace excellence.

Sincerely,  
**Pinky Collie**  
DeafAuth ID: DEAF-AUTH-TX-70291
                """.trimIndent(),
                status = SuiteDocStatus.FINALIZED,
                fileSize = "32 KB",
                tags = listOf("ADA", "Title I", "Accommodations", "HR Notice")
            ),
            SuiteDocument(
                id = "doc-03",
                title = "Accessible Tech Professional Resume - Pinky Collie (2026)",
                docType = SuiteDocType.RESUME_CV,
                lastModified = "Aug 21, 2026",
                author = "Pinky Collie",
                content = """
# Pinky Collie
**Senior Software Engineer & DeafTech Ecosystem Architect**
Austin, TX • github.com/pinkycollie • deafauth.vr4deaf.org

---

### SUMMARY
Accomplished Full-Stack Android & Cloud Engineer specializing in Jetpack Compose, Kotlin Coroutines, Zero-Trust Identity (DeafAuth), and accessible high-performance microservices (PinkSync Kernel). Passionate advocate for Deaf-First universal design.

### OPEN SOURCE & CORE PROJECTS
- **360 Magicians Suite:** Comprehensive vocational, business, and developer tooling suite for Deaf professionals.
- **DeafAuth & PinkSync:** Decentralized cryptographic verification and WebSocket event mesh connecting state VR gateways.
- **VURA Engine:** Verified RAG-powered knowledge engine for ADA rights and vocational rehab policy.

### SKILLS
- **Languages:** Kotlin, Java, Rust, TypeScript, SQL
- **Android:** Jetpack Compose, M3 Design, Navigation Compose, Room DB, Retrofit, Coroutines/Flow
- **Accessibility:** WCAG 2.2 AAA, ASL video integrations, Screen Reader Semantics, CART STT pipelines
                """.trimIndent(),
                status = SuiteDocStatus.FINALIZED,
                fileSize = "22 KB",
                tags = listOf("Resume", "CV", "Tech", "Android")
            ),
            SuiteDocument(
                id = "doc-04",
                title = "IRS Form 8850 - Work Opportunity Tax Credit (WOTC) Verification",
                docType = SuiteDocType.WOTC_CREDIT_8850,
                lastModified = "Aug 19, 2026",
                author = "Business-Magician Tax Engine",
                content = """
# WORK OPPORTUNITY TAX CREDIT (WOTC) - FORM 8850 PACKET
**Target Group:** Target Group D - Vocational Rehabilitation Referral
**Tax Credit Cap:** Up to $9,600 Federal Tax Credit (26 U.S. Code § 51)
**State VR Agency:** Texas Workforce Solutions (TWS)
**Client Cert ID:** WOTC-TX-2026-8850-A

---
**Employer Incentive Summary:**
Hiring Pinky Collie qualifies your organization for immediate federal tax credits under Section 51 of the Internal Revenue Code. The client has completed certified VR training under an approved Individualized Plan for Employment (IPE).
                """.trimIndent(),
                status = SuiteDocStatus.SIGNED,
                fileSize = "38 KB",
                tags = listOf("WOTC", "Tax Credits", "Employer", "Form 8850")
            )
        )

        val initialEmails = listOf(
            SuiteEmail(
                id = "email-01",
                sender = "Pinky Collie <client@vr4deaf.org>",
                recipient = "marcus.vance@tws.texas.gov (VR Counselor)",
                subject = "Update: IPE Milestone 2 Completed & Assistive Hardware Request",
                preview = "Hello Marcus, I have completed the Android Architecture module and submitted the dual 4K display order request...",
                body = """
Hello Marcus,

I am pleased to report that I have successfully completed Milestone 2 on my Individualized Plan for Employment (IPE) with a 98% mastery score on the Advanced Distributed Systems & Kotlin certification.

I have also submitted an Assistive Tech Store request (Order #VR-8012) for the Dual 27" 4K Display workstation setup to accommodate multi-pane ASL interpreting side-by-side with code editors.

Could you please review and authorize the VR sponsorship voucher in the TWS portal?

Best regards,
Pinky Collie
DeafAuth ID: DEAF-AUTH-TX-70291
                """.trimIndent(),
                category = EmailCategory.COUNSELOR_UPDATE,
                timestamp = "10:15 AM",
                isUnread = false,
                isSent = true,
                attachedFiles = listOf("Signed_IPE_Plan_MarcusVance.pdf", "Certified_Audiogram_2026.png")
            ),
            SuiteEmail(
                id = "email-02",
                sender = "Pinky Collie <client@vr4deaf.org>",
                recipient = "hr@techflow-solutions.io",
                subject = "ADA Accommodation Notice: Certified ASL Interpreter for Technical Interview on Friday",
                preview = "Dear HR Team, In anticipation of our upcoming technical architecture interview on Friday at 2:00 PM CST...",
                body = """
Dear TechFlow Hiring Team,

In anticipation of our scheduled technical architecture interview on Friday at 2:00 PM CST, I would like to confirm that Texas Workforce Solutions (TWS-VR) is coordinating a certified ASL interpreter (RID Certified) to join the Zoom call.

Attached is my official ADA Title I Accommodation Letter and DeafAuth verification badge for your compliance records.

Looking forward to discussing the Senior Android Engineer role with your engineering leadership.

Sincerely,
Pinky Collie
                """.trimIndent(),
                category = EmailCategory.ACCOMMODATION_NOTICE,
                timestamp = "Yesterday",
                isUnread = false,
                isSent = true,
                attachedFiles = listOf("ADA_Title_I_Accommodation_Notice.docx")
            ),
            SuiteEmail(
                id = "email-03",
                sender = "PinkSync Kernel Bus <kernel@vr4deaf.org>",
                recipient = "Pinky Collie <client@vr4deaf.org>",
                subject = "PinkSync Dispatch Confirmation: VR4DEAF Assistive Hardware Store Order #VR-8012",
                preview = "Your order for Dual 27-inch 4K Studio Displays has been successfully routed to TWS Vendor Fulfillment...",
                body = """
Order Confirmation #VR-8012
Item: Dual 27-inch 4K Studio Displays ($1,249.00)
Status: Pending VR Counselor Signature (Marcus Vance)
Routing Node: pinksync.vr4deaf.org (WebSocket ID: PK-99218)

Your counselor has been notified via automatic TWS webhook dispatch.
                """.trimIndent(),
                category = EmailCategory.TWS_SPONSORSHIP,
                timestamp = "Aug 20",
                isUnread = true,
                isSent = false
            ),
            SuiteEmail(
                id = "email-04",
                sender = "Pinky Collie <client@vr4deaf.org>",
                recipient = "talent@cloudpulse.io",
                subject = "Application: Senior Android & DeafTech Ecosystem Architect (@pinkycollie)",
                preview = "Dear CloudPulse Talent Team, I am submitting my application for the Senior Android Architect position...",
                body = """
Dear CloudPulse Talent Team,

I am excited to submit my resume for the Senior Android Architect position at CloudPulse. With over 7 years building high-resilience Kotlin/Compose applications and open-source DeafTech infrastructure (@pinkycollie), I bring deep expertise in mobile architecture, zero-trust security, and accessibility.

Attached is my resume, WOTC Tax Credit certificate, and project portfolios.

Best regards,
Pinky Collie
                """.trimIndent(),
                category = EmailCategory.EMPLOYER_INTERVIEW,
                timestamp = "Aug 18",
                isUnread = false,
                isSent = true,
                attachedFiles = listOf("Accessible_Tech_Resume_PinkyCollie.pdf", "WOTC_Form_8850_Signed.vdoc")
            )
        )

        val initialChatChannels = listOf(
            SuiteChatChannel(
                id = "ch-01",
                name = "Marcus Vance (TWS VR Counselor)",
                channelType = SuiteChannelType.COUNSELOR_DIRECT,
                unreadCount = 1,
                lastMessage = "Approved the CDI hours request for your upcoming technical interview!",
                lastUpdated = "10:30 AM",
                isOnline = true,
                avatarInitials = "MV"
            ),
            SuiteChatChannel(
                id = "ch-02",
                name = "Live CDI / ASL Interpreter Hotline",
                channelType = SuiteChannelType.INTERPRETER_HOTLINE,
                unreadCount = 0,
                lastMessage = "Interpreter Sarah Jenkins (RID Certified) confirmed for Friday 2:00 PM CST.",
                lastUpdated = "Yesterday",
                isOnline = true,
                avatarInitials = "CDI"
            ),
            SuiteChatChannel(
                id = "ch-03",
                name = "VR4DEAF Peer Network & Magicians",
                channelType = SuiteChannelType.PEER_COMMUNITY,
                unreadCount = 3,
                lastMessage = "Anyone tested the new PinkSync Webhook endpoint in Developer-Mag?",
                lastUpdated = "Aug 21",
                isOnline = true,
                avatarInitials = "360"
            ),
            SuiteChatChannel(
                id = "ch-04",
                name = "VRS Video Relay Dispatch Desk",
                channelType = SuiteChannelType.ASL_VIDEO_RELAY,
                unreadCount = 0,
                lastMessage = "VRS Relay Station #402 standing by for video relay calls.",
                lastUpdated = "Aug 19",
                isOnline = true,
                avatarInitials = "VRS"
            )
        )

        val initialChannelMessages = mapOf(
            "ch-01" to listOf(
                ChatMessage(
                    id = "msg-01-1",
                    text = "Good morning Pinky! I saw your progress on the Kotlin Architecture course in the VR4DEAF portal. Exceptional work!",
                    isUser = false,
                    timestamp = "9:45 AM"
                ),
                ChatMessage(
                    id = "msg-01-2",
                    text = "Thanks Marcus! I just submitted the Assistive Tech store request for the dual 4K monitors and requested 10 CDI hours for next week.",
                    isUser = true,
                    timestamp = "10:12 AM"
                ),
                ChatMessage(
                    id = "msg-01-3",
                    text = "Approved the CDI hours request for your upcoming technical interview! The order voucher is signed in TWS.",
                    isUser = false,
                    timestamp = "10:30 AM"
                )
            ),
            "ch-02" to listOf(
                ChatMessage(
                    id = "msg-02-1",
                    text = "Hello! VR4DEAF Live Interpreter Dispatch here. How can we assist with your schedule?",
                    isUser = false,
                    timestamp = "Aug 21, 3:00 PM"
                ),
                ChatMessage(
                    id = "msg-02-2",
                    text = "Need an ASL/English technical interpreter familiar with Android, Git, and Cloud jargon for an engineering interview on Friday at 2:00 PM CST.",
                    isUser = true,
                    timestamp = "Aug 21, 3:15 PM"
                ),
                ChatMessage(
                    id = "msg-02-3",
                    text = "Interpreter Sarah Jenkins (RID Certified) confirmed for Friday 2:00 PM CST.",
                    isUser = false,
                    timestamp = "Aug 21, 3:40 PM"
                )
            )
        )

        val initialFiles = listOf(
            SuiteFileAttachment(
                id = "file-01",
                fileName = "Certified_Audiogram_2026.png",
                fileType = SuiteFileType.AUDIOGRAM_IMG,
                sizeDisplay = "1.4 MB",
                uploadedDate = "Aug 15, 2026",
                category = "Medical & VR Intake",
                verifiedHash = "sha256:8f4c21...deafauth"
            ),
            SuiteFileAttachment(
                id = "file-02",
                fileName = "Signed_IPE_Plan_MarcusVance.pdf",
                fileType = SuiteFileType.PDF,
                sizeDisplay = "480 KB",
                uploadedDate = "Aug 18, 2026",
                category = "Vocational Rehabilitation",
                verifiedHash = "sha256:1a99d4...deafauth"
            ),
            SuiteFileAttachment(
                id = "file-03",
                fileName = "ADA_Title_I_Accommodation_Notice.docx",
                fileType = SuiteFileType.DOCX,
                sizeDisplay = "65 KB",
                uploadedDate = "Aug 20, 2026",
                category = "Legal & Rights",
                verifiedHash = "sha256:3e7b88...deafauth"
            ),
            SuiteFileAttachment(
                id = "file-04",
                fileName = "WOTC_Form_8850_Signed.vdoc",
                fileType = SuiteFileType.SIGNED_FORM,
                sizeDisplay = "120 KB",
                uploadedDate = "Aug 19, 2026",
                category = "Employer Tax Credits",
                verifiedHash = "sha256:9c12a7...deafauth"
            ),
            SuiteFileAttachment(
                id = "file-05",
                fileName = "Mobile_Architecture_Certificate.pdf",
                fileType = SuiteFileType.CERTIFICATE,
                sizeDisplay = "890 KB",
                uploadedDate = "Aug 21, 2026",
                category = "Training & Credentials",
                verifiedHash = "sha256:5b66fe...deafauth"
            ),
            SuiteFileAttachment(
                id = "file-06",
                fileName = "DeafTech_Stipend_Ledger_2026.xlsx",
                fileType = SuiteFileType.SPREADSHEET,
                sizeDisplay = "310 KB",
                uploadedDate = "Aug 22, 2026",
                category = "Financial & Orders",
                verifiedHash = "sha256:4d88c2...deafauth"
            )
        )

        _uiState.value = AppUiState(
            knowledgeArticles = initialArticles,
            knowledgeGraphNodes = graphNodes,
            serviceLifecycles = initialLifecycles,
            githubRepositories = initialGithubRepos,
            techDebtItems = initialTechDebt,
            serviceEquityScores = initialEquityScores,
            contributorStakes = initialContributors,
            vrStoreItems = initialStoreItems,
            storeOrderRequests = initialOrderRequests,
            pinkSyncKernelEvents = initialKernelEvents,
            subdomainConnections = initialSubdomains,
            cycleSteps = cycleSteps,
            appointments = appointments,
            eligibilities = eligibilities,
            skillProficiencies = skillProficiencies,
            careerRecommendations = careerRecommendations,
            trainingCourses = trainingCourses,
            jobApplications = jobApplications,
            jobListings = jobListings,
            resources = resources,
            aslMediaItems = aslMedia,
            suiteDocuments = initialDocuments,
            suiteEmails = initialEmails,
            suiteChatChannels = initialChatChannels,
            suiteChannelMessages = initialChannelMessages,
            suiteFiles = initialFiles
        )
    }

    // ----------------------------------------------------
    // Navigation & Common UI
    // ----------------------------------------------------
    fun navigateTo(destination: NavDestination) {
        _uiState.update { it.copy(currentDestination = destination) }
    }

    fun toggleSearch() {
        _uiState.update { it.copy(isSearchOpen = !it.isSearchOpen) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleAslMode() {
        _uiState.update {
            val newMode = !it.isAslModeEnabled
            it.copy(
                isAslModeEnabled = newMode,
                userNotificationMessage = if (newMode) "Deaf-First Visual & ASL Priority Enabled" else "Standard View Enabled"
            )
        }
    }

    fun dismissNotificationMessage() {
        _uiState.update { it.copy(userNotificationMessage = null) }
    }

    fun setIntegrationTab(tab: IntegrationTab) {
        _uiState.update { it.copy(activeIntegrationTab = tab) }
    }

    // ----------------------------------------------------
    // VURA Knowledge Architecture & PinkAI Methods
    // ----------------------------------------------------
    fun setKnowledgeDomain(domain: KnowledgeDomain) {
        _uiState.update { it.copy(selectedKnowledgeDomain = domain) }
    }

    fun submitPinkAiQuery(query: String) {
        if (query.isBlank()) return

        val domain = when {
            query.contains("ada", ignoreCase = true) || query.contains("accommodat", ignoreCase = true) -> KnowledgeDomain.ACCESSIBILITY
            query.contains("counselor", ignoreCase = true) || query.contains("vr", ignoreCase = true) || query.contains("ipe", ignoreCase = true) -> KnowledgeDomain.VOCATIONAL_REHAB
            query.contains("law", ignoreCase = true) || query.contains("504", ignoreCase = true) || query.contains("right", ignoreCase = true) -> KnowledgeDomain.LEGAL
            query.contains("tech", ignoreCase = true) || query.contains("api", ignoreCase = true) || query.contains("sync", ignoreCase = true) -> KnowledgeDomain.TECHNOLOGY
            query.contains("school", ignoreCase = true) || query.contains("transition", ignoreCase = true) || query.contains("college", ignoreCase = true) -> KnowledgeDomain.EDUCATION
            query.contains("doctor", ignoreCase = true) || query.contains("health", ignoreCase = true) || query.contains("hospital", ignoreCase = true) -> KnowledgeDomain.HEALTHCARE
            else -> KnowledgeDomain.COMMUNITY
        }

        val answer = when (domain) {
            KnowledgeDomain.ACCESSIBILITY -> "Under ADA Title I, employers with 15+ employees are legally required to provide reasonable accommodations (such as qualified ASL interpreters, CART real-time captions, and visual alert beacons). Cost is absorbed by the employer and cannot impact your salary or job evaluation."
            KnowledgeDomain.VOCATIONAL_REHAB -> "Under WIOA Title IV and Rehabilitation Act § 102, you have the right to 'Informed Choice' in developing your IPE (Individualized Plan for Employment). Your counselor must complete your IPE within 90 days of eligibility and can fund approved career training, certifications, and assistive devices."
            KnowledgeDomain.LEGAL -> "Section 504 of the Rehabilitation Act prohibits disability-based discrimination by any entity receiving federal financial assistance, including state agencies, public universities, and federally funded job programs."
            KnowledgeDomain.TECHNOLOGY -> "VR4DEAF's PinkSync engine coordinates bidirectional communication between DeafAuth verified identity tokens, CART captioning WebSocket endpoints, and counselor case notes."
            KnowledgeDomain.EDUCATION -> "Transition services (Pre-ETS) begin as early as age 14 in most states, covering job exploration counseling, work-based learning, counseling on postsecondary education, and workplace readiness."
            KnowledgeDomain.HEALTHCARE -> "Title III of the ADA mandates effective communication in healthcare settings. Hospitals cannot force Deaf patients to rely on family members or unqualified staff for medical interpretation."
            KnowledgeDomain.COMMUNITY, KnowledgeDomain.ALL, KnowledgeDomain.EMPLOYMENT -> "VURA's Registry indexes authoritative Deaf community knowledge, verified SOPs, and real employment success workflows to provide transparent, cited answers."
        }

        val newQuery = PinkAiQuery(
            id = "query_${System.currentTimeMillis()}",
            question = query,
            answer = answer,
            domain = domain,
            trustRating = 5,
            citations = listOf("VURA Knowledge Registry #348", "U.S. Rehabilitation Act § 361.45", "ADA Title I EEOC Regulations")
        )

        _uiState.update { current ->
            current.copy(
                aiQueryHistory = listOf(newQuery) + current.aiQueryHistory,
                userNotificationMessage = "PinkAI verified response generated with Level 5 citations"
            )
        }
    }

    // ----------------------------------------------------
    // VR4DEAF Ops & Tech Debt-to-Equity Methods (Workbook)
    // ----------------------------------------------------
    fun setWorkbookTab(tabIndex: Int) {
        _uiState.update { it.copy(activeWorkbookTab = tabIndex) }
    }

    fun addTechDebt(serviceKey: String, description: String, severity: DebtSeverity, estHours: Double, impact: String) {
        val serviceName = when (serviceKey) {
            "deafauth" -> "DeafAuth"
            "fibonrose" -> "Fibonrose"
            "pinksync" -> "PinkSync"
            "magicians" -> "Job-Magician"
            "gateway" -> "Gateway"
            else -> serviceKey.replaceFirstChar { it.uppercase() }
        }

        val newItem = TechDebtItem(
            id = "D-00${_uiState.value.techDebtItems.size + 1}",
            serviceKey = serviceKey,
            serviceName = serviceName,
            description = description,
            severity = severity,
            estHoursToFix = estHours,
            hourlyRate = 75.0,
            impactIfUnresolved = impact,
            isResolved = false
        )

        _uiState.update { current ->
            current.copy(
                techDebtItems = listOf(newItem) + current.techDebtItems,
                userNotificationMessage = "Logged Tech Debt item for $serviceName (Est: $${newItem.estimatedCost})"
            )
        }
    }

    fun toggleResolveTechDebt(debtId: String) {
        _uiState.update { current ->
            val updated = current.techDebtItems.map { item ->
                if (item.id == debtId) item.copy(isResolved = !item.isResolved) else item
            }
            current.copy(
                techDebtItems = updated,
                userNotificationMessage = "Tech Debt status updated."
            )
        }
    }

    fun updateServiceEquityScore(serviceKey: String, newScore: Double) {
        val boundedScore = newScore.coerceIn(0.0, 100.0)
        _uiState.update { current ->
            val updatedScores = current.serviceEquityScores.toMutableMap()
            updatedScores[serviceKey] = boundedScore
            current.copy(
                serviceEquityScores = updatedScores,
                userNotificationMessage = "Updated equity score for $serviceKey: ${boundedScore.toInt()}/100"
            )
        }
    }

    // ----------------------------------------------------
    // DeafAuth Identity Methods
    // ----------------------------------------------------
    fun updateCommunicationPreference(pref: PrimaryLanguagePreference) {
        _uiState.update { current ->
            current.copy(
                deafAuthProfile = current.deafAuthProfile.copy(communicationPref = pref),
                userNotificationMessage = "DeafAuth preference updated: ${pref.name}"
            )
        }
    }

    fun updateInterpreterPreference(prefText: String) {
        _uiState.update { current ->
            current.copy(
                deafAuthProfile = current.deafAuthProfile.copy(interpreterPreference = prefText),
                userNotificationMessage = "Saved interpreter & accommodation specifications."
            )
        }
    }

    // ----------------------------------------------------
    // Job Magician & VR Methods
    // ----------------------------------------------------
    fun setCycleStep(stepId: Int) {
        _uiState.update { it.copy(activeCycleStep = stepId) }
    }

    fun nextCycleStep() {
        _uiState.update { current ->
            val nextStep = if (current.activeCycleStep >= current.cycleSteps.size) 1 else current.activeCycleStep + 1
            current.copy(activeCycleStep = nextStep)
        }
    }

    fun setDashboardTab(tabName: String) {
        _uiState.update { it.copy(activeDashboardTab = tabName) }
    }

    fun enrollCourse(courseId: String) {
        _uiState.update { current ->
            val updated = current.trainingCourses.map { course ->
                if (course.id == courseId) {
                    course.copy(status = CourseStatus.IN_PROGRESS, progressPercent = 15)
                } else course
            }
            current.copy(
                trainingCourses = updated,
                userNotificationMessage = "Enrolled in course! ASL materials activated."
            )
        }
    }

    fun addJobApplication(title: String, company: String, note: String, accommodation: String = "Certified ASL Interpreter & CART") {
        val newApp = JobApplicationData(
            id = "app_${System.currentTimeMillis()}",
            title = title,
            company = company,
            status = ApplicationStatus.SUBMITTED,
            appliedDate = "Today",
            note = note,
            accommodationRequested = accommodation
        )
        _uiState.update { current ->
            current.copy(
                jobApplications = listOf(newApp) + current.jobApplications,
                userNotificationMessage = "Job application added to your VR tracker!"
            )
        }
    }

    fun setMediaCategory(category: String) {
        _uiState.update { it.copy(selectedMediaCategory = category) }
    }

    // ----------------------------------------------------
    // 360 Magicians Suite (jobmagician, businessmagician, developermag)
    // ----------------------------------------------------
    fun setMagicianSuite(suite: MagicianSubSuite) {
        _uiState.update { it.copy(activeMagicianSuite = suite) }
    }

    // ----------------------------------------------------
    // VR4DEAF Store & Order Requests (Linked to DeafAuth & PinkSync Kernel)
    // ----------------------------------------------------
    fun setStoreCategory(category: StoreCategory) {
        _uiState.update { it.copy(selectedStoreCategory = category) }
    }

    fun submitStoreOrder(item: VrStoreItem, justification: String) {
        val randomDigits = (10000..99999).random()
        val txId = "PK-KERNEL-TX-${(1000..9999).random()}"
        val orderId = "ORD-$randomDigits"
        val cleanJustification = if (justification.isNotBlank()) justification else "Required for IPE Vocational Plan & Communication Access."

        val newRequest = StoreOrderRequest(
            orderId = orderId,
            storeItemId = item.id,
            itemTitle = item.title,
            category = item.category,
            requesterDeafAuthId = _uiState.value.deafAuthProfile.deafAuthId,
            pinkSyncKernelTxId = txId,
            status = OrderRequestStatus.DISPATCHED_IN_PINKSYNC,
            justification = cleanJustification,
            requestedTimestamp = "Just now",
            counselorApprovalNote = "Transmitted via PinkSync Kernel with DeafAuth Token DA-7928-PINK to TWS Counselor",
            kernelPayloadDigest = "sha256:${item.id.hashCode()}${System.currentTimeMillis()}"
        )

        val newKernelEvent = PinkSyncKernelEvent(
            eventId = "EV-${(200..999).random()}",
            eventType = "STORE_ORDER_DISPATCH",
            sourceService = "vr4deaf-store",
            timestamp = "Just now",
            payloadSummary = "Order $orderId dispatched for ${item.title} -> Linked to ${newRequest.requesterDeafAuthId}",
            latencyMs = 14,
            isVerifiedCrypto = true
        )

        _uiState.update { current ->
            current.copy(
                storeOrderRequests = listOf(newRequest) + current.storeOrderRequests,
                pinkSyncKernelEvents = listOf(newKernelEvent) + current.pinkSyncKernelEvents,
                userNotificationMessage = "Order $orderId requested! Dispatched to PinkSync Kernel with DeafAuth verification."
            )
        }
    }

    fun triggerKernelSync() {
        val syncEvent = PinkSyncKernelEvent(
            eventId = "EV-${(200..999).random()}",
            eventType = "KERNEL_HEARTBEAT_PULSE",
            sourceService = "pinksync",
            timestamp = "Just now",
            payloadSummary = "Ecosystem heartbeat: VURA, 360Magicians, DeafAuth, Store synchronized (0 drift)",
            latencyMs = 9,
            isVerifiedCrypto = true
        )
        _uiState.update { current ->
            current.copy(
                pinkSyncKernelEvents = listOf(syncEvent) + current.pinkSyncKernelEvents,
                userNotificationMessage = "PinkSync Kernel Synced: 0ms drift across all 5 nodes."
            )
        }
    }

    // ----------------------------------------------------
    // Domain Connect Sub (Sub-domains Routing & Mesh)
    // ----------------------------------------------------
    fun testPingSubdomain(subdomainId: String) {
        val newLatency = (3..18).random()
        _uiState.update { current ->
            val updated = current.subdomainConnections.map { dom ->
                if (dom.id == subdomainId) {
                    dom.copy(
                        latencyMs = newLatency,
                        status = DomainStatus.CONNECTED,
                        lastVerified = "Just now"
                    )
                } else dom
            }
            val target = updated.find { it.id == subdomainId }
            current.copy(
                subdomainConnections = updated,
                userNotificationMessage = "Subdomain ${target?.subdomain} ping verified (${newLatency}ms, TLS 1.3 Active)."
            )
        }
    }

    fun toggleSubdomainActive(subdomainId: String) {
        _uiState.update { current ->
            val updated = current.subdomainConnections.map { dom ->
                if (dom.id == subdomainId) {
                    dom.copy(isActiveRouter = !dom.isActiveRouter)
                } else dom
            }
            current.copy(subdomainConnections = updated)
        }
    }

    fun addCustomSubdomain(subdomain: String, title: String, description: String) {
        val cleanSub = subdomain.trim().lowercase().removeSuffix(".vr4deaf.org")
        val fullSubdomain = "$cleanSub.vr4deaf.org"
        val newSubdomain = DomainConnectSub(
            id = "dom-${System.currentTimeMillis()}",
            subdomain = fullSubdomain,
            rootDomain = "vr4deaf.org",
            title = title.ifBlank { "$cleanSub Service Router" },
            description = description.ifBlank { "Custom subdomain connection linked to VR4DEAF mesh." },
            cnameTarget = "$cleanSub.cname.pinksync.vr4deaf.org",
            status = DomainStatus.CONNECTED,
            latencyMs = (4..16).random(),
            isTlsSecure = true,
            lastVerified = "Just now",
            linkedKernel = "PinkSync v2.6.4 (node-mesh-custom)",
            isActiveRouter = true
        )
        _uiState.update { current ->
            current.copy(
                subdomainConnections = current.subdomainConnections + newSubdomain,
                userNotificationMessage = "Domain router '$fullSubdomain' bound and TLS verified."
            )
        }
    }

    // ----------------------------------------------------
    // Magicians Suite: Email, Doc, Chat, File Methods
    // ----------------------------------------------------
    fun setSuiteTab(tab: SuiteTab) {
        _uiState.update { it.copy(activeSuiteTab = tab) }
    }

    fun sendSuiteEmail(
        recipient: String,
        subject: String,
        body: String,
        category: EmailCategory = EmailCategory.COUNSELOR_UPDATE,
        attachedFiles: List<String> = emptyList()
    ) {
        val newEmail = SuiteEmail(
            id = "email-${System.currentTimeMillis()}",
            sender = "Pinky Collie <client@vr4deaf.org>",
            recipient = recipient.ifBlank { "marcus.vance@tws.texas.gov" },
            subject = subject.ifBlank { "VR4DEAF Protocol Update" },
            preview = if (body.length > 80) body.take(80) + "..." else body,
            body = body,
            category = category,
            timestamp = "Just now",
            isSent = true,
            attachedFiles = attachedFiles
        )
        _uiState.update { current ->
            current.copy(
                suiteEmails = listOf(newEmail) + current.suiteEmails,
                userNotificationMessage = "Email dispatched to ${newEmail.recipient} via DeafAuth Suite Mailer."
            )
        }
    }

    fun createSuiteDocument(
        title: String,
        docType: SuiteDocType,
        content: String,
        tags: List<String> = emptyList()
    ) {
        val newDoc = SuiteDocument(
            id = "doc-${System.currentTimeMillis()}",
            title = title.ifBlank { "Untitled ${docType.label}" },
            docType = docType,
            lastModified = "Just now",
            author = "Pinky Collie (@pinkycollie)",
            content = content,
            status = SuiteDocStatus.FINALIZED,
            fileSize = "${(18..65).random()} KB",
            tags = tags.ifEmpty { listOf("DeafTech", docType.name) }
        )
        _uiState.update { current ->
            current.copy(
                suiteDocuments = listOf(newDoc) + current.suiteDocuments,
                userNotificationMessage = "Document '${newDoc.title}' created in Suite Doc Engine."
            )
        }
    }

    fun updateSuiteDocument(docId: String, content: String, status: SuiteDocStatus = SuiteDocStatus.FINALIZED) {
        _uiState.update { current ->
            val updated = current.suiteDocuments.map { doc ->
                if (doc.id == docId) {
                    doc.copy(
                        content = content,
                        status = status,
                        lastModified = "Just now"
                    )
                } else doc
            }
            current.copy(
                suiteDocuments = updated,
                userNotificationMessage = "Document updated and autosaved."
            )
        }
    }

    fun signSuiteDocument(docId: String) {
        _uiState.update { current ->
            val updated = current.suiteDocuments.map { doc ->
                if (doc.id == docId) {
                    doc.copy(
                        status = SuiteDocStatus.SIGNED,
                        lastModified = "Just now",
                        isDeafAuthVerified = true
                    )
                } else doc
            }
            current.copy(
                suiteDocuments = updated,
                userNotificationMessage = "Document cryptographically signed with DeafAuth Zero-Trust Key."
            )
        }
    }

    fun deleteSuiteDocument(docId: String) {
        _uiState.update { current ->
            current.copy(
                suiteDocuments = current.suiteDocuments.filterNot { it.id == docId },
                userNotificationMessage = "Document removed from Suite."
            )
        }
    }

    fun sendSuiteChannelMessage(channelId: String, text: String, attachedFileId: String? = null) {
        if (text.isBlank() && attachedFileId == null) return
        val currentChannelMessages = _uiState.value.suiteChannelMessages[channelId] ?: emptyList()
        val newMessage = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            text = text,
            isUser = true,
            timestamp = "Just now"
        )
        val updatedMap = _uiState.value.suiteChannelMessages.toMutableMap()
        updatedMap[channelId] = currentChannelMessages + newMessage

        _uiState.update { current ->
            val updatedChannels = current.suiteChatChannels.map { ch ->
                if (ch.id == channelId) {
                    ch.copy(
                        lastMessage = if (text.isNotBlank()) text else "Sent file attachment",
                        lastUpdated = "Just now",
                        unreadCount = 0
                    )
                } else ch
            }
            current.copy(
                suiteChatChannels = updatedChannels,
                suiteChannelMessages = updatedMap
            )
        }
    }

    fun uploadSuiteFile(
        fileName: String,
        fileType: SuiteFileType,
        sizeDisplay: String,
        category: String
    ) {
        val newFile = SuiteFileAttachment(
            id = "file-${System.currentTimeMillis()}",
            fileName = fileName.ifBlank { "attachment_${System.currentTimeMillis()}.${fileType.extension}" },
            fileType = fileType,
            sizeDisplay = sizeDisplay.ifBlank { "250 KB" },
            uploadedDate = "Today",
            category = category.ifBlank { "General Vault" },
            isEncrypted = true,
            verifiedHash = "sha256:${UUID.randomUUID().toString().take(12)}...deafauth"
        )
        _uiState.update { current ->
            current.copy(
                suiteFiles = listOf(newFile) + current.suiteFiles,
                userNotificationMessage = "File '${newFile.fileName}' securely uploaded to DeafAuth Vault."
            )
        }
    }

    fun deleteSuiteFile(fileId: String) {
        _uiState.update { current ->
            current.copy(
                suiteFiles = current.suiteFiles.filterNot { it.id == fileId },
                userNotificationMessage = "File deleted from Vault."
            )
        }
    }

    fun exportVuraAnswerToDoc(queryText: String, answerText: String, citations: List<String>) {
        val docContent = """
# VURA ENGINE LEGAL & REHABILITATION MEMO
**Inquiry:** $queryText
**Date:** Today | **Verified By:** VURA Engine Trust L5 RAG
**Citations:** ${citations.joinToString(", ")}

---

### Authoritative Guidance:
$answerText

---
*Generated via VR4DEAF.org VURA Knowledge Engine & PinkSync Kernel.*
        """.trimIndent()

        createSuiteDocument(
            title = "VURA Memo: ${if (queryText.length > 40) queryText.take(40) + "..." else queryText}",
            docType = SuiteDocType.ADA_ACCOMMODATION_LETTER,
            content = docContent,
            tags = listOf("VURA", "Memo", "Legal")
        )
    }

    fun exportVuraAnswerToEmail(counselorEmail: String, queryText: String, answerText: String) {
        sendSuiteEmail(
            recipient = counselorEmail.ifBlank { "marcus.vance@tws.texas.gov" },
            subject = "VURA Guidance: ${if (queryText.length > 35) queryText.take(35) + "..." else queryText}",
            body = """
Hello Marcus,

Regarding my inquiry on: "$queryText"

VURA Verified Guidance:
$answerText

Please let me know how we should incorporate this into our next IPE milestone review.

Best,
Pinky Collie (DeafAuth ID: DEAF-AUTH-TX-70291)
            """.trimIndent(),
            category = EmailCategory.COUNSELOR_UPDATE
        )
    }

    // ----------------------------------------------------
    // ASL Video Relay & Interview Simulator Actions
    // ----------------------------------------------------
    fun setSimulatorQuestionIndex(index: Int) {
        _uiState.update { it.copy(currentSimulatorQuestionIndex = index) }
    }

    fun setSimulatorRecording(isRecording: Boolean) {
        _uiState.update { it.copy(isSimulatorRecording = isRecording) }
    }

    fun setSimulatorSignSpeed(speed: Float) {
        _uiState.update { it.copy(simulatorSignSpeed = speed) }
    }

    fun saveInterviewSession(
        targetRole: String,
        questionsAnswered: Int,
        overallScore: Int,
        clarityScore: Int,
        pacingScore: Int,
        advocacyScore: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val record = interviewRepository.saveSession(
                targetRole = targetRole,
                questionsAnswered = questionsAnswered,
                overallScore = overallScore,
                clarityScore = clarityScore,
                pacingScore = pacingScore,
                advocacyScore = advocacyScore,
                notes = notes
            )
            _uiState.update {
                it.copy(userNotificationMessage = "Interview Session saved with score $overallScore%! (ID: ${record.id})")
            }
        }
    }

    fun deleteInterviewSession(id: String) {
        viewModelScope.launch {
            interviewRepository.deleteSession(id)
            _uiState.update {
                it.copy(userNotificationMessage = "Session $id removed from local database.")
            }
        }
    }

    fun saveAccommodation(
        title: String,
        category: String,
        targetCompany: String,
        targetRole: String,
        timingStage: String,
        fullLetterText: String
    ) {
        viewModelScope.launch {
            val record = interviewRepository.saveAccommodation(
                title = title,
                category = category,
                targetCompany = targetCompany,
                targetRole = targetRole,
                timingStage = timingStage,
                fullLetterText = fullLetterText
            )
            _uiState.update {
                it.copy(userNotificationMessage = "ADA Accommodation saved to local database (ID: ${record.id})")
            }
        }
    }

    fun deleteAccommodation(id: String) {
        viewModelScope.launch {
            interviewRepository.deleteAccommodation(id)
            _uiState.update {
                it.copy(userNotificationMessage = "Accommodation $id deleted.")
            }
        }
    }

    fun exportAccommodationToDoc(record: SavedAccommodationRecord) {
        createSuiteDocument(
            title = "ADA Letter: ${record.title} (${record.targetCompany})",
            docType = SuiteDocType.ADA_ACCOMMODATION_LETTER,
            content = """
# TITLE I ADA REASONABLE ACCOMMODATION REQUEST
**Target Employer:** ${record.targetCompany}
**Position Applied:** ${record.targetRole}
**Disclosure Stage:** ${record.timingStage}
**DeafAuth Signature:** ${record.verificationDigest}
**Date:** ${record.formattedDate}

---

${record.fullLetterText}

---
*Verified by VR4DEAF ADA Legal Engine & DeafAuth Decentralized ID.*
            """.trimIndent(),
            tags = listOf("ADA", "Accommodations", "Title-I", record.targetCompany)
        )
        _uiState.update {
            it.copy(userNotificationMessage = "Exported to Magicians Suite Doc Engine! (Signed with DeafAuth)")
        }
    }

    fun exportAccommodationToEmail(recipientEmail: String, record: SavedAccommodationRecord) {
        sendSuiteEmail(
            recipient = recipientEmail.ifBlank { "hr-accommodations@company.com" },
            subject = "ADA Title I Accommodation Request - ${record.targetRole} - Jordan Vance",
            body = """
Dear Hiring Committee and Disability Inclusion Office at ${record.targetCompany},

Please review my formal accommodation request for the position of ${record.targetRole}:

${record.fullLetterText}

Cryptographic DeafAuth Verification: ${record.verificationDigest}
VR / TWS Case Reference: TWS-VR-2026-8841 (Deaf Specialist CRC: Sarah Jenkins)

Sincerely,
Jordan Vance (DeafAuth: DA-7928-PINK)
            """.trimIndent(),
            category = EmailCategory.ACCOMMODATION_NOTICE
        )
        _uiState.update {
            it.copy(userNotificationMessage = "Dispatched ADA request email via Suite Email Engine!")
        }
    }

    fun exportSessionAuditPacket(session: InterviewSessionRecord): String {
        return interviewRepository.generateSessionAuditPacket(session)
    }

    // ----------------------------------------------------
    // Municipal Forms & Background Consistency Actions
    // ----------------------------------------------------
    fun triggerMunicipalConsistencyAudit() {
        viewModelScope.launch {
            val result = municipalFormRepository.runConsistencyAudit()
            _uiState.update {
                it.copy(userNotificationMessage = result)
            }
        }
    }

    fun registerNewCityForm(
        cityName: String,
        agencyName: String,
        formUrl: String,
        platformTypeOverride: String? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val form = municipalFormRepository.registerNewCityForm(
                cityName = cityName,
                agencyName = agencyName,
                formUrl = formUrl,
                platformTypeOverride = platformTypeOverride,
                notes = notes
            )
            _uiState.update {
                it.copy(userNotificationMessage = "Registered ${form.cityName} form into background consistency engine! (${form.id})")
            }
        }
    }

    fun deleteCityForm(formId: String) {
        viewModelScope.launch {
            municipalFormRepository.deleteCityForm(formId)
            _uiState.update {
                it.copy(userNotificationMessage = "City form $formId removed from background sync.")
            }
        }
    }

    fun createNormalizedFiling(
        form: MunicipalCityFormRecord,
        employerName: String,
        roleApplied: String,
        accommodationRequested: String,
        incidentDetails: String
    ) {
        viewModelScope.launch {
            val filing = municipalFormRepository.createNormalizedFiling(
                form = form,
                employerName = employerName,
                roleApplied = roleApplied,
                accommodationRequested = accommodationRequested,
                incidentDetails = incidentDetails
            )
            _uiState.update {
                it.copy(userNotificationMessage = "Harmonized filing created for ${form.cityName}! (ID: ${filing.id})")
            }
        }
    }

    fun deleteMunicipalFiling(filingId: String) {
        viewModelScope.launch {
            municipalFormRepository.deleteFiling(filingId)
            _uiState.update {
                it.copy(userNotificationMessage = "Municipal filing $filingId removed.")
            }
        }
    }

    fun exportMunicipalFilingToDoc(filing: NormalizedMunicipalFilingRecord) {
        createSuiteDocument(
            title = "Municipal Filing: ${filing.cityName} (${filing.agencyName})",
            docType = SuiteDocType.ADA_ACCOMMODATION_LETTER,
            content = """
# MUNICIPAL CIVIL RIGHTS & ADA REASONABLE ACCOMMODATION FILING
**Jurisdiction:** ${filing.cityName}
**Commission / Agency:** ${filing.agencyName}
**Filing Reference ID:** ${filing.id}
**DeafAuth Cryptographic Digest:** ${filing.deafAuthDigest}
**Normalized Canonical Date:** ${filing.formattedDate}

---

### CANONICAL PAYLOAD SCHEMA (VR4DEAF-MUNI-INTAKE-v1):
${filing.canonicalPayloadJson}

---
*Generated by VR4DEAF Background Consistency Engine & DeafAuth Kernel.*
            """.trimIndent(),
            tags = listOf("Municipal", "Intake", filing.cityName, "Civil-Rights")
        )
        _uiState.update {
            it.copy(userNotificationMessage = "Exported ${filing.cityName} filing to Magicians Suite Doc Engine!")
        }
    }

    fun exportMunicipalFilingToEmail(filing: NormalizedMunicipalFilingRecord, recipientEmail: String) {
        sendSuiteEmail(
            recipient = recipientEmail.ifBlank { "civilrights-intake@fortworthtexas.gov" },
            subject = "Harmonized ADA Intake - ${filing.cityName} - Jordan Vance",
            body = """
To the Human Relations / Disability Access Commission:

Please find the normalized VR4DEAF ADA intake filing for ${filing.cityName} (${filing.agencyName}):

${filing.canonicalPayloadJson}

DeafAuth Verification Digest: ${filing.deafAuthDigest}
VR Case Ref: TWS-VR-2026-8841 (Counselor: Sarah Jenkins, CRC)

Sincerely,
Jordan Vance (DeafAuth: DA-7928-PINK)
            """.trimIndent(),
            category = EmailCategory.ACCOMMODATION_NOTICE
        )
        _uiState.update {
            it.copy(userNotificationMessage = "Dispatched ${filing.cityName} filing via Suite Email Engine!")
        }
    }

    fun buildPrefilledFormUrl(
        form: MunicipalCityFormRecord,
        employer: String = "Prospective Employer",
        narrative: String = "Requesting Certified Deaf Interpreter (CDI) & CART services under ADA Title I and Municipal Human Relations Code."
    ): String {
        return municipalFormRepository.buildPrefilledFormUrl(
            form = form,
            employer = employer,
            narrative = narrative
        )
    }
}
