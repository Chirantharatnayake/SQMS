package com.example.queuemanagmentsystem.pages

// ---------- Android + runtime permissions ----------
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// ---------- Compose ----------
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Navigation ----------
import androidx.navigation.NavController

// ---------- App resources ----------
import com.example.queuemanagmentsystem.R

// ---------- Firebase ----------
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// ---------- Coroutines ----------
import kotlinx.coroutines.delay

// ---------- Maps (camera & types) ----------
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

// ---------- Maps Compose ----------
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// ---------- Your API layer ----------
import com.example.queuemanagmentsystem.API.LocationProvider
import com.example.queuemanagmentsystem.API.PlacesRepository
import com.example.queuemanagmentsystem.API.BranchPlace
import com.google.android.libraries.places.api.Places
import com.example.queuemanagmentsystem.ui.theme.LocalAppDarkMode

// Enhanced color palette for People's Bank theme
private val PrimaryYellow = Color(0xFFF9A825)
private val SecondaryYellow = Color(0xFFFDD835)
private val PrimaryRed = Color(0xFFC62828)
private val SecondaryRed = Color(0xFFD32F2F)
private val White = Color.White
private val LightGray = Color(0xFFF5F5F5)
private val DarkGray = Color(0xFF424242)
private val SoftShadow = Color(0x1A000000)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LandingScreen(navController: NavController) {
    val yellow = Color(0xFFF9A825)
    val red = Color(0xFFC62828)
    val dark = LocalAppDarkMode.current

    // Dark mode color scheme
    val background = if (dark) Color(0xFF121212) else Color.White
    val surface = if (dark) Color(0xFF1E1E1E) else Color.White
    val onBackground = if (dark) Color.White else Color.Black
    val onSurface = if (dark) Color.White else Color.Black
    val cardBackground = if (dark) Color(0xFF2A2A2A) else Color.White
    val subtleBackground = if (dark) Color(0xFF2A2A2A) else LightGray.copy(alpha = 0.3f)

    val bannerImages = listOf(
        R.drawable.ai_banking1,
        R.drawable.ai_banking2,
        R.drawable.ai_banking3
    )

    var currentBannerIndex by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf("Home") }
    var username by remember { mutableStateOf("User") }
    var isVisible by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        isVisible = true
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        username = document.getString("username") ?: "User"
                    }
                }
                .addOnFailureListener {
                    username = "User"
                }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentBannerIndex = (currentBannerIndex + 1) % bannerImages.size
        }
    }

    // Replace invalid mixed Color/Brush background call with conditional modifier
    val pageBgModifier = if (dark) {
        Modifier.background(background)
    } else {
        Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    PrimaryYellow.copy(alpha = 0.1f),
                    White,
                    LightGray.copy(alpha = 0.3f)
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(pageBgModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 72.dp)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Enhanced Header Section with dark mode support
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -100 },
                    animationSpec = tween(800, easing = EaseOutBounce)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Welcome Back!",
                                fontSize = 16.sp,
                                color = if (dark) Color.White.copy(alpha = 0.8f) else DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = username,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dark) Color.White else PrimaryRed
                            )
                        }

                        // Profile Icon
                        Card(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = if (dark) PrimaryYellow.copy(alpha = 0.2f) else PrimaryYellow.copy(alpha = 0.1f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enhanced Banner Section with indicators
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(1000, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        Crossfade(
                            targetState = currentBannerIndex,
                            label = "BannerSlider",
                            animationSpec = tween(800)
                        ) { index ->
                            Image(
                                painter = painterResource(id = bannerImages[index]),
                                contentDescription = "Banner Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        }

                        // Enhanced Banner indicators
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(bannerImages.size) { index ->
                                Card(
                                    modifier = Modifier
                                        .size(if (index == currentBannerIndex) 12.dp else 8.dp)
                                        .animateContentSize(),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (index == currentBannerIndex) White else White.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced Services Section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { 200 },
                    animationSpec = tween(1000, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 400))
            ) {
                Column {
                    // Enhanced Services Header with dark mode support
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBackground
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (dark) {
                                        Modifier.background(cardBackground)
                                    } else {
                                        Modifier.background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    PrimaryYellow.copy(alpha = 0.1f),
                                                    SecondaryYellow.copy(alpha = 0.05f),
                                                    White
                                                )
                                            )
                                        )
                                    }
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BusinessCenter,
                                    contentDescription = null,
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Our Services",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dark) Color.White else PrimaryRed
                                    )
                                    Text(
                                        text = "Book your appointment for personalized banking",
                                        fontSize = 14.sp,
                                        color = if (dark) Color.White.copy(alpha = 0.8f) else DarkGray.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val services = listOf(
                        "Account Opening" to R.drawable.addaccount,
                        "Loan Consultation" to R.drawable.loan,
                        "Bank Card Services" to R.drawable.creditcard,
                        "Locker Access" to R.drawable.locker,
                    )

                    services.forEachIndexed { index, (title, iconRes) ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { if (index % 2 == 0) -300 else 300 },
                                animationSpec = tween(800, delayMillis = 600 + (index * 100))
                            ) + fadeIn(animationSpec = tween(800, delayMillis = 600 + (index * 100)))
                        ) {
                            EnhancedServiceCard(
                                iconRes = iconRes,
                                title = title
                            ) {
                                navController.navigate("appointment/${title}")
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced Map Section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { 300 },
                    animationSpec = tween(1000, delayMillis = 800)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 800))
            ) {
                EnhancedBranchesMapSection(sectionTitleColor = red)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Keep existing bottom navigation exactly as is
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        "Home" -> navController.navigate("landing")
                        "Slots" -> navController.navigate("slots")
                        "Account" -> navController.navigate("account")
                    }
                },
                yellow = yellow,
                red = red
            )
        }
    }
}

@Composable
fun EnhancedServiceCard(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    val dark = LocalAppDarkMode.current
    val cardBackground = if (dark) Color(0xFF2A2A2A) else Color.White
    val textColor = if (dark) Color.White else DarkGray
    val subtleTextColor = if (dark) Color.White.copy(alpha = 0.7f) else DarkGray.copy(alpha = 0.6f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        )
    ) {
        // FIX: remove malformed .then() nesting; apply conditional background cleanly
        val boxModifier = if (dark) {
            Modifier.fillMaxSize().background(cardBackground)
        } else {
            Modifier.fillMaxSize().background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PrimaryYellow.copy(alpha = 0.1f),
                        SecondaryYellow.copy(alpha = 0.05f),
                        White
                    )
                )
            )
        }
        Box(modifier = boxModifier) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced icon container with glow effect
                Card(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(6.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryRed.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryRed.copy(alpha = 0.2f),
                                        PrimaryRed.copy(alpha = 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = title,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "Book your appointment",
                        fontSize = 12.sp,
                        color = subtleTextColor
                    )
                }

                Card(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryRed.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go to $title",
                            tint = PrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedBranchesMapSection(
    sectionTitleColor: Color
) {
    val dark = LocalAppDarkMode.current
    val cardBackground = if (dark) Color(0xFF2A2A2A) else Color.White
    val textColor = if (dark) Color.White else DarkGray
    val loadingCardBackground = if (dark) Color(0xFF2A2A2A).copy(alpha = 0.9f) else White.copy(alpha = 0.9f)

    // Sri Lanka geographic clamp
    val sriLankaBounds = remember { LatLngBounds(LatLng(5.7,79.4), LatLng(9.9,82.1)) }
    fun isInSriLanka(latLng: LatLng?): Boolean = latLng != null &&
            latLng.latitude in 5.7..9.9 && latLng.longitude in 79.4..82.1

    val context = LocalContext.current

    // Permissions
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermission =
            result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        hasPermission = fine || coarse
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // API glue
    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyDixkE6Z6eoaB6dEwZtJvLdMRq2AchlLmA")
        }
        Places.createClient(context)
    }
    val repo = remember { PlacesRepository(placesClient) }
    val locationProvider = remember { LocationProvider(context) }

    // State
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var branches by remember { mutableStateOf<List<BranchPlace>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Fetch location + branches when we have permission
    LaunchedEffect(hasPermission) {
        if (!hasPermission) return@LaunchedEffect
        loading = true
        errorMsg = null
        try {
            val center = locationProvider.getCurrentOrFallback()
            myLocation = if (isInSriLanka(center)) center else null // hide if outside
            branches = repo.searchPeoplesBankNearby(center = (myLocation ?: LatLng(7.8731,80.7718)))
        } catch (e: Exception) {
            errorMsg = e.localizedMessage ?: "Search failed"
        } finally { loading = false }
    }

    // Initial camera position
    val initialCameraPosition = remember {
        CameraPosition.fromLatLngZoom(LatLng(7.8731, 80.7718), 7f)
    }
    val cameraPositionState = rememberCameraPositionState { position = initialCameraPosition }

    // Animate camera to user location when it's available
    LaunchedEffect(myLocation) {
        val target = if (isInSriLanka(myLocation)) myLocation else LatLng(7.8731,80.7718)
        target?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, if (myLocation!=null) 14f else 7f))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Enhanced map header with dark mode support
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (dark) Color(0xFF3A3A3A) else PrimaryYellow.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = sectionTitleColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Nearby Branches",
                            color = if (dark) Color.White else sectionTitleColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Find the nearest People's Bank branch",
                            fontSize = 12.sp,
                            color = if (dark) Color.White.copy(alpha = 0.8f) else DarkGray.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (dark) Color(0xFF1A1A1A) else LightGray
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasPermission && myLocation!=null,
                            latLngBoundsForCameraTarget = sriLankaBounds,
                            minZoomPreference = 5f,
                            maxZoomPreference = 20f
                        ),
                        uiSettings = MapUiSettings(zoomControlsEnabled = true)
                    ) {
                        myLocation?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "You are here"
                            )
                        }

                        branches.forEach { b ->
                            Marker(
                                state = MarkerState(position = b.latLng),
                                title = b.name,
                                snippet = "Tap for directions",
                                onClick = {
                                    val uri = b.mapsUri
                                        ?: Uri.parse("geo:${b.latLng.latitude},${b.latLng.longitude}?q=${Uri.encode(b.name)}")
                                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    runCatching { context.startActivity(intent) }
                                    true
                                }
                            )
                        }
                    }

                    if (loading) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = loadingCardBackground
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = PrimaryRed
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Finding branches...",
                                    color = textColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    errorMsg?.let { msg ->
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryRed.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = White,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Keep existing BottomNavBar exactly as is
@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    yellow: Color,
    red: Color
) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, "Home"),
        Triple("Slots", Icons.Default.CalendarToday, "Slots"),
        Triple("Account", Icons.Default.AccountCircle, "Account")
    )

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        color = yellow,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (key, icon, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(key) }
                        .fillMaxHeight()
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selectedTab == key) red else Color.DarkGray,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (selectedTab == key) red else Color.DarkGray,
                        fontWeight = if (selectedTab == key) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
