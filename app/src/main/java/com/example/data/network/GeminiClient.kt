package com.example.data.network

import com.example.BuildConfig
import com.example.data.model.AnamneseData
import com.example.data.model.ChatMessage
import com.example.data.model.CheckInSubmission
import com.example.data.model.Exercise
import com.example.data.model.ExperienceLevel
import com.example.data.model.PrimaryGoal
import com.example.data.model.WorkoutLocation
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateWorkoutPlan(anamnese: AnamneseData): WorkoutPlan = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = buildPlanPrompt(anamnese)
                val responseJson = callGemini(apiKey, prompt)
                val parsedPlan = parseWorkoutPlanJson(responseJson)
                if (parsedPlan != null) {
                    return@withContext parsedPlan
                }
            } catch (e: Exception) {
                // Fallback to evidence-based deterministic sports science generator
            }
        }

        // Fallback generator based strictly on anamnese parameters
        return@withContext generateEvidenceBasedPlan(anamnese)
    }

    suspend fun askAiCoach(
        userMessage: String,
        anamnese: AnamneseData?,
        activePlan: WorkoutPlan?,
        recentWorkoutsSummary: String,
        history: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalCoachResponse(userMessage, activePlan, recentWorkoutsSummary)
        }

        try {
            val systemContext = """
                Você é o Coach FormaAI, um personal trainer experiente e especialista em biomecânica e fisiologia do exercício no Brasil.
                Contexto do Aluno:
                - Objetivo: ${anamnese?.primaryGoal?.label ?: "Hipertrofia"}
                - Experiência: ${anamnese?.experienceLevel?.label ?: "Intermediário"}
                - Frequência: ${anamnese?.daysPerWeek ?: 4} dias por semana
                - Local: ${anamnese?.location?.label ?: "Academia"}
                - Limitações / Evitar: ${anamnese?.exercisesToAvoid?.ifBlank { "Nenhuma" } ?: "Nenhuma"}
                - Plano Ativo: ${activePlan?.name ?: "Sem plano ativo"}
                - Resumo Recente de Treinos e Cargas: $recentWorkoutsSummary

                Diretrizes Obrigatórias:
                1. Responda em Português do Brasil com tom encorajador, técnico e direto.
                2. Use os dados reais do aluno informados acima.
                3. Se o aluno perguntar sobre última carga, consulte o histórico fornecido.
                4. Se sugerir substituições de exercícios, forneça alternativas seguras e biomecanicamente compatíveis.
                5. NUNCA faça diagnósticos médicos e NUNCA recomende treinar com dor aguda.
            """.trimIndent()

            val prompt = "$systemContext\n\nPergunta do Aluno: $userMessage"
            val response = callGemini(apiKey, prompt)
            val text = extractTextFromGeminiResponse(response)
            if (text.isNotBlank()) text else generateLocalCoachResponse(userMessage, activePlan, recentWorkoutsSummary)
        } catch (e: Exception) {
            generateLocalCoachResponse(userMessage, activePlan, recentWorkoutsSummary)
        }
    }

    suspend fun analyzeWeeklyCheckIn(
        checkIn: CheckInSubmission,
        anamnese: AnamneseData?,
        activePlan: WorkoutPlan?
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalCheckInFeedback(checkIn)
        }

        try {
            val prompt = """
                Você é o Coach FormaAI analisando o Check-in Semanal do aluno.
                Dados do Check-in:
                - Treinos realizados: ${checkIn.consistencyDaysDone} de ${checkIn.plannedDays} planejados.
                - Nível de Fadiga Geral: ${checkIn.fatigueLevel}/10.
                - Nível de Dor Muscular Tardia: ${checkIn.sorenessRating}/10.
                - RPE Médio (Percepção de Esforço): ${checkIn.perceivedEffortRpe}/10.
                - Notas do aluno: ${checkIn.userNotes}
                - Plano atual: ${activePlan?.name}

                Forneça uma resposta curta e estruturada em 2 partes:
                [DIAGNOSTICO]: Breve avaliação da recuperação, adesão e resposta ao estímulo.
                [AJUSTE]: Recomendação clara de progressão de carga, manutenção ou deload para a próxima semana.
            """.trimIndent()

            val response = callGemini(apiKey, prompt)
            val fullText = extractTextFromGeminiResponse(response)
            if (fullText.contains("[AJUSTE]")) {
                val diag = fullText.substringAfter("[DIAGNOSTICO]").substringBefore("[AJUSTE]").trim(':',' ','\n')
                val adj = fullText.substringAfter("[AJUSTE]").trim(':',' ','\n')
                Pair(diag, adj)
            } else {
                generateLocalCheckInFeedback(checkIn)
            }
        } catch (e: Exception) {
            generateLocalCheckInFeedback(checkIn)
        }
    }

    private fun callGemini(apiKey: String, prompt: String): String {
        val url = "$BASE_URL?key=$apiKey"
        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
                put("topP", 0.9)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Gemini HTTP Error: ${response.code}")
            }
            return response.body?.string() ?: ""
        }
    }

    private fun extractTextFromGeminiResponse(rawJson: String): String {
        return try {
            val root = JSONObject(rawJson)
            val candidates = root.optJSONArray("candidates") ?: return ""
            val firstCandidate = candidates.optJSONObject(0) ?: return ""
            val content = firstCandidate.optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            val firstPart = parts.optJSONObject(0) ?: return ""
            firstPart.optString("text", "")
        } catch (_: Exception) {
            ""
        }
    }

    private fun buildPlanPrompt(anamnese: AnamneseData): String {
        return """
            Gere uma ficha de treinamento de musculação estruturada em JSON para o aplicativo FormaAI.
            Perfil:
            - Objetivo Principal: ${anamnese.primaryGoal.label}
            - Nível: ${anamnese.experienceLevel.label}
            - Frequência: ${anamnese.daysPerWeek} dias/semana
            - Duração por sessão: ${anamnese.sessionDurationMinutes} minutos
            - Local/Equipamentos: ${anamnese.location.label}
            - Exercícios a evitar/Limitações: ${anamnese.exercisesToAvoid.ifBlank { "Nenhuma" }}

            Responda EXCLUSIVAMENTE em formato JSON puro (sem marcações markdown extras se possível, ou delimitado por ```json) seguindo rigorosamente este esquema:
            {
              "name": "Nome do Plano",
              "objective": "Objetivo resumido",
              "weeklySplit": "ex: ABC 3x por semana",
              "progressionGuidelines": "Instrução de sobrecarga progressiva",
              "days": [
                {
                  "dayLetter": "A",
                  "title": "Treino A - Peito e Tríceps",
                  "muscleGroups": ["Peito", "Tríceps", "Deltoide Anterior"],
                  "exercises": [
                    {
                      "name": "Supino Reto com Halteres",
                      "muscleGroup": "Peitoral",
                      "sets": 3,
                      "reps": "8-12",
                      "restSeconds": 90,
                      "suggestedWeightKg": 16.0,
                      "executionTips": "Controle a fase excêntrica em 2 segundos.",
                      "alternatives": ["Supino Máquina", "Flexão de Braço"],
                      "reasoning": "Estimula o peitoral com segurança e amplitude total."
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
    }

    private fun parseWorkoutPlanJson(jsonString: String): WorkoutPlan? {
        return try {
            var cleanJson = jsonString.trim()
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substringAfter("```").substringBeforeLast("```").trim()
            }

            val root = JSONObject(cleanJson)
            val name = root.optString("name", "Plano Personalizado FormaAI")
            val objective = root.optString("objective", "Hipertrofia e Condicionamento")
            val weeklySplit = root.optString("weeklySplit", "Divisão Semanal Personalizada")
            val progressionGuidelines = root.optString("progressionGuidelines", "Aumente as repetições ou a carga quando atingir o limite superior de repetições com técnica perfeita.")

            val daysArray = root.optJSONArray("days") ?: return null
            val daysList = mutableListOf<WorkoutSessionDay>()

            for (i in 0 until daysArray.length()) {
                val dayObj = daysArray.getJSONObject(i)
                val dayLetter = dayObj.optString("dayLetter", ('A'.code + i).toChar().toString())
                val dayTitle = dayObj.optString("title", "Treino $dayLetter")

                val muscleGroupsList = mutableListOf<String>()
                val mgArray = dayObj.optJSONArray("muscleGroups")
                if (mgArray != null) {
                    for (m in 0 until mgArray.length()) {
                        muscleGroupsList.add(mgArray.getString(m))
                    }
                }

                val exercisesArray = dayObj.optJSONArray("exercises") ?: JSONArray()
                val exercisesList = mutableListOf<Exercise>()

                for (e in 0 until exercisesArray.length()) {
                    val exObj = exercisesArray.getJSONObject(e)
                    val altList = mutableListOf<String>()
                    val altArray = exObj.optJSONArray("alternatives")
                    if (altArray != null) {
                        for (a in 0 until altArray.length()) {
                            altList.add(altArray.getString(a))
                        }
                    }

                    exercisesList.add(
                        Exercise(
                            name = exObj.optString("name", "Exercício"),
                            muscleGroup = exObj.optString("muscleGroup", "Geral"),
                            sets = exObj.optInt("sets", 3),
                            reps = exObj.optString("reps", "10-12"),
                            restSeconds = exObj.optInt("restSeconds", 90),
                            suggestedWeightKg = exObj.optDouble("suggestedWeightKg", 12.0),
                            executionTips = exObj.optString("executionTips", "Execução controlada."),
                            alternatives = altList,
                            reasoning = exObj.optString("reasoning", "Movimento de alta ativação.")
                        )
                    )
                }

                daysList.add(
                    WorkoutSessionDay(
                        dayLetter = dayLetter,
                        title = dayTitle,
                        muscleGroups = muscleGroupsList,
                        exercises = exercisesList
                    )
                )
            }

            WorkoutPlan(
                name = name,
                objective = objective,
                weeklySplit = weeklySplit,
                days = daysList,
                progressionGuidelines = progressionGuidelines
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun generateEvidenceBasedPlan(anamnese: AnamneseData): WorkoutPlan {
        val days = when (anamnese.daysPerWeek) {
            2 -> createFullBodyTwoDaySplit(anamnese)
            3 -> createFullBodyThreeDaySplit(anamnese)
            4 -> createUpperLowerFourDaySplit(anamnese)
            5 -> createPplFiveDaySplit(anamnese)
            else -> createPplSixDaySplit(anamnese)
        }

        val planName = when (anamnese.primaryGoal) {
            PrimaryGoal.HYPERTROPHY -> "FormaAI Hipertrofia & Densidade Muscular"
            PrimaryGoal.WEIGHT_LOSS -> "FormaAI Definição & Eficiência Metabólica"
            PrimaryGoal.STRENGTH -> "FormaAI Força Funcional & Sobrecarga"
            PrimaryGoal.CONDITIONING -> "FormaAI Condicionamento & Longevidade"
        }

        return WorkoutPlan(
            name = planName,
            objective = "${anamnese.primaryGoal.label} com foco em técnica, descanso inteligente e consistência",
            weeklySplit = "${anamnese.daysPerWeek} dias/semana - ${anamnese.sessionDurationMinutes} min por sessão",
            days = days,
            progressionGuidelines = "Tente adicionar 1 repetição a cada treino ou 1-2kg quando atingir o teto da faixa de repetições mantendo a execução rigorosa."
        )
    }

    private fun createUpperLowerFourDaySplit(anamnese: AnamneseData): List<WorkoutSessionDay> {
        val upperA = WorkoutSessionDay(
            dayLetter = "A",
            title = "Superior A - Foco Peito & Costas",
            muscleGroups = listOf("Peito", "Costas", "Ombros", "Tríceps"),
            exercises = listOf(
                Exercise(
                    name = "Supino Reto com Halteres",
                    muscleGroup = "Peitoral Maior",
                    sets = 4,
                    reps = "8 - 10",
                    restSeconds = 90,
                    suggestedWeightKg = 18.0,
                    executionTips = "Escápulas aduzidas e pés firmes no chão. Desça em 2 segundos controlados.",
                    alternatives = listOf("Supino com Barra", "Supino Máquina Convergente"),
                    reasoning = "Exercício composto de base para desenvolvimento da porção média e esternal do peitoral."
                ),
                Exercise(
                    name = "Puxada Frontal no Pulley",
                    muscleGroup = "Dorsal",
                    sets = 4,
                    reps = "10 - 12",
                    restSeconds = 75,
                    suggestedWeightKg = 45.0,
                    executionTips = "Puxe trazendo os cotovelos na direção dos bolsos da calça, sem jogar o tronco excessivamente para trás.",
                    alternatives = listOf("Barra Fixa com Elástico", "Puxada com Triângulo"),
                    reasoning = "Trabalha largura da grande dorsal e estabilidade escapular."
                ),
                Exercise(
                    name = "Desenvolvimento com Halteres",
                    muscleGroup = "Deltoide Anterior e Lateral",
                    sets = 3,
                    reps = "10 - 12",
                    restSeconds = 60,
                    suggestedWeightKg = 12.0,
                    executionTips = "Cotovelos ligeiramente à frente do plano coronal (30 graus). Não trave os cotovelos no topo.",
                    alternatives = listOf("Desenvolvimento Máquina", "Elevação Frontal no Cabo"),
                    reasoning = "Construção de força vertical e ombros funcionais."
                ),
                Exercise(
                    name = "Remada Curvada com Halteres",
                    muscleGroup = "Dorsal e Romboides",
                    sets = 3,
                    reps = "10 - 12",
                    restSeconds = 75,
                    suggestedWeightKg = 16.0,
                    executionTips = "Tronco inclinado a 45 graus, coluna neutra. Foque em contrair os romboides no pico.",
                    alternatives = listOf("Remada Baixa no Cabo", "Remada Cavalinho"),
                    reasoning = "Densidade muscular das costas e correção postural."
                ),
                Exercise(
                    name = "Tríceps Pulley com Corda",
                    muscleGroup = "Tríceps",
                    sets = 3,
                    reps = "12 - 15",
                    restSeconds = 60,
                    suggestedWeightKg = 20.0,
                    executionTips = "Cotovelos colados ao tronco. Abra a corda no ponto de máxima contração.",
                    alternatives = listOf("Tríceps Testa", "Mergulho entre Bancos"),
                    reasoning = "Isolamento da cabeça lateral do tríceps."
                )
            )
        )

        val lowerA = WorkoutSessionDay(
            dayLetter = "B",
            title = "Inferior A - Foco Quadríceps & Panturrilha",
            muscleGroups = listOf("Quadríceps", "Glúteos", "Panturrilhas"),
            exercises = listOf(
                Exercise(
                    name = "Agachamento Globet / Livre",
                    muscleGroup = "Quadríceps & Glúteos",
                    sets = 4,
                    reps = "8 - 10",
                    restSeconds = 120,
                    suggestedWeightKg = 24.0,
                    executionTips = "Joelhos alinhados com a ponta dos pés. Desça mantendo o tronco ereto e estável.",
                    alternatives = listOf("Leg Press 45", "Agachamento no Hack"),
                    reasoning = "Principal construtor de força e hipertrofia para membros inferiores."
                ),
                Exercise(
                    name = "Leg Press 45 Graus",
                    muscleGroup = "Quadríceps",
                    sets = 3,
                    reps = "10 - 12",
                    restSeconds = 90,
                    suggestedWeightKg = 120.0,
                    executionTips = "Pés afastados na largura dos ombros. Não estenda totalmente os joelhos no final.",
                    alternatives = listOf("Passada com Halteres", "Agachamento Búlgaro"),
                    reasoning = "Permite sobrecarga de volume sem desgaste excessivo da musculatura paravertebral."
                ),
                Exercise(
                    name = "Cadeira Extensora",
                    muscleGroup = "Reto Femoral",
                    sets = 3,
                    reps = "12 - 15",
                    restSeconds = 60,
                    suggestedWeightKg = 40.0,
                    executionTips = "Segure 1 segundo no topo da contração máxima.",
                    alternatives = listOf("Afundo Estático", "Sissy Squat"),
                    reasoning = "Isolamento analítico do quadríceps com ênfase no reto femoral."
                ),
                Exercise(
                    name = "Panturrilha em Pé",
                    muscleGroup = "Gastrocnêmio",
                    sets = 4,
                    reps = "15 - 20",
                    restSeconds = 45,
                    suggestedWeightKg = 30.0,
                    executionTips = "Pausa de 2 segundos no ponto mais baixo para anular o reflexo elástico do tendão.",
                    alternatives = listOf("Panturrilha no Leg Press", "Gêmeos Sentado"),
                    reasoning = "Estimula hipertrofia das panturrilhas com amplitude profunda."
                )
            )
        )

        val upperB = WorkoutSessionDay(
            dayLetter = "C",
            title = "Superior B - Foco Deltoides & Braços",
            muscleGroups = listOf("Ombros", "Bíceps", "Tríceps", "Costas"),
            exercises = listOf(
                Exercise(
                    name = "Supino Inclinado com Halteres",
                    muscleGroup = "Peitoral Superior (Clavicular)",
                    sets = 4,
                    reps = "10 - 12",
                    restSeconds = 90,
                    suggestedWeightKg = 16.0,
                    executionTips = "Banco inclinado em 30 a 45 graus. Não use impulso.",
                    alternatives = listOf("Supino Inclinado Máquina", "Crucifixo Inclinado"),
                    reasoning = "Enfatiza a porção superior do peitoral, gerando estética em V."
                ),
                Exercise(
                    name = "Elevação Lateral com Halteres",
                    muscleGroup = "Deltoide Lateral",
                    sets = 4,
                    reps = "12 - 15",
                    restSeconds = 60,
                    suggestedWeightKg = 8.0,
                    executionTips = "Eleve os braços até a linha dos ombros, cotovelos levemente flexionados.",
                    alternatives = listOf("Elevação Lateral no Cabo", "Elevação Lateral na Máquina"),
                    reasoning = "Desenvolve a largura dos ombros e proporção estética."
                ),
                Exercise(
                    name = "Rosca Direta com Halteres",
                    muscleGroup = "Bíceps Braquial",
                    sets = 3,
                    reps = "10 - 12",
                    restSeconds = 60,
                    suggestedWeightKg = 10.0,
                    executionTips = "Gire o punho em supinação ao subir. Não balance a lombar.",
                    alternatives = listOf("Rosca Scott", "Rosca no Pulley"),
                    reasoning = "Pico de ativação da cabeça longa e curta do bíceps."
                ),
                Exercise(
                    name = "Tríceps Francês no Cabo",
                    muscleGroup = "Tríceps (Cabeça Longa)",
                    sets = 3,
                    reps = "10 - 12",
                    restSeconds = 60,
                    suggestedWeightKg = 15.0,
                    executionTips = "Mantenha o braço estendido acima da cabeça para alongar a cabeça longa.",
                    alternatives = listOf("Tríceps Testa", "Paralelas"),
                    reasoning = "Completa o desenvolvimento total dos braços."
                )
            )
        )

        val lowerB = WorkoutSessionDay(
            dayLetter = "D",
            title = "Inferior B - Foco Posterior & Glúteos",
            muscleGroups = listOf("Posterior de Coxa", "Glúteos", "Lombar"),
            exercises = listOf(
                Exercise(
                    name = "Stiff com Halteres ou Barra",
                    muscleGroup = "Isquiotibiais & Glúteos",
                    sets = 4,
                    reps = "8 - 10",
                    restSeconds = 90,
                    suggestedWeightKg = 20.0,
                    executionTips = "Jogue o quadril para trás, joelhos semiflexionados. Sinta o alongamento posterior.",
                    alternatives = listOf("Levantamento Terra Romeno", "Mesa Flexora"),
                    reasoning = "Estimulador potente da cadeia posterior com alta tensão mecânica."
                ),
                Exercise(
                    name = "Mesa Flexora ou Cadeira Flexora",
                    muscleGroup = "Isquiotibiais",
                    sets = 4,
                    reps = "10 - 12",
                    restSeconds = 60,
                    suggestedWeightKg = 35.0,
                    executionTips = "Quadril colado no apoio, sem arquear a lombar.",
                    alternatives = listOf("Flexão Nórdica com Elástico", "Stiff Unilateral"),
                    reasoning = "Isolamento seguro e flexão direta do joelho."
                ),
                Exercise(
                    name = "Elevação Pélvica com Barra",
                    muscleGroup = "Glúteo Máximo",
                    sets = 4,
                    reps = "10 - 12",
                    restSeconds = 90,
                    suggestedWeightKg = 50.0,
                    executionTips = "Pausa de 2 segundos de contração voluntária no topo.",
                    alternatives = listOf("Glúteo Cabo", "Passada Afundo"),
                    reasoning = "Maior ativação biomecânica do glúteo máximo sem compressão axial na coluna."
                )
            )
        )

        return listOf(upperA, lowerA, upperB, lowerB)
    }

    private fun createFullBodyThreeDaySplit(anamnese: AnamneseData): List<WorkoutSessionDay> {
        return listOf(
            WorkoutSessionDay(
                dayLetter = "A",
                title = "Treino A - Full Body Estímulo A",
                muscleGroups = listOf("Peito", "Costas", "Quadríceps", "Ombros"),
                exercises = listOf(
                    Exercise(name = "Agachamento Globet", muscleGroup = "Pernas", sets = 3, reps = "10-12", restSeconds = 90, suggestedWeightKg = 16.0),
                    Exercise(name = "Supino Reto com Halteres", muscleGroup = "Peitoral", sets = 3, reps = "8-10", restSeconds = 90, suggestedWeightKg = 14.0),
                    Exercise(name = "Puxada Frontal", muscleGroup = "Costas", sets = 3, reps = "10-12", restSeconds = 75, suggestedWeightKg = 40.0),
                    Exercise(name = "Elevação Lateral", muscleGroup = "Ombros", sets = 3, reps = "12-15", restSeconds = 60, suggestedWeightKg = 7.0),
                    Exercise(name = "Prancha Abdominal", muscleGroup = "Core", sets = 3, reps = "45 seg", restSeconds = 45, suggestedWeightKg = 0.0)
                )
            ),
            WorkoutSessionDay(
                dayLetter = "B",
                title = "Treino B - Full Body Estímulo B",
                muscleGroups = listOf("Posterior de Coxa", "Costas", "Deltoides", "Braços"),
                exercises = listOf(
                    Exercise(name = "Stiff com Halteres", muscleGroup = "Posterior", sets = 3, reps = "10-12", restSeconds = 90, suggestedWeightKg = 16.0),
                    Exercise(name = "Remada Baixa no Pulley", muscleGroup = "Costas", sets = 3, reps = "10-12", restSeconds = 75, suggestedWeightKg = 40.0),
                    Exercise(name = "Desenvolvimento com Halteres", muscleGroup = "Ombros", sets = 3, reps = "10-12", restSeconds = 60, suggestedWeightKg = 10.0),
                    Exercise(name = "Cadeira Extensora", muscleGroup = "Quadríceps", sets = 3, reps = "12-15", restSeconds = 60, suggestedWeightKg = 35.0),
                    Exercise(name = "Rosca Direta com Halteres", muscleGroup = "Bíceps", sets = 3, reps = "12-15", restSeconds = 60, suggestedWeightKg = 9.0)
                )
            ),
            WorkoutSessionDay(
                dayLetter = "C",
                title = "Treino C - Full Body Estímulo C",
                muscleGroups = listOf("Glúteos", "Peito", "Costas", "Braços"),
                exercises = listOf(
                    Exercise(name = "Leg Press 45", muscleGroup = "Quadríceps/Glúteos", sets = 3, reps = "10-12", restSeconds = 90, suggestedWeightKg = 100.0),
                    Exercise(name = "Supino Inclinado com Halteres", muscleGroup = "Peitoral", sets = 3, reps = "10-12", restSeconds = 90, suggestedWeightKg = 14.0),
                    Exercise(name = "Remada Curvada com Halteres", muscleGroup = "Costas", sets = 3, reps = "10-12", restSeconds = 75, suggestedWeightKg = 14.0),
                    Exercise(name = "Mesa Flexora", muscleGroup = "Posterior", sets = 3, reps = "12-15", restSeconds = 60, suggestedWeightKg = 30.0),
                    Exercise(name = "Tríceps Corda", muscleGroup = "Tríceps", sets = 3, reps = "12-15", restSeconds = 60, suggestedWeightKg = 18.0)
                )
            )
        )
    }

    private fun createFullBodyTwoDaySplit(anamnese: AnamneseData): List<WorkoutSessionDay> {
        return createFullBodyThreeDaySplit(anamnese).take(2)
    }

    private fun createPplFiveDaySplit(anamnese: AnamneseData): List<WorkoutSessionDay> {
        val base = createUpperLowerFourDaySplit(anamnese).toMutableList()
        base.add(
            WorkoutSessionDay(
                dayLetter = "E",
                title = "Estímulo Extra - Braços, Ombros & Core",
                muscleGroups = listOf("Bíceps", "Tríceps", "Ombros", "Abdômen"),
                exercises = listOf(
                    Exercise(name = "Elevação Lateral no Cabo", muscleGroup = "Deltoide Lateral", sets = 4, reps = "12-15", restSeconds = 60, suggestedWeightKg = 6.0),
                    Exercise(name = "Rosca Scott", muscleGroup = "Bíceps", sets = 3, reps = "10-12", restSeconds = 60, suggestedWeightKg = 20.0),
                    Exercise(name = "Tríceps Francês Unilateral", muscleGroup = "Tríceps", sets = 3, reps = "10-12", restSeconds = 60, suggestedWeightKg = 8.0),
                    Exercise(name = "Abdominal na Polia", muscleGroup = "Abdômen", sets = 3, reps = "15-20", restSeconds = 45, suggestedWeightKg = 25.0)
                )
            )
        )
        return base
    }

    private fun createPplSixDaySplit(anamnese: AnamneseData): List<WorkoutSessionDay> {
        return listOf(
            WorkoutSessionDay(dayLetter = "A", title = "Push A - Peito, Ombros e Tríceps", muscleGroups = listOf("Peito", "Ombro", "Tríceps"), exercises = createUpperLowerFourDaySplit(anamnese)[0].exercises),
            WorkoutSessionDay(dayLetter = "B", title = "Pull A - Costas, Trapézio e Bíceps", muscleGroups = listOf("Costas", "Bíceps"), exercises = createUpperLowerFourDaySplit(anamnese)[2].exercises),
            WorkoutSessionDay(dayLetter = "C", title = "Legs A - Quadríceps e Panturrilhas", muscleGroups = listOf("Quadríceps", "Panturrilhas"), exercises = createUpperLowerFourDaySplit(anamnese)[1].exercises),
            WorkoutSessionDay(dayLetter = "D", title = "Push B - Foco Superior e Peitoral", muscleGroups = listOf("Peito", "Ombro", "Tríceps"), exercises = createUpperLowerFourDaySplit(anamnese)[0].exercises),
            WorkoutSessionDay(dayLetter = "E", title = "Pull B - Densidade Costas e Bíceps", muscleGroups = listOf("Costas", "Bíceps"), exercises = createUpperLowerFourDaySplit(anamnese)[2].exercises),
            WorkoutSessionDay(dayLetter = "F", title = "Legs B - Posterior e Glúteos", muscleGroups = listOf("Posterior", "Glúteos"), exercises = createUpperLowerFourDaySplit(anamnese)[3].exercises)
        )
    }

    private fun generateLocalCoachResponse(userMessage: String, activePlan: WorkoutPlan?, recentWorkoutsSummary: String): String {
        val lower = userMessage.lowercase()
        return when {
            lower.contains("última carga") || lower.contains("ultima carga") || lower.contains("peso") -> {
                if (recentWorkoutsSummary.isNotBlank()) {
                    "Consultando seus registros: $recentWorkoutsSummary. Para continuar evoluindo de forma segura, busque aumentar 1 a 2kg quando conseguir completar todas as séries na faixa superior de repetições com boa técnica!"
                } else {
                    "Você ainda não registrou sessões anteriores com cargas salvas. Ao finalizar seu primeiro treino na aba 'Meu Treino', suas cargas ficarão gravadas automaticamente para consulta!"
                }
            }
            lower.contains("substitu") || lower.contains("alternativa") || lower.contains("ocupado") -> {
                "Se o aparelho estiver ocupado, você pode substituir mantendo o mesmo padrão motor! Exemplo: Supino com halteres substitui barra; Puxada triângulo substitui puxador pegada aberta; Passada com halteres substitui Leg Press. Lembre-se de manter a mesma faixa de repetições e descanso."
            }
            lower.contains("semana") || lower.contains("consistência") || lower.contains("treinei") -> {
                "Sua meta semanal está configurada no seu plano '${activePlan?.name ?: "Personalizado"}'. O segredo para resultados reais é a regularidade e a recuperação adequada: sono de 7 a 8 horas e ingestão proteica equilibrada!"
            }
            lower.contains("dor") || lower.contains("lesão") || lower.contains("machuc") -> {
                "Atenção importante: o FormaAI recomenda nunca treinar através de dor aguda ou pontadas articulares. Se a dor persistir, descontinue o exercício imediatamente e consulte um médico ou fisioterapeuta para avaliação presencial."
            }
            else -> {
                "Como seu Coach FormaAI, estou acompanhando sua evolução! Seu plano ativo é '${activePlan?.name ?: "Personalizado"}'. Você pode me perguntar sobre cargas passadas, substituições de exercícios para quando a máquina estiver ocupada ou dicas de execução."
            }
        }
    }

    private fun generateLocalCheckInFeedback(checkIn: CheckInSubmission): Pair<String, String> {
        val diagnosis = if (checkIn.consistencyDaysDone >= checkIn.plannedDays) {
            "Excelente adesão! Você completou ${checkIn.consistencyDaysDone}/${checkIn.plannedDays} treinos planejados. Nível de fadiga registrado em ${checkIn.fatigueLevel}/10 com esforço percebido de ${checkIn.perceivedEffortRpe}/10."
        } else {
            "Adesão parcial nesta semana: ${checkIn.consistencyDaysDone} de ${checkIn.plannedDays} treinos completados. Lembre-se de que a consistência a longo prazo supera a perfeição em uma única semana."
        }

        val adjustment = if (checkIn.fatigueLevel >= 8 || checkIn.sorenessRating >= 8) {
            "Sinais de alta fadiga acumulada detectados. Recomendação para a próxima semana: mantenha as cargas atuais e aumente o descanso entre séries em 15 a 30 segundos para permitir recuperação do sistema nervoso central."
        } else if (checkIn.consistencyDaysDone >= checkIn.plannedDays && checkIn.perceivedEffortRpe <= 6) {
            "Recuperação excelente e esforço controlado! Você tem margem para sobrecarga progressiva: tente aumentar 1 a 2kg nos exercícios principais ou buscar 1 repetição a mais nas primeiras séries."
        } else {
            "Mantenha a programação atual. Foque na técnica impecável em cada repetição e garanta hidratação e sono adequados."
        }

        return Pair(diagnosis, adjustment)
    }
}
