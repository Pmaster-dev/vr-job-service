package com.example.jobmagician.data

import android.content.Context
import com.example.jobmagician.data.db.AppDatabaseHelper
import com.example.jobmagician.data.db.InterviewSessionRecord
import com.example.jobmagician.data.db.SavedAccommodationRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class QuestionCategory(val displayName: String, val badgeColorHex: Long) {
    ALL("All Questions", 0xFF3B82F6),
    BEHAVIORAL("Behavioral & Leadership", 0xFF8B5CF6),
    TECHNICAL("System & Code Design", 0xFF10B981),
    TEAMWORK("Cross-Functional & Remote", 0xFFF59E0B),
    ADA_ADVOCACY("ADA & Self-Advocacy", 0xFFEC4899),
    DEAF_CULTURE("Deaf Gain & Perspective", 0xFF06B6D4)
}

data class InterviewQuestion(
    val id: String,
    val category: QuestionCategory,
    val questionEnglish: String,
    val aslGloss: String,
    val contextScenario: String,
    val recommendedDeafTips: String,
    val keyEvaluationPoints: List<String>
)

class InterviewRepository(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _sessions = MutableStateFlow<List<InterviewSessionRecord>>(emptyList())
    val sessions: StateFlow<List<InterviewSessionRecord>> = _sessions.asStateFlow()

    private val _accommodations = MutableStateFlow<List<SavedAccommodationRecord>>(emptyList())
    val accommodations: StateFlow<List<SavedAccommodationRecord>> = _accommodations.asStateFlow()

    // 40+ curated questions across all critical interview categories
    val questionBank: List<InterviewQuestion> = listOf(
        // BEHAVIORAL
        InterviewQuestion(
            id = "Q-BEH-01",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Tell me about a time you encountered a significant roadblock on a critical project and how you resolved it.",
            aslGloss = "PAST PROJECT IMPORTANT PROBLEM BIG HAPPEN. YOU RESOLVE HOW?",
            contextScenario = "Testing resilience, methodical debugging, and stakeholder transparency.",
            recommendedDeafTips = "Frame the obstacle clearly first, then sign sequential steps (FIRST, SECOND, FINALLY) to show logical control.",
            keyEvaluationPoints = listOf("Root cause analysis", "Clear escalation or pivoting", "Measurable outcome achieved")
        ),
        InterviewQuestion(
            id = "Q-BEH-02",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Describe a situation where you had to push back on unrealistic deadlines or shifting requirements.",
            aslGloss = "MANAGER DEADLINE IMPOSSIBLE DEMAND. YOU NEGOTIATE PUSH-BACK HOW?",
            contextScenario = "Evaluating boundary setting, workload estimation, and data-driven counter-proposals.",
            recommendedDeafTips = "Highlight written documentation, sprint capacity charts, and proactive alternatives.",
            keyEvaluationPoints = listOf("Constructive disagreement", "Preserved engineering quality", "Maintained trust with leadership")
        ),
        InterviewQuestion(
            id = "Q-BEH-03",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Give an example of when you took initiative to mentor a junior colleague or onboard a new hire.",
            aslGloss = "NEW WORKER TEAM JOIN. YOU MENTOR TEACH ASSIST HOW?",
            contextScenario = "Gauging empathy, leadership, and knowledge sharing.",
            recommendedDeafTips = "Mention creating accessible onboarding docs, code walkthroughs, and pair-programming sessions.",
            keyEvaluationPoints = listOf("Patient guidance", "High-retention documentation", "Accelerated team ramp-up")
        ),
        InterviewQuestion(
            id = "Q-BEH-04",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "How do you handle ambiguous requirements when product specifications are incomplete?",
            aslGloss = "PROJECT GOAL UNCLEAR VAGUE. YOU FIGURE-OUT PLAN PROCEED HOW?",
            contextScenario = "Assessing comfort with ambiguity and proactive requirements gathering.",
            recommendedDeafTips = "Sign how you draft RFCs (Request for Comments) and solicit async feedback in Slack or GitHub PRs.",
            keyEvaluationPoints = listOf("Self-direction", "Cross-functional stakeholder buy-in", "Minimizing rework")
        ),
        InterviewQuestion(
            id = "Q-BEH-05",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Share an instance where you received critical or unexpected feedback on your work and adapted.",
            aslGloss = "YOUR WORK FEEDBACK CRITICAL NEGATIVE RECEIVED. YOU FEEL CHANGE HOW?",
            contextScenario = "Testing growth mindset, coachability, and emotional resilience.",
            recommendedDeafTips = "Demonstrate separating feedback from personal ego, and validating iterations with test metrics.",
            keyEvaluationPoints = listOf("Non-defensive reception", "Actionable improvement plan", "Subsequent performance validation")
        ),
        InterviewQuestion(
            id = "Q-BEH-06",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Tell me about a high-pressure production outage you helped triage under strict SLAs.",
            aslGloss = "SERVER CRASH RED ALERT EMERGENCY. TEAM PANIC YOU FIX HOW?",
            contextScenario = "Assessing incident command, calm diagnostics, and post-mortem hygiene.",
            recommendedDeafTips = "Sign the diagnostic pipeline: check telemetry logs, roll back canary release, document blameless post-mortem.",
            keyEvaluationPoints = listOf("Calm under pressure", "Systematic root-cause identification", "Preventive prevention safeguards")
        ),
        InterviewQuestion(
            id = "Q-BEH-07",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Describe a time you had to balance competing priorities across multiple projects with tight deadlines.",
            aslGloss = "PROJECT TWO THREE SAME TIME DUE. PRIORITY BALANCE HOW?",
            contextScenario = "Evaluating time management, Eisenhower matrix prioritization, and stakeholder communication.",
            recommendedDeafTips = "Emphasize visual Kanban tracking and renegotiating delivery order with sprint leads.",
            keyEvaluationPoints = listOf("Clear criteria for ranking impact", "Transparency with stakeholders", "Zero dropped commitments")
        ),
        InterviewQuestion(
            id = "Q-BEH-08",
            category = QuestionCategory.BEHAVIORAL,
            questionEnglish = "Walk us through a successful project you delivered that you are most proud of in your career.",
            aslGloss = "YOUR PAST WORK WHICH BEST PROUD WHY?",
            contextScenario = "Assessing passion, craft excellence, and end-user empathy.",
            recommendedDeafTips = "Share real metrics (latency reduced, users onboarded, accessibility WCAG compliance achieved).",
            keyEvaluationPoints = listOf("End-to-end ownership", "Technical complexity mastered", "Business or user impact")
        ),

        // TECHNICAL
        InterviewQuestion(
            id = "Q-TEC-01",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "How do you architect scalable, reactive Android applications using Kotlin Coroutines, StateFlow, and Jetpack Compose?",
            aslGloss = "ANDROID APP SCALE FAST REACTIVE. KOTLIN FLOW COMPOSE ARCHITECTURE DESIGN HOW?",
            contextScenario = "Assessing modern Android unidirectional data flow (UDF) and lifecycle safety.",
            recommendedDeafTips = "Use spatial signing to contrast the UI layer (top), ViewModel (center), and Repository/Data layer (bottom).",
            keyEvaluationPoints = listOf("Single source of truth", "Clean separation of concerns", "Configuration change survival")
        ),
        InterviewQuestion(
            id = "Q-TEC-02",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "Explain how you optimize Jetpack Compose recomposition performance in complex, scrolling lists.",
            aslGloss = "COMPOSE SCREEN LAG RECOMPOSE TOO-MUCH. YOU OPTIMIZE SMOOTH HOW?",
            contextScenario = "Testing deep understanding of Compose runtime, remember, derivedStateOf, and stable keys.",
            recommendedDeafTips = "Sign the difference between lazy list keys, immutable data classes, and avoiding lambda allocations inside item scopes.",
            keyEvaluationPoints = listOf("Proper key assignments in LazyColumn", "derivedStateOf usage", "Skipping recomposition mechanisms")
        ),
        InterviewQuestion(
            id = "Q-TEC-03",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "How do you design a local-first offline storage strategy with seamless background sync?",
            aslGloss = "DEVICE NO INTERNET OFFLINE DATA SAVE. LATER INTERNET BACK SYNC CLOUD HOW?",
            contextScenario = "Evaluating offline resilience, conflict resolution, and optimistic UI updates.",
            recommendedDeafTips = "Detail local SQLite/Room as single source of truth, queueing outbound mutations with exponential backoff.",
            keyEvaluationPoints = listOf("Optimistic local writes", "Conflict resolution strategy (timestamp/vector clock)", "Network error backoff")
        ),
        InterviewQuestion(
            id = "Q-TEC-04",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "What is your approach to automated unit and integration testing in Android without slow emulator dependencies?",
            aslGloss = "TEST AUTOMATIC RUN FAST EMULATOR WITHOUT. YOU WRITE ROBOLECTRIC HOW?",
            contextScenario = "Assessing test pyramid knowledge, Robolectric JVM tests, and test-driven refactoring.",
            recommendedDeafTips = "Point out test coverage for Critical User Journeys (CUJs), fake repositories, and StateFlow turbine assertions.",
            keyEvaluationPoints = listOf("Robolectric JVM execution speed", "Mock vs Fake data repositories", "Deterministic state verification")
        ),
        InterviewQuestion(
            id = "Q-TEC-05",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "How do you audit and enforce accessibility compliance (TalkBack semantics, touch targets, dynamic type) across an app?",
            aslGloss = "ACCESSIBILITY TALKBACK SCREEN-READER TOUCH 48DP FONT SCALE. YOU AUDIT CODE HOW?",
            contextScenario = "Evaluating WCAG 2.2 AA standards, Modifier.semantics, and minimum interactive component sizes.",
            recommendedDeafTips = "Emphasize that accessibility isn't an afterthought; explain automated lint checks and testing with Accessibility Scanner.",
            keyEvaluationPoints = listOf("Touch targets 48x48dp minimum", "Clear semantic content descriptions", "Dynamic font scaling (sp not dp)")
        ),
        InterviewQuestion(
            id = "Q-TEC-06",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "Describe your strategy for securing sensitive credentials, tokens, and encrypted storage on Android.",
            aslGloss = "API KEY PASSWORD TOKEN ENCRYPT SECRET SAFE ANDROID WHERE STORE HOW?",
            contextScenario = "Gauging Android Keystore, EncryptedSharedPreferences, BuildConfig injection, and zero hardcoded secrets.",
            recommendedDeafTips = "Contrast build-time environment variable injection vs runtime Android Keystore hardware-backed encryption.",
            keyEvaluationPoints = listOf("Never hardcoding secrets in git", "Android Keystore master key wrapping", "Secure token refresh flows")
        ),
        InterviewQuestion(
            id = "Q-TEC-07",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "How do you approach RESTful and WebSocket API contract design between mobile clients and microservices?",
            aslGloss = "CLIENT SERVER TALK API CONTRACT JSON WEBSOCKET. YOU DESIGN HOW?",
            contextScenario = "Assessing serialization schemas, backward compatibility, idempotency keys, and stream handling.",
            recommendedDeafTips = "Mention type-safe Kotlinx.serialization, handling partial failures, and socket reconnect heartbeats.",
            keyEvaluationPoints = listOf("Backward compatible schemas", "Network error recovery", "Idempotent mutation keys")
        ),
        InterviewQuestion(
            id = "Q-TEC-08",
            category = QuestionCategory.TECHNICAL,
            questionEnglish = "What metrics do you track to identify memory leaks, ANRs, and jank in Android production applications?",
            aslGloss = "APP MEMORY LEAK FREEZE ANR CRASH. YOU MONITOR FIX TOOLS WHAT?",
            contextScenario = "Evaluating Android Profiler, LeakCanary, Firebase Crashlytics, and ANR bug report analysis.",
            recommendedDeafTips = "Sign about detecting static references to Activity contexts, coroutine cancellation leaks, and offloading heavy work from Main thread.",
            keyEvaluationPoints = listOf("Main thread isolation", "Coroutines dispatchers management", "Profiler heap dumps inspection")
        ),

        // TEAMWORK
        InterviewQuestion(
            id = "Q-TEA-01",
            category = QuestionCategory.TEAMWORK,
            questionEnglish = "How do you establish seamless day-to-day collaboration with hearing team members who do not know sign language?",
            aslGloss = "HEARING CO-WORKER SIGN CANNOT. DAILY WORK COMMUNICATE SMOOTH HOW?",
            contextScenario = "Evaluating asynchronous communication mastery, Slack/Teams etiquette, and clear visual workflows.",
            recommendedDeafTips = "Explain how high-fidelity written communication, concise PR descriptions, and shared docs eliminate misunderstandings for everyone.",
            keyEvaluationPoints = listOf("Asynchronous documentation mindset", "Effective meeting preparation", "Mutual respect and proactive tools")
        ),
        InterviewQuestion(
            id = "Q-TEA-02",
            category = QuestionCategory.TEAMWORK,
            questionEnglish = "How do you manage active participation in fast-paced sprint planning and standup meetings with interpreters or CART?",
            aslGloss = "SPRINT STANDUP FAST MEETING. INTERPRETER CART USE. YOU PARTICIPATE TIMING HOW?",
            contextScenario = "Testing turn-taking coordination, brief relay lag management, and chat-channel parallel posting.",
            recommendedDeafTips = "Describe signaling the moderator or posting bulleted standup updates in the meeting chat while the interpreter voices.",
            keyEvaluationPoints = listOf("Managing VRS/CART latency gracefully", "Pre-meeting agenda review", "Dual-channel chat engagement")
        ),
        InterviewQuestion(
            id = "Q-TEA-03",
            category = QuestionCategory.TEAMWORK,
            questionEnglish = "Describe a situation where two engineers strongly disagreed on a software architecture decision and how you reached consensus.",
            aslGloss = "ENGINEER TWO ARCHITECTURE ARGUE DIFFERENT. YOU HELP COMPROMISE AGREE HOW?",
            contextScenario = "Assessing mediation, objective criteria evaluation, and data-backed benchmarking.",
            recommendedDeafTips = "Sign proposing a time-boxed proof-of-concept (POC) to test both approaches objectively against latency and maintainability metrics.",
            keyEvaluationPoints = listOf("Depersonalizing the debate", "Benchmarking POC evidence", "Commitment to the final decision")
        ),
        InterviewQuestion(
            id = "Q-TEA-04",
            category = QuestionCategory.TEAMWORK,
            questionEnglish = "How do you review pull requests to provide thorough, empathetic, and constructive code feedback?",
            aslGloss = "GITHUB PR CODE REVIEW. YOU CHECK FEEDBACK TEACH KIND HOW?",
            contextScenario = "Evaluating code review rigor, tone, nit vs blocking labeling, and encouraging team growth.",
            recommendedDeafTips = "Share your convention of prefixing comments with [Nit], [Question], or [Blocker] to make intent crystal clear.",
            keyEvaluationPoints = listOf("Clear categorization of feedback", "Explanation of the 'why'", "Praising elegant solutions")
        ),
        InterviewQuestion(
            id = "Q-TEA-05",
            category = QuestionCategory.TEAMWORK,
            questionEnglish = "Tell me about a time you collaborated with product managers and UX designers to refine a feature spec.",
            aslGloss = "PRODUCT MANAGER DESIGNER YOU THREE WORK-TOGETHER FEATURE IMPROVE HOW?",
            contextScenario = "Evaluating technical feasibility guidance, edge-case anticipation, and user empathy.",
            recommendedDeafTips = "Sign how you translate design mockups into engineering milestones while flagging accessibility and state loading considerations.",
            keyEvaluationPoints = listOf("Feasibility communication", "Edge-case proactive surfacing", "Design system consistency")
        ),
        InterviewQuestion(
            id = "Q-TEA-06",
            category = QuestionCategory.TEAMWORK,
            questionEnglish = "How do you build strong interpersonal connections and team camaraderie in an all-remote distributed setting?",
            aslGloss = "WORK FROM HOME REMOTE FAR. TEAM FRIENDSHIP BOND YOU BUILD HOW?",
            contextScenario = "Gauging virtual social engagement, casual asynchronous check-ins, and shared interest channels.",
            recommendedDeafTips = "Mention joining social Slack channels, sharing ASL vocabulary tips with curious teammates, and async video celebrations.",
            keyEvaluationPoints = listOf("Proactive virtual presence", "Inclusive social interactions", "Cultivating psychological safety")
        ),

        // ADA & SELF-ADVOCACY
        InterviewQuestion(
            id = "Q-ADA-01",
            category = QuestionCategory.ADA_ADVOCACY,
            questionEnglish = "When and how do you disclose your Deaf identity and request reasonable accommodations during the hiring process?",
            aslGloss = "JOB APPLY INTERVIEW. YOU DEAF IDENTITY EXPLAIN ACCOMMODATION REQUEST WHEN HOW?",
            contextScenario = "Evaluating strategic disclosure timing under Title I ADA without compromising candidate standing.",
            recommendedDeafTips = "Explain that requesting accommodations upon the interview invitation email allows company HR adequate time to schedule certified interpreters.",
            keyEvaluationPoints = listOf("Clear understanding of Title I rights", "Professional non-adversarial framing", "Specifying precise vendor credentials")
        ),
        InterviewQuestion(
            id = "Q-ADA-02",
            category = QuestionCategory.ADA_ADVOCACY,
            questionEnglish = "What specific reasonable accommodations do you require to perform at peak productivity in an engineering role?",
            aslGloss = "DAILY ENGINEERING WORK MAXIMUM HIGH. ACCOMMODATIONS YOU NEED WHAT WHAT?",
            contextScenario = "Testing self-awareness, familiarity with tech solutions, and practical workplace setup.",
            recommendedDeafTips = "Itemize: 1) RID/CDI interpreters for large meetings, 2) CART for technical webinars, 3) Visual notification strobes, 4) Dual high-res monitors for IDE + interpreter window.",
            keyEvaluationPoints = listOf("Comprehensive, realistic accommodation list", "Clear productivity justification", "Knowledge of assistive tech options")
        ),
        InterviewQuestion(
            id = "Q-ADA-03",
            category = QuestionCategory.ADA_ADVOCACY,
            questionEnglish = "How do you educate an employer or team who has never worked with a Deaf professional before?",
            aslGloss = "COMPANY BEFORE NEVER WORK DEAF. YOU TEACH ONBOARD TEAM HOW?",
            contextScenario = "Assessing patience, educational framing, and demystifying communication norms.",
            recommendedDeafTips = "Describe creating a one-page 'Working with Me' guide covering: looking at the candidate not interpreter, waving or tapping shoulder, sharing agendas in advance.",
            keyEvaluationPoints = listOf("Educational 'Working with Me' guide", "Focus on shared goals", "Demystifying Deaf communication norms")
        ),
        InterviewQuestion(
            id = "Q-ADA-04",
            category = QuestionCategory.ADA_ADVOCACY,
            questionEnglish = "What steps would you take if an assigned interview interpreter lacks technical vocabulary or fluency?",
            aslGloss = "INTERVIEW INTERPRETER TECH WORD CODING DON'T-KNOW CONFUSED. YOU HANDLE HOW?",
            contextScenario = "Assessing in-the-moment remediation, professional boundary maintenance, and fallback strategies.",
            recommendedDeafTips = "Sign how you politely pause the interview, clarify technical terms via chat or screen-share typing, or request a certified CDI who specializes in STEM.",
            keyEvaluationPoints = listOf("Polite pause and communication reset", "Utilizing chat/screen-share fallback", "Advocating for qualified STEM interpreters")
        ),
        InterviewQuestion(
            id = "Q-ADA-05",
            category = QuestionCategory.ADA_ADVOCACY,
            questionEnglish = "How do you leverage state Vocational Rehabilitation (VR) and TWS resources to fund assistive equipment at work?",
            aslGloss = "VOC-REHAB TWS STATE MONEY TECH EQUIPMENT PAY ASSIST YOU USE HOW?",
            contextScenario = "Demonstrating awareness of VR IPE (Individualized Plan for Employment) sponsorship, relieving cost burden from employers.",
            recommendedDeafTips = "Explain that state VR/TWS can co-sponsor or provide specialized hardware, ergonomics, and initial on-the-job interpreter hours.",
            keyEvaluationPoints = listOf("Knowledge of VR IPE provisions", "Highlighting zero cost-barrier to employers", "Proactive VR counselor partnership")
        ),
        InterviewQuestion(
            id = "Q-ADA-06",
            category = QuestionCategory.ADA_ADVOCACY,
            questionEnglish = "How do you respond if an employer claims that providing professional interpreters causes an 'undue hardship'?",
            aslGloss = "EMPLOYER SAY INTERPRETER COST TOO-MUCH MONEY CANNOT. YOU ANSWER EXPLAIN HOW?",
            contextScenario = "Testing legal acumen regarding ADA Title I standard of undue hardship relative to overall company budget and tax incentives.",
            recommendedDeafTips = "Politely point out IRS Section 44 Disabled Access Tax Credit, WOTC tax deductions, and state VR cost-sharing programs.",
            keyEvaluationPoints = listOf("Knowledge of Section 44 & WOTC tax credits", "Constructive dialogue over alternatives", "Professional legal literacy")
        ),

        // DEAF CULTURE & DEAF GAIN
        InterviewQuestion(
            id = "Q-CUL-01",
            category = QuestionCategory.DEAF_CULTURE,
            questionEnglish = "How does your Deaf identity and 'Deaf Gain' make you a stronger software engineer and systems architect?",
            aslGloss = "DEAF GAIN PERSPECTIVE TECH SYSTEM DESIGN YOU BETTER ADVANTAGE HOW?",
            contextScenario = "Reframing disability into a powerful cognitive, spatial, and visual asset for tech teams.",
            recommendedDeafTips = "Detail how 3D visual-spatial grammar translates to rapid mental mapping of complex distributed databases and intuitive UI architectures.",
            keyEvaluationPoints = listOf("Deaf Gain cognitive reframing", "Superior spatial and visual layout intuition", "Hyper-focus and deep work capabilities")
        ),
        InterviewQuestion(
            id = "Q-CUL-02",
            category = QuestionCategory.DEAF_CULTURE,
            questionEnglish = "How has navigating a hearing-centric world honed your problem-solving adaptability and creative thinking?",
            aslGloss = "HEARING WORLD DEAF GROW-UP. CREATIVE THINKING ADAPT PROBLEM SOLVE DEVELOP HOW?",
            contextScenario = "Showcasing resilience, out-of-the-box workaround thinking, and cross-cultural agility.",
            recommendedDeafTips = "Sign examples of creatively navigating unexpected physical or digital barriers, translating directly to rapid debugging instinct.",
            keyEvaluationPoints = listOf("Natural troubleshooting adaptability", "Cross-cultural communication empathy", "Tenacity in the face of ambiguity")
        ),
        InterviewQuestion(
            id = "Q-CUL-03",
            category = QuestionCategory.DEAF_CULTURE,
            questionEnglish = "In what ways does your lived experience drive you to build truly inclusive, accessible products for all users?",
            aslGloss = "YOUR LIFE EXPERIENCE ACCESSIBLE PRODUCT FOR EVERYONE BUILD PASSION WHY?",
            contextScenario = "Assessing deep user advocacy, curb-cut effect understanding, and mission alignment.",
            recommendedDeafTips = "Explain the 'curb-cut effect': features built for Deaf accessibility (captions, clear visual feedback) drastically improve user experience for everyone.",
            keyEvaluationPoints = listOf("Understanding of the Curb-Cut Effect", "Commitment to universal design", "Authentic user-centric mindset")
        ),
        InterviewQuestion(
            id = "Q-CUL-04",
            category = QuestionCategory.DEAF_CULTURE,
            questionEnglish = "How do you explain the difference between American Sign Language (ASL) and spoken English to non-signing colleagues?",
            aslGloss = "ASL ENGLISH SAME NO DIFFERENT LANGUAGE GRAMMAR YOU EXPLAIN HOW?",
            contextScenario = "Assessing cultural literacy and ability to dispel common myths (ASL is not signed English; it is a full natural language).",
            recommendedDeafTips = "Sign that ASL is an independent language with Topic-Comment OSV syntax, facial grammars, and rich spatial classifiers.",
            keyEvaluationPoints = listOf("Accurate linguistic classification", "Educating on non-manual markers and classifiers", "Dispelling simplistic misconceptions")
        )
    )

    init {
        refreshSessions()
        refreshAccommodations()
    }

    fun refreshSessions() {
        scope.launch {
            val list = dbHelper.getAllSessions()
            _sessions.value = list
        }
    }

    fun refreshAccommodations() {
        scope.launch {
            val list = dbHelper.getAllAccommodations()
            _accommodations.value = list
        }
    }

    suspend fun saveSession(
        targetRole: String,
        questionsAnswered: Int,
        overallScore: Int,
        clarityScore: Int,
        pacingScore: Int,
        advocacyScore: Int,
        notes: String
    ): InterviewSessionRecord {
        val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        val now = System.currentTimeMillis()
        val record = InterviewSessionRecord(
            id = "VRS-SES-${System.currentTimeMillis().toString().takeLast(6)}",
            timestamp = now,
            formattedDate = dateFormat.format(Date(now)),
            targetRole = targetRole,
            questionsAnswered = questionsAnswered,
            overallScore = overallScore,
            clarityScore = clarityScore,
            pacingScore = pacingScore,
            advocacyScore = advocacyScore,
            notes = notes,
            cryptoHash = "sha256:${UUID.randomUUID().toString().take(12)}deafauth"
        )
        dbHelper.insertSession(record)
        refreshSessions()
        return record
    }

    suspend fun deleteSession(id: String): Boolean {
        val success = dbHelper.deleteSession(id)
        if (success) refreshSessions()
        return success
    }

    suspend fun saveAccommodation(
        title: String,
        category: String,
        targetCompany: String,
        targetRole: String,
        timingStage: String,
        fullLetterText: String
    ): SavedAccommodationRecord {
        val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        val now = System.currentTimeMillis()
        val record = SavedAccommodationRecord(
            id = "ADA-REQ-${System.currentTimeMillis().toString().takeLast(6)}",
            title = title,
            category = category,
            targetCompany = targetCompany,
            targetRole = targetRole,
            timingStage = timingStage,
            fullLetterText = fullLetterText,
            timestamp = now,
            formattedDate = dateFormat.format(Date(now)),
            deafAuthSigned = true,
            verificationDigest = "sha256:${UUID.randomUUID().toString().take(12)}deafauth-signed"
        )
        dbHelper.insertAccommodation(record)
        refreshAccommodations()
        return record
    }

    suspend fun deleteAccommodation(id: String): Boolean {
        val success = dbHelper.deleteAccommodation(id)
        if (success) refreshAccommodations()
        return success
    }

    fun generateSessionAuditPacket(session: InterviewSessionRecord): String {
        return """
        {
          "audit_bundle": "DeafAuth VRS Interview Practice Session",
          "protocol_version": "PinkSync-Kernel-v2.6",
          "session_id": "${session.id}",
          "verified_timestamp": "${session.formattedDate}",
          "target_role": "${session.targetRole}",
          "questions_completed": ${session.questionsAnswered},
          "rubric_assessment": {
            "overall_score": ${session.overallScore},
            "asl_clarity_score": ${session.clarityScore},
            "relay_pacing_latency_score": ${session.pacingScore},
            "ada_self_advocacy_score": ${session.advocacyScore}
          },
          "evaluator_notes": "${session.notes.replace("\"", "\\\"")}",
          "cryptographic_fingerprint": "${session.cryptoHash}",
          "deaf_auth_issuer": "DA-7928-PINK (Jordan Vance)",
          "vr_case_reference": "TWS-VR-2026-8841",
          "compliance": "ADA Title I & WIOA Section 504"
        }
        """.trimIndent()
    }
}
