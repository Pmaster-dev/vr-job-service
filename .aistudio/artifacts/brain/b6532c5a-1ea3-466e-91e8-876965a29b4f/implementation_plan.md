# Live ASL Video Relay & Interview Practice Simulator with Job Seeker ADA Toolkit

A dedicated interactive interview preparation suite for Deaf and Hard of Hearing job seekers, featuring a simulated Live ASL Video Relay Service (VRS/CDI) split-screen interface, real-time closed captions with ASL gloss syntax, automated interview question drill sequences, ADA accommodation disclosure card generator, and a local-first Room database for session history, rubrics, and portfolio exports.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed through Phase 1 planning questions and will guide the implementation:

- **Priority Focus**: Live ASL video relay and interview practice simulator with split-screen video feed views (Candidate self-view, ASL CDI/Interpreter feed, and Employer Interviewer feed).
- **Core Persona Workflow**: Job seeker preparing for high-stakes interviews with tailored ADA accommodation requests (e.g., CART transcription, onsite CDI interpreter, visual cues, lighting optimization) and real-time confidence ratings.
- **Data Persistence & Export**: Local-first reactive Room database using KSP and Kotlin Flow for on-device session recording, scored practice rubrics, saved accommodation disclosure letters, and multi-format export (JSON / plain text DeafAuth packet).

---

### 1. Overview & Core Concept

- **What It Does**:
  - Provides a high-fidelity **Live Interview Simulator** designed specifically for Deaf job seekers. It simulates an authentic 3-way Video Relay Service (VRS) / remote interview environment:
    1. **Employer Interviewer Video Feed**: Generates simulated interview scenarios across tech, healthcare, government, and corporate sectors with realistic questioning and facial expressions.
    2. **ASL Interpreter / CDI Video Feed**: Visual interpreter viewport with expressive animation states (listening, translating into ASL, fingerspelling, sign confirmation) and real-time gloss stream.
    3. **Candidate Self-View & Camera Mirror**: Picture-in-picture view showing lighting status, signing frame bounds, and optional device camera preview.
  - **Live Real-Time Captions & ASL Gloss Engine**: Displays dual synchronized subtitles: English captions with word-by-word streaming + ASL syntax glossing (e.g., `TIME-PAST YOU WORK WHERE?`).
  - **ADA Accommodation Request Generator**: Built-in interactive generator that produces customized ADA Title I accommodation letters, pre-interview notification templates, and employer etiquette guides (e.g., "Look at the candidate, not the interpreter").
  - **Interview Assessment & Rubric Scorer**: Post-session debrief scoring signing clarity, response conciseness, pacing, and self-advocacy confidence, stored directly in Room.

- **Target Audience / Persona**:
  - Deaf and Hard of Hearing candidates navigating job interviews, Vocational Rehabilitation (VR) clients preparing for Individualized Plan for Employment (IPE) milestones, and VR counselors running mock interview clinics.

- **Key Value**:
  - Eliminates interview anxiety by allowing candidates to rehearse realistic three-party VRS dynamics before facing real employers, ensuring their ADA accommodation needs are clearly asserted and documented.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Interview Setup & Track Selection**: User selects job role (e.g., *Software Engineer, Administrative Specialist, Healthcare Assistant*), interview type (*Technical, Behavioral, HR Screen*), and chooses accommodation preferences (*CDI Relay, CART Transcriber, Dual Interpreter*).
  2. **Active VRS Practice Room**:
     - Upper pane: Employer interviewer asking question with audio visualizer and animated video avatar.
     - Center pane: Certified Deaf Interpreter (CDI) stream signing with ASL gloss caption ticker.
     - Bottom control bar: Live microphone/speech-to-text response toggle, visual timer, "Ask Interpreter to Repeat/Clarify", and "Pause for Visual Check".
     - Real-time coaching prompts (e.g., "Good eye contact", "Remember to mention your screen magnifier accommodation").
  3. **Accommodation Disclosure Studio**:
     - Interactive builder for ADA Title I accommodation cards.
     - 1-click export to Magicians Suite Doc Engine or Email Dispatch.
  4. **Session History & Progress Analytics**:
     - Reactive Room-backed list of past mock interviews, scores, recorded answers, and rubric evaluations.
     - Export comprehensive "Interview Readiness Certificate" stamped with DeafAuth verification.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Accessible, high-contrast, professional, and confidence-inspiring. Deep navy and obsidian surfaces paired with high-visibility electric cyan accents and hot pink highlights from the PinkSync palette.
  - *Color Palette*:
    - Background: Deep Obsidian (`#0F1117`) and Charcoal Slate (`#181C26`)
    - Primary Accent: Accessible Cyan (`#00E5FF`) for ASL gloss and active relays
    - Secondary Accent: Neon Orchid (`#F50057`) for PinkSync badges and critical alerts
    - Interpreter Feed Tint: Deep Indigo Emerald (`#0A2540`) with high-contrast text
    - Status Indicators: Emerald Green (`#00E676`) for connected relay, Amber (`#FFD600`) for translation in progress
  - *Typography*: Clean sans-serif with high legibility, generous line spacing (minimum 1.4x), and high-contrast captions compliant with WCAG 2.1 AAA.
  - *Layout Density*: Spacious 8dp grid, minimum 48dp touch targets, clear tactile indicators, and full landscape/portrait adaptive responsiveness.

- **Interactive Feedback & Motion**:
  - Smooth animated transitions between interview question prompts.
  - Vibrational haptic feedback for timer countdowns and turn-taking indicators (vital for Deaf users who cannot rely on audio cues).
  - Ticker animation for real-time word-by-word closed captioning.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Tri-Pane VRS Relay Architecture vs. Single Video View**:
  - *Chosen Approach*: Tri-pane layout (Employer, ASL Interpreter, Candidate Self-View) with configurable layout modes (Side-by-Side, Speaker Focus, Picture-in-Picture).
  - *Why*: Deaf job seekers almost always interview via VRS or 3-party Zoom/Teams with an interpreter pinned. Training in a single-speaker environment fails to prepare candidates for the real spatial and attentional demands of watching both the interviewer and interpreter.
  - *Alternatives Considered*: Standard text chat mock interview. Rejected as it fails to capture authentic video relay dynamics.

- **Decision 2: Local-First Room Persistence vs. Remote Cloud Backend**:
  - *Chosen Approach*: SQLite Room Database with KSP, DAO interfaces, and Kotlin `Flow<List<InterviewSession>>` for reactive updates.
  - *Why*: Guarantees instant offline availability, zero latency, privacy for sensitive personal interview recordings and accommodation notes, and complies with user selection.
  - *Alternatives Considered*: Firebase Firestore. Deferred to keep candidate interview practice data strictly private on-device with optional manual encrypted export.

- **Decision 3: Integrated ADA Disclosure Toolkit**:
  - *Chosen Approach*: Embed the accommodation generator directly into the interview prep workflow so candidates can practice *disclosing accommodations during the interview itself* (e.g., how to explain CART or assistive software smoothly).
  - *Why*: Research shows that accommodation disclosure timing (application vs. phone screen vs. final round) is the #1 stressor for Deaf applicants.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

#### System Architecture & Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├────────────────────────────────────────────────────────────────────────┤
│  [InterviewPracticeScreen]   [VrsSimulatorScreen]   [AdaGeneratorScreen] │
│           │                           │                      │         │
│           ▼                           ▼                      ▼         │
│    Tri-Pane Video Feeds       Real-Time Gloss/CART    Export & Badges  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     JobMagicianViewModel (MVVM)                        │
│   - MutableStateFlow<InterviewState> & uiState                         │
│   - Question cadence driver & simulated interpreter coroutine loop     │
│   - Haptic feedback dispatcher                                         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      Repository & Local Data Layer                     │
├───────────────────────────────────┬────────────────────────────────────┤
│      InterviewRepository          │         DeafAuth Exporter          │
│  (abstracts DAO & Flow queries)   │  (Generates verified JSON/Text)    │
└───────────────────────────────────┴────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   AppDatabase (AndroidX Room 2.6+)                     │
├────────────────────────────────────────────────────────────────────────┤
│  • Entity: InterviewSessionEntity (id, role, score, date, duration)    │
│  • Entity: InterviewAnswerEntity (sessionId, question, answer, gloss)  │
│  • Entity: SavedAccommodationEntity (id, title, category, letterText)  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Core Entities & Room Schemas

1. **`InterviewSessionEntity`**:
   - `id: String` (Primary Key)
   - `roleTitle: String` (e.g. "Front-End Developer")
   - `companyType: String` (e.g. "Tech Enterprise")
   - `dateMillis: Long`
   - `overallScore: Int` (0-100)
   - `clarityScore: Int`
   - `advocacyScore: Int`
   - `notes: String`
   - `isVerifiedExport: Boolean`

2. **`InterviewQuestionEntity` / Presets**:
   - Built-in bank of 40+ curated questions categorized into *Behavioral*, *Technical*, *Teamwork*, *ADA & Accommodations*, and *Leadership*.
   - Each question includes: question text, ASL gloss transcription, suggested Deaf-advocacy talking points, and interpreter cue hints.

3. **`SavedAccommodationEntity`**:
   - `id: String`
   - `category: String` (e.g., "Interpreter & Communication", "Software & Hardware", "Emergency & Visual Alerts")
   - `title: String`
   - `statutoryReference: String` (e.g. "ADA Title I, 42 U.S.C. § 12112")
   - `requestLetterBody: String`
   - `employerEtiquetteGuide: String`

#### Interactive Component & State Mapping

- **`VrsVideoPane`**: Simulates camera preview and animated interpreter gestures with status badge `[VRS RELAY 1080p | AES-256]`.
- **`AslGlossTicker`**: Synchronized marquee displaying both raw English transcription and capitalized ASL grammatical gloss.
- **`AccommodationCard`**: Expandable card with copy, print, and export actions, plus quick dispatch to Magicians Suite Doc Engine.
- **`SessionHistoryView`**: Reactive Room-backed LazyColumn with swipe-to-delete, detailed rubric inspector, and "Share with VR Counselor" export button.
