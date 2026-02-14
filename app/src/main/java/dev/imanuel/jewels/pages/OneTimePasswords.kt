package dev.imanuel.jewels.pages

import android.content.ClipData
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.android.gms.common.util.DeviceProperties.isTablet
import dev.imanuel.jewels.R
import dev.imanuel.jewels.api.OneTimePassword
import dev.imanuel.jewels.api.SharedOneTimePassword
import dev.imanuel.jewels.api.SimpleOneTimePassword
import dev.imanuel.jewels.api.User
import dev.imanuel.jewels.api.createOneTimePassword
import dev.imanuel.jewels.api.deleteOneTimePassword
import dev.imanuel.jewels.api.getOneTimePasswords
import dev.imanuel.jewels.api.getUsers
import dev.imanuel.jewels.api.shareOneTimePassword
import dev.imanuel.jewels.api.updateOneTimePassword
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.pages.components.BottomNavBar
import dev.imanuel.jewels.pages.components.TopBarActions
import dev.imanuel.jewels.utils.insertSortedBy
import dev.imanuel.jewels.utils.rememberQrScanner
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

fun generateOtp(secretKey: String): String {
    return GoogleAuthenticator(secretKey.encodeToByteArray()).generate()
}

data class CurrentState(val code: String, val secondsRemaining: Int)

@Composable
fun rememberTotpState(
    secretKey: String, periodSeconds: Int = 30
): State<CurrentState> {
    val state = remember { mutableStateOf(CurrentState("", periodSeconds)) }

    LaunchedEffect(secretKey, periodSeconds) {
        var lastStep: Long = -1L

        while (isActive) {
            val nowSec = System.currentTimeMillis() / 1000L
            val step = nowSec / periodSeconds
            val secondsRemaining = (periodSeconds - (nowSec % periodSeconds)).toInt()

            if (step != lastStep) {
                lastStep = step
                val newCode = generateOtp(secretKey)
                state.value = CurrentState(newCode, secondsRemaining)
            } else {
                state.value = CurrentState(state.value.code, secondsRemaining)
            }

            delay(1_000)
        }
    }

    return state
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTimePasswordItem(
    otp: SimpleOneTimePassword,
    users: List<User> = emptyList(),
    onShareSuccess: (List<User>) -> Unit = {},
    onUpdateSuccess: (String) -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    context: Context = koinInject(),
    httpClient: HttpClient = koinInject(),
    settings: ServerSettings = koinInject()
) {
    val clipboard = LocalClipboard.current

    val totp by rememberTotpState(otp.secretKey, periodSeconds = 30)
    val coroutineScope = rememberCoroutineScope()

    var menuExpanded by remember { mutableStateOf(false) }
    var shareOpen by remember { mutableStateOf(false) }
    var editOpen by remember { mutableStateOf(false) }
    var deleteOpen by remember { mutableStateOf(false) }
    var editAccountName by remember { mutableStateOf(otp.accountName) }
    var sharedWith by remember { mutableStateOf<List<Long>>(otp.sharedWith.map { it.id }) }

    val shareOtp = {
        coroutineScope.launch {
            if (shareOneTimePassword(otp.id, sharedWith, httpClient, context)) {
                Toast.makeText(context, "${otp.accountIssuer} wurde geteilt", Toast.LENGTH_SHORT)
                    .show()
                onShareSuccess(sharedWith.map { id -> users.find { it.id == id }!! })
            } else {
                Toast.makeText(
                    context, "${otp.accountIssuer} konnte nicht geteilt werden", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    val updateOtp = {
        coroutineScope.launch {
            if (updateOneTimePassword(otp.id, editAccountName, httpClient, context)) {
                Toast.makeText(context, "${otp.accountIssuer} wurde umbenannt", Toast.LENGTH_SHORT)
                    .show()
                onUpdateSuccess(editAccountName)
            } else {
                Toast.makeText(
                    context, "${otp.accountIssuer} konnte nicht umbenannt werden", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    val deleteOtp = {
        coroutineScope.launch {
            if (deleteOneTimePassword(otp.id, httpClient, context)) {
                Toast.makeText(context, "${otp.accountIssuer} wurde gelöscht", Toast.LENGTH_SHORT)
                    .show()
                onDeleteSuccess()
            } else {
                Toast.makeText(
                    context, "${otp.accountIssuer} konnte nicht gelöscht werden", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Card(
        onClick = {
            coroutineScope.launch {
                clipboard.setClipEntry(
                    ClipEntry(
                        ClipData.newPlainText(
                            otp.accountIssuer, totp.code
                        )
                    )
                )
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val targetProgress = totp.secondsRemaining.toFloat() / 30f
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress, animationSpec = tween(
                    durationMillis = 1_000, easing = LinearEasing
                ), label = "totpProgress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
            )
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val icon = createRef()
                val issuer = createRef()
                val name = createRef()
                val code = createRef()
                val menuButton = createRef()

                if (otp.canEdit) {
                    Box(
                        modifier = Modifier
                            .constrainAs(menuButton) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                            }
                            .wrapContentSize(Alignment.TopEnd)) {
                        IconButton(
                            onClick = {
                                menuExpanded = true
                            },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_menu),
                                contentDescription = "Menü"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(text = { Text("Teilen") }, onClick = {
                                menuExpanded = false
                                shareOpen = true
                            })
                            DropdownMenuItem(text = { Text("Bearbeiten") }, onClick = {
                                menuExpanded = false
                                editOpen = true
                            })
                            DropdownMenuItem(text = { Text("Löschen") }, onClick = {
                                menuExpanded = false
                                deleteOpen = true
                            })
                        }
                    }
                }
                AsyncImage(
                    model = "${settings.host}/${otp.iconSource}",
                    contentDescription = otp.accountIssuer,
                    modifier = Modifier
                        .constrainAs(icon) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .height(92.dp)
                        .width(92.dp)
                        .padding(16.dp))
                Text(
                    text = otp.accountIssuer,
                    modifier = Modifier
                        .constrainAs(issuer) {
                            top.linkTo(parent.top)
                            start.linkTo(icon.end)
                        }
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Start)
                Text(
                    text = totp.code,
                    modifier = Modifier.constrainAs(code) {
                        top.linkTo(issuer.bottom)
                        start.linkTo(icon.end)
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Monospace),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = otp.accountName,
                    modifier = Modifier
                        .constrainAs(name) {
                            top.linkTo(code.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(icon.end)
                        }
                        .padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start)
            }
        }
        if (deleteOpen) {
            AlertDialog(
                onDismissRequest = { deleteOpen = false },
                modifier = Modifier.padding(16.dp),
                confirmButton = {
                    TextButton(
                        onClick = {
                            deleteOtp()
                            deleteOpen = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Löschen")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { deleteOpen = false }) {
                        Text("Nicht löschen")
                    }
                },
                text = { Text("Soll der Zwei-Faktor Code für ${otp.accountName} wirklich gelöscht werden?") },
                title = { Text("Account löschen?") })
        }
        if (editOpen) {
            ModalBottomSheet(onDismissRequest = { editOpen = false }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "${otp.accountIssuer} umbenennen",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextField(
                        value = editAccountName, onValueChange = { newName ->
                            editAccountName = newName
                        }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = { editOpen = false }) {
                            Text("Verwerfen")
                        }
                        Button(onClick = {
                            updateOtp()
                            editOpen = false
                        }) {
                            Text("Speichern")
                        }
                    }
                }
            }
        }
        if (shareOpen) {
            ModalBottomSheet(
                onDismissRequest = { shareOpen = false },
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "${otp.accountIssuer} teilen", style = MaterialTheme.typography.titleLarge
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (user in users) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = sharedWith.contains(user.id),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            sharedWith += user.id
                                        } else {
                                            sharedWith -= user.id
                                        }
                                    },
                                )
                                Text(user.name)
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = { shareOpen = false }) {
                            Text("Verwerfen")
                        }
                        Button(onClick = {
                            shareOtp()
                            shareOpen = false
                        }) {
                            Text("Teilen")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTimePasswords(
    context: Context = koinInject(),
    httpClient: HttpClient = koinInject(),
    navController: NavController,
    goToSetup: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val reachability = rememberReachability()

    var isLoading by remember { mutableStateOf(false) }
    var loadingFailed by remember { mutableStateOf(false) }
    var myOneTimePasswords by remember { mutableStateOf<List<OneTimePassword>>(emptyList()) }
    var sharedOneTimePasswords by remember {
        mutableStateOf<Map<String, List<SharedOneTimePassword>>>(
            emptyMap()
        )
    }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    val saveOtp = { issuer: String, secret: String, name: String ->
        coroutineScope.launch {
            try {
                val result = createOneTimePassword(name, issuer, secret, httpClient, context)
                val otpComparator =
                    compareBy<OneTimePassword>({ it.accountIssuer })
                        .thenBy({ it.accountName })
                        .thenBy { it.id }
                myOneTimePasswords = myOneTimePasswords.insertSortedBy(result, otpComparator)
                Toast.makeText(context, "$issuer wurde hinzugefügt", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(
                    context,
                    "$issuer konnte leider nicht hinzugefügt werden",
                    Toast.LENGTH_LONG
                ).show()
            }
            try {
                getOneTimePasswords(httpClient, context)
            } catch (ex: Exception) {
            }
        }
    }

    val startScan = rememberQrScanner(
        onResult = { value ->
            val url = Url(value)
            val issuer = url.parameters["issuer"]!!
            val secret = url.parameters["secret"]!!
            val name = url.encodedPath.split("/")[1].split(":")[1]
            saveOtp(issuer, secret, name)
        },
        onCancel = { },
        onError = {
            Toast.makeText(context, "QR Code konnte nicht gelesen werden", Toast.LENGTH_SHORT)
                .show()
        },
    )

    val loadOneTimePasswords = {
        coroutineScope.launch {
            isLoading = true
            try {
                val otps = getOneTimePasswords(httpClient, context)
                myOneTimePasswords = otps.myOneTimePasswords
                val sharedOtps = HashMap<String, List<SharedOneTimePassword>>()
                for (otp in otps.sharedOneTimePasswords) {
                    sharedOtps[otp.sharedBy.name] = sharedOtps[otp.sharedBy.name].orEmpty() + otp
                }

                sharedOneTimePasswords = sharedOtps
                loadingFailed = false
            } catch (ex: Exception) {
                loadingFailed = true
            } finally {
                isLoading = false
            }
        }
    }
    val loadUsers = {
        coroutineScope.launch {
            try {
                users = getUsers(httpClient)
            } catch (ex: Exception) {
            }
        }
    }

    LaunchedEffect(Unit) {
        loadUsers()
    }

    LaunchedEffect(Unit) {
        loadOneTimePasswords()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
        topBar = {
            TopAppBar(title = {
                Text("Zwei-Faktor Codes")
            }, scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(), actions = {
                TopBarActions(goToSetup = goToSetup)
            })
        }, bottomBar = {
            if (!isTablet(context)) {
                BottomNavBar(navController)
            }
        }, floatingActionButton = {
            if (!isLoading && !loadingFailed && reachability.value == Reachability.Reachable) {
                FloatingActionButton(onClick = {
                    startScan()
                }) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_scan_code), "Neuen Code scannen")
                }
            }
        }, floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        PullToRefreshBox(
            isLoading,
            onRefresh = {
                loadOneTimePasswords()
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (loadingFailed) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "Laden fehlgeschlagen",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Image(
                        ImageBitmap.imageResource(R.drawable.loading_error),
                        contentDescription = "Laden fehlgeschlagen",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Meine Codes",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    items(
                        items = myOneTimePasswords,
                        key = { otp -> otp.id }
                    ) { otp ->
                        OneTimePasswordItem(
                            otp = SimpleOneTimePassword.fromOneTimePassword(otp)
                                .copy(canEdit = reachability.value == Reachability.Reachable),
                            users = users,
                            onShareSuccess = { sharedWith ->
                                myOneTimePasswords = myOneTimePasswords.map {
                                    if (it.id == otp.id) it.copy(sharedWith = sharedWith) else it
                                }
                            },
                            onUpdateSuccess = { newName ->
                                myOneTimePasswords = myOneTimePasswords.map {
                                    if (it.id == otp.id) it.copy(accountName = newName) else it
                                }
                            },
                            onDeleteSuccess = {
                                myOneTimePasswords -= otp
                            })
                    }
                    for (otp in sharedOneTimePasswords) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Geteilt von ${otp.key}",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        items(
                            items = otp.value,
                            key = { otp -> otp.id }
                        ) { otp ->
                            OneTimePasswordItem(
                                SimpleOneTimePassword.fromSharedOneTimePassword(otp),
                            )
                        }
                    }
                }
            }
        }
    }
}