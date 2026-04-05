package com.example.ceres

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
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
data class TriviaQuestion(val question: String, @SerializedName("correct_answer") val correctAnswer: String)

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

        // UI Elements for data
        val ivAvatar = findViewById<ImageView>(R.id.ivAvatar)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvIdentityDetails = findViewById<TextView>(R.id.tvIdentityDetails)
        val tvTrivia = findViewById<TextView>(R.id.tvTrivia)
        val ivPet = findViewById<ImageView>(R.id.ivPet)
        val tvKeepPet = findViewById<TextView>(R.id.tvKeepPet)
        val ivYesNo = findViewById<ImageView>(R.id.ivYesNo)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://unused.com/") // Base URL is required but overridden in @GET
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnGenerate.setOnClickListener {
            mainLayout.setBackgroundResource(R.drawable.bg2)
            firstScreen.visibility = View.GONE
            secondScreen.visibility = View.VISIBLE
            
            loadIdentity(tvName, tvIdentityDetails, ivAvatar)
            loadTrivia(tvTrivia)
            loadPet(ivPet)
            loadKeepDecision(tvKeepPet, ivYesNo)
        }
    }

    private fun loadIdentity(tvName: TextView, tvDetails: TextView, ivAvatar: ImageView) {
        apiService.getRandomUser().enqueue(object : Callback<RandomUserResponse> {
            override fun onResponse(call: Call<RandomUserResponse>, response: Response<RandomUserResponse>) {
                val user = response.body()?.results?.firstOrNull()
                user?.let {
                    val fullName = "${it.name.first} ${it.name.last}"
                    tvName.text = fullName
                    tvDetails.text = "${it.location.city}, ${it.location.country} | ${it.email}"
                    
                    // RoboHash Avatar using the name as seed
                    val avatarUrl = "https://robohash.org/$fullName"
                    Glide.with(this@MainActivity).load(avatarUrl).into(ivAvatar)
                }
            }
            override fun onFailure(call: Call<RandomUserResponse>, t: Throwable) {
                tvName.text = "Error loading identity"
            }
        })
    }

    private fun loadTrivia(tvTrivia: TextView) {
        apiService.getTrivia().enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                val trivia = response.body()?.results?.firstOrNull()
                trivia?.let {
                    tvTrivia.text = Html.fromHtml(it.question, Html.FROM_HTML_MODE_LEGACY)
                }
            }
            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                tvTrivia.text = "Error loading trivia"
            }
        })
    }

    private fun loadPet(ivPet: ImageView) {
        apiService.getRandomDog().enqueue(object : Callback<DogResponse> {
            override fun onResponse(call: Call<DogResponse>, response: Response<DogResponse>) {
                response.body()?.message?.let {
                    Glide.with(this@MainActivity).load(it).into(ivPet)
                }
            }
            override fun onFailure(call: Call<DogResponse>, t: Throwable) {}
        })
    }

    private fun loadKeepDecision(tvKeep: TextView, ivDecision: ImageView) {
        apiService.getYesNo().enqueue(object : Callback<YesNoResponse> {
            override fun onResponse(call: Call<YesNoResponse>, response: Response<YesNoResponse>) {
                val res = response.body()
                res?.let {
                    tvKeep.text = "Can you keep this pet? ${it.answer.uppercase()}"
                    Glide.with(this@MainActivity).load(it.image).into(ivDecision)
                }
            }
            override fun onFailure(call: Call<YesNoResponse>, t: Throwable) {}
        })
    }
}