---
ID: 35
TITLE: OpenAI Integration Continuation
STAGE: explainer
DATE_ISO: 2026-04-24
SURFACE: agent
MODEL: GPT-OSS 120B (Medium)
FEATURE: none
BRANCH: main
USER: pds
COMMAND: null
LABELS: ["openai","integration","continuation"]
LINKS:
  SPEC: null
  TICKET: null
  ADR: null
  PR: null
FILES_YAML:
  - - e:/Projects/dribile/sp_dribbile/history/prompts/general/35-openai-integration-continue.explainer.prompt.md
TESTS_YAML: []
PROMPT_TEXT: "Continue"
RESPONSE_TEXT: "Added further implementation steps for OpenAI integration: DI registration, Gradle sync, Manifest update, and sample usage."
---

# 📌 Continuation: Deep‑Dive OpenAI Integration

Below are the **next concrete steps** you can apply to your EchoLogic project after adding the dependency and basic repository (covered earlier).

---

## 1️⃣ Register the OpenAI repository in Hilt DI
Add a provider method to `AppModule.kt`:
```kotlin
@Provides
@Singleton
fun provideOpenAIRepository(): OpenAIRepository = OpenAIRepository()
```
> **Why**: Keeps the repository injectable wherever you need it (ViewModels, Use‑Cases, etc.).

---

## 2️⃣ Sync Gradle & Re‑import
Run the Gradle sync (or `./gradlew assembleDebug`). This ensures the new `openai-java` library is downloaded.
```bash
./gradlew clean build
```
> **Safety**: No destructive actions – just a rebuild.

---

## 3️⃣ Add Internet permission (if not already present)
In `app/src/main/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
> **Note**: Required for any network call, including OpenAI.

---

## 4️⃣ Secure the API key in `local.properties`
```properties
openai.api.key=sk-XXXXXXXXXXXXXXXXXXXXXXXX
```
Then expose it via `BuildConfig` in `app/build.gradle.kts`:
```kotlin
android {
    // ... existing config
    defaultConfig {
        // ... existing config
        buildConfigField("String", "OPENAI_API_KEY", "\"${openaiApiKey}\"")
    }
}
```
> **Do NOT** commit `local.properties` to VCS.

---

## 5️⃣ Sample usage in a ViewModel (Chat example)
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val openAIRepo: OpenAIRepository
) : ViewModel() {
    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> = _answer

    fun ask(prompt: String) {
        viewModelScope.launch {
            _answer.value = openAIRepo.completePrompt(prompt)
        }
    }
}
```
> **Tip**: Replace `ChatViewModel` with any existing ViewModel (e.g., `MainViewModel`) if you prefer.

---

## 6️⃣ UI Hook (Compose)
```kotlin
@Composable
fun OpenAIChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    var userInput by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Prompt") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { viewModel.ask(userInput) }) {
            Text("Send to OpenAI")
        }
        Spacer(Modifier.height(16.dp))
        Text(text = viewModel.answer.collectAsState().value)
    }
}
```
> **Integration point**: Add this composable to your navigation graph or replace an existing screen.

---

## 7️⃣ Unit‑Testing the Repository
Create a fake client for tests:
```kotlin
class FakeOpenAIClient : OpenAI {
    override suspend fun createChatCompletion(request: CompletionRequest): CompletionResponse {
        return CompletionResponse(choices = listOf(Choice(message = ChatMessage("assistant", "Fake response"))))
    }
    // implement other methods as no‑ops
}
```
Then inject it via a test‑specific Hilt module.

---

## 8️⃣ Optional: Rate‑limit handling & error fallback
```kotlin
suspend fun safeComplete(prompt: String): String = try {
    openAIRepo.completePrompt(prompt)
} catch (e: Exception) {
    // log & fallback
    "Sorry, I couldn’t reach OpenAI."
}
```
> **Why**: Prevents crashes on network errors or quota limits.

---

## ✅ Quick Checklist
- [ ] `implementation "com.openai:openai-java:<latest>"` added to `build.gradle.kts`.
- [ ] API key stored in `local.properties` and exposed via `BuildConfig`.
- [ ] `OpenAIRepository` registered in `AppModule`.
- [ ] `android.permission.INTERNET` declared.
- [ ] ViewModel and Compose UI wired up.
- [ ] Gradle sync completed.
- [ ] Unit tests added for repository.

---

**Next steps**: Follow the checklist, run the app, and test the chat screen. Let me know if you hit any roadblocks or need further customization (e.g., streaming responses, custom models, etc.).

---

*Last Updated: April 24, 2026*
