package com.example.ceres

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- API Models ---
data class RandomUserResponse(val results: List<User>)
data class User(val name: UserName, val email: String, val location: UserLocation)
data class UserName(val first: String, val last: String)
data class UserLocation(val city: String, val country: String)

data class TriviaResponse(val results: List<TriviaQuestion>)
data class TriviaQuestion(
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)

data class DogResponse(val message: String)
data class YesNoResponse(val image: String, val answer: String)

// --- Retrofit Interfaces ---
interface ApiService {
    @GET("https://randomuser.me/api/")
    fun getRandomUser(): Call<RandomUserResponse>

    @GET("https://opentdb.com/api.php")
    fun getTrivia(@Query("amount") amount: Int = 1): Call<TriviaResponse>

    @GET("https://dog.ceo/api/breeds/image/random")
    fun getRandomDog(): Call<DogResponse>

    @GET("https://yesno.wtf/api")
    fun getYesNo(): Call<YesNoResponse>
}

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val firstScreen = findViewById<LinearLayout>(R.id.firstScreen)
        val secondScreen = findViewById<ScrollView>(R.id.secondScreen)
        val btnGenerate = findViewById<Button>(R.id.btnGenerate)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://unused.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Pre-fetch
        loadIdentity()
        loadTrivia()
        loadPetData()

        btnGenerate.setOnClickListener {
            mainLayout.setBackgroundResource(R.drawable.bg2)
            firstScreen.visibility = View.GONE
            secondScreen.visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.petSection).visibility = View.GONE
        }
    }

    private fun loadIdentity() {
        val ivAvatar = findViewById<ImageView>(R.id.ivAvatar)
        val pbAvatar = findViewById<ProgressBar>(R.id.pbAvatar)
        val tvName = findViewById<TextView>(R.id.tvName)
        val pbName = findViewById<ProgressBar>(R.id.pbName)
        val tvDetails = findViewById<TextView>(R.id.tvIdentityDetails)

        apiService.getRandomUser().enqueue(object : Callback<RandomUserResponse> {
            override fun onResponse(call: Call<RandomUserResponse>, response: Response<RandomUserResponse>) {
                val user = response.body()?.results?.firstOrNull()
                user?.let {
                    val fullName = "${it.name.first} ${it.name.last}"
                    
                    // Hide name loader and show text
                    pbName.visibility = View.GONE
                    tvName.text = fullName
                    tvDetails.text = "${it.location.city}, ${it.location.country}\n${it.email}"
                    
                    val avatarUrl = "https://robohash.org/$fullName"
                    
                    // Use Glide listener to hide avatar loader
                    Glide.with(this@MainActivity)
                        .load(avatarUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                pbAvatar.visibility = View.GONE
                                return false
                            }
                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                pbAvatar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(ivAvatar)
                }
            }
            override fun onFailure(call: Call<RandomUserResponse>, t: Throwable) {
                pbName.visibility = View.GONE
                pbAvatar.visibility = View.GONE
                tvName.text = "Error"
            }
        })
    }

    private fun loadTrivia() {
        val tvTrivia = findViewById<TextView>(R.id.tvTrivia)
        val optionsContainer = findViewById<LinearLayout>(R.id.triviaOptionsContainer)
        optionsContainer.removeAllViews()

        apiService.getTrivia().enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                val trivia = response.body()?.results?.firstOrNull()
                trivia?.let { q ->
                    tvTrivia.text = Html.fromHtml(q.question, Html.FROM_HTML_MODE_LEGACY)
                    val allAnswers = (q.incorrectAnswers + q.correctAnswer).shuffled()
                    
                    allAnswers.forEach { answer ->
                        val btn = MaterialButton(this@MainActivity)
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 8, 0, 8)
                        btn.layoutParams = params
                        btn.text = Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY)
                        btn.isAllCaps = false
                        btn.setBackgroundColor(getColor(android.R.color.white))
                        btn.setTextColor(getColor(android.R.color.black))
                        btn.alpha = 0.85f
                        
                        btn.setOnClickListener {
                            if (answer == q.correctAnswer) {
                                btn.setBackgroundColor(getColor(android.R.color.holo_green_light))
                            } else {
                                btn.setBackgroundColor(getColor(android.R.color.holo_red_light))
                            }
                            for (i in 0 until optionsContainer.childCount) {
                                optionsContainer.getChildAt(i).isEnabled = false
                            }
                            findViewById<LinearLayout>(R.id.petSection).visibility = View.VISIBLE
                            val secondScreen = findViewById<ScrollView>(R.id.secondScreen)
                            secondScreen.post { secondScreen.fullScroll(View.FOCUS_DOWN) }
                        }
                        optionsContainer.addView(btn)
                    }
                }
            }
            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {}
        })
    }

    private fun loadPetData() {
        val ivPet = findViewById<ImageView>(R.id.ivPet)
        val tvKeep = findViewById<TextView>(R.id.tvKeepPet)
        val ivYesNo = findViewById<ImageView>(R.id.ivYesNo)

        apiService.getRandomDog().enqueue(object : Callback<DogResponse> {
            override fun onResponse(call: Call<DogResponse>, response: Response<DogResponse>) {
                response.body()?.message?.let {
                    Glide.with(this@MainActivity).load(it).into(ivPet)
                }
            }
            override fun onFailure(call: Call<DogResponse>, t: Throwable) {}
        })

        apiService.getYesNo().enqueue(object : Callback<YesNoResponse> {
            override fun onResponse(call: Call<YesNoResponse>, response: Response<YesNoResponse>) {
                response.body()?.let {
                    tvKeep.text = "Can you keep this pet? ${it.answer.uppercase()}"
                    Glide.with(this@MainActivity).load(it.image).into(ivYesNo)
                }
            }
            override fun onFailure(call: Call<YesNoResponse>, t: Throwable) {}
        })
    }
}