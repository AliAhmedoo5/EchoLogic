---
ID: 34
TITLE: Integrate OpenAI API
STAGE: explainer
DATE_ISO: 2026-04-24
SURFACE: agent
MODEL: GPT-OSS 120B (Medium)
FEATURE: none
BRANCH: main
USER: pds
COMMAND: null
LABELS: ["openai","integration","explainer"]
LINKS:
  SPEC: null
  TICKET: null
  ADR: null
  PR: null
FILES_YAML:
  - - e:/Projects/dribile/sp_dribbile/history/prompts/general/34-integrate-openai-api.explainer.prompt.md
TESTS_YAML: []
PROMPT_TEXT: "how to open ai in the project"
RESPONSE_TEXT: "To integrate OpenAI into your Android project, add the OpenAI SDK dependency, store your API key securely (e.g., in local.properties or Android keystore), create a Retrofit service or use the provided client, and call the API from a repository or ViewModel. Remember to perform network calls off the main thread (e.g., using Kotlin coroutines) and handle responses safely."
---

# Explanation: Integrating OpenAI into EchoLogic

## 1️⃣ Add the OpenAI dependency
Add the official OpenAI Java client (or a lightweight Retrofit wrapper) to your `build.gradle`:
```gradle
implementation "com.openai:openai-java:3.2.0" // check latest version
```

## 2️⃣ Secure your API key
1. Open `local.properties` (never commit this file).
2. Add:
   ```properties
   openai.api.key=sk-****************
   ```
3. Access it in code:
   ```kotlin
   val apiKey = BuildConfig.OPENAI_API_KEY // expose via BuildConfig
   ```
   *Add the following to `app/build.gradle` to expose the key:* 
   ```gradle
   buildConfigField "String", "OPENAI_API_KEY", "\"${openaiApiKey}\""
   ```

## 3️⃣ Create a client wrapper
```kotlin
class OpenAIRepository @Inject constructor(@ApplicationContext context: Context) {
    private val client = OpenAI(apiKey = BuildConfig.OPENAI_API_KEY)

    suspend fun completePrompt(prompt: String): String = withContext(Dispatchers.IO) {
        val request = CompletionRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(ChatMessage(role = "user", content = prompt))
        )
        client.createChatCompletion(request).choices.first().message.content
    }
}
```

## 4️⃣ Use it from a ViewModel
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val openAIRepo: OpenAIRepository
) : ViewModel() {
    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    fun sendPrompt(prompt: String) {
        viewModelScope.launch {
            _response.value = openAIRepo.completePrompt(prompt)
        }
    }
}
```

## 5️⃣ UI example (Compose)
```kotlin
@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    var userInput by remember { mutableStateOf("") }
    Column {
        TextField(value = userInput, onValueChange = { userInput = it })
        Button(onClick = { viewModel.sendPrompt(userInput) }) { Text("Send") }
        Text(text = viewModel.response.collectAsState().value)
    }
}
```

## 6️⃣ Permissions & Network
Make sure `android.permission.INTERNET` is declared in `AndroidManifest.xml`.

## 7️⃣ Testing
- Write unit tests for `OpenAIRepository` using a mock client.
- Verify that the ViewModel updates the `response` state correctly.

---

**Next steps**: Add the dependency, configure the API key, and create the repository as shown. Let me know if you need help wiring it into a specific part of EchoLogic!
