package com.example.jobmagician.data

enum class NavDestination(val label: String, val badge: String? = null) {
    VRS_SIMULATOR("ASL Interview Sim", "Live"),
    ADA_STUDIO("ADA Disclosure", "Law"),
    MUNICIPAL_FORMS("City Forms", "Sync"),
    VURA_KNOWLEDGE("VURA Engine", "Main"),
    MAGICIANS_SUITE("MagicianCore", "Core"),
    DEAF_AUTH("Profile", "ID"),
    STORE_REQUESTS("VR4DEAF Store", "Orders"),
    DASHBOARD("VR Plan & IPE"),
    HOME("Overview"),
    OPS_WORKBOOK("Tech Debt & Ops", "Ops"),
    SERVICES("Ecosystem Map"),
    COMMUNITY("ASL Media Vault"),
    JOBS("Job Search"),
    CYCLE("VR 7-Step Cycle")
}

enum class DomainStatus(val label: String, val badgeColorHex: Long) {
    CONNECTED("Connected & Live", 0xFF10B981),
    PROPAGATING("DNS Propagating", 0xFFF59E0B),
    DISCONNECTED("Offline", 0xFFEF4444)
}

data class DomainConnectSub(
    val id: String,
    val subdomain: String,
    val rootDomain: String = "vr4deaf.org",
    val title: String,
    val description: String,
    val cnameTarget: String,
    val status: DomainStatus = DomainStatus.CONNECTED,
    val latencyMs: Int,
    val isTlsSecure: Boolean = true,
    val lastVerified: String = "Just now",
    val linkedKernel: String,
    val isActiveRouter: Boolean = true
)

enum class MagicianSubSuite(val label: String, val slug: String, val subtitle: String) {
    JOB_MAGICIAN("Job-Magician", "jobmagician", "Client VR Journey, IPE & Career Matching"),
    BUSINESS_MAGICIAN("Business-Magician", "businessmagician", "Employer Portal, Tax Credits & Workplace Audits"),
    DEVELOPER_MAGICIAN("Developer-Mag", "developermag", "DeafTech SDKs, PinkSync Kernel APIs & Webhooks")
}

enum class StoreCategory(val label: String, val iconName: String) {
    ALL("All Offerings", "apps"),
    ASSISTIVE_HARDWARE("Assistive Tech Hardware", "devices"),
    INTERPRETER_SERVICES("CDI & ASL Interpreters", "record_voice_over"),
    CART_CAPTIONING("Live CART Captioning", "closed_caption"),
    VR_TRAINING_GRANTS("VR Training Vouchers", "card_membership"),
    ERGONOMICS("Deaf Workspace Ergonomics", "desk")
}

enum class OrderRequestStatus(val label: String, val badgeColorHex: Long) {
    PENDING_VR_APPROVAL("Pending VR Counselor Review", 0xFFF59E0B), // Amber
    VR_SPONSORED_APPROVED("VR/TWS Sponsored (Approved)", 0xFF10B981), // Emerald
    DISPATCHED_IN_PINKSYNC("Dispatched over PinkSync Kernel", 0xFF3B82F6), // Blue
    DELIVERED_ACTIVE("Delivered & Active in Profile", 0xFF8B5CF6) // Purple
}

data class VrStoreItem(
    val id: String,
    val title: String,
    val category: StoreCategory,
    val description: String,
    val estimatedCost: Double,
    val isVrFundedEligible: Boolean = true,
    val twsSponsorshipCode: String,
    val deliveryLeadTime: String,
    val provider: String,
    val features: List<String> = emptyList()
)

data class StoreOrderRequest(
    val orderId: String,
    val storeItemId: String,
    val itemTitle: String,
    val category: StoreCategory,
    val requesterDeafAuthId: String = "DA-7928-PINK",
    val pinkSyncKernelTxId: String,
    val status: OrderRequestStatus,
    val justification: String,
    val requestedTimestamp: String = "Today, 10:45 AM",
    val counselorApprovalNote: String = "TWS Case #TWS-VR-2026-8841 authorized under IPE Section 4 (Assistive Technology Provision)",
    val kernelPayloadDigest: String
)

data class PinkSyncKernelEvent(
    val eventId: String,
    val eventType: String,
    val sourceService: String, // e.g. "deafauth", "vr4deaf-store", "360magicians", "vura"
    val timestamp: String,
    val payloadSummary: String,
    val latencyMs: Int,
    val isVerifiedCrypto: Boolean = true
)

enum class IntegrationTab(val title: String) {
    VR4DEAF_CORE("VR4DEAF Platform"),
    TWS("Vocational Rehab (VR/TWS)"),
    MAGICIAN("Job-Magician 360"),
    VURA_AI("VURA Knowledge Engine"),
    PINKSYNC("PinkSync & DeafAuth")
}

// ----------------------------------------------------
// VURA Knowledge Architecture & PinkAI RAG Models
// ----------------------------------------------------
enum class KnowledgeDomain(val displayName: String, val iconName: String) {
    ALL("All Domains", "category"),
    COMMUNITY("Community & Culture", "groups"),
    ACCESSIBILITY("Accessibility & ADA", "accessibility"),
    VOCATIONAL_REHAB("Vocational Rehab", "work"),
    EMPLOYMENT("Employment & IPE", "business_center"),
    EDUCATION("Education & Transition", "school"),
    HEALTHCARE("Healthcare Rights", "local_hospital"),
    LEGAL("Legal & Rehab Act", "gavel"),
    TECHNOLOGY("Tech & Assistive APIs", "smart_toy")
}

enum class TrustLevel(val level: Int, val description: String, val badgeColorHex: Long) {
    LEVEL_5(5, "Law, Regulations & Official Government", 0xFF10B981), // Emerald
    LEVEL_4(4, "Universities & Deaf Organizations", 0xFF3B82F6), // Blue
    LEVEL_3(3, "Internal Reviewed SOPs & Documentation", 0xFF8B5CF6), // Purple
    LEVEL_2(2, "Community Lived Experiences", 0xFFF59E0B), // Amber
    LEVEL_1(1, "Drafts & Community Submissions", 0xFF6B7280) // Gray
}

data class KnowledgeArticle(
    val id: String,
    val title: String,
    val summary: String,
    val domain: KnowledgeDomain,
    val topic: String,
    val source: String,
    val trustLevel: TrustLevel,
    val jurisdiction: String = "Federal / State",
    val keyTakeaways: List<String> = emptyList(),
    val citationText: String = "",
    val aslVideoAvailable: Boolean = true,
    val reviewedDate: String = "August 2026"
)

data class KnowledgeGraphNode(
    val stepOrder: Int,
    val entityName: String,
    val entityType: String,
    val exampleInstance: String,
    val description: String
)

data class PinkAiQuery(
    val id: String,
    val question: String,
    val answer: String,
    val domain: KnowledgeDomain,
    val trustRating: Int,
    val citations: List<String>,
    val timestamp: String = "Just now"
)

// ----------------------------------------------------
// VR4DEAF Ops & Lifecycle / Tech Debt Models (PDF Workbook)
// ----------------------------------------------------
enum class ServicePhase(val label: String) {
    IDEA("Idea"),
    BUILD("Build"),
    GROW("Grow"),
    MANAGED("Managed")
}

data class GithubRepoInfo(
    val repoName: String,
    val repoSlug: String,
    val fullUrl: String,
    val description: String,
    val primaryFocus: String,
    val statusBadge: String = "Active",
    val isCoreEcosystem: Boolean = true
)

data class ServiceLifecycleItem(
    val serviceKey: String,
    val serviceName: String,
    val githubRepo: String = "pinkycollie/vr4deaf",
    val currentPhase: ServicePhase,
    val status: String,
    val lastUpdated: String,
    val notes: String
)

enum class DebtSeverity(val label: String, val multiplier: Double) {
    LOW("Low", 1.0),
    MEDIUM("Medium", 1.5),
    HIGH("High", 2.0),
    CRITICAL("Critical", 3.0)
}

data class TechDebtItem(
    val id: String,
    val serviceKey: String,
    val serviceName: String,
    val description: String,
    val severity: DebtSeverity,
    val estHoursToFix: Double,
    val hourlyRate: Double = 75.0,
    val impactIfUnresolved: String,
    val isResolved: Boolean = false
) {
    val estimatedCost: Double
        get() = estHoursToFix * hourlyRate
}

data class ServiceEquityDebtSummary(
    val serviceKey: String,
    val serviceName: String,
    val totalDebtCost: Double,
    val equityScore: Double, // 0 - 100
    val notes: String = ""
) {
    val debtToEquityRatio: Double
        get() = if (equityScore > 0) (totalDebtCost / (equityScore * 100.0)) else 0.0

    val healthFlag: String
        get() = when {
            equityScore <= 0 -> "No equity score yet"
            debtToEquityRatio < 0.15 -> "Healthy (Low Debt)"
            debtToEquityRatio < 0.40 -> "Moderate (Monitor)"
            else -> "High Debt Warning"
        }
}

data class ContributorStake(
    val id: String,
    val nameRole: String,
    val contributionArea: String,
    val equityPercentage: Double,
    val notes: String
)

// ----------------------------------------------------
// DeafAuth & Accessibility Identity
// ----------------------------------------------------
enum class PrimaryLanguagePreference {
    ASL_NATIVE,
    ASL_BLACK_ASL_VARIANT,
    ASL_REGIONAL_VARIANT,
    TACTILE_PRO_TACTILE,
    CART_REALTIME_CAPTIONS,
    ORAL_LIPREADING_WITH_ASSIST
}

data class DeafAuthProfile(
    val deafAuthId: String = "DA-7928-PINK",
    val displayName: String = "Jordan Vance",
    val githubHandle: String = "@pinkycollie",
    val githubOrgUrl: String = "https://github.com/pinkycollie",
    val communicationPref: PrimaryLanguagePreference = PrimaryLanguagePreference.ASL_NATIVE,
    val interpreterPreference: String = "Certified Deaf Interpreter (CDI) preferred for formal interviews",
    val cartSpeedWpm: Int = 220,
    val vrCaseNumber: String = "TWS-VR-2026-8841",
    val assignedCounselor: String = "Sarah Jenkins, CRC (Deaf Specialist)",
    val syncIdentities: List<String> = listOf(
        "github.com/pinkycollie/vura (Main RAG Hub)",
        "github.com/pinkycollie/360magicians (Job, Business, Dev)",
        "github.com/pinkycollie/deafauth (Decentralized ID)",
        "github.com/pinkycollie/pinksync (Telemetry Kernel)",
        "vr4deaf.org/store (Order & Accommodation Requests)",
        "github.com/pinkycollie/vr4deaf (Universal VR Gateway)",
        "TWS Statewide VR (Texas)"
    ),
    val verifiedLevel: String = "Tier 3: Verified Deaf VR Candidate"
)

// ----------------------------------------------------
// Vocational Rehabilitation & Job Magician Models
// ----------------------------------------------------
data class CycleStepData(
    val id: Int,
    val title: String,
    val description: String,
    val keyActivities: List<String>
)

data class AppointmentData(
    val id: String,
    val title: String,
    val dateTime: String,
    val location: String,
    val tag: String,
    val aslAccommodated: Boolean = true
)

data class EligibilityData(
    val program: String,
    val status: String,
    val isEligible: Boolean?
)

data class SkillScoreData(
    val skill: String,
    val percentage: Int
)

data class CareerRecommendationData(
    val title: String,
    val matchScore: Int,
    val demandOutlook: String = "High Growth"
)

enum class CourseStatus {
    IN_PROGRESS,
    SCHEDULED,
    RECOMMENDED,
    COMPLETED
}

data class TrainingCourseData(
    val id: String,
    val title: String,
    val description: String,
    val status: CourseStatus,
    val progressPercent: Int = 0,
    val dateText: String = "",
    val isAslInstruction: Boolean = true
)

enum class ApplicationStatus {
    INTERVIEW_SCHEDULED,
    SUBMITTED,
    DRAFT,
    OFFER_RECEIVED
}

data class JobApplicationData(
    val id: String,
    val title: String,
    val company: String,
    val status: ApplicationStatus,
    val appliedDate: String,
    val note: String = "",
    val accommodationRequested: String = "Certified ASL Interpreter & CART"
)

data class JobListingData(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val postedDate: String,
    val matchScore: Int,
    val tags: List<String>,
    val description: String,
    val deafFriendlyRating: Int = 5
)

data class ResourceData(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val isAslAvailable: Boolean = true
)

// ----------------------------------------------------
// ASL Media Vault & Educational Video Models
// ----------------------------------------------------
data class AslMediaItem(
    val id: String,
    val title: String,
    val presenter: String,
    val durationText: String,
    val category: String,
    val views: Int,
    val description: String,
    val tags: List<String> = emptyList(),
    val hasVisualProof: Boolean = true
)

// ----------------------------------------------------
// Magicians Suite Collaboration: Email, Doc, Chat, File
// ----------------------------------------------------
enum class SuiteTab(val label: String, val iconName: String) {
    EMAIL("Email Suite", "email"),
    DOCS("Doc Engine", "description"),
    CHAT("Multi-Chat", "chat"),
    FILES("File Vault", "folder")
}

enum class SuiteDocType(val label: String, val badgeColorHex: Long) {
    IPE_PLAN("VR IPE Employment Plan", 0xFF3B82F6),
    ADA_ACCOMMODATION_LETTER("ADA Title I Accommodation Request", 0xFF10B981),
    VOCATIONAL_ASSESSMENT("Vocational Skills Assessment", 0xFF8B5CF6),
    RESUME_CV("Accessible Tech Resume / CV", 0xFFF59E0B),
    WOTC_CREDIT_8850("IRS Form 8850 (WOTC)", 0xFFEC4899),
    WORKPLACE_AUDIT("Deaf Workplace Accessibility Audit", 0xFF06B6D4)
}

enum class SuiteDocStatus(val label: String, val badgeColorHex: Long) {
    DRAFT("Drafting", 0xFF6B7280),
    FINALIZED("Finalized", 0xFF3B82F6),
    SIGNED("DeafAuth Signed", 0xFF10B981),
    EXPORTED_PDF("Exported PDF", 0xFF8B5CF6)
}

data class SuiteDocument(
    val id: String,
    val title: String,
    val docType: SuiteDocType,
    val lastModified: String,
    val author: String = "Pinky Collie (VR Client)",
    val content: String,
    val status: SuiteDocStatus = SuiteDocStatus.FINALIZED,
    val fileSize: String = "24 KB",
    val tags: List<String> = emptyList(),
    val isDeafAuthVerified: Boolean = true
)

enum class EmailCategory(val label: String, val badgeColorHex: Long) {
    COUNSELOR_UPDATE("VR Counselor Dispatch", 0xFF3B82F6),
    ACCOMMODATION_NOTICE("ADA Accommodation Notice", 0xFF10B981),
    TWS_SPONSORSHIP("TWS Order Sponsorship", 0xFFF59E0B),
    EMPLOYER_INTERVIEW("Employer Interview Prep", 0xFF8B5CF6),
    SYSTEM_NOTIFY("PinkSync Notification", 0xFF6B7280)
}

data class SuiteEmail(
    val id: String,
    val sender: String,
    val recipient: String,
    val subject: String,
    val preview: String,
    val body: String,
    val category: EmailCategory,
    val timestamp: String,
    val isUnread: Boolean = false,
    val isSent: Boolean = false,
    val isDraft: Boolean = false,
    val attachedFiles: List<String> = emptyList()
)

enum class SuiteChannelType(val label: String) {
    COUNSELOR_DIRECT("VR Counselor Direct (TWS)"),
    INTERPRETER_HOTLINE("Live CDI / ASL Interpreter Hotline"),
    PEER_COMMUNITY("VR4DEAF Developer & Job Peer Network"),
    ASL_VIDEO_RELAY("VRS Video Relay Dispatch Desk"),
    VURA_AI("VURA Conversational Engine")
}

data class SuiteChatChannel(
    val id: String,
    val name: String,
    val channelType: SuiteChannelType,
    val unreadCount: Int = 0,
    val lastMessage: String,
    val lastUpdated: String,
    val isOnline: Boolean = true,
    val avatarInitials: String = "VR"
)

enum class SuiteFileType(val label: String, val extension: String, val badgeColorHex: Long) {
    PDF("Adobe PDF Document", "pdf", 0xFFEF4444),
    DOCX("Word Document", "docx", 0xFF3B82F6),
    AUDIOGRAM_IMG("Audiogram / Medical Scan", "png", 0xFF10B981),
    SIGNED_FORM("DeafAuth Cryptographic Form", "vdoc", 0xFF8B5CF6),
    CERTIFICATE("Vocational Certificate", "pdf", 0xFFF59E0B),
    SPREADSHEET("Financial / WOTC Sheet", "xlsx", 0xFF059669)
}

data class SuiteFileAttachment(
    val id: String,
    val fileName: String,
    val fileType: SuiteFileType,
    val sizeDisplay: String,
    val uploadedDate: String,
    val category: String,
    val isEncrypted: Boolean = true,
    val verifiedHash: String = "sha256:7f83b1...deafauth"
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val domain: KnowledgeDomain? = null,
    val trustRating: Int? = null,
    val citations: List<String> = emptyList(),
    val attachedFileId: String? = null
)

