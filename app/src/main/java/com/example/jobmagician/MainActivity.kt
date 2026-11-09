package com.example.jobmagician

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobmagician.data.*
import com.example.jobmagician.ui.components.AppHeader
import com.example.jobmagician.ui.components.AppNavigationDrawerContent
import com.example.jobmagician.ui.screens.*
import com.example.jobmagician.ui.theme.JobMagicianTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JobMagicianTheme {
                val viewModel: JobMagicianViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                var showNotificationDialog by remember { mutableStateOf(false) }

                LaunchedEffect(uiState.userNotificationMessage) {
                    uiState.userNotificationMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.dismissNotificationMessage()
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppNavigationDrawerContent(
                            currentDestination = uiState.currentDestination,
                            onDestinationSelected = { destination ->
                                viewModel.navigateTo(destination)
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        topBar = {
                            AppHeader(
                                uiState = uiState,
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                },
                                onToggleSearch = { viewModel.toggleSearch() },
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onToggleAslMode = { viewModel.toggleAslMode() },
                                onOpenNotifications = { showNotificationDialog = true }
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("main_bottom_navigation_bar"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavDestination.VRS_SIMULATOR,
                                    onClick = { viewModel.navigateTo(NavDestination.VRS_SIMULATOR) },
                                    icon = { Icon(Icons.Default.Videocam, contentDescription = "Live ASL Video Relay & Interview Simulator") },
                                    label = { Text("ASL Sim", fontWeight = if (uiState.currentDestination == NavDestination.VRS_SIMULATOR) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_vrs_simulator")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavDestination.ADA_STUDIO,
                                    onClick = { viewModel.navigateTo(NavDestination.ADA_STUDIO) },
                                    icon = { Icon(Icons.Default.Gavel, contentDescription = "ADA Title I Accommodation Studio") },
                                    label = { Text("ADA Law", fontWeight = if (uiState.currentDestination == NavDestination.ADA_STUDIO) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_ada_studio")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavDestination.MUNICIPAL_FORMS,
                                    onClick = { viewModel.navigateTo(NavDestination.MUNICIPAL_FORMS) },
                                    icon = { Icon(Icons.Default.LocationCity, contentDescription = "Municipal Form Intake & Consistency") },
                                    label = { Text("City Forms", fontWeight = if (uiState.currentDestination == NavDestination.MUNICIPAL_FORMS) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_municipal_forms")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavDestination.VURA_KNOWLEDGE,
                                    onClick = { viewModel.navigateTo(NavDestination.VURA_KNOWLEDGE) },
                                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "VURA Engine") },
                                    label = { Text("VURA", fontWeight = if (uiState.currentDestination == NavDestination.VURA_KNOWLEDGE) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_vura_engine")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentDestination == NavDestination.MAGICIANS_SUITE,
                                    onClick = { viewModel.navigateTo(NavDestination.MAGICIANS_SUITE) },
                                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = "magiciancore") },
                                    label = { Text("Suite", fontWeight = if (uiState.currentDestination == NavDestination.MAGICIANS_SUITE) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_magician_core")
                                )
                            }
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        contentWindowInsets = WindowInsets.safeDrawing
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.currentDestination) {
                                NavDestination.VRS_SIMULATOR -> VrsInterviewSimulatorScreen(
                                    questionBank = viewModel.interviewRepository.questionBank,
                                    savedSessions = uiState.interviewSessions,
                                    currentIndex = uiState.currentSimulatorQuestionIndex,
                                    isRecording = uiState.isSimulatorRecording,
                                    signSpeed = uiState.simulatorSignSpeed,
                                    onSelectQuestionIndex = { viewModel.setSimulatorQuestionIndex(it) },
                                    onToggleRecording = { viewModel.setSimulatorRecording(it) },
                                    onChangeSignSpeed = { viewModel.setSimulatorSignSpeed(it) },
                                    onSaveSession = { role, qCount, overall, clarity, pacing, advocacy, notes ->
                                        viewModel.saveInterviewSession(role, qCount, overall, clarity, pacing, advocacy, notes)
                                    },
                                    onDeleteSession = { viewModel.deleteInterviewSession(it) },
                                    onExportPacket = { viewModel.exportSessionAuditPacket(it) },
                                    onNavigateToAdaStudio = { viewModel.navigateTo(NavDestination.ADA_STUDIO) }
                                )

                                NavDestination.ADA_STUDIO -> AdaAccommodationStudioScreen(
                                    savedAccommodations = uiState.savedAccommodations,
                                    onSaveAccommodation = { title, cat, comp, role, timing, letter ->
                                        viewModel.saveAccommodation(title, cat, comp, role, timing, letter)
                                    },
                                    onDeleteAccommodation = { viewModel.deleteAccommodation(it) },
                                    onExportToDoc = { record ->
                                        viewModel.exportAccommodationToDoc(record)
                                        viewModel.navigateTo(NavDestination.MAGICIANS_SUITE)
                                        viewModel.setSuiteTab(SuiteTab.DOCS)
                                    },
                                    onExportToEmail = { email, record ->
                                        viewModel.exportAccommodationToEmail(email, record)
                                        viewModel.navigateTo(NavDestination.MAGICIANS_SUITE)
                                        viewModel.setSuiteTab(SuiteTab.EMAIL)
                                    },
                                    onNavigateToSimulator = { viewModel.navigateTo(NavDestination.VRS_SIMULATOR) },
                                    onNavigateToCityForms = { viewModel.navigateTo(NavDestination.MUNICIPAL_FORMS) }
                                )

                                NavDestination.MUNICIPAL_FORMS -> CityFormIntakeScreen(
                                    municipalForms = uiState.municipalForms,
                                    municipalFilings = uiState.municipalFilings,
                                    isSyncing = uiState.isMunicipalSyncing,
                                    syncLogs = uiState.municipalSyncLogs,
                                    lastSyncTime = uiState.lastMunicipalSyncTime,
                                    onTriggerAudit = { viewModel.triggerMunicipalConsistencyAudit() },
                                    onRegisterCityForm = { city, agency, url, platform, notes ->
                                        viewModel.registerNewCityForm(city, agency, url, platform, notes)
                                    },
                                    onDeleteCityForm = { viewModel.deleteCityForm(it) },
                                    onCreateFiling = { form, emp, role, acc, inc ->
                                        viewModel.createNormalizedFiling(form, emp, role, acc, inc)
                                    },
                                    onDeleteFiling = { viewModel.deleteMunicipalFiling(it) },
                                    onExportFilingToDoc = { viewModel.exportMunicipalFilingToDoc(it) },
                                    onExportFilingToEmail = { filing, email ->
                                        viewModel.exportMunicipalFilingToEmail(filing, email)
                                    },
                                    onBuildPrefillUrl = { form, emp, nar ->
                                        viewModel.buildPrefilledFormUrl(form, emp, nar)
                                    },
                                    onNavigateToAdaStudio = { viewModel.navigateTo(NavDestination.ADA_STUDIO) }
                                )

                                NavDestination.HOME -> HomeScreen(
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onExploreServices = { viewModel.navigateTo(NavDestination.SERVICES) },
                                    onViewCycle = { viewModel.navigateTo(NavDestination.MAGICIANS_SUITE) },
                                    onViewDashboard = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                                )

                                NavDestination.VURA_KNOWLEDGE -> VuraKnowledgeScreen(
                                    articles = uiState.knowledgeArticles,
                                    graphNodes = uiState.knowledgeGraphNodes,
                                    selectedDomain = uiState.selectedKnowledgeDomain,
                                    queryHistory = uiState.aiQueryHistory,
                                    onDomainSelected = { viewModel.setKnowledgeDomain(it) },
                                    onSubmitAiQuery = { viewModel.submitPinkAiQuery(it) },
                                    onExportToDoc = { q, a, cit ->
                                        viewModel.exportVuraAnswerToDoc(q, a, cit)
                                        viewModel.navigateTo(NavDestination.MAGICIANS_SUITE)
                                        viewModel.setSuiteTab(SuiteTab.DOCS)
                                    },
                                    onExportToEmail = { to, q, a ->
                                        viewModel.exportVuraAnswerToEmail(to, q, a)
                                        viewModel.navigateTo(NavDestination.MAGICIANS_SUITE)
                                        viewModel.setSuiteTab(SuiteTab.EMAIL)
                                    }
                                )

                                NavDestination.MAGICIANS_SUITE -> MagiciansSuiteScreen(
                                    activeSuite = uiState.activeMagicianSuite,
                                    onSelectSuite = { viewModel.setMagicianSuite(it) },
                                    activeSuiteTab = uiState.activeSuiteTab,
                                    onSelectSuiteTab = { viewModel.setSuiteTab(it) },
                                    emails = uiState.suiteEmails,
                                    documents = uiState.suiteDocuments,
                                    chatChannels = uiState.suiteChatChannels,
                                    channelMessages = uiState.suiteChannelMessages,
                                    files = uiState.suiteFiles,
                                    onSendEmail = { to, subj, body, cat, atts ->
                                        viewModel.sendSuiteEmail(to, subj, body, cat, atts)
                                    },
                                    onCreateDocument = { title, dt, body, tags ->
                                        viewModel.createSuiteDocument(title, dt, body, tags)
                                    },
                                    onUpdateDocument = { id, content, st ->
                                        viewModel.updateSuiteDocument(id, content, st)
                                    },
                                    onSignDocument = { viewModel.signSuiteDocument(it) },
                                    onDeleteDocument = { viewModel.deleteSuiteDocument(it) },
                                    onSendChannelMessage = { chId, txt, att ->
                                        viewModel.sendSuiteChannelMessage(chId, txt, att)
                                    },
                                    onUploadFile = { name, ft, size, cat ->
                                        viewModel.uploadSuiteFile(name, ft, size, cat)
                                    },
                                    onDeleteFile = { viewModel.deleteSuiteFile(it) },
                                    cycleSteps = uiState.cycleSteps,
                                    activeStep = uiState.activeCycleStep,
                                    onSelectStep = { viewModel.setCycleStep(it) },
                                    jobListings = uiState.jobListings,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )

                                NavDestination.STORE_REQUESTS -> VrStoreRequestsScreen(
                                    storeItems = uiState.vrStoreItems,
                                    orderRequests = uiState.storeOrderRequests,
                                    kernelEvents = uiState.pinkSyncKernelEvents,
                                    selectedCategory = uiState.selectedStoreCategory,
                                    onSelectCategory = { viewModel.setStoreCategory(it) },
                                    onSubmitOrder = { item, justification ->
                                        viewModel.submitStoreOrder(item, justification)
                                    },
                                    onTriggerKernelSync = { viewModel.triggerKernelSync() },
                                    onNavigateToDeafAuth = { viewModel.navigateTo(NavDestination.DEAF_AUTH) }
                                )

                                NavDestination.SERVICES -> ServicesScreen(
                                    activeTab = uiState.activeIntegrationTab,
                                    onTabSelected = { viewModel.setIntegrationTab(it) },
                                    onStartJourney = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                                )

                                NavDestination.CYCLE -> CycleScreen(
                                    cycleSteps = uiState.cycleSteps,
                                    activeStepId = uiState.activeCycleStep,
                                    onStepSelected = { viewModel.setCycleStep(it) },
                                    onNextStep = { viewModel.nextCycleStep() }
                                )

                                NavDestination.DASHBOARD -> DashboardScreen(
                                    activeTab = uiState.activeDashboardTab,
                                    uiState = uiState,
                                    onTabSelected = { viewModel.setDashboardTab(it) },
                                    onEnrollCourse = { viewModel.enrollCourse(it) },
                                    onAddApplication = { title, company, note ->
                                        viewModel.addJobApplication(title, company, note)
                                    }
                                )

                                NavDestination.JOBS -> JobsScreen(
                                    jobListings = uiState.jobListings,
                                    onApplyJob = { job ->
                                        viewModel.addJobApplication(
                                            title = job.title,
                                            company = job.company,
                                            note = "Applied via VR4DEAF Job Match portal.",
                                            accommodation = "Certified ASL Interpreter & CART Requested"
                                        )
                                        viewModel.navigateTo(NavDestination.DASHBOARD)
                                        viewModel.setDashboardTab("placement")
                                    }
                                )

                                NavDestination.DEAF_AUTH -> DeafAuthScreen(
                                    profile = uiState.deafAuthProfile,
                                    githubRepos = uiState.githubRepositories,
                                    subdomains = uiState.subdomainConnections,
                                    onTestPingSubdomain = { viewModel.testPingSubdomain(it) },
                                    onToggleSubdomainActive = { viewModel.toggleSubdomainActive(it) },
                                    onAddSubdomain = { sub, title, desc -> viewModel.addCustomSubdomain(sub, title, desc) },
                                    onUpdateLanguagePref = { viewModel.updateCommunicationPreference(it) },
                                    onUpdateInterpreterPref = { viewModel.updateInterpreterPreference(it) },
                                    onNavigateToStore = { viewModel.navigateTo(NavDestination.STORE_REQUESTS) },
                                    onNavigateToVrPlan = { viewModel.navigateTo(NavDestination.DASHBOARD) }
                                )

                                NavDestination.OPS_WORKBOOK -> OpsWorkbookScreen(
                                    activeTab = uiState.activeWorkbookTab,
                                    serviceLifecycles = uiState.serviceLifecycles,
                                    techDebtItems = uiState.techDebtItems,
                                    serviceEquityScores = uiState.serviceEquityScores,
                                    contributorStakes = uiState.contributorStakes,
                                    onTabSelected = { viewModel.setWorkbookTab(it) },
                                    onAddTechDebt = { serviceKey, description, severity, estHours, impact ->
                                        viewModel.addTechDebt(serviceKey, description, severity, estHours, impact)
                                    },
                                    onToggleResolveDebt = { viewModel.toggleResolveTechDebt(it) },
                                    onUpdateEquityScore = { serviceKey, score ->
                                        viewModel.updateServiceEquityScore(serviceKey, score)
                                    }
                                )

                                NavDestination.COMMUNITY -> AslMediaScreen(
                                    mediaItems = uiState.aslMediaItems,
                                    selectedCategory = uiState.selectedMediaCategory,
                                    onCategorySelected = { viewModel.setMediaCategory(it) }
                                )
                            }
                        }
                    }
                }

                if (showNotificationDialog) {
                    AlertDialog(
                        onDismissRequest = { showNotificationDialog = false },
                        icon = { Icon(Icons.Default.SignLanguage, contentDescription = null) },
                        title = { Text("VR & System Alerts") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("• Tomorrow at 10:00 AM: IPE Goal Review with TWS CRC Specialist.")
                                Text("• March 15 at 2:00 PM: Virtual Resume & ASL Interview Prep Workshop.")
                                Text("• PinkSync Telemetry: All 5 core services operational (100% healthz pass).")
                                Text("• New VURA Legal Digest: 2026 EEOC Reasonable Accommodation updates published.")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showNotificationDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
        }
    }
}
